package com.getyoteam.budamind.adapter

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.getyoteam.budamind.Model.ChapterListModel
import com.getyoteam.budamind.Model.ChapterListPlayedModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.raw_chapter_item.view.*


class ChapterAdapter(
    val chapterModelArraylist: ArrayList<ChapterListModel>?,
    val chapterPlayedModelArraylist: ArrayList<ChapterListPlayedModel>?,
    var onChapterAdapterInteractionListner: OnChapterAdapterInteractionListener,
    val context: Context
) : RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    lateinit var onChapterAdapterInteractionListener: OnChapterAdapterInteractionListener

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onChapterAdapterInteractionListener = onChapterAdapterInteractionListner
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_chapter_item,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {
        return chapterModelArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
        viewHolder.tvChapterNo.text = chapterModelArraylist!!.get(possion).getChapterName().toString()

        if (chapterModelArraylist.get(possion).getFreePaid().equals("paid", ignoreCase = true)) {
            if (chapterModelArraylist.get(possion).purchased!!) {
                loadChapterIconInList(possion, viewHolder)
            } else {
                viewHolder.ivChapterIcon.setImageResource(R.drawable.ic_lock_round)
                viewHolder.tvChapterNo.setTextColor(ContextCompat.getColor(context, R.color.color_text_gray))
                viewHolder.itemView.isEnabled = true
            }
        } else {
            loadChapterIconInList(possion, viewHolder)
        }

        viewHolder.itemView.setOnClickListener {

            if (chapterPlayedModelArraylist!!.size > possion) {
                onChapterAdapterInteractionListener.onChapterAdapterInteractionListener(chapterModelArraylist.get(possion))
            } else if (chapterPlayedModelArraylist.size == possion) {
                onChapterAdapterInteractionListener.onChapterAdapterInteractionListener(chapterModelArraylist.get(possion))
            }else{
                Toast.makeText(context,"Please finish the current chapter first",Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun loadChapterIconInList(
        possion: Int,
        viewHolder: ViewHolder
    ) {
        if (chapterPlayedModelArraylist!!.size > possion) {
            viewHolder.ivChapterIcon.setImageResource(R.drawable.ic_checked)
            viewHolder.tvChapterNo.setTextColor(ContextCompat.getColor(context, R.color.app_pink_color))
            viewHolder.itemView.isEnabled = true
        } else if (chapterPlayedModelArraylist.size == possion) {
            viewHolder.ivChapterIcon.setImageResource(R.drawable.ic_play_fill_drk)
            viewHolder.tvChapterNo.setTextColor(ContextCompat.getColor(context, R.color.color_text_gray))
            viewHolder.itemView.isEnabled = true
        } else {
            viewHolder.ivChapterIcon.setImageResource(R.drawable.ic_play_light)
            viewHolder.tvChapterNo.setTextColor(ContextCompat.getColor(context, R.color.color_text_gray))
            viewHolder.itemView.isEnabled = true
        }
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val tvChapterNo = view.tvChapterNumber
        val ivChapterIcon = view.ivChapterIcon
    }

    interface OnChapterAdapterInteractionListener {
        fun onChapterAdapterInteractionListener(
            chapterListModel: ChapterListModel
        )
    }

}