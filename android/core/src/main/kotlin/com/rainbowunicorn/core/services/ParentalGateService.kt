package com.rainbowunicorn.core.services

data class MathQuestion(
    val numA: Int,
    val numB: Int,
    val questionText: String,
    val correctAnswer: Int,
    val options: List<Int>
)

class ParentalGateService(private val randomSource: RandomSource = SeededRandomSource()) {

    companion object {
        const val REQUIRED_HOLD_SECONDS = 3.0
        const val REQUIRED_HOLD_MILLIS = 3000L
    }

    fun generateQuestion(): MathQuestion {
        val a = randomSource.nextInt(1, 9)
        val b = randomSource.nextInt(1, 9)
        val correct = a + b

        val distractors = mutableSetOf<Int>()
        while (distractors.size < 2) {
            val offset = if (randomSource.nextDouble() > 0.5) 1 else -1
            val dist = correct + offset * randomSource.nextInt(1, 4)
            if (dist > 0 && dist != correct) {
                distractors.add(dist)
            }
        }

        val allOptions = (distractors.toList() + correct).shuffled()
        return MathQuestion(
            numA = a,
            numB = b,
            questionText = "$a + $b = ?",
            correctAnswer = correct,
            options = allOptions
        )
    }

    fun verifyHold(heldDurationMillis: Long): Boolean {
        return heldDurationMillis >= REQUIRED_HOLD_MILLIS
    }

    fun verifyAnswer(question: MathQuestion, selectedAnswer: Int): Boolean {
        return question.correctAnswer == selectedAnswer
    }
}
