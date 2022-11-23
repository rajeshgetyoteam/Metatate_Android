package com.getyoteam.budamind.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.getyoteam.budamind.Model.WallateHistoryModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.raw_wallete.view.*
import java.text.SimpleDateFormat
import java.util.*


class WallateHisteoryAdapter(
    val dataArraylist: List<WallateHistoryModel>?,
    val context: Context
) : RecyclerView.Adapter<WallateHisteoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_wallete,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {
        return dataArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {

       val dataModel = dataArraylist!!.get(possion)
        viewHolder.tvTitle.text = dataModel.description
        val token = Utils.format(dataModel.coins!!.toBigInteger())
        val a = "$"+"CHI"
        viewHolder.tvPrice.text = token.replace("$",a)
        viewHolder.tvType.text = dataModel.type

        if(dataModel.type.equals("purchase")){
            viewHolder.tvEarnSpent.text = "Spent"
        }else{
            viewHolder.tvEarnSpent.text = "Earned"
        }

        val d = dataModel.createdAt!!.toDate().formatTo("yyyy-MM-dd  hh:mm a")

        viewHolder.tvDateTime.text = d

    }
//    var tz = TimeZone.getDefault()
    fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvTitle = view.tvDescription
        val tvPrice = view.tvPrice
        val tvType = view.tvType
        val tvEarnSpent = view.tvEarnSpent
        val tvDateTime = view.tvDateTime

    }

}