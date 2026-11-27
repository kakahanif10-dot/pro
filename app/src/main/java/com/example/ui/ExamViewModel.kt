package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExamPreferences
import com.example.data.ExamQuestionBank
import com.example.data.LocalPreferencesRepository
import com.example.data.NetworkMonitor
import com.example.model.CloudSyncState
import com.example.model.ExamAttempt
import com.example.model.ExamProgress
import com.example.model.OptionKey
import com.example.model.UiState
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ExamScreenRoute {
    data object Cover : ExamScreenRoute
    data object Quiz : ExamScreenRoute
    data class Result(val attempt: ExamAttempt) : ExamScreenRoute
}

data class ActiveExamState(
    val studentName: String = "",
    val studentNumber: String = "",
    val answers: List<String> = List(ExamProgress.TOTAL_QUESTIONS) { "" },
    val flaggedQuestions: Set<Int> = emptySet(),
    val currentQuestion: Int = 1,
    val remainingSeconds: Long = ExamProgress.TOTAL_EXAM_SECONDS,
    val status: String = ExamProgress.STATUS_IN_PROGRESS,
    val hydratedFromCache: Boolean = false
) {
    val answeredCount: Int
        get() = answers.count { OptionKey.fromCode(it) != null }

    val firstUnansweredNumber: Int?
        get() {
            val idx = answers.indexOfFirst { OptionKey.fromCode(it) == null }
            return if (idx >= 0) idx + 1 else null
        }
}

class ExamViewModel(
    private val localStore: LocalPreferencesRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _route = MutableStateFlow<ExamScreenRoute>(ExamScreenRoute.Cover)
    val route: StateFlow<ExamScreenRoute> = _route.asStateFlow()

    private val _activeExam = MutableStateFlow(ActiveExamState())
    val activeExam: StateFlow<ActiveExamState> = _activeExam.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    val preferences: StateFlow<ExamPreferences> = localStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ExamPreferences()
        )

    val attemptsState: StateFlow<UiState<List<ExamAttempt>>> = localStore.cachedAttempts
        .map<List<ExamAttempt>, UiState<List<ExamAttempt>>> { UiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val syncState: StateFlow<CloudSyncState> = isOnline
        .map { online ->
            if (online) CloudSyncState.SYNCED_CLOUD else CloudSyncState.OFFLINE_SAVED
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = CloudSyncState.OFFLINE_SAVED
        )

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            localStore.cachedProgress.collect { cached ->
                _activeExam.update { current ->
                    if (cached == null) {
                        current.copy(hydratedFromCache = true)
                    } else if (!current.hydratedFromCache) {
                        val normalizedAnswers = List(ExamProgress.TOTAL_QUESTIONS) { idx ->
                            cached.answers.getOrNull(idx).orEmpty()
                        }
                        current.copy(
                            studentName = cached.studentName,
                            studentNumber = cached.studentNumber,
                            answers = normalizedAnswers,
                            flaggedQuestions = cached.flaggedQuestions.map { it.toInt() }
                                .filter { it in 1..ExamProgress.TOTAL_QUESTIONS }
                                .toSet(),
                            currentQuestion = cached.currentQuestion.toInt()
                                .coerceIn(1, ExamProgress.TOTAL_QUESTIONS),
                            remainingSeconds = cached.remainingSeconds
                                .coerceAtMost(ExamProgress.TOTAL_EXAM_SECONDS),
                            status = cached.status,
                            hydratedFromCache = true
                        )
                    } else {
                        current
                    }
                }
            }
        }
    }

    fun updateStudentName(name: String) {
        _activeExam.update { it.copy(studentName = name.take(80)) }
        persistCurrentProgress()
    }

    fun updateStudentNumber(number: String) {
        _activeExam.update { it.copy(studentNumber = number.take(30)) }
        persistCurrentProgress()
    }

    fun startOrResumeExam() {
        val current = _activeExam.value
        val needsReset = current.status == ExamProgress.STATUS_COMPLETED &&
            current.answeredCount == ExamProgress.TOTAL_QUESTIONS
        if (needsReset) {
            startFreshExam()
            return
        }
        if (current.status == ExamProgress.STATUS_COMPLETED) {
            _activeExam.update {
                it.copy(
                    status = ExamProgress.STATUS_IN_PROGRESS,
                    remainingSeconds = if (it.remainingSeconds <= 0L) {
                        ExamProgress.TOTAL_EXAM_SECONDS
                    } else {
                        it.remainingSeconds
                    }
                )
            }
        }
        _route.value = ExamScreenRoute.Quiz
        startTimer()
        persistCurrentProgress()
    }

    fun startFreshExam() {
        _activeExam.update {
            it.copy(
                answers = List(ExamProgress.TOTAL_QUESTIONS) { "" },
                flaggedQuestions = emptySet(),
                currentQuestion = 1,
                remainingSeconds = ExamProgress.TOTAL_EXAM_SECONDS,
                status = ExamProgress.STATUS_IN_PROGRESS
            )
        }
        _route.value = ExamScreenRoute.Quiz
        startTimer()
        persistCurrentProgress()
    }

    fun selectAnswer(questionNumber: Int, option: OptionKey) {
        if (questionNumber !in 1..ExamProgress.TOTAL_QUESTIONS) return
        _activeExam.update { current ->
            val updated = current.answers.toMutableList()
            updated[questionNumber - 1] = option.code
            current.copy(
                answers = updated,
                status = ExamProgress.STATUS_IN_PROGRESS
            )
        }
        persistCurrentProgress()
    }

    fun toggleFlagQuestion(questionNumber: Int) {
        if (questionNumber !in 1..ExamProgress.TOTAL_QUESTIONS) return
        _activeExam.update { current ->
            val updated = current.flaggedQuestions.toMutableSet()
            if (!updated.add(questionNumber)) {
                updated.remove(questionNumber)
            }
            current.copy(flaggedQuestions = updated)
        }
        persistCurrentProgress()
    }

    fun goToQuestion(questionNumber: Int) {
        val target = questionNumber.coerceIn(1, ExamProgress.TOTAL_QUESTIONS)
        _activeExam.update { it.copy(currentQuestion = target) }
        persistCurrentProgress()
    }

    fun exitQuizToCover() {
        stopTimer()
        persistCurrentProgress()
        _route.value = ExamScreenRoute.Cover
    }

    fun openAttemptResult(attempt: ExamAttempt) {
        stopTimer()
        _route.value = ExamScreenRoute.Result(attempt)
    }

    fun backToCoverFromResult() {
        stopTimer()
        _route.value = ExamScreenRoute.Cover
    }

    fun submitExam() {
        stopTimer()
        val snapshot = _activeExam.value
        val correctCount = ExamQuestionBank.calculateCorrectCount(snapshot.answers)
        val score = ExamQuestionBank.calculateScore(correctCount)
        val durationSeconds = (ExamProgress.TOTAL_EXAM_SECONDS - snapshot.remainingSeconds)
            .coerceAtLeast(0L)
        val safeName = snapshot.studentName.trim().ifBlank { "Siswa Kelas X" }.take(80)
        val safeNumber = snapshot.studentNumber.trim().take(30)
        val attemptId = "att_${System.currentTimeMillis()}"

        val attempt = ExamAttempt(
            id = attemptId,
            userId = "local_student",
            examId = ExamProgress.EXAM_ID_DEFAULT,
            studentName = safeName,
            studentNumber = safeNumber,
            answers = snapshot.answers,
            correctCount = correctCount.toLong(),
            score = score.toLong(),
            durationSeconds = durationSeconds,
            completedAt = Timestamp.now()
        )

        _activeExam.update {
            it.copy(
                studentName = safeName,
                studentNumber = safeNumber,
                status = ExamProgress.STATUS_COMPLETED
            )
        }
        _route.value = ExamScreenRoute.Result(attempt)

        viewModelScope.launch {
            localStore.saveProgress(
                studentName = safeName,
                studentNumber = safeNumber,
                answers = snapshot.answers,
                flaggedQuestions = snapshot.flaggedQuestions,
                currentQuestion = snapshot.currentQuestion,
                remainingSeconds = snapshot.remainingSeconds,
                status = ExamProgress.STATUS_COMPLETED
            )
            localStore.saveAttempt(attempt)
        }
    }

    fun deleteAttempt(attemptId: String) {
        viewModelScope.launch {
            localStore.deleteAttempt(attemptId)
        }
    }

    fun setTextScale(scale: Float) {
        viewModelScope.launch {
            localStore.setTextScale(scale)
        }
    }

    fun setShowTimer(enabled: Boolean) {
        viewModelScope.launch {
            localStore.setShowTimer(enabled)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            localStore.setDarkTheme(enabled)
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var tickCount = 0
            while (true) {
                delay(1000L)
                val current = _activeExam.value
                if (_route.value != ExamScreenRoute.Quiz ||
                    current.status != ExamProgress.STATUS_IN_PROGRESS
                ) {
                    break
                }
                _activeExam.update { it.copy(remainingSeconds = it.remainingSeconds - 1L) }
                tickCount++
                if (tickCount % 10 == 0) {
                    persistCurrentProgress()
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun persistCurrentProgress() {
        val snapshot = _activeExam.value
        viewModelScope.launch {
            localStore.saveProgress(
                studentName = snapshot.studentName,
                studentNumber = snapshot.studentNumber,
                answers = snapshot.answers,
                flaggedQuestions = snapshot.flaggedQuestions,
                currentQuestion = snapshot.currentQuestion,
                remainingSeconds = snapshot.remainingSeconds,
                status = snapshot.status
            )
        }
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
