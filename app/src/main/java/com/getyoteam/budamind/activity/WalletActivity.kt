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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.Model.WallateHistoryModel
import com.getyoteam.budamind.Model.WallateResponceModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.adapter.WallateHisteoryAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_wallet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.text.*
import java.util.*
import java.util.regex.Pattern


class WalletActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    var withdrawLimit: BigInteger? = null

    val CHAIN_ADDRESS = Pattern.compile("^0x[a-fA-F0-9]{40}\$")
    var isWithdrawEnable: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

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
                    val blnc = MyApplication.prefs!!.myBalance.toBigInteger()
                    Log.d("okh", "BALANCE : " + blnc.toString())
                    Log.d("okh", "LIMITE : " + withdrawLimit)

                    if (blnc < withdrawLimit) {
                        val msg =
                            "You don’t have sufficient balance to withdraw, it must be minimum  $isWithdrawEnable"
                        Toast.makeText(
                            this,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        withdrawDialog()
                    }


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


    private fun getWalletAndHistory() {

        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.setRefreshing(true)

        val call = ApiUtils.getAPIService().getWalletAndHistory(userId, "")

        call.enqueue(object : Callback<WallateResponceModel> {
            override fun onFailure(call: Call<WallateResponceModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
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

                        withdrawLimit = commonModel.withdrawLimit!!.toBigInteger()

                        setData()

//                        val p = 0.0000000000869
//                        val bal = p.toBigDecimal() * MyApplication.prefs!!.myBalance.toBigDecimal()
//
//                        val usdBal = DecimalFormat("0.00000").format(bal)
//                        tvBalance.text = "$" + usdBal + ""
//                        getBudhaPrice()
                    }
                }
            }
        })
    }

    fun String.isChainAddressValid(): Boolean {
        return CHAIN_ADDRESS.matcher(this).matches()
    }


    fun setData() {

        val gson = Gson()

        val a = "$" + "CHI"
        val blnc = Utils.formatBal(MyApplication.prefs!!.myBalance.toBigInteger())
        tvMyBalance.setText(blnc.replace("$", a))
//        tvMyBalance.text = MyApplication.prefs!!.myBalance.toBigInteger().toString()
        val rsrned = Utils.formatBal(MyApplication.prefs!!.totRsrned.toBigInteger())
        tvTotEarn.text = rsrned.replace("$", a)

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
        var withdrawAmount = 0L
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
        val lbAmount: TextView
        val lblProgress: TextView
        val seekAmount: SeekBar
        close = dialog.findViewById(R.id.ivClose)
        etAddress = dialog.findViewById(R.id.etAddress)
        etAmount = dialog.findViewById(R.id.etAmmount)
        tvSend = dialog.findViewById(R.id.tvSend)
        lbAmount = dialog.findViewById(R.id.lblAmount)
        lblProgress = dialog.findViewById(R.id.lblProgress)
        seekAmount = dialog.findViewById(R.id.seekAmount)
        close.setOnClickListener {
            dialog.dismiss()
        }

        seekAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar

                val balance = MyApplication.prefs!!.myBalance.toLong()
                val amount = progress.toLong() * balance
                val finalAmount = amount / 100
                withdrawAmount = finalAmount
                val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                formatter.applyPattern("#,###")
                val a = formatter.format(finalAmount)

                lbAmount.text = "Amount" + "\n" + a
                lblProgress.text = progress.toString() + " %"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

        tvSend.setOnClickListener {

            val address = etAddress.text.toString().trim()

            Log.d("okh", "withdrawAmount : " + withdrawAmount.toString())
            Log.d("okh", "LIMITE : " + withdrawLimit)

            if (address.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter ERC-20 Receive Address!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (!address.isChainAddressValid()) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter Valid ERC-20 Receive Address!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (withdrawAmount <= 0) {
                Toast.makeText(
                    applicationContext,
                    "Please Select Withdraw Amount!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (withdrawAmount.toBigInteger() < withdrawLimit) {
                val msg = "Please withdraw minimum amount which is $withdrawLimit"
                Toast.makeText(
                    applicationContext,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                dialog.dismiss()
                callWithdrawRequest(address, withdrawAmount.toString())

            }

        }
        dialog.show()
    }

    fun doubleToStringNoDecimal(d: Double): String? {
        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        return formatter.format(d)
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
                    ).show()
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
