package com.example

import com.example.data.ExamQuestionBank
import com.example.model.OptionKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamQuestionBankTest {

    @Test
    fun bankContainsExactly50QuestionsAndAllPassages() {
        assertEquals(50, ExamQuestionBank.questions.size)
        assertEquals((1..50).toList(), ExamQuestionBank.questions.map { it.number })
        assertEquals(4, ExamQuestionBank.passages.size)
        ExamQuestionBank.questions.forEach { q ->
            assertEquals(4, q.options.size)
            assertTrue(q.prompt.isNotBlank())
            assertTrue(q.explanation.isNotBlank())
            if (q.number in 35..50) {
                assertNotNull("Question ${q.number} must reference a passage", q.passageId)
                assertNotNull(ExamQuestionBank.passages[q.passageId])
            }
        }
    }

    @Test
    fun answerKeyMatchesPdfExactly() {
        val expectedPdfKeys = listOf(
            "a", "c", "a", "b", "d", "b", "a", "c", "a", "d",
            "a", "d", "c", "b", "d", "c", "d", "a", "c", "a",
            "c", "d", "b", "c", "a", "c", "d", "a", "d", "a",
            "d", "c", "d", "a", "b", "a", "d", "b", "d", "b",
            "a", "d", "c", "d", "c", "d", "b", "c", "a", "b"
        )
        assertEquals(50, expectedPdfKeys.size)
        ExamQuestionBank.questions.forEachIndexed { index, question ->
            assertEquals(
                "Mismatch at question ${index + 1}",
                OptionKey.fromCode(expectedPdfKeys[index]),
                question.correctAnswer
            )
        }
    }

    @Test
    fun scoreCalculationIsCorrectCountTimesTwo() {
        val allCorrect = ExamQuestionBank.questions.map { it.correctAnswer.code }
        assertEquals(50, ExamQuestionBank.calculateCorrectCount(allCorrect))
        assertEquals(100, ExamQuestionBank.calculateScore(50))

        val halfCorrect = allCorrect.mapIndexed { idx, code -> if (idx < 25) code else "" }
        assertEquals(25, ExamQuestionBank.calculateCorrectCount(halfCorrect))
        assertEquals(50, ExamQuestionBank.calculateScore(25))
    }
}
