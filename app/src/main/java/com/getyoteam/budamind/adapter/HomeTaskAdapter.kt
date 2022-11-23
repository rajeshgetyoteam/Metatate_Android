package com.getyoteam.budamind.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.MomentListModel
import com.getyoteam.budamind.Model.TaskListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.PlayTaskMomentsActivity
import com.getyoteam.budamind.activity.PlayTaskSoundActivity
import com.getyoteam.budamind.testaudioexohls.PlayerExoTaskActivity
import com.getyoteam.budamind.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.raw_home_task.view.*

class HomeTaskAdapter(
    private val dataArraylist: List<TaskListModel>?,
    val context: Context, var onMomentTaskAdapterInteraction: OnTaskHomeAdapterInteractionListener
) : RecyclerView.Adapter<HomeTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_home_task,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {

        if (dataArraylist!!.size > 4) {
            return 4
        } else {
            return dataArraylist!!.size
        }

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {

        val dataModel = dataArraylist!![possion]

//        if (dataModel.claim.equals("true")) {
//
//            viewHolder.layMain.background =
//                context.resources.getDrawable(R.drawable.round_gray_corner_bg)
//            viewHolder.itemView.alpha = 0.9f
//
//        } else {
        viewHolder.layMain.background =
            context.resources.getDrawable(R.drawable.round_white_corner_bg)
//        }
        viewHolder.layAdds.setOnClickListener {

            if (dataModel.type.equals("courses")) {
                try {
                    val webpage: Uri = Uri.parse(dataModel.courseData!!.adLink)
                    val myIntent = Intent(Intent.ACTION_VIEW, webpage)
                    context.startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No application can handle this request. Please install a web browser or check your URL.",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }

             } else if (dataModel.type.equals("moments")) {
                try {
                    val webpage: Uri = Uri.parse(dataModel.moment!!.adLink)
                    val myIntent = Intent(Intent.ACTION_VIEW, webpage)
                    context.startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No application can handle this request. Please install a web browser or check your URL.",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }

             } else if (dataModel.type.equals("sound")) {

                try {
                    val webpage: Uri = Uri.parse(dataModel.sounds!!.adLink)
                    val myIntent = Intent(Intent.ACTION_VIEW, webpage)
                    context.startActivity(myIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No application can handle this request. Please install a web browser or check your URL.",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }

             }

        }

        if (dataModel.type.equals("courses")) {
            Glide.with(context)
                .load(dataModel.courseData!!.getBanner())
                .placeholder(
                    ColorDrawable(
                        ContextCompat.getColor(
                            context,
                            R.color.color_light_gray
                        )
                    )
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivTaskImage)
            viewHolder.tvTaskMin.text = dataModel.courseData!!.getFromMinutes() + " Minutes"

            viewHolder.tvTitle.text = dataModel.courseData!!.getCourseName()!!.toString()


            val token = Utils.format(MyApplication.prefs!!.courseCoin!!.toBigInteger())

            val a = "$"+"CHI"
            viewHolder.tvPrice.text = "Earn "+token+" $"+"CHI "
//            viewHolder.tvPrice.text = "300 $ CHI"
            if (dataModel!!.courseData!!.isAds!!) {
                viewHolder.layAdds.visibility = View.VISIBLE
                Glide.with(context)
                    .load(dataModel!!.courseData!!.adUrl)
                    .placeholder(
                        ColorDrawable(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivAddBanner)
//            viewHolder.layAdds.layoutParams.width = dataModel!!.adWidth!!.toInt()
//                if (dataModel.courseData!!.adHeight.isNullOrEmpty()) {
//                    viewHolder.layAdds.layoutParams.height = context.resources.getDimension(R.dimen._92sdp).toInt()
//                } else {
//                    val density = context.resources.displayMetrics.density
//                    val hight: Float = dataModel.courseData!!.adHeight!!.toInt() * density
//                    viewHolder.layAdds.layoutParams.height = hight.toInt()
//                }
            } else {

                viewHolder.layAdds.visibility = View.GONE
            }


        } else if (dataModel.type.equals("moments")) {
            Glide.with(context)
                .load(dataModel.moment!!.getImage())
                .placeholder(
                    ColorDrawable(
                        ContextCompat.getColor(
                            context,
                            R.color.color_light_gray
                        )
                    )
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivTaskImage)
            viewHolder.tvTaskMin.text = dataModel.moment!!.getMinutes() + " Minutes"
            viewHolder.tvTitle.text = dataModel.moment!!.getTitle()!!.toString()

//            viewHolder.tvPrice.text = "200 $ CHI"
            val token = Utils.format(MyApplication.prefs!!.momentCoin!!.toBigInteger())

            val a = "$"+"CHI"
            viewHolder.tvPrice.text = "Earn "+token+" $"+"CHI "

            if (dataModel!!.moment!!.isAds!!) {
                viewHolder.layAdds.visibility = View.VISIBLE
                Glide.with(context)
                    .load(dataModel!!.moment!!.adUrl)
                    .placeholder(
                        ColorDrawable(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivAddBanner)
//                if (dataModel.moment!!.adHeight.isNullOrEmpty()) {
//                    viewHolder.layAdds.layoutParams.height =
//                        context.resources.getDimension(R.dimen._92sdp).toInt()
//                } else {
//                    val density = context.resources.displayMetrics.density
//                    val hight: Float = dataModel.moment!!.adHeight!!.toInt() * density
//                    viewHolder.layAdds.layoutParams.height = hight.toInt()
//                }
            } else {

                viewHolder.layAdds.visibility = View.GONE
            }

        } else if (dataModel.type.equals("sound")) {
            Glide.with(context)
                .load(dataModel.sounds!!.image)
                .placeholder(
                    ColorDrawable(
                        ContextCompat.getColor(
                            context,
                            R.color.color_light_gray
                        )
                    )
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivTaskImage)
            viewHolder.tvTaskMin.text = dataModel.sounds!!.getMinutes() + " Minutes"

            viewHolder.tvTitle.text = dataModel.sounds!!.getTitle()!!.toString()

//            viewHolder.tvPrice.text = "100 $ CHI"

            val token = Utils.format(MyApplication.prefs!!.soundsCoin!!.toBigInteger())

            val a = "$"+"CHI"
            viewHolder.tvPrice.text = "Earn "+token+" $"+"CHI "

            if (dataModel!!.sounds!!.isAds!!) {
                viewHolder.layAdds.visibility = View.VISIBLE
                Glide.with(context)
                    .load(dataModel!!.sounds!!.adUrl)
                    .placeholder(
                        ColorDrawable(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivAddBanner)
//            viewHolder.layAdds.layoutParams.width = dataModel!!.adWidth!!.toInt()
//                if (dataModel.sounds!!.adHeight.isNullOrEmpty()) {
//                    viewHolder.layAdds.layoutParams.height =
//                        context.resources.getDimension(R.dimen._92sdp).toInt()
//                } else {
//                    val density = context.resources.displayMetrics.density
//                    val hight: Float = dataModel.sounds!!.adHeight!!.toInt() * density
//                    viewHolder.layAdds.layoutParams.height = hight.toInt()
//                }
            } else {

                viewHolder.layAdds.visibility = View.GONE
            }

        }



        viewHolder.layItem.setOnClickListener {

            onMomentTaskAdapterInteraction.onTaskHomeAdapterInteractionListener(dataModel)
        }

    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvPrice = view.tvPrice
        val tvTitle = view.tvTasktTitle
        val tvTaskMin = view.tvTaskMin
        val ivTaskImage = view.ivTaskImage
        val layMain = view.layMain
        val layItem = view.layItem
        val ivAddBanner = view.ivAddBanner
        val layAdds = view.layAdds
        val layPrice = view.layPrice

    }

    interface OnTaskHomeAdapterInteractionListener {
        fun onTaskHomeAdapterInteractionListener(taskListModel :TaskListModel)
    }
}


