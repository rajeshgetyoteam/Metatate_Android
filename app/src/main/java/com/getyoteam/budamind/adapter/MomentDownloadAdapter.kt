package com.getyoteam.budamind.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.raw_moment_item.view.*


class MomentDownloadAdapter(
    val momentModelArraylist: ArrayList<DownloadFileModel>?,
    val context: Context
) : RecyclerView.Adapter<MomentDownloadAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_moment_item,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {
        return momentModelArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
        viewHolder.tvMomentTitle.text = momentModelArraylist!!.get(possion).getTitle()!!.trim()
        viewHolder.tvMomentSubTitle.text = momentModelArraylist!!.get(possion).getSubTitle()!!.trim()
        val min = momentModelArraylist.get(possion).getMinute().toString()
        viewHolder.tvMomentMin.text = min + " mins"
        viewHolder.ivMomentLock.visibility = View.GONE
        Glide.with(context)
            .load(momentModelArraylist.get(possion).getImageFile())
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.ivMomentImages)
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvMomentSubTitle = view.tvMomentSubTitle
        val tvMomentMin = view.tvMomentMin
        val tvMomentTitle = view.tvMomentTitle
        val ivMomentImages = view.ivMomentImages
        val ivMomentLock = view.ivMomentLock
    }

}