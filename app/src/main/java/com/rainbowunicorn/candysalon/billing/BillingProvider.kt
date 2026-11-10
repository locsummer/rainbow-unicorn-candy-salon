package com.rainbowunicorn.candysalon.billing

interface BillingProvider {
    fun hasEntitlement(entitlementId: String): Boolean
    fun purchase(entitlementId: String, onResult: (Boolean) -> Unit)
    fun restorePurchases(onResult: (Set<String>) -> Unit)
}

class FakeBillingProvider(
    private val ownedEntitlements: MutableSet<String> = mutableSetOf()
) : BillingProvider {

    var simulateError: Boolean = false

    override fun hasEntitlement(entitlementId: String): Boolean {
        return ownedEntitlements.contains(entitlementId)
    }

    override fun purchase(entitlementId: String, onResult: (Boolean) -> Unit) {
        if (simulateError) {
            onResult(false)
            return
        }
        ownedEntitlements.add(entitlementId)
        onResult(true)
    }

    override fun restorePurchases(onResult: (Set<String>) -> Unit) {
        onResult(ownedEntitlements.toSet())
    }
}
