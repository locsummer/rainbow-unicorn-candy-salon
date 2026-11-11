import Foundation

public struct MathQuestion: Equatable {
    public let numA: Int
    public let numB: Int
    public let questionText: String
    public let correctAnswer: Int
    public let options: [Int]

    public init(numA: Int, numB: Int, questionText: String, correctAnswer: Int, options: [Int]) {
        self.numA = numA
        self.numB = numB
        self.questionText = questionText
        self.correctAnswer = correctAnswer
        self.options = options
    }
}

public final class ParentalGateService {

    public static let requiredHoldSeconds: Double = 3.0
    public static let requiredHoldMillis: Int64 = 3000

    private let randomSource: RandomSource

    public init(randomSource: RandomSource = SeededRandomSource()) {
        self.randomSource = randomSource
    }

    public func generateQuestion() -> MathQuestion {
        let a = randomSource.nextInt(from: 1, to: 9)
        let b = randomSource.nextInt(from: 1, to: 9)
        let correct = a + b

        var distractors: Set<Int> = []
        while distractors.count < 2 {
            let offset = randomSource.nextDouble() > 0.5 ? 1 : -1
            let dist = correct + offset * randomSource.nextInt(from: 1, to: 4)
            if dist > 0 && dist != correct {
                distractors.insert(dist)
            }
        }

        var all = Array(distractors)
        all.append(correct)
        all.shuffle()

        return MathQuestion(
            numA: a,
            numB: b,
            questionText: "\(a) + \(b) = ?",
            correctAnswer: correct,
            options: all
        )
    }

    public func verifyHold(heldDurationMillis: Int64) -> Bool {
        return heldDurationMillis >= Self.requiredHoldMillis
    }

    public func verifyAnswer(question: MathQuestion, selectedAnswer: Int) -> Bool {
        return question.correctAnswer == selectedAnswer
    }
}
