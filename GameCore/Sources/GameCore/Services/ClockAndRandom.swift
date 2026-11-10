import Foundation

public protocol Clock {
    func nowMillis() -> Int64
}

public final class SystemClock: Clock {
    public init() {}
    public func nowMillis() -> Int64 {
        return Int64(Date().timeIntervalSince1970 * 1000)
    }
}

public final class TestClock: Clock {
    public var currentMillis: Int64

    public init(currentMillis: Int64 = 1_000_000) {
        self.currentMillis = currentMillis
    }

    public func nowMillis() -> Int64 {
        return currentMillis
    }

    public func advanceSeconds(_ seconds: Int64) {
        currentMillis += seconds * 1000
    }
}

public protocol RandomSource {
    func nextInt(bound: Int) -> Int
    func nextInt(from: Int, to: Int) -> Int
    func nextDouble() -> Double
}

public final class SeededRandomSource: RandomSource {
    private var state: UInt64

    public init(seed: UInt64 = 42) {
        self.state = seed
    }

    private func nextUInt64() -> UInt64 {
        // Linear Congruential Generator (64-bit)
        state = state &* 6364136223846793005 &+ 1442695040888963407
        return state
    }

    public func nextInt(bound: Int) -> Int {
        guard bound > 0 else { return 0 }
        return Int(nextUInt64() % UInt64(bound))
    }

    public func nextInt(from: Int, to: Int) -> Int {
        guard to >= from else { return from }
        let range = UInt64(to - from + 1)
        return from + Int(nextUInt64() % range)
    }

    public func nextDouble() -> Double {
        return Double(nextUInt64() % 1_000_000) / 1_000_000.0
    }
}
