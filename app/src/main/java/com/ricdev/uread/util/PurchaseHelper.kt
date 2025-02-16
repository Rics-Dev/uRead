package com.ricdev.uread.util

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.common.collect.ImmutableList
import com.ricdev.uread.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PurchaseHelper(val activity: Activity) {

    private lateinit var billingClient: BillingClient
    private var productDetails: ProductDetails? = null
    private lateinit var purchase: Purchase

    private val productId = BuildConfig.PRODUCT_ID
//    private val base64Key = BuildConfig.BASE_64_ENCODED_PUBLIC_KEY

    private val _productName = MutableStateFlow("Searching...")
    private val _formattedPrice = MutableStateFlow("N/A")
    private val _priceCurrencyCode = MutableStateFlow("N/A")
    val formattedPrice = _formattedPrice.asStateFlow()
//    val priceCurrencyCode = _priceCurrencyCode.asStateFlow()
    private val _buyEnabled = MutableStateFlow(false)
    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()
    private val _statusText = MutableStateFlow("Initializing...")



    fun billingSetup() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        _statusText.value = "Billing Client Connected"
                        queryProduct(productId)
                        checkPurchaseStatus()
                    }
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                        _statusText.value = "Billing Unavailable"
                        // Handle billing unavailability
                    }
                    else -> {
                        _statusText.value = "Billing Client Connection Failed: ${billingResult.debugMessage}"
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusText.value = "Billing Client Connection Lost"
            }
        })
    }

    fun queryProduct(productId: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            BillingClient.ProductType.INAPP
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { _, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                productDetails = productDetailsList[0]
                _productName.value = "Product: " + productDetails?.name
                productDetails?.toString()?.let { Log.e("Product Name", it) }
                _formattedPrice.value = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
                _priceCurrencyCode.value = productDetails?.oneTimePurchaseOfferDetails?.priceCurrencyCode ?: "N/A"
                _buyEnabled.value = true
            } else {
                _statusText.value = "No Matching Products Found"
                _buyEnabled.value = false
            }
        }
    }


    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                completePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _statusText.value = "Purchase Canceled"
        } else {
            _statusText.value = "Purchase Error"
        }
    }


    private fun completePurchase(item: Purchase) {
        purchase = item
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                // Unlock premium features
                _isPremium.value = true
                _buyEnabled.value = false
                _statusText.value = "Purchase Completed"

                // Acknowledge the purchase
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _statusText.value = "Purchase acknowledged"
                    } else {
                        _statusText.value = "Failed to acknowledge purchase: ${billingResult.debugMessage}"
                    }
                }
            }
            Purchase.PurchaseState.PENDING -> {
                _statusText.value = "Purchase is pending. Complete the transaction to unlock premium features."
            }
            else -> {
                _statusText.value = "Purchase not completed."
            }
        }
    }



    fun makePurchase() {
        if (productDetails == null) {
            _statusText.value = "Product details not available. Please try again."
            return
        }
        try {
            // Ensure billing client is ready
            if (billingClient.connectionState != BillingClient.ConnectionState.CONNECTED) {
                _statusText.value = "Billing client not connected. Please try again."
                return
            }


            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails!!)
                            .build()
                    )
                )
                .build()

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    _statusText.value = "Purchase flow started"
                }
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                    _statusText.value = "Billing feature not supported"
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                    _statusText.value = "Billing service disconnected"
                }
                else -> {
                    _statusText.value = "Failed to launch billing flow: ${billingResult.debugMessage}"
                }
            }
        } catch (e: Exception) {
            _statusText.value = "Error launching purchase: ${e.message}"
            e.printStackTrace()
        }
    }

    fun checkPurchaseStatus() {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Determine premium status
                val isPremium = purchases.any { purchase ->
                    purchase.products.contains(productId) &&
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }

                // Update local state
                _isPremium.value = isPremium
                _buyEnabled.value = !isPremium
                _statusText.value = if (isPremium) "Premium Features Activated" else "Premium Features Available"
            } else {
                _statusText.value = "Failed to query purchases: ${billingResult.debugMessage}"
            }
        }
    }


}
