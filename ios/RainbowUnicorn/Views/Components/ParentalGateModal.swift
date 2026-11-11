import SwiftUI
import GameCore

public struct ParentalGateModal: View {
    public let language: String
    public let onVerified: () -> Void
    public let onDismiss: () -> Void

    @State private var isHolding = false
    @State private var holdProgress: Double = 0.0
    @State private var holdCompleted = false
    @State private var currentQuestion: MathQuestion?
    @State private var errorMessage: String?
    private let gateService = ParentalGateService()
    private let timer = Timer.publish(every: 0.1, on: .main, in: .common).autoconnect()

    public init(language: String, onVerified: @escaping () -> Void, onDismiss: @escaping () -> Void) {
        self.language = language
        self.onVerified = onVerified
        self.onDismiss = onDismiss
    }

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45).ignoresSafeArea()

            VStack(spacing: 18) {
                Text("🔒 " + L10n.string("parental_gate_title", language: language))
                    .font(.system(size: 22, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                    .multilineTextAlignment(.center)

                if !holdCompleted {
                    Text(L10n.string("parental_gate_instruction", language: language))
                        .font(.system(size: 14, weight: .medium, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum.opacity(0.85))
                        .multilineTextAlignment(.center)

                    ZStack {
                        Circle()
                            .stroke(CandyColors.pastelPink, lineWidth: 8)
                            .frame(width: 112, height: 112)

                        Circle()
                            .fill(CandyColors.pastelPink.opacity(0.4))
                            .frame(width: 100, height: 100)

                        Circle()
                            .trim(from: 0, to: CGFloat(holdProgress))
                            .stroke(CandyColors.pink, style: StrokeStyle(lineWidth: 8, lineCap: .round))
                            .rotationEffect(.degrees(-90))
                            .frame(width: 112, height: 112)

                        Text(L10n.string("parental_gate_hold", language: language))
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)
                            .padding(8)
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
                                holdProgress = 1.0
                                holdCompleted = true
                                currentQuestion = gateService.generateQuestion()
                            }
                        }
                    }
                } else if let q = currentQuestion {
                    Text(q.questionText)
                        .font(.system(size: 30, weight: .heavy, design: .rounded))
                        .foregroundColor(CandyColors.pink)

                    HStack(spacing: 12) {
                        ForEach(q.options, id: \.self) { option in
                            Button(action: {
                                if gateService.verifyAnswer(question: q, selectedAnswer: option) {
                                    onVerified()
                                } else {
                                    errorMessage = L10n.string("parental_gate_wrong", language: language)
                                    currentQuestion = gateService.generateQuestion()
                                }
                            }) {
                                Text("\(option)")
                                    .font(.system(size: 20, weight: .bold, design: .rounded))
                                    .frame(maxWidth: .infinity, minHeight: 54)
                                    .background(CandyColors.purple)
                                    .foregroundColor(.white)
                                    .cornerRadius(16)
                            }
                        }
                    }

                    if let err = errorMessage {
                        Text(err)
                            .font(.system(size: 13, weight: .semibold, design: .rounded))
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                    }
                }

                NavButton(
                    iconName: "xmark",
                    label: L10n.string("btn_cancel", language: language),
                    backgroundColor: Color(white: 0.88),
                    foregroundColor: CandyColors.deepPlum,
                    fullWidth: true,
                    action: onDismiss
                )
            }
            .padding(22)
            .background(Color.white)
            .cornerRadius(24)
            .padding(.horizontal, 20)
            .frame(maxWidth: 420)
            .shadow(radius: 12)
        }
    }
}
