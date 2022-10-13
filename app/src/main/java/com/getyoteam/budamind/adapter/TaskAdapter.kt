package com.getyoteam.budamind.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.getyoteam.budamind.Model.TaskDataModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.MainActivity
import com.getyoteam.budamind.activity.PlayTaskMomentsActivity
import com.getyoteam.budamind.testaudioexohls.PlayerExoTaskActivity
import com.getyoteam.budamind.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.raw_task.view.*
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(
    val dataArraylist: List<TaskDataModel>?,
    val context: Context
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_task,
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

        viewHolder.tvTitle.text = dataModel.titleText
        var token = Utils.format(dataModel.coins!!.toBigInteger())

        val a = "$"+"CHI"
        viewHolder.tvPrice.text = token.replace("$",a)

        if (dataModel.claim.equals("true")) {
            viewHolder.ivStatus.setBackgroundResource(R.drawable.right_gray)
            viewHolder.tvClaim.setBackgroundResource(R.drawable.ic_button_claim)
            viewHolder.tvClaim.text = "Claimed"
            viewHolder.tvEarn.text = "Earned"
        } else {
            viewHolder.ivStatus.setBackgroundResource(R.drawable.right_green)
            viewHolder.tvClaim.setBackgroundResource(R.drawable.selected_state)
            viewHolder.tvClaim.text = "Claim"
            viewHolder.tvEarn.text = "Earn"
        }

        viewHolder.tvClaim.setOnClickListener {


            if (dataModel.claim.equals("true")) {
                Toast.makeText(
                    context,
                    "Task Completed!",
                    Toast.LENGTH_SHORT
                )
                    .show()
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

//
//                    if (context is MainActivity) {
//                        (context as MainActivity).ChangeToSoundFragment(
//                            dataModel.sounds!!.getSoundId().toString(), dataModel.id
//                        )
//                    }
                }

            }

        }


    }

    fun String.toDate(
        dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone(
            "UTC"
        )
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvPrice = view.tvPrice
        val tvTitle = view.tvTitle
        val tvClaim = view.tvClaim
        val ivStatus = view.ivStatus
        val tvEarn = view.tvEarn


    }

}