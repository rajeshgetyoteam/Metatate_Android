package com.getyoteam.budamind.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.raw_common_items_course.view.*

class CourseHomeAdapter(
    val courseArrayList: ArrayList<CourseListModel>?,
    var onCourseHomeAdapterInteraction: OnCourseHomeAdapterInteractionListener,
    val context: Context
) : RecyclerView.Adapter<CourseHomeAdapter.ViewHolder>() {

    lateinit var onCourseHomeAdapterInteractionListener: OnCourseHomeAdapterInteractionListener

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onCourseHomeAdapterInteractionListener = onCourseHomeAdapterInteraction

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.raw_common_items_course, parent, false), context)
    }

    override fun getItemCount(): Int {
        return courseArrayList!!.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {

        viewHolder.ivLock.visibility = View.VISIBLE
        Glide.with(context)
            .load(courseArrayList!!.get(possion).getBanner())
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.ivCommonItem)
        viewHolder.tvCommonItemName.text = courseArrayList.get(possion).getCourseName()

        val min = courseArrayList.get(possion).getToMinutes().toString()
        viewHolder.tvCommonItemDescreption.text = courseArrayList.get(possion).getDescription()
        viewHolder.tvCommonItemTime.text = min + " Min"
        if (courseArrayList.get(possion).freePaid.equals("Free", true)) {
            viewHolder.layPrice.visibility = View.GONE
            viewHolder.ivLock.visibility = View.GONE

        } else {
            if (courseArrayList.get(possion).purchased!!) {
                viewHolder.layPrice.visibility = View.GONE
                viewHolder.ivLock.visibility = View.GONE
            } else {

                if (courseArrayList.get(possion).coins != null) {
                    val p = Utils.format(courseArrayList.get(possion).coins!!.toBigInteger())
                    viewHolder.tvPrice.text = p.toString().replace("$","$"+"CHI")

                } else {
                    viewHolder.tvPrice.text = "0"
                }
            }
        }

        viewHolder.ivCommonItem.setOnClickListener {
            onCourseHomeAdapterInteractionListener!!.onCourseHomeAdapterInteractionListener(
                courseArrayList.get(possion)
            )
        }


    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val tvCommonItemName = view.tvCommonItemName
        val tvCommonItemTime = view.tvCommonItemTime
        val tvCommonItemDescreption = view.tvCommonItemDescreption
        val ivCommonItem = view.ivCommonItem
        val ivLock = view.ivLock
        val layPrice = view.layCoin
        val tvPrice = view.tvPrice
    }

    interface OnCourseHomeAdapterInteractionListener {
        fun onCourseHomeAdapterInteractionListener(courseListModel: CourseListModel)
    }
}