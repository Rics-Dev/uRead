package com.ricdev.uread.util

import android.app.Activity
import android.util.Base64
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
import com.ricdev.uread.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

data class PurchaseHelper(val activity: Activity) {

    private lateinit var billingClient: BillingClient
    private var productDetails: ProductDetails? = null
    private lateinit var purchase: Purchase

    private val productId = BuildConfig.PRODUCT_ID
//    private val base64Key = BuildConfig.BASE_64_ENCODED_PUBLIC_KEY

    private val _productName = MutableStateFlow("Searching...")
    private val _buyEnabled = MutableStateFlow(false)
    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()
    private val _statusText = MutableStateFlow("Initializing...")

//    private object Security {
//        fun verifyPurchase(base64PublicKey: String, signedData: String, signature: String): Boolean {
//            if (signedData.isEmpty() || base64PublicKey.isEmpty() || signature.isEmpty()) {
//                return false
//            }
//            try {
//                val key = generatePublicKey(base64PublicKey)
//                return verify(key, signedData, signature)
//            } catch (e: Exception) {
//                return false
//            }
//        }
//
//        private fun generatePublicKey(encodedPublicKey: String): PublicKey {
//            try {
//                val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
//                val keyFactory = KeyFactory.getInstance("RSA")
//                return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
//            } catch (e: Exception) {
//                throw RuntimeException("Error generating public key", e)
//            }
//        }
//
//        private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
//            try {
//                val sig = Signature.getInstance("SHA1withRSA")
//                sig.initVerify(publicKey)
//                sig.update(signedData.toByteArray())
//                return sig.verify(Base64.decode(signature, Base64.DEFAULT))
//            } catch (e: Exception) {
//                return false
//            }
//        }
//    }


    fun billingSetup() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _statusText.value = "Billing Client Connected"
                    queryProduct(productId)
                    checkPurchaseStatus()
                } else {
                    _statusText.value = "Billing Client Connection Failure"
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
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Unlock the premium features
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
    }



    fun makePurchase() {
        productDetails?.let { details ->
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .build()
                    )
                )
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } ?: run {
            _statusText.value = "Product details not available. Please try again."
        }
    }


    fun checkPurchaseStatus() {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val isPremium = purchases.any { purchase ->
                    purchase.products.contains(productId) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                _isPremium.value = isPremium
                _buyEnabled.value = !isPremium
                _statusText.value = if (isPremium) "Premium Features Activated" else "Premium Features Available"

                // Notify the ViewModel of the updated status
                (activity as? MainActivity)?.viewModel?.updatePremiumStatus(isPremium)
//                (activity as? MainActivity)?.viewModel?.updatePremiumStatus(true)
            } else {
                _statusText.value = "Failed to query purchases"
            }
        }
    }
}
