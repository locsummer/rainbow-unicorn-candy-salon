import SwiftUI

public struct NavButton: View {
    public let iconName: String
    public let label: String
    public let backgroundColor: Color
    public let foregroundColor: Color
    public let action: () -> Void

    public init(
        iconName: String,
        label: String,
        backgroundColor: Color = Color(red: 1.0, green: 0.35, blue: 0.59),
        foregroundColor: Color = .white,
        action: @escaping () -> Void
    ) {
        self.iconName = iconName
        self.label = label
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.action = action
    }

    public var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Image(systemName: iconName)
                    .font(.system(size: 16, weight: .bold))
                Text(label)
                    .font(.system(size: 15, weight: .bold, design: .rounded))
            }
            .padding(.horizontal, 16)
            .frame(minHeight: 44) // RULE-074 iOS touch target minimum 44pt
            .background(backgroundColor)
            .foregroundColor(foregroundColor)
            .cornerRadius(22)
            .shadow(color: Color.black.opacity(0.12), radius: 3, x: 0, y: 2)
        }
    }
}
