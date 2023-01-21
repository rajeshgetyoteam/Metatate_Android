package com.getyoteam.budamind.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.getyoteam.budamind.Model.SizeListModel
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.item_view_size.view.*

class SizeListAdapter(
    val context: Context,
    val categoryList: ArrayList<SizeListModel>?,
    val itemClickListener: ItemClickListener,
    categoryFilter: Int
) :
    RecyclerView.Adapter<SizeListAdapter.ViewHolder>() {
    var selectedItemPosition: Int = 0
    var attachedRecyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_size, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return categoryList?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ivSelected: ImageView = view.ivSelected
        val tvSize: TextView = view.tvSize

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val categoryItem = categoryList?.get(position)

        holder.tvSize.text = categoryItem!!.productName
        if (selectedItemPosition == position) {
            holder.ivSelected.visibility = View.VISIBLE

        } else {
            holder.ivSelected.visibility = View.GONE

        }

        holder.itemView.setOnClickListener {

            if (position != selectedItemPosition) {
                selectedItemPosition = position

                notifyDataSetChanged()

            }
        }

    }

    interface ItemClickListener {

        fun itemCategoryClick(time: String)

    }
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }
}