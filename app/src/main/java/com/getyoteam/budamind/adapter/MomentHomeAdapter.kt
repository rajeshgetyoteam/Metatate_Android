//package com.getyoteam.budamind.adapter
//
////import androidx.recyclerview.widget.RecyclerView
//import android.content.Context
//import android.graphics.drawable.ColorDrawable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
//import com.getyoteam.budamind.Model.MomentListModel
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.utils.Utils
//import kotlinx.android.synthetic.main.raw_common_items.view.*
//
//class MomentHomeAdapter(
//    val momentArrayList: ArrayList<MomentListModel>?,
//    var onMomentHomeAdapterInteraction: OnMomentHomeAdapterInteractionListener,
//    val context: Context
//) : RecyclerView.Adapter<MomentHomeAdapter.ViewHolder>() {
//
//    lateinit var onMomentHomeAdapterInteractionListener: OnMomentHomeAdapterInteractionListener
//
//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
//        this.onMomentHomeAdapterInteractionListener = onMomentHomeAdapterInteraction
//
//        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.raw_common_items, parent, false), context)
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return position
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//    override fun getItemCount(): Int {
//        return momentArrayList!!.size
//    }
//
//    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
//        val isSubscribe = MyApplication.prefs!!.subPurchase
//        Glide.with(context)
//            .load(momentArrayList!!.get(possion).getImage())
//            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
//            .transition(DrawableTransitionOptions.withCrossFade())
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(viewHolder.ivCommonItem)
//        viewHolder.tvCommonItemName.text = momentArrayList.get(possion).getTitle()
////        viewHolder.tvPrice.text = momentArrayList.get(possion).coinForContent
////        viewHolder.tvCommonItemDescreption.visibility=View.GONE
////        viewHolder.tvCommonItemTime.visibility=View.GONE
//
//        if (momentArrayList.get(possion).getFreePaid().equals("Free", ignoreCase = true)) {
//
//            viewHolder.ivLock.visibility = View.GONE
//            viewHolder.layCoin.visibility = View.GONE
//        } else {
//            if (momentArrayList.get(possion).purchased!!) {
//                viewHolder.ivLock.visibility = View.GONE
//                viewHolder.layCoin.visibility = View.GONE
//            } else {
//                viewHolder.ivLock.visibility = View.VISIBLE
//                viewHolder.layCoin.visibility = View.VISIBLE
//            }
//        }
//        if (momentArrayList.get(possion).coins != null){
//            val token = Utils.format(momentArrayList.get(possion).coins!!.toBigInteger())
//            viewHolder.tvPrice.text = token.replace("$","$"+"CHI")
//        }else{
//            viewHolder.tvPrice.text = "0"
//        }
//        val min = momentArrayList.get(possion).getMinutes().toString()
//        viewHolder.tvCommonItemDescreption.text = momentArrayList.get(possion).getSubtitle()
//        viewHolder.tvCommonItemTime.text = min + " Min"
//
//        viewHolder.ivCommonItem.setOnClickListener {
//            onMomentHomeAdapterInteractionListener.onMomentHomeAdapterInteractionListener(
//                momentArrayList.get(possion),
//                possion
//            )
//        }
//    }
//
//    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
//        val tvCommonItemName = view.tvCommonItemName
//        val tvCommonItemTime = view.tvCommonItemTime
//        val tvCommonItemDescreption = view.tvCommonItemDescreption
//        val ivCommonItem = view.ivCommonItem
//        val ivLock = view.ivLock
//        val layCoin = view.layCoin
//        val tvPrice = view.tvPrice
//    }
//
//    interface OnMomentHomeAdapterInteractionListener {
//        fun onMomentHomeAdapterInteractionListener(momentListModel: MomentListModel, possion: Int)
//    }
//}