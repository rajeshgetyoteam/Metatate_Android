package com.getyoteam.budamind.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.TaskDataModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.MainActivity
import com.getyoteam.budamind.activity.PlayTaskMomentsActivity
import com.getyoteam.budamind.testaudioexohls.PlayerExoTaskActivity
import com.getyoteam.budamind.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.raw_home_task.view.*

class HomeTaskAdapter(
    private val dataArraylist: List<TaskDataModel>?,
    val context: Context
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
        return dataArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {

        val dataModel = dataArraylist!![possion]

        viewHolder.tvTitle.text = dataModel.titleText
        val token = Utils.format(dataModel.coins!!.toBigInteger())
        viewHolder.tvPrice.text = token.replace("$", "$" + "CHI")

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
        } else if (dataModel.type.equals("sounds")) {
            Glide.with(context)
                .load("")
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
        } else {
            Glide.with(context)
                .load("")
//            .placeholder(R.drawable.gradient_color)
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
            viewHolder.tvTaskMin.text = "00 Minutes"
        }


        if (dataModel.claim.equals("true")) {

            viewHolder.layMain.background =
                context.resources.getDrawable(R.drawable.round_gray_corner_bg)
            viewHolder.itemView.alpha = 0.9f

        } else {
            viewHolder.layMain.background =
                context.resources.getDrawable(R.drawable.round_white_corner_bg)
        }

        viewHolder.itemView.setOnClickListener {

            if (dataModel.claim.equals("true")) {
                Toast.makeText(
                    context,
                    "Task Completed!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (dataModel.type.equals("moments")) {

                    val gson = Gson()
                    val jsonMoment = gson.toJson(dataModel.moment)
//                val jsonMeditation = gson.toJson(meditationStateModel)
                    MyApplication.prefs!!.momentModel = jsonMoment
//                MyApplication.prefs!!.stateModel = jsonMeditation
                    val intent = Intent(context, PlayTaskMomentsActivity::class.java)
                    intent.putExtra("m", "")
                    intent.putExtra("taskid", dataModel.id)
                    context.startActivity(intent)
                } else if (dataModel.type.equals("courses")) {

                    val gson = Gson()
                    val jsonChapter = gson.toJson(dataModel.courses);
                    val jsonCourse = gson.toJson(dataModel.courseData);


                    MyApplication.prefs!!.chapterModel = jsonChapter
                    MyApplication.prefs!!.courseModel = jsonCourse

                    val intent = Intent(context, PlayerExoTaskActivity::class.java)

                    intent.putExtra("taskid", dataModel.id)
                    context.startActivity(intent)
                } else if (dataModel.type.equals("sounds")) {

                    if (context is MainActivity) {
                        (context as MainActivity).ChangeToSoundFragment(
                            dataModel.sounds!!.getSoundId().toString(), dataModel.id
                        )
                    }
                }

            }

        }


    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvPrice = view.tvPrice
        val tvTitle = view.tvTasktTitle
        val tvTaskMin = view.tvTaskMin
        val ivTaskImage = view.ivTaskImage
        val layMain = view.layMain


    }

}