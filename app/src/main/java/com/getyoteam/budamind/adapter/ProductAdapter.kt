package com.getyoteam.budamind.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.ProductListModel
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.raw_product.view.*

class ProductAdapter(
    private val dataList: ArrayList<ProductListModel>?,
    var onProductAdapterInteractionListner: OnProductAdapterInteractionListener,
    val context: Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    lateinit var onProductAdapterInteractionListener: OnProductAdapterInteractionListener


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onProductAdapterInteractionListener = onProductAdapterInteractionListner
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_product,
                parent,
                false
            ), context
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
//        viewHolder.tvSleepTitle.text = courseModelArraylist!!.get(possion).getCourseName()!!.trim()

//        viewHolder.tvSleepSubTitle.text = "$count Courses"


        val dataModel = dataList!![possion]
        viewHolder.tvProductTitle.text = dataModel.productName
        Glide.with(context)
            .load(dataModel.productImage)
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(viewHolder.ivImage)



        viewHolder.itemView.setOnClickListener {
            onProductAdapterInteractionListener.onProductAdapterInteractionListener(dataList!![possion])
        }
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

//        val tvSleepSubTitle = view.tvCourseSubTitle
//        val tvSleepTitle = view.tvCourseTitle
        val ivImage = view.ivImage
        val tvProductTitle = view.tvProductTitle
//        val tvPrice = view.tvPrice
//        val layPrice = view.layPrice
//        val tvCourseGenre = view.tvCourseGenre
//        val ivLock = view.ivLock
//        val ivAddBanner = view.ivAddBanner
//        val layAdds = view.layAdds
    }

    interface OnProductAdapterInteractionListener {
        fun onProductAdapterInteractionListener(
            productListModel: ProductListModel
        )
    }

}