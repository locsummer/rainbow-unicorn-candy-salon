import Foundation

public protocol BillingProvider: AnyObject {
    func hasEntitlement(_ id: String) -> Bool
    func purchase(_ id: String, completion: @escaping (Bool) -> Void)
    func restorePurchases(completion: @escaping (Set<String>) -> Void)
}

public final class FakeBillingProvider: BillingProvider {
    private var entitlements: Set<String>
    public var simulateError: Bool = false

    public init(entitlements: Set<String> = []) {
        self.entitlements = entitlements
    }

    public func hasEntitlement(_ id: String) -> Bool {
        return entitlements.contains(id)
    }

    public func purchase(_ id: String, completion: @escaping (Bool) -> Void) {
        if simulateError {
            completion(false)
            return
        }
        entitlements.insert(id)
        completion(true)
    }

    public func restorePurchases(completion: @escaping (Set<String>) -> Void) {
        completion(entitlements)
    }
}
