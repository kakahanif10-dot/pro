package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class OptionKey(val code: String, val label: String) {
    A("a", "a"),
    B("b", "b"),
    C("c", "c"),
    D("d", "d");

    companion object {
        fun fromCode(raw: String?): OptionKey? =
            entries.firstOrNull { it.code.equals(raw?.trim(), ignoreCase = true) }
    }
}

data class QuestionPassage(
    val id: String,
    val title: String,
    val instruction: String,
    val header: String? = null,
    val body: String
)

data class ExamQuestion(
    val number: Int,
    val prompt: String,
    val options: Map<OptionKey, String>,
    val correctAnswer: OptionKey,
    val explanation: String,
    val passageId: String? = null
)

@Keep
@IgnoreExtraProperties
data class ExamProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val examId: String = EXAM_ID_DEFAULT,
    val studentName: String = "",
    val studentNumber: String = "",
    val answers: List<String> = List(TOTAL_QUESTIONS) { "" },
    val flaggedQuestions: List<Long> = emptyList(),
    val currentQuestion: Long = 1L,
    val remainingSeconds: Long = TOTAL_EXAM_SECONDS,
    val status: String = STATUS_IN_PROGRESS,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    companion object {
        const val EXAM_ID_DEFAULT = "uas_ganjil_2026"
        const val TOTAL_QUESTIONS = 50
        const val TOTAL_EXAM_SECONDS = 5400L // 90 menit
        const val STATUS_IN_PROGRESS = "in_progress"
        const val STATUS_COMPLETED = "completed"
    }
}

@Keep
@IgnoreExtraProperties
data class ExamAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val examId: String = ExamProgress.EXAM_ID_DEFAULT,
    val studentName: String = "",
    val studentNumber: String = "",
    val answers: List<String> = List(ExamProgress.TOTAL_QUESTIONS) { "" },
    val correctCount: Long = 0L,
    val score: Long = 0L,
    val durationSeconds: Long = 0L,
    @ServerTimestamp val completedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class SyncMetadata(
    val hasPendingWrites: Boolean = false,
    val fromCache: Boolean = false
)

enum class CloudSyncState {
    OFFLINE_SAVED,
    SYNCING,
    SYNCED_CLOUD
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
