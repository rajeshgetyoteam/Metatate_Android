package com.getyoteam.budamind.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.getyoteam.budamind.Model.CommanResponseModel
import com.getyoteam.budamind.Model.WallateHistoryModel
import com.getyoteam.budamind.Model.WallateResponceModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.RecyclerItemTouchHelperNotification
import com.getyoteam.budamind.adapter.TokenHisteoryAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_wallet_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WalletHistoryActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    RecyclerItemTouchHelperNotification.RecyclerItemTouchHelperListener {

    var tokenDataArrayList: ArrayList<WallateHistoryModel>? = null
    var tokenHisteoryAdapter: TokenHisteoryAdapter? = null
    private var alertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_history)
        tokenDataArrayList = ArrayList()
        showDialog()
        ivHeaderLeft.setOnClickListener { finish() }
        val c1 = ContextCompat.getColor(this, R.color.app_pink_color)
        swipeToRefresh.setColorSchemeColors(c1)
        swipeToRefresh.setOnRefreshListener(this)
        ivDelete.setOnClickListener {
            if (tokenDataArrayList!!.size > 0){
                alertDialog!!.show()
            }else{
                Toast.makeText(this,"You have no any token history",Toast.LENGTH_SHORT).show()
            }
        }
        var data = MyApplication.prefs!!.wallateResponce

        if (data.isNullOrBlank()) {
            getWalletAndHistory()
        } else {
            setData()
        }

    }

    private fun showDialog() {
        // Late initialize an alert dialog object

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Delete All?")

        // Set a message for alert dialog
        builder.setMessage("Are you sure, you would like to delete?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    callCleareHostoryAPI()
                    alertDialog!!.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> alertDialog!!.dismiss()
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Yes", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button

        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

    }

    fun setData() {

        val gson = Gson()


        val data = MyApplication.prefs!!.wallateResponce.toString()

        if (data.isNullOrEmpty()) {
            rvHistory.visibility = View.GONE
        } else {
            val type = object : TypeToken<java.util.ArrayList<WallateHistoryModel?>?>() {}.type
            tokenDataArrayList = gson.fromJson<java.util.ArrayList<WallateHistoryModel>>(
                data,
                type
            ) as ArrayList<WallateHistoryModel>

            if (tokenDataArrayList!!.size > 0) {
                tokenHisteoryAdapter =
                    TokenHisteoryAdapter(tokenDataArrayList, this@WalletHistoryActivity)
                rvHistory.adapter = tokenHisteoryAdapter
                tvNodata.visibility = View.GONE

                val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
                    RecyclerItemTouchHelperNotification(
                        0,
                        ItemTouchHelper.LEFT,
                        this@WalletHistoryActivity
                    )
                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(
                    rvHistory
                )

                val itemTouchHelperCallback1: ItemTouchHelper.SimpleCallback =
                    object : ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.LEFT
                    ) {

                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(
                            viewHolder: RecyclerView.ViewHolder,
                            direction: Int
                        ) {

                        }

                        override fun onChildDraw(
                            c: Canvas,
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            dX: Float,
                            dY: Float,
                            actionState: Int,
                            isCurrentlyActive: Boolean
                        ) {
                            super.onChildDraw(
                                c!!,
                                recyclerView!!,
                                viewHolder!!,
                                dX,
                                dY,
                                actionState,
                                isCurrentlyActive
                            )
                        }
                    }

                ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(
                    rvHistory
                )
            } else {
                rvHistory.visibility = View.GONE
                tvNodata.visibility = View.VISIBLE
            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
//        val id: File = recordinglist!!.get(viewHolder!!.adapterPosition).absoluteFile


        showDeleteDialog(tokenDataArrayList!!.get(position).id)

    }

    fun showDeleteDialog(tid: String?) {
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to delete this data?")
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("YES") { dialog, id ->
                calldeleteTransHistory(tid.toString())
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, which ->

                tokenHisteoryAdapter!!.refreshdata()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Delete?")
        alert.setCancelable(false)
        alert.show()
    }

    private fun callCleareHostoryAPI() {
        swipeToRefresh.isRefreshing = true
        val wallateId = MyApplication.prefs!!.myWallateId


        val call = ApiUtils.getAPIService().clearHistory(wallateId)

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {

                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {

                    val commonModel = response.body()
                    if (commonModel!!.getStatus().equals(getString(R.string.str_success))) {
                        MyApplication.prefs!!.wallateResponce = ""
                        getWalletAndHistory()
                        Toast.makeText(
                            applicationContext,
                            "Deleted",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false
                }
            }
        })
    }

    private fun calldeleteTransHistory(id: String) {

        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().deleteTransHistory(id)

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {

                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {

                    val commonModel = response.body()
                    if (commonModel!!.getStatus().equals(getString(R.string.str_success))) {

                        getWalletAndHistory()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false
                }
            }
        })
    }

    private fun getWalletAndHistory() {

        val userId = MyApplication.prefs!!.userId

        if (swipeToRefresh == null)
            swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().getWalletAndHistory(userId, "")

        call.enqueue(object : Callback<WallateResponceModel> {
            override fun onFailure(call: Call<WallateResponceModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
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
                        swipeToRefresh.isRefreshing = false

                    val commonModel = response.body()
                    if (commonModel!!.status.equals(getString(R.string.str_success))) {

                        val gson = Gson()
                        val data = gson.toJson(commonModel.history)
                        MyApplication.prefs!!.wallateResponce = data

                        setData()
                    }
                }
            }
        })
    }

    override fun onRefresh() {


        if (checkInternetConnection()) {

            getWalletAndHistory()
        } else {

            if (swipeToRefresh != null)
                swipeToRefresh.isRefreshing = false
        }

    }
}
