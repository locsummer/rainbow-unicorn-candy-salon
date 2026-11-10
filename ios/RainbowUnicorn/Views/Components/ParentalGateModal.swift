import SwiftUI
import GameCore

public struct ParentalGateModal: View {
    public let onVerified: () -> Void
    public let onDismiss: () -> Void

    @State private var isHolding = false
    @State private var holdProgress: Double = 0.0
    @State private var holdCompleted = false
    @State private var currentQuestion: MathQuestion?
    @State private var errorMessage: String?
    private let gateService = ParentalGateService()
    private let timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()

    public var body: some View {
        ZStack {
            Color.black.opacity(0.4).ignoresSafeArea()

            VStack(spacing: 20) {
                Text(NSLocalizedString("parental_gate_title", comment: ""))
                    .font(.system(size: 22, weight: .bold, design: .rounded))
                    .foregroundColor(Color(red: 0.29, green: 0.08, blue: 0.29))

                if !holdCompleted {
                    Text(NSLocalizedString("parental_gate_instruction", comment: ""))
                        .font(.system(size: 14, weight: .medium, design: .rounded))
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)

                    ZStack {
                        Circle()
                            .stroke(Color.pink.opacity(0.2), lineWidth: 8)
                            .frame(width: 100, height: 100)

                        Circle()
                            .trim(from: 0, to: CGFloat(holdProgress))
                            .stroke(Color.pink, style: StrokeStyle(lineWidth: 8, lineCap: .round))
                            .rotationEffect(.degrees(-90))
                            .frame(width: 100, height: 100)

                        Text(NSLocalizedString("parental_gate_hold", comment: ""))
                            .font(.system(size: 12, weight: .bold, design: .rounded))
                            .multilineTextAlignment(.center)
                    }
                    .contentShape(Circle())
                    .gesture(
                        DragGesture(minimumDistance: 0)
                            .onChanged { _ in isHolding = true }
                            .onEnded { _ in
                                isHolding = false
                                if !holdCompleted { holdProgress = 0 }
                            }
                    )
                    .onReceive(timer) { _ in
                        if isHolding && !holdCompleted {
                            holdProgress += 0.1 / ParentalGateService.requiredHoldSeconds
                            if holdProgress >= 1.0 {
                                holdCompleted = true
                                currentQuestion = gateService.generateQuestion()
                            }
                        }
                    }
                } else if let q = currentQuestion {
                    Text(q.questionText)
                        .font(.system(size: 32, weight: .bold, design: .rounded))
                        .foregroundColor(.pink)

                    HStack(spacing: 16) {
                        ForEach(q.options, id: \.self) { option in
                            Button(action: {
                                if gateService.verifyAnswer(question: q, selectedAnswer: option) {
                                    onVerified()
                                } else {
                                    errorMessage = "Sai rồi! Thử lại câu khác nhé."
                                    currentQuestion = gateService.generateQuestion()
                                }
                            }) {
                                Text("\(option)")
                                    .font(.system(size: 22, weight: .bold, design: .rounded))
                                    .frame(width: 60, height: 60)
                                    .background(Color.purple.opacity(0.8))
                                    .foregroundColor(.white)
                                    .cornerRadius(16)
                            }
                        }
                    }

                    if let err = errorMessage {
                        Text(err)
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.red)
                    }
                }

                Button(action: onDismiss) {
                    Text(NSLocalizedString("btn_cancel", comment: ""))
                        .font(.system(size: 15, weight: .semibold, design: .rounded))
                        .foregroundColor(.gray)
                }
            }
            .padding(24)
            .background(Color.white)
            .cornerRadius(24)
            .frame(maxWidth: 420)
            .shadow(radius: 10)
        }
    }
}
