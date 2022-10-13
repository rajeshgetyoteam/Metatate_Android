package com.getyoteam.budamind.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.Model.WallateHistoryModel
import com.getyoteam.budamind.Model.WallateResponceModel
import com.getyoteam.budamind.Model.responceBudaPriceModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.adapter.WallateHisteoryAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.getyoteam.budamind.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_wallet.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WalletActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {


    var isWithdrawEnable :Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
//        MyApplication.prefs!!.myWallateAddress = "0x77799e843c0cfd5ba2f5e52c6012b6b5720d5c06"
//        var myaddress = MyApplication.prefs!!.myWallateAddress
//        val binanceManager = BinanceManager.getInstance()
//        /**
//         * @param infura - Initialize infura
//         */
////        binanceManager.init("https://data-seed-prebsc-1-s1.binance.org:8545"); // for test net
//        binanceManager.init("https://bsc-dataseed1.binance.org:443")
//        binanceManager.getBNBBalance(myaddress, this)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ balance ->
//                /**
//                 * if function successfully completes result can be caught in this block
//                 */
////                Toast.makeText(this, balance.toString(), Toast.LENGTH_SHORT).show()
//                tvMyBalance.setText( balance.toString())
//            }, { error ->
//                /**
//                 * if function fails error can be caught in this block
//                 */
//                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
//            })
        val c1 = ContextCompat.getColor(this, R.color.app_pink_color)
        swipeToRefresh.setColorSchemeColors(c1)
        swipeToRefresh.setOnRefreshListener(this)
        ivHeaderLeft.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                finish()
            }
        }
        layMore.setOnClickListener {
            val intent = Intent(this, WalletHistoryActivity::class.java)
            startActivity(intent)
        }

        layhostory.setOnClickListener {
            val intent = Intent(this, WalletHistoryActivity::class.java)
            startActivity(intent)
        }


//        getNextWithdrowlDate()

        tvSend.setOnClickListener {
            val intent = Intent(this, SendTokenActivity::class.java)
            startActivity(intent)
        }
        tvReceived.setOnClickListener {

            var bal = MyApplication.prefs!!.myBalance

            if (bal.toFloat() > 0) {

                var nextDate = MyApplication.prefs!!.nextDate

                if (isWithdrawEnable!!) {
                    withdrawDialog()

                } else {
                    Toast.makeText(
                        this,
                        "You can withdraw the amount once in a week!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(this, "You do not have sufficient balance!", Toast.LENGTH_SHORT)
                    .show()
            }

        }
//        MyApplication.prefs!!.nextDate = ""

//        var data =  MyApplication.prefs!!.wallateResponce
//
//        if (data.isNullOrBlank()){
//            getWalletAndHistory()
//        }else{
//            setData()
//        }

        getWalletAndHistory()
//        var nextDate = MyApplication.prefs!!.nextDate
//
//        var curuntDate = SimpleDateFormat("dd/MM/yyyy").format(Date())
//
//        if (!nextDate.isNullOrEmpty()) {
//            var isWithdra = CheckDates(curuntDate, nextDate)
//
//            if (isWithdra) {
//                MyApplication.prefs!!.nextDate = ""
//            }
//        }





    }
    private fun getBudhaPrice() {
        swipeToRefresh.isRefreshing = true

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nomics.com/v1/currencies/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)
        val call = mindFulNessAPI.getTicker(
            "f74b0f300342169ca51b24e4b0b5f23bec262dc4",
            "CHI",
            "1d",
            "USD",
            "100",
            "1"
        )

        call.enqueue(object : Callback<ArrayList<responceBudaPriceModel>> {
            override fun onFailure(call: Call<ArrayList<responceBudaPriceModel>>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    this@WalletActivity,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<ArrayList<responceBudaPriceModel>>,
                response: Response<ArrayList<responceBudaPriceModel>>
            ) {
                if (response.code() == 200) {
                    if (swipeToRefresh != null) {
                        swipeToRefresh.isRefreshing = false

                        var data = response


                        val todayPrice = response.body()!!.get(0).price

                        val bal =
                            todayPrice!!.toBigDecimal() * MyApplication.prefs!!.myBalance.toBigDecimal()

                        val usdBal = DecimalFormat("0.00000").format(bal)
                        tvBalance.text = "$" + usdBal + ""

                    }
                } else {
                    if (swipeToRefresh != null) {
                        swipeToRefresh.isRefreshing = false
                    }
                    val p = 1
                    val bal = p.toBigDecimal() * MyApplication.prefs!!.myBalance.toBigDecimal()

                    val usdBal = DecimalFormat("0.00000").format(bal)
                    tvBalance.text = "$" + usdBal + ""
                }
            }
        })
    }



    private fun getWalletAndHistory() {

        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.setRefreshing(true)

        val call = ApiUtils.getAPIService().getWalletAndHistory(userId, "")

        call.enqueue(object : Callback<WallateResponceModel> {
            override fun onFailure(call: Call<WallateResponceModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.setRefreshing(false);
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<WallateResponceModel>,
                response: Response<WallateResponceModel>
            ) {
                if (response.code() == 200) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.setRefreshing(false);

                    val commonModel = response.body()


                    if (commonModel!!.status.equals(getString(R.string.str_success))) {

                        isWithdrawEnable = commonModel.isWithdrawEnable

                        if (!isWithdrawEnable!!) {
                            tvReceived.getBackground().setAlpha(140);
//            tvReceived.isClickable = false
                            tvReceived.setAlpha(0.8f)
                        }

                        MyApplication.prefs!!.myBalance = commonModel.balance.toString()
                        MyApplication.prefs!!.totRsrned = commonModel.totalEarn.toString()
                        MyApplication.prefs!!.myWallateId = commonModel.walletId.toString()
                        val gson = Gson()
                        val data = gson.toJson(commonModel.history)
                        MyApplication.prefs!!.wallateResponce = data

                        setData()

                        val p = 0.0000000000869
                        val bal = p.toBigDecimal() * MyApplication.prefs!!.myBalance.toBigDecimal()

                        val usdBal = DecimalFormat("0.00000").format(bal)
                        tvBalance.text = "$" + usdBal + ""
//                        getBudhaPrice()
                    }
                }
            }
        })
    }

    fun setData() {

        val gson = Gson()

        val a = "$"+"CHI"
        val blnc = Utils.formatBal(MyApplication.prefs!!.myBalance.toBigInteger())
        tvMyBalance.setText(blnc.replace("$",a))
        val rsrned = Utils.formatBal(MyApplication.prefs!!.totRsrned.toBigInteger())
        tvTotEarn.setText(rsrned.replace("$",a))

        val data = MyApplication.prefs!!.wallateResponce.toString()
        val type = object : TypeToken<java.util.ArrayList<WallateHistoryModel?>?>() {}.type
        val dataArrayList = gson.fromJson<java.util.ArrayList<WallateHistoryModel>>(
            data,
            type
        ) as ArrayList<WallateHistoryModel>

//        val dataArrayList = commonModel.history

        if (dataArrayList!!.size > 2) {

            var data: ArrayList<WallateHistoryModel>? = null
            data = ArrayList()

            data.add(dataArrayList.get(0))
            data.add(dataArrayList.get(1))

            rvHis.adapter =
                WallateHisteoryAdapter(data, this@WalletActivity)
        } else {
            rvHis.adapter =
                WallateHisteoryAdapter(dataArrayList, this@WalletActivity)
        }
    }

    fun withdrawDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_withdraw_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                android.R.color.transparent
            )
        )
        val close: ImageView
        val etAddress: EditText
        val etAmount: EditText
        val tvSend: TextView
        close = dialog.findViewById(R.id.ivClose)
        etAddress = dialog.findViewById(R.id.etAddress)
        etAmount = dialog.findViewById(R.id.etAmmount)
        tvSend = dialog.findViewById(R.id.tvSend)
        close.setOnClickListener {
            dialog.dismiss()
        }

        tvSend.setOnClickListener {

            var address = etAddress.text.toString().trim()
            var amount = etAmount.text.toString().trim()

            if (address.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter Receive Address!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (amount.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter Receive Amount!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                dialog.dismiss()
                callWithdrawRequest(address, amount.toString())

            }

        }
        dialog.show()
    }

    fun getCurrentWeekDate() {
        val c = GregorianCalendar.getInstance()
        c.firstDayOfWeek = Calendar.MONDAY
        c[Calendar.DAY_OF_WEEK] = c.firstDayOfWeek
        c.add(Calendar.DAY_OF_WEEK, 0)
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate: String
        val endDate: String
        startDate = df.format(c.time)
        c.add(Calendar.DAY_OF_MONTH, 7)
        endDate = df.format(c.time)
        println("Start Date = $startDate")
        println("End Date = $endDate")

        MyApplication.prefs!!.nextDate = endDate

        tvReceived.getBackground().setAlpha(140);
//        tvReceived.isClickable = false
        tvReceived.setAlpha(0.8f)
        Log.d("DATE", endDate)

    }


    fun getNextWithdrowlDate() {

        val now = Calendar.getInstance();
        val weekday = now.get(Calendar.DAY_OF_WEEK);
//        now.firstDayOfWeek = Calendar.MONDAY
//        now.add(Calendar.DAY_OF_WEEK, 1)
        if (weekday != Calendar.MONDAY) {
            // calculate how much to add
            // the 2 is the difference between Saturday and Monday
            val days = (Calendar.SATURDAY - weekday + 2) % 7;
            now.add(Calendar.DAY_OF_YEAR, days);
        }
// now is the date you want
        val date = now.getTime();
        var format = SimpleDateFormat("dd/MM/yyyy").format(date);

        MyApplication.prefs!!.nextDate = format

        tvReceived.getBackground().setAlpha(140);
        tvReceived.isClickable = false
        tvReceived.setAlpha(0.8f)
        Log.d("DATE", format)
    }


    private fun callWithdrawRequest(address: String, amount: String) {

        val userId = MyApplication.prefs!!.userId
        val walletId = MyApplication.prefs!!.myWallateId
        progressBarW.visibility = View.VISIBLE

        val call = ApiUtils.getAPIService().withdrawalRequest(userId, walletId, address, amount)

        call.enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                progressBarW.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<CommonModel>,
                response: Response<CommonModel>
            ) {
                if (response.code() == 200) {
                    progressBarW.visibility = View.INVISIBLE

                    val commonModel = response.body()
                    if (commonModel!!.getStatus().equals(getString(R.string.str_success))) {

                        Toast.makeText(
                            applicationContext,
                            "Request Sent Successfully! Balance will reflect in your wallet within 24-48 hours.",
                            Toast.LENGTH_SHORT
                        ).show()
                        getWalletAndHistory()
//                        getCurrentWeekDate()
//                        getNextWithdrowlDate()
//                        getCurrentWeekDate()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressBarW.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    fun CheckDates(startDate: String?, endDate: String?): Boolean {
        val dfDate = SimpleDateFormat("dd/MM/yyyy")
        var b = false
        try {
            b = if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                false // If start date is before end date.
            } else if (dfDate.parse(startDate) == dfDate.parse(endDate)) {
                true // If two dates are equal.
            } else {
                true // If start date is after the end date.
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return b
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onRefresh() {
        if (checkInternetConnection()) {
            getWalletAndHistory()
        } else {
            swipeToRefresh.setRefreshing(false);
            Toast.makeText(
                this,
                "Please Chek Internet Connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
