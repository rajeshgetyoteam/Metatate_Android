package com.getyoteam.budamind.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.getyoteam.budamind.Model.Status
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigInteger
import java.text.DecimalFormat

object Utils {
    private val suffix = arrayOf("", "K", "M", "B", "T")
    private val SUFFIXES = charArrayOf('K', 'M', 'B', 't', 'p', 'e')
    private const val MAX_LENGTH = 4
    fun songArt(path: String?, context: Context): Bitmap {
        val retriever = MediaMetadataRetriever()
        val inputStream: InputStream
        retriever.setDataSource(path)
        return if (retriever.embeddedPicture != null) {
            inputStream = ByteArrayInputStream(retriever.embeddedPicture)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            retriever.release()
            bitmap
        } else {
            getLargeIcon(context)
        }
    }

    private fun getLargeIcon(context: Context): Bitmap {
        val vectorDrawable = context.getDrawable(R.drawable.ic_logo_white) as VectorDrawable?
        val largeIconSize = context.resources.getDimensionPixelSize(R.dimen._12sdp)
        val bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.alpha = 100
            vectorDrawable.draw(canvas)
        }
        return bitmap
    }

    //    public static String format(BigInteger number) {
    //        String r = new DecimalFormat("##0E0").format(number);
    //        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
    //        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
    //            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
    //        }
    //        return r;
    //    }
    //    public static String format(BigInteger number) {
    //        if(number < 1000) {
    //            // No need to format this
    //            return String.valueOf(number);
    //        }
    //        // Convert to a string
    //        final String string = String.valueOf(number);
    //        // The suffix we're using, 1-based
    //        final int magnitude = (string.length() - 1) / 3;
    //        // The number of digits we must show before the prefix
    //        final int digits = (string.length() - 1) % 3 + 1;
    //
    //        // Build the string
    //        char[] value = new char[4];
    //        for(int i = 0; i < digits; i++) {
    //            value[i] = string.charAt(i);
    //        }
    //        int valueLength = digits;
    //        // Can and should we add a decimal point and an additional number?
    //        if(digits == 1 && string.charAt(1) != '0') {
    //            value[valueLength++] = '.';
    //            value[valueLength++] = string.charAt(1);
    //        }
    //        value[valueLength++] = SUFFIXES[magnitude - 1];
    //        return new String(value, 0, valueLength);
    //    }
    fun format(number: BigInteger): String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        //        char[] suffix = {' ', '$', '$', '$', '$', '$', '$'};
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0")
                .format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun formatBal(number: BigInteger): String {
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        //        char[] suffix = {' ', '$', '$', '$', '$', '$', '$'};
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.000")
                .format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun wanCoinDialog(context: Context?, coin: String) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_congratulation_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                context,
                android.R.color.transparent
            )
        )
        val close: ImageView
        val tvCoin: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvCoin = dialog.findViewById(R.id.tvCoin)
        tvCoin.text = "You have won $coin Metatate tokens."
        close.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun callUpdateSeconds(type: String, data: JSONObject) {

        val jsonParser = JsonParser()
        var gsonObject = JsonObject()
        gsonObject = jsonParser.parse(data.toString()) as JsonObject
        //print parameter
        Log.e("MY gson.JSON:  ", "AS PARAMETER  $gsonObject")
        try {
            ApiUtils.getAPIService().updateSeconds(gsonObject)
                .enqueue(object : Callback<Status> {
                    override fun onResponse(
                        call: Call<Status>,
                        response: Response<Status>
                    ) {
                        Log.d("Response", response.toString())
                    }

                    override fun onFailure(
                        call: Call<Status>,
                        t: Throwable
                    ) {
                        Log.d("Response", t.toString())
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callAnalytics(type: String, data: JSONObject) {

        val jsonParser = JsonParser()
        var gsonObject = JsonObject()
        gsonObject = jsonParser.parse(data.toString()) as JsonObject
        //print parameter
        Log.e("MY gson.JSON:  ", "AS PARAMETER  $gsonObject")
        try {
            if (type.equals("courses")) {
                ApiUtils.getAPIService().coursesAnalytics(gsonObject)
                    .enqueue(object : Callback<Status> {
                        override fun onResponse(
                            call: Call<Status>,
                            response: Response<Status>
                        ) {
                            Log.d("Response", response.toString())
                        }

                        override fun onFailure(
                            call: Call<Status>,
                            t: Throwable
                        ) {
                            Log.d("Response", t.toString())
                        }
                    })
            } else if (type.equals("moments")) {
                ApiUtils.getAPIService().momentsAnalytics(gsonObject)
                    .enqueue(object : Callback<Status> {
                        override fun onResponse(
                            call: Call<Status>,
                            response: Response<Status>
                        ) {
                            Log.d("Response", response.toString())
                        }

                        override fun onFailure(
                            call: Call<Status>,
                            t: Throwable
                        ) {
                            Log.d("Response", t.toString())
                        }
                    })

            } else if (type.equals("sound")) {
                ApiUtils.getAPIService().soundAnalytics(gsonObject)
                    .enqueue(object : Callback<Status> {
                        override fun onResponse(
                            call: Call<Status>,
                            response: Response<Status>
                        ) {
                            Log.d("Response", response.toString())
                        }

                        override fun onFailure(
                            call: Call<Status>,
                            t: Throwable
                        ) {
                            Log.d("Response", t.toString())
                        }
                    })

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}