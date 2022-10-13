package com.getyoteam.budamind.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.getyoteam.budamind.Model.WallateHistoryModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.raw_token_history.view.*
import java.text.SimpleDateFormat
import java.util.*


class TokenHisteoryAdapter(
    val dataArraylist: ArrayList<WallateHistoryModel>?,
    val context: Context
) : RecyclerView.Adapter<TokenHisteoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_token_history,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {
        return dataArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
        viewHolder.viewForeground = viewHolder.itemView.view_foreground
       val dataModel = dataArraylist!!.get(possion)
        viewHolder.tvTitle.text = dataModel.description
        val token = Utils.format(dataModel.coins!!.toBigInteger())
        viewHolder.tvPrice.text = token.replace("$","$"+"CHI")
        viewHolder.tvType.text = dataModel.type

        val date = dataModel.createdAt!!.toDate().formatTo("yyyy-MM-dd  hh:mm a")
        viewHolder.tvDateTime.text = date

    }
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
        val tvDateTime = view.tvDateTime
        lateinit var viewForeground: LinearLayout
        lateinit var viewBackground: RelativeLayout

    }

    fun refreshdata() {

        notifyDataSetChanged()

    }

    fun removeAt(position: Int) {
        dataArraylist?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataArraylist!!.size)
        notifyDataSetChanged()
    }

}