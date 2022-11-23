//package com.getyoteam.budamind.activity
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import com.android.billingclient.api.*
//import com.getyoteam.budamind.Model.PurchaseModel
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.utils.AppDatabase
//import com.getyoteam.budamind.utils.Security
//import io.branch.referral.util.BRANCH_STANDARD_EVENT
//import io.branch.referral.util.BranchEvent
//import io.branch.referral.util.CurrencyType
//import kotlinx.android.synthetic.main.actionbar_layout.*
//import kotlinx.android.synthetic.main.activity_subscribe.*
//import java.util.*
//
//
//class SubscribeActivity : AppCompatActivity(), View.OnClickListener,
//    BillingClientStateListener, PurchasesUpdatedListener {
//
//    private var intentExtra: Bundle? = null
//    private var isMonthly: Boolean? = false
//    private var isFirstTime: Boolean = false
//
//    private object GameSku {
//        val GAS = "gas"
//        val PREMIUM_CAR = "premium_car"
//        val GOLD_MONTHLY = "gold_monthly1"
//        val GOLD_YEARLY = "gold_yearly"
//
//        val INAPP_SKUS = listOf(GAS, PREMIUM_CAR)
//        val SUBS_SKUS = listOf(GOLD_MONTHLY, GOLD_YEARLY)
//        val CONSUMABLE_SKUS = listOf(GAS)
//        val GOLD_STATUS_SKUS = SUBS_SKUS // coincidence that there only gold_status is a sub
//    }
//
//    private lateinit var db: AppDatabase
//    private var skuListSubPurchase: List<PurchaseModel>? = null
//
//    companion object {
//        private const val LOG_TAG = "BillingRepository"
//    }
//
//    lateinit private var billingClient: BillingClient
//    private var skuList = mutableListOf<SkuDetails>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_subscribe)
//        db = AppDatabase.getDatabase(this)
//
//        intentExtra = intent.extras
//
//        tvHeader.text = getString(R.string.str_subscribe_clarity)
//        ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
//
//        llYearlyPlan.setOnClickListener(this)
//        llMonthlyPlan.setOnClickListener(this)
//
//
//        ivHeaderLeft.setOnClickListener(this)
//        tvCountinue.setOnClickListener(this)
////        rippalSubscription.setOnClickListener(View.OnClickListener {
//////
////        })
////        rippalSubscription.setOnRippleCompleteListener(OnRippleCompleteListener {
////            Log.d(
////                "Sample",
////                "Ripple completed"
////            )
//
////            var price = 0.0
////            var type = ""
////            if (isMonthly!!) {
////                price = 59.99
////                type = "MONTHLY"
////            } else {
////                price = 9.99
////                type = "YEARLY"
////            }
////
////            BranchEvent(BRANCH_STANDARD_EVENT.INITIATE_PURCHASE)
////                .setCurrency(CurrencyType.USD)
////                .setCoupon("INITIATE_PURCHASE")
////                .setDescription(type)
////                .logEvent(this)
////            if (isMonthly!!) {
////                loadItemForPurchase(GameSku.GOLD_MONTHLY)
////            } else {
////                loadItemForPurchase(GameSku.GOLD_YEARLY)
////            }
////        })
//
//        isFirstTime = intent.getBooleanExtra("isFirstTime", false)
//        if (isFirstTime) {
//            tvCountinue.setText(R.string.str_continue)
//        } else {
//            tvCountinue.setText(R.string.str_subscribe_now)
//        }
//
////        BranchEvent(BRANCH_STANDARD_EVENT.START_TRIAL)
////            .setCurrency(CurrencyType.USD)
////            .setDescription("START_TRIAL")
////            .logEvent(this)
//
//        setBillingConnect()
//
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.tvCountinue -> {
//                var price = 0.0
//                var type = ""
//                if (isMonthly!!) {
//                    price = 59.99
//                    type = "MONTHLY"
//                } else {
//                    price = 9.99
//                    type = "YEARLY"
//                }
//
////                BranchEvent(BRANCH_STANDARD_EVENT.INITIATE_PURCHASE)
////                    .setCurrency(CurrencyType.USD)
////                    .setCoupon("INITIATE_PURCHASE")
////                    .setDescription(type)
////                    .logEvent(this)
////                if (isMonthly!!) {
////                    loadItemForPurchase(GameSku.GOLD_MONTHLY)
////                } else {
////                    loadItemForPurchase(GameSku.GOLD_YEARLY)
////                }
//            }
//
//            R.id.llMonthlyPlan -> {
//                tv_999.setTextColor(resources.getColor(R.color.color_292929))
//                tvFreeTrail.setTextColor(resources.getColor(R.color.color_292929))
//                tvMonthlyPlan.setTextColor(resources.getColor(R.color.color_292929))
//                isMonthly = true
//                tv_499.setTextColor(resources.getColor(R.color.app_yellow_color))
//                tvPerMonth.setTextColor(resources.getColor(R.color.color_black))
//                tvYearlyPlan.setTextColor(resources.getColor(R.color.color_black))
//                llMonthlyPlan.setBackground(resources.getDrawable(R.drawable.card_edge_subscribe))
//                llYearlyPlan.setBackground(resources.getDrawable(R.drawable.card_edge_gray))
//            }
//            R.id.llYearlyPlan -> {
//                tv_499.setTextColor(resources.getColor(R.color.color_292929))
//                tvPerMonth.setTextColor(resources.getColor(R.color.color_292929))
//                tvYearlyPlan.setTextColor(resources.getColor(R.color.color_292929))
//                isMonthly = false
//                tv_999.setTextColor(resources.getColor(R.color.app_yellow_color))
//                tvFreeTrail.setTextColor(resources.getColor(R.color.color_black))
//                tvMonthlyPlan.setTextColor(resources.getColor(R.color.color_black))
//                llYearlyPlan.setBackground(resources.getDrawable(R.drawable.card_edge_subscribe))
//                llMonthlyPlan.setBackground(resources.getDrawable(R.drawable.card_edge_gray))
//            }
//            R.id.ivHeaderLeft -> {
//                val fromScreen = intentExtra!!.getString("fromScreen")
//                if (intentExtra != null && fromScreen.equals("login", ignoreCase = true)) {
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                }
//                finish()
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        val fromScreen = intentExtra!!.getString("fromScreen")
//        if (intentExtra != null && fromScreen.equals("login", ignoreCase = true)) {
//            val intent = Intent(applicationContext, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent)
//        }
//        finish()
//    }
//
//    private fun setBillingConnect() {
//        billingClient =
//            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
//        billingClient.startConnection(this)
//    }
//
//
//    private fun loadItemForPurchase(productName: String) {
//        for (i in 0..skuList.size - 1) {
//            if (skuList.get(i).sku.equals(productName, ignoreCase = true)) {
//                onProductItem(skuList.get(i))
//            }
//        }
//    }
//
//    private fun onProductItem(skuDetail: SkuDetails) {
//        val billingFlowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(skuDetail)
//            .build()
//        billingClient.launchBillingFlow(this, billingFlowParams)
//    }
//
//    override fun onBillingServiceDisconnected() {
//        Log.d(LOG_TAG, "onBillingServiceDisconnected")
//        connectToPlayBillingService()
//    }
//
//    override fun onBillingSetupFinished(billingResult: BillingResult?) {
//        when (billingResult!!.responseCode) {
//            BillingClient.BillingResponseCode.OK -> {
//                Log.d(LOG_TAG, "onBillingSetupFinished successfully")
////                querySkuDetailsAsync(BillingClient.SkuType.INAPP, GameSku.INAPP_SKUS)
//                querySkuDetailsAsync(BillingClient.SkuType.SUBS, GameSku.SUBS_SKUS)
//                queryPurchasesAsync()
//            }
//            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
//                //Some apps may choose to make decisions based on this knowledge.
//                Log.d(LOG_TAG, billingResult.debugMessage)
//            }
//            else -> {
//                //do nothing. Someone else will connect it through retry policy.
//                //May choose to send to server though
//                Log.d(LOG_TAG, billingResult.debugMessage)
//            }
//        }
//    }
//
//    override fun onPurchasesUpdated(
//        billingResult: BillingResult?,
//        purchases: MutableList<Purchase>?
//    ) {
//        when (billingResult!!.responseCode) {
//            BillingClient.BillingResponseCode.OK -> {
//                purchases?.apply { processPurchases(this.toSet()) }
//                Toast.makeText(
//                    this@SubscribeActivity,
//                    "Subscription successfully purchased",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
//                // item already owned? call queryPurchasesAsync to verify and process all such items
//                Log.d(LOG_TAG, billingResult.debugMessage)
//                queryPurchasesAsync()
//                Toast.makeText(
//                    this@SubscribeActivity,
//                    "Subscription already purchased",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
//                connectToPlayBillingService()
//                Toast.makeText(
//                    this@SubscribeActivity,
//                    "Cancel subscription purchase ",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            else -> {
//                Log.i(LOG_TAG, billingResult.debugMessage)
//            }
//        }
//    }
//
//    private fun addEvenetIntoBranch(productId: String?) {
//        var price = 0.0
//        var type = ""
//        if (productId.equals("gold_yearly")) {
//            price = 59.99
//            type = "MONTHLY"
//        } else {
//            price = 9.99
//            type = "YEARLY"
//        }
//
//
//        BranchEvent(BRANCH_STANDARD_EVENT.PURCHASE)
//            .setCurrency(CurrencyType.USD)
//            .setDescription("PURCHASE")
//            .setRevenue(price)
//            .logEvent(this)
//    }
//
//    private fun connectToPlayBillingService(): Boolean {
//        Log.d(LOG_TAG, "connectToPlayBillingService")
//        if (!billingClient.isReady) {
//            billingClient.startConnection(this)
//            return true
//        }
//        return false
//    }
//
//    private fun querySkuDetailsAsync(
//        @BillingClient.SkuType skuType: String,
//        productIdList: List<String>
//    ) {
//        val params =
//            SkuDetailsParams.newBuilder().setSkusList(productIdList).setType(skuType).build()
//        Log.d(LOG_TAG, "querySkuDetailsAsync for $skuType")
//        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
//            when (billingResult.responseCode) {
//                BillingClient.BillingResponseCode.OK -> {
//                    if (skuDetailsList.orEmpty().isNotEmpty()) {
//                        skuList.addAll(skuDetailsList)
//                    }
//                }
//                else -> {
//                    Log.e(LOG_TAG, billingResult.debugMessage)
//                }
//            }
//        }
//    }
//
//    fun queryPurchasesAsync() {
//        Log.d(LOG_TAG, "queryPurchasesAsync called")
//        val purchasesResult = HashSet<Purchase>()
//        var result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
//        Log.d(LOG_TAG, "queryPurchasesAsync INAPP results: ${result?.purchasesList?.size}")
//        result?.purchasesList?.apply { purchasesResult.addAll(this) }
//        result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
//        result?.purchasesList?.apply { purchasesResult.addAll(this) }
//        Log.d(LOG_TAG, "queryPurchasesAsync SUBS results: ${result?.purchasesList?.size}")
////        Log.d(LOG_TAG, "PuymentState--" + result?.billingResult)
////        Log.d(LOG_TAG, "PuymentState--" + result?.purchasesList!!.get(0).originalJson)
////        Log.d(LOG_TAG, "PuymentState--" + result?.purchasesList!!.get(0).developerPayload)
////        Log.d(LOG_TAG, "PuymentState--" + result)
//
//        if (result?.purchasesList?.size!! > 0) {
//            addEvenetIntoBranch(result?.purchasesList?.get(0)?.sku)
//        }
//        processPurchases(purchasesResult)
//    }
//
//    private fun processPurchases(purchasesResult: Set<Purchase>) {
//        Log.d(LOG_TAG, "processPurchases called")
//        val validPurchases = HashSet<Purchase>(purchasesResult.size)
//        Log.d(LOG_TAG, "processPurchases newBatch content $purchasesResult")
//        purchasesResult.forEach { purchase ->
//            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
////                addEvenetIntoBranch(purchase.sku)
//                if (isSignatureValid(purchase)) {
//                    validPurchases.add(purchase)
//                }
//            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
//                Log.d(LOG_TAG, "Received a pending purchase of SKU: ${purchase.sku}")
//                // handle pending purchases, e.g. confirm with users about the pending
//                // purchases, prompt them to complete it, etc.
//            }
//        }
//        val (consumables, nonConsumables) = validPurchases.partition {
//            GameSku.CONSUMABLE_SKUS.contains(it.sku)
//        }
//        Log.d(LOG_TAG, "processPurchases consumables content $consumables")
//        Log.d(LOG_TAG, "processPurchases non-consumables content $nonConsumables")
//        /*
//          As is being done in this sample, for extra reliability you may store the
//          receipts/purchases to a your own remote/local database for until after you
//          disburse entitlements. That way if the Google Play Billing library fails at any
//          given point, you can independently verify whether entitlements were accurately
//          disbursed. In this sample, the receipts are then removed upon entitlement
//          disbursement.
//         */
//        val testing = db.purchaseDao().getPurchases()
//        Log.d(LOG_TAG, "processPurchases purchases in the lcl db ${testing.size}")
//        db.purchaseDao().insert(*validPurchases.toTypedArray())
//        acknowledgeNonConsumablePurchasesAsync(nonConsumables)
//    }
//
//    /**
//     * Ideally your implementation will comprise a secure server, rendering this check
//     * unnecessary. @see [Security]
//     */
//    private fun isSignatureValid(purchase: Purchase): Boolean {
//        return Security.verifyPurchase(
//            Security.BASE_64_ENCODED_PUBLIC_KEY, purchase.originalJson, purchase.signature
//        )
//    }
//
//    /**
//     * If you do not acknowledge a purchase, the Google Play Store will provide a refund to the
//     * users within a few days of the transaction. Therefore you have to implement
//     * [BillingClient.acknowledgePurchaseAsync] inside your app.
//     */
//    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumables: List<Purchase>) {
//        nonConsumables.forEach { purchase ->
//            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(
//                purchase
//                    .purchaseToken
//            ).build()
//            billingClient.acknowledgePurchase(params) { billingResult ->
//                when (billingResult.responseCode) {
//                    BillingClient.BillingResponseCode.OK -> {
//                        disburseNonConsumableEntitlement(purchase)
//                    }
//                    else -> Log.d(
//                        LOG_TAG,
//                        "acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}"
//                    )
//                }
//            }
//
//        }
//    }
//
//    /**
//     * This is the final step, where purchases/receipts are converted to premium contents.
//     * In this sample, once the entitlement is disbursed the receipt is thrown out.
//     */
//    private fun disburseNonConsumableEntitlement(purchase: Purchase) {
//
//        val purchaseModel = PurchaseModel()
//        purchaseModel.setOrderId(purchase.orderId)
//        purchaseModel.setProductId(purchase.sku)
//        purchaseModel.setAcknowledged(purchase.isAcknowledged)
//        purchaseModel.setPackageName(purchase.packageName)
//        purchaseModel.setPurchaseToken(purchase.purchaseToken)
//        purchaseModel.setPurchaseState(purchase.purchaseState)
//        purchaseModel.setPurchaseTime(purchase.purchaseTime)
//
//        when (purchase.sku) {
//            GameSku.PREMIUM_CAR -> {
//                db.purchaseModelDao()
//                    .insertAll(purchaseModel)
//            }
//            GameSku.GOLD_MONTHLY, GameSku.GOLD_YEARLY -> {
//                db.purchaseModelDao()
//                    .insertAll(purchaseModel)
//            }
//        }
////        db.purchaseDao().delete(purchase)
//        loadPurchaseItem()
//    }
//
//    private fun loadPurchaseItem() {
//        skuListSubPurchase = db.purchaseModelDao().getAll()
//
//        for (i in 0..skuListSubPurchase!!.size - 1) {
//            Log.e(LOG_TAG, skuListSubPurchase!!.get(i).getProductId() + ":@@@")
//
//            if (skuListSubPurchase!!.get(i).getProductId().equals(
//                    GameSku.GOLD_MONTHLY,
//                    ignoreCase = true
//                )
//            ) {
//                MyApplication.prefs!!.purchaseDate =
//                    skuListSubPurchase!!.get(i).getPurchaseTime()!!
//                MyApplication.prefs!!.subPurchase = true
//                MyApplication.prefs!!.subscriptionType = getString(R.string.str_monthly)
//                val intent = Intent(this@SubscribeActivity, SubScribedActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else if (skuListSubPurchase!!.get(i).getProductId().equals(
//                    GameSku.GOLD_YEARLY,
//                    ignoreCase = true
//                )
//            ) {
//                MyApplication.prefs!!.purchaseDate =
//                    skuListSubPurchase!!.get(i).getPurchaseTime()!!
//                MyApplication.prefs!!.subPurchase = true
//                MyApplication.prefs!!.subscriptionType = getString(R.string.str_yearly)
//                val intent = Intent(this@SubscribeActivity, SubScribedActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//    }
//
//}
