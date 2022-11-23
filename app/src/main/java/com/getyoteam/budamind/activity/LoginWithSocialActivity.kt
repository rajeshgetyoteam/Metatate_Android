//package com.getyoteam.budamind.activity
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.PorterDuff
//import android.net.ConnectivityManager
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.android.billingclient.api.*
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.GraphRequest
//import com.facebook.login.LoginResult
//import com.getyoteam.budamind.Model.CommonModel
//import com.getyoteam.budamind.Model.LoginResponseModel
//import com.getyoteam.budamind.Model.PurchaseModel
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.interfaces.ApiUtils
//import com.getyoteam.budamind.interfaces.ClarityAPI
//import com.getyoteam.budamind.utils.AppDatabase
//import com.getyoteam.budamind.utils.Security
//import com.google.android.gms.auth.api.signin.*
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.tasks.Task
//import com.google.firebase.messaging.FirebaseMessaging
//import kotlinx.android.synthetic.main.activity_login_with_social.*
//import kotlinx.android.synthetic.main.fragment_home.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.*
//
//class LoginWithSocialActivity : AppCompatActivity(), View.OnClickListener,
//    BillingClientStateListener,
//    PurchasesUpdatedListener {
//
//    private var callbackManager: CallbackManager? = null
//    private lateinit var db: AppDatabase
//    private var mGoogleSignInClient: GoogleSignInClient? = null
//    private val RC_SIGN_IN: Int = 2001
//    private var skuListSubPurchase: List<PurchaseModel>? = null
//    private var skuList = mutableListOf<SkuDetails>()
//    var billingClient: BillingClient? = null
//
//    companion object {
//        private const val LOG_TAG = "BillingRepository"
//    }
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
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login_with_social)
//
//        db = AppDatabase.getDatabase(this)
//        callbackManager = CallbackManager.Factory.create();
//        facebookLoginManagerCallBack()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        tvSignUpFacebook.setOnClickListener(this)
//        tvSignUpEmail.setOnClickListener(this)
//        tvSignUpGoogle.setOnClickListener(this)
//        tvTermsCondi.setOnClickListener(this)
//        tvSignIn.setOnClickListener(this)
//
//        progressBar.indeterminateDrawable.setColorFilter(
//            ContextCompat.getColor(this, R.color.color_blue),
//            PorterDuff.Mode.SRC_IN
//        )
//        setBillingConnect()
//    }
//
//    private fun checkInternetConnection(): Boolean {
//        val connectivityManager =
//            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnected
//    }
//
//    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.tvSignUpFacebook -> {
//                if (checkInternetConnection()) {
//                    progressBarFacebook.visibility = View.VISIBLE
//                    defaultLoginButtonFB.performClick()
//                } else {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.str_check_internet_connection),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            R.id.tvSignUpGoogle -> {
//                if (checkInternetConnection()) {
//                    progressBarGoogle.visibility = View.VISIBLE
//                    googleSignIn()
//                } else {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.str_check_internet_connection),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//            R.id.tvSignUpEmail -> {
//                val intent = Intent(this, SignUpActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.tvSignIn -> {
//                val intent = Intent(this, SignInActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.tvTermsCondi -> {
//            }
//        }
//    }
//
//    private fun facebookLoginManagerCallBack() {
//        defaultLoginButtonFB.setPermissions("email");
//        defaultLoginButtonFB.registerCallback(callbackManager,
//            object : FacebookCallback<LoginResult> {
//                override fun onSuccess(result: LoginResult?) {
//                    progressBarFacebook.visibility = View.INVISIBLE
//
//                    val request =
//                        GraphRequest.newMeRequest(result!!.accessToken) { `object`, response ->
//                            try {
//                                val id = `object`.getString("id")
//                                val profile_pic =
//                                    "http://graph.facebook.com/" + id + "/picture?type=large"
//                                val name = `object`.optString("name");
//                                val email = `object`.optString("email");
//                                val gender = `object`.optString("gender");
//                                loginWithSocial(
//                                    name!!.split(" ")[0],
//                                    name.split(" ")[1], id,
//                                    profile_pic, getString(R.string.str_facebook)
//                                )
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//                    val parameters = Bundle()
//                    parameters.putString("fields", "name,email,id,picture.type(large),gender")
//                    request.parameters = parameters
//                    request.executeAsync()
//                }
//
//                override fun onCancel() {
//                    Log.e("FBLOGIN_FAILD", "Cancel")
//                    progressBarFacebook.visibility = View.INVISIBLE
//                }
//
//                override fun onError(error: FacebookException) {
//                    progressBarFacebook.visibility = View.INVISIBLE
//                    Log.e("FBLOGIN_FAILD", "ERROR", error)
//                }
//            })
//    }
//
//    private fun googleSignIn() {
//        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
//
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        callbackManager!!.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            progressBarGoogle.visibility = View.INVISIBLE
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
////            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
////            handleSignInResultNew(result);
//        }
//    }
//
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            Log.e(
//                "Google**",
//                "**" + account!!.displayName + "**" + account.email + "**" + account.photoUrl
//            )
//            val name = account.displayName
//            val email = account.email.toString()
//            val profile_pic = account.photoUrl.toString()
//            if (!name!!.isEmpty() && name.contains(" ")) {
//                loginWithSocial(
//                    name!!.split(" ")[0],
//                    name.split(" ")[1], email,
//                    profile_pic, getString(R.string.str_google)
//                )
//            } else {
//                loginWithSocial(
//                    "", "", email,
//                    profile_pic, getString(R.string.str_google)
//                )
//            }
//        } catch (e: ApiException) {
//            Log.e("Google**", "signInResult:failed code=" + e.statusCode)
//            Toast.makeText(
//                applicationContext,
//                getString(R.string.str_something_went_wrong),
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//
//    }
//
//    private fun loginWithSocial(
//        pFirstName: String, pLastName: String,
//        pCustomerEmail: String, pProfilePic: String,
//        pLoginThrough: String
//    ) {
//        val tz = TimeZone.getDefault();
//
//        if (pLoginThrough.equals("gmail", ignoreCase = true))
//            progressBarGoogle.visibility = View.VISIBLE
//        else
//            progressBarFacebook.visibility = View.VISIBLE
//        val retrofit = Retrofit.Builder()
//            .baseUrl(getString(R.string.base_url))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val clarityAPI = retrofit.create(ClarityAPI::class.java)
//        val loginResponseModel = LoginResponseModel(
//            pFirstName, pLastName, "", pCustomerEmail,
//            pProfilePic, pLoginThrough, MyApplication.prefs!!.firebaseToken,
//            tz.id.toString()
//        )
//        val call = ApiUtils.getAPIService().login(loginResponseModel)
//
//        call.enqueue(object : Callback<CommonModel> {
//            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
//                if (pLoginThrough.equals("gmail", ignoreCase = true))
//                    progressBarGoogle.visibility = View.INVISIBLE
//                else
//                    progressBarFacebook.visibility = View.INVISIBLE
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//
//            override fun onResponse(
//                call: Call<CommonModel>,
//                response: Response<CommonModel>
//            ) {
//                if (response.code() == 200) {
//                    if (pLoginThrough.equals("gmail", ignoreCase = true))
//                        progressBarGoogle.visibility = View.VISIBLE
//                    else
//                        progressBarFacebook.visibility = View.VISIBLE
//
//                    val commonModel = response.body()!!
//                    if (commonModel.getStatus().equals(getString(R.string.str_success))) {
//                        val userId = commonModel.getUserId()
//                        MyApplication.prefs!!.userId = userId.toString()
//                        MyApplication.prefs!!.loginType = pLoginThrough
//                        MyApplication.prefs!!.authToken = commonModel.getAuthToken()!!
//                        Toast.makeText(
//                            applicationContext,
//                            getString(R.string.str_login_successfully),
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_COURSE")
//                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_SOUND")
//                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOMENT")
//                        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATION")
//
//                        if (MyApplication.prefs!!.subPurchase) {
//                            val intent = Intent(applicationContext, MainActivity::class.java)
//                            startActivity(intent)
//                        } else {
////                            val intent = Intent(applicationContext, SubscribeActivity::class.java)
////                            intent.putExtra("fromScreen", "Login")
////                            startActivity(intent)
//                        }
//                        finish()
//                    } else {
//                        Toast.makeText(
//                            applicationContext,
//                            commonModel.getMessage(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        })
//    }
//
//    private fun setBillingConnect() {
//        billingClient =
//            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
//        billingClient!!.startConnection(this)
//    }
//
//    override fun onBillingServiceDisconnected() {
//        Log.d(LOG_TAG, "onBillingServiceDisconnected")
//        connectToPlayBillingService()
//    }
//
//    override fun onBillingSetupFinished(billingResult: BillingResult) {
//        when (billingResult.responseCode) {
//            BillingClient.BillingResponseCode.OK -> {
//                Log.d(LOG_TAG, "onBillingSetupFinished successfully")
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
//    private fun connectToPlayBillingService(): Boolean {
//        Log.d(LOG_TAG, "connectToPlayBillingService")
//        if (!billingClient!!.isReady) {
//            billingClient!!.startConnection(this)
//            return true
//        }
//        return false
//    }
//
//    fun queryPurchasesAsync() {
//        db.purchaseModelDao().deleteAll()
//        Log.d(LOG_TAG, "queryPurchasesAsync called")
//        val purchasesResult = HashSet<Purchase>()
//        var result = billingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
//        Log.d(LOG_TAG, "queryPurchasesAsync INAPP results: ${result?.purchasesList?.size}")
//        result?.purchasesList?.apply { purchasesResult.addAll(this) }
//        if (isSubscriptionSupported()) {
//            result = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
//            result?.purchasesList?.apply { purchasesResult.addAll(this) }
//            Log.d(LOG_TAG, "queryPurchasesAsync SUBS results: ${result?.purchasesList?.size}")
//        }
//        processPurchases(purchasesResult)
//    }
//
//    private fun isSubscriptionSupported(): Boolean {
//        val billingResult =
//            billingClient!!.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
//        var succeeded = false
//        when (billingResult.responseCode) {
//            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> connectToPlayBillingService()
//            BillingClient.BillingResponseCode.OK -> succeeded = true
//            else -> Log.w(
//                LOG_TAG,
//                "isSubscriptionSupported() error: ${billingResult.debugMessage}"
//            )
//        }
//        return succeeded
//    }
//
//    private fun processPurchases(purchasesResult: Set<Purchase>) {
//        Log.d(LOG_TAG, "processPurchases called")
//        val validPurchases = HashSet<Purchase>(purchasesResult.size)
//        Log.d(LOG_TAG, "processPurchases newBatch content $purchasesResult")
//        purchasesResult.forEach { purchase ->
//            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                if (isSignatureValid(purchase)) {
//                    validPurchases.add(purchase)
//                    val purchaseModel = PurchaseModel()
//                    purchaseModel.setOrderId(purchase.orderId)
//                    purchaseModel.setProductId(purchase.sku)
//                    purchaseModel.setAcknowledged(purchase.isAcknowledged)
//                    purchaseModel.setPackageName(purchase.packageName)
//                    purchaseModel.setPurchaseToken(purchase.purchaseToken)
//                    purchaseModel.setPurchaseState(purchase.purchaseState)
//                    purchaseModel.setPurchaseTime(purchase.purchaseTime)
//
//                    when (purchase.sku) {
//                        GameSku.GOLD_MONTHLY -> {
//                            db.purchaseModelDao()
//                                .insertAll(purchaseModel)
//                        }
//                        GameSku.GOLD_YEARLY -> {
//                            db.purchaseModelDao()
//                                .insertAll(purchaseModel)
//                        }
//                    }
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
//        Log.d(LOG_TAG, "processPurchases purchases in the lcl db ${testing?.size}")
//        db.purchaseDao().insert(*validPurchases.toTypedArray())
//        acknowledgeNonConsumablePurchasesAsync(nonConsumables)
//        loadPurchaseItem()
//
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
//            billingClient!!.acknowledgePurchase(params) { billingResult ->
//                when (billingResult.responseCode) {
//                    BillingClient.BillingResponseCode.OK -> {
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
//
//    private fun loadPurchaseItem() {
//        skuListSubPurchase = db.purchaseModelDao().getAll()
//
//        for (i in 0..skuListSubPurchase!!.size - 1) {
//            Log.e(LOG_TAG, skuListSubPurchase!!.get(i).getProductId() + ":@@@")
//
//            if (skuListSubPurchase!!.get(i).getProductId()
//                    .equals(GameSku.GOLD_MONTHLY, ignoreCase = true)
//            ) {
//                MyApplication.prefs!!.purchaseDate =
//                    skuListSubPurchase!!.get(i).getPurchaseTime()!!
//                MyApplication.prefs!!.subPurchase = true
//                MyApplication.prefs!!.subscriptionType = getString(R.string.str_monthly)
//            } else if (skuListSubPurchase!!.get(i).getProductId()
//                    .equals(GameSku.GOLD_YEARLY, ignoreCase = true)
//            ) {
//                MyApplication.prefs!!.purchaseDate =
//                    skuListSubPurchase!!.get(i).getPurchaseTime()!!
//                MyApplication.prefs!!.subPurchase = true
//                MyApplication.prefs!!.subscriptionType = getString(R.string.str_yearly)
//            }
//        }
//
//    }
//
//
//    override fun onPurchasesUpdated(
//        billingResult: BillingResult?,
//        purchases: MutableList<Purchase>?
//    ) {
//    }
//
//
//}
