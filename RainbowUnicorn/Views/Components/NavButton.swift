import SwiftUI

public enum CandyColors {
    public static let pink = Color(red: 1.0, green: 0.35, blue: 0.59)
    public static let pastelPink = Color(red: 1.0, green: 0.71, blue: 0.76)
    public static let purple = Color(red: 0.78, green: 0.57, blue: 0.92)
    public static let mint = Color(red: 0.49, green: 0.91, blue: 0.84)
    public static let yellow = Color(red: 1.0, green: 0.90, blue: 0.50)
    public static let deepPlum = Color(red: 0.29, green: 0.08, blue: 0.29)
    public static let cream = Color(red: 1.0, green: 0.98, blue: 0.99)
}

public struct NavButton: View {
    public let iconName: String
    public let label: String
    public let backgroundColor: Color
    public let foregroundColor: Color
    public let isEnabled: Bool
    public let compact: Bool
    public let fullWidth: Bool
    public let action: () -> Void

    public init(
        iconName: String,
        label: String,
        backgroundColor: Color = CandyColors.pink,
        foregroundColor: Color = .white,
        isEnabled: Bool = true,
        compact: Bool = false,
        fullWidth: Bool = false,
        action: @escaping () -> Void
    ) {
        self.iconName = iconName
        self.label = label
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.isEnabled = isEnabled
        self.compact = compact
        self.fullWidth = fullWidth
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: compact ? 4 : 6) {
                Image(systemName: iconName)
                    .font(.system(size: compact ? 14 : 16, weight: .bold))
                Text(label)
                    .font(.system(size: compact ? 12 : 15, weight: .bold, design: .rounded))
                    .lineLimit(1)
                    .minimumScaleFactor(0.8)
            }
            .padding(.horizontal, compact ? 10 : 16)
            .frame(maxWidth: fullWidth ? .infinity : nil, minHeight: 44)
            .background(backgroundColor.opacity(isEnabled ? 1.0 : 0.4))
            .foregroundColor(foregroundColor.opacity(isEnabled ? 1.0 : 0.6))
            .cornerRadius(22)
            .shadow(color: Color.black.opacity(isEnabled ? 0.12 : 0.0), radius: 3, x: 0, y: 2)
        }
        .disabled(!isEnabled)
    }
}

public struct CandyBalanceBadge: View {
    public let candyBalance: Int
    public let language: String

    public init(candyBalance: Int, language: String) {
        self.candyBalance = candyBalance
        self.language = language
    }

    public var body: some View {
        HStack(spacing: 6) {
            Text("🍬")
                .font(.system(size: 16))
            Text(L10n.format("candy_balance", language: language, candyBalance))
                .font(.system(size: 14, weight: .bold, design: .rounded))
                .foregroundColor(CandyColors.deepPlum)
                .lineLimit(1)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(Color.white.opacity(0.95))
        .cornerRadius(20)
        .shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1)
    }
}

public struct ConfirmDialog: View {
    public let title: String
    public let message: String
    public let confirmText: String
    public let cancelText: String
    public let onConfirm: () -> Void
    public let onDismiss: () -> Void

    public init(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String,
        onConfirm: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.message = message
        self.confirmText = confirmText
        self.cancelText = cancelText
        self.onConfirm = onConfirm
        self.onDismiss = onDismiss
    }

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45).ignoresSafeArea()
            VStack(spacing: 14) {
                Text(title)
                    .font(.system(size: 20, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                    .multilineTextAlignment(.center)
                Text(message)
                    .font(.system(size: 15, weight: .regular, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum.opacity(0.85))
                    .multilineTextAlignment(.center)
                HStack(spacing: 10) {
                    NavButton(
                        iconName: "xmark",
                        label: cancelText,
                        backgroundColor: Color(white: 0.88),
                        foregroundColor: CandyColors.deepPlum,
                        compact: true,
                        fullWidth: true,
                        action: onDismiss
                    )
                    NavButton(
                        iconName: "checkmark",
                        label: confirmText,
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        fullWidth: true,
                        action: onConfirm
                    )
                }
            }
            .padding(22)
            .background(Color.white)
            .cornerRadius(24)
            .padding(.horizontal, 24)
            .frame(maxWidth: 400)
            .shadow(radius: 12)
        }
    }
}
