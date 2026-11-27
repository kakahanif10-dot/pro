package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ExamAttempt
import com.example.model.ExamProgress
import com.example.model.OptionKey
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "exam_preferences")

data class ExamPreferences(
    val textScale: Float = 1.0f,
    val showTimer: Boolean = true,
    val darkTheme: Boolean = false
)

class LocalPreferencesRepository(context: Context) {
    private val appContext = context.applicationContext

    private object Keys {
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val PROGRESS_JSON = stringPreferencesKey("student_progress_json")
        val ATTEMPTS_JSON = stringPreferencesKey("student_attempts_json")
    }

    val preferences: Flow<ExamPreferences> = appContext.settingsDataStore.data.map { prefs ->
        ExamPreferences(
            textScale = (prefs[Keys.TEXT_SCALE] ?: 1.0f).coerceIn(0.9f, 1.3f),
            showTimer = prefs[Keys.SHOW_TIMER] ?: true,
            darkTheme = prefs[Keys.DARK_THEME] ?: false
        )
    }

    val cachedProgress: Flow<ExamProgress?> = appContext.settingsDataStore.data.map { prefs ->
        parseProgressJson(prefs[Keys.PROGRESS_JSON])
    }

    val cachedAttempts: Flow<List<ExamAttempt>> = appContext.settingsDataStore.data.map { prefs ->
        parseAttemptsJson(prefs[Keys.ATTEMPTS_JSON])
    }

    suspend fun setTextScale(scale: Float) {
        appContext.settingsDataStore.edit { prefs ->
            prefs[Keys.TEXT_SCALE] = scale.coerceIn(0.9f, 1.3f)
        }
    }

    suspend fun setShowTimer(enabled: Boolean) {
        appContext.settingsDataStore.edit { prefs ->
            prefs[Keys.SHOW_TIMER] = enabled
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        appContext.settingsDataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun saveProgress(
        studentName: String,
        studentNumber: String,
        answers: List<String>,
        flaggedQuestions: Set<Int>,
        currentQuestion: Int,
        remainingSeconds: Long,
        status: String
    ) {
        val sanitizedAnswers = List(ExamProgress.TOTAL_QUESTIONS) { idx ->
            OptionKey.fromCode(answers.getOrNull(idx))?.code ?: ""
        }
        val obj = JSONObject().apply {
            put("studentName", studentName.trim().take(80))
            put("studentNumber", studentNumber.trim().take(30))
            put("answers", JSONArray(sanitizedAnswers))
            put(
                "flaggedQuestions",
                JSONArray(flaggedQuestions.filter { it in 1..ExamProgress.TOTAL_QUESTIONS }.sorted())
            )
            put("currentQuestion", currentQuestion.coerceIn(1, ExamProgress.TOTAL_QUESTIONS))
            put("remainingSeconds", remainingSeconds.coerceAtMost(ExamProgress.TOTAL_EXAM_SECONDS))
            put(
                "status",
                if (status == ExamProgress.STATUS_COMPLETED) {
                    ExamProgress.STATUS_COMPLETED
                } else {
                    ExamProgress.STATUS_IN_PROGRESS
                }
            )
        }
        appContext.settingsDataStore.edit { prefs ->
            prefs[Keys.PROGRESS_JSON] = obj.toString()
        }
    }

    suspend fun saveAttempt(attempt: ExamAttempt) {
        appContext.settingsDataStore.edit { prefs ->
            val current = parseAttemptsJson(prefs[Keys.ATTEMPTS_JSON]).toMutableList()
            current.removeAll { it.id == attempt.id }
            current.add(0, attempt)
            prefs[Keys.ATTEMPTS_JSON] = encodeAttemptsJson(current)
        }
    }

    suspend fun deleteAttempt(attemptId: String) {
        appContext.settingsDataStore.edit { prefs ->
            val current = parseAttemptsJson(prefs[Keys.ATTEMPTS_JSON])
                .filterNot { it.id == attemptId }
            prefs[Keys.ATTEMPTS_JSON] = encodeAttemptsJson(current)
        }
    }

    private fun parseProgressJson(raw: String?): ExamProgress? {
        if (raw.isNullOrBlank()) return null
        return runCatching {
            val obj = JSONObject(raw)
            val ansArr = obj.optJSONArray("answers")
            val answers = List(ExamProgress.TOTAL_QUESTIONS) { idx ->
                OptionKey.fromCode(ansArr?.optString(idx))?.code ?: ""
            }
            val flagArr = obj.optJSONArray("flaggedQuestions")
            val flagged = buildList {
                if (flagArr != null) {
                    for (i in 0 until flagArr.length()) {
                        val n = flagArr.optInt(i, -1)
                        if (n in 1..ExamProgress.TOTAL_QUESTIONS) add(n.toLong())
                    }
                }
            }
            ExamProgress(
                id = ExamProgress.EXAM_ID_DEFAULT,
                studentName = obj.optString("studentName", ""),
                studentNumber = obj.optString("studentNumber", ""),
                answers = answers,
                flaggedQuestions = flagged,
                currentQuestion = obj.optLong("currentQuestion", 1L)
                    .coerceIn(1L, ExamProgress.TOTAL_QUESTIONS.toLong()),
                remainingSeconds = obj.optLong("remainingSeconds", ExamProgress.TOTAL_EXAM_SECONDS)
                    .coerceAtMost(ExamProgress.TOTAL_EXAM_SECONDS),
                status = obj.optString("status", ExamProgress.STATUS_IN_PROGRESS)
            )
        }.getOrNull()
    }

    private fun parseAttemptsJson(raw: String?): List<ExamAttempt> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val ansArr = obj.optJSONArray("answers")
                    val answers = List(ExamProgress.TOTAL_QUESTIONS) { idx ->
                        OptionKey.fromCode(ansArr?.optString(idx))?.code ?: ""
                    }
                    val completedMs = obj.optLong("completedAtMs", System.currentTimeMillis())
                    add(
                        ExamAttempt(
                            id = obj.optString("id", "att_$i"),
                            studentName = obj.optString("studentName", "Siswa Kelas X"),
                            studentNumber = obj.optString("studentNumber", ""),
                            answers = answers,
                            correctCount = obj.optLong("correctCount", 0L),
                            score = obj.optLong("score", 0L),
                            durationSeconds = obj.optLong("durationSeconds", 0L),
                            completedAt = Timestamp(completedMs / 1000L, ((completedMs % 1000L) * 1_000_000L).toInt())
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeAttemptsJson(list: List<ExamAttempt>): String {
        val arr = JSONArray()
        list.forEach { att ->
            val obj = JSONObject().apply {
                put("id", att.id)
                put("studentName", att.studentName)
                put("studentNumber", att.studentNumber)
                put("answers", JSONArray(att.answers))
                put("correctCount", att.correctCount)
                put("score", att.score)
                put("durationSeconds", att.durationSeconds)
                put("completedAtMs", att.completedAt?.toDate()?.time ?: System.currentTimeMillis())
            }
            arr.put(obj)
        }
        return arr.toString()
    }
}
