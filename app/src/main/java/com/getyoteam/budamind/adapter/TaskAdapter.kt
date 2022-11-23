//package com.getyoteam.budamind.adapter
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.drawable.ColorDrawable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
//import com.getyoteam.budamind.Model.MomentListModel
//import com.getyoteam.budamind.Model.TaskDataModel
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.activity.MainActivity
//import com.getyoteam.budamind.activity.PlaySoundActivity
//import com.getyoteam.budamind.activity.PlayTaskMomentsActivity
//import com.getyoteam.budamind.activity.PlayTaskSoundActivity
//import com.getyoteam.budamind.testaudioexohls.PlayerExoTaskActivity
//import com.getyoteam.budamind.utils.Utils
//import com.google.gson.Gson
//import com.mindfulness.greece.model.MeditationStateModel
//import kotlinx.android.synthetic.main.raw_task.view.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//class TaskAdapter(
//    private val dataArraylist: List<TaskDataModel>?,
//    val context: Context
//) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
//        return ViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.raw_task,
//                parent,
//                false
//            ), context
//        )
//    }
//
//    override fun getItemCount(): Int {
//        return dataArraylist!!.size
//    }
//
//    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
//
//        val dataModel = dataArraylist!!.get(possion)
//
//        viewHolder.tvTitle.text = dataModel.titleText
//        var token = Utils.format(dataModel.coins!!.toBigInteger())
//
//        val a = "$"+"CHI"
//        viewHolder.tvPrice.text = token.replace("$",a)
//
//        if (dataModel.claim.equals("true")) {
//            viewHolder.ivStatus.setBackgroundResource(R.drawable.right_gray)
//            viewHolder.tvClaim.setBackgroundResource(R.drawable.ic_button_claim)
//            viewHolder.tvClaim.text = "Completed"
//            viewHolder.tvEarn.text = "Earned"
//        } else {
//            viewHolder.ivStatus.setBackgroundResource(R.drawable.right_green)
//            viewHolder.tvClaim.setBackgroundResource(R.drawable.selected_state)
//            viewHolder.tvClaim.text = "Start Task"
//            viewHolder.tvEarn.text = "Earn"
//        }
//
//        viewHolder.layAdds.setOnClickListener {
//
//        }
//
//        if (dataModel!!.isAds!!){
//            viewHolder.layAdds.visibility = View.VISIBLE
//            Glide.with(context)
//                .load(dataModel!!.adUrl)
//                .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(viewHolder.ivAddBanner)
//
////            viewHolder.layAdds.layoutParams.width = dataModel!!.adWidth!!.toInt()
//            if (dataModel.adHeight.isNullOrEmpty()){
//                viewHolder.layAdds.layoutParams.height =
//                    context.resources.getDimension(R.dimen._140sdp).toInt()
//            }else{
//                val density = context.resources.displayMetrics.density
//                val hight: Float = dataModel.adHeight!!.toInt() * density
//                viewHolder.layAdds.layoutParams.height = hight.toInt()
//            }
//        }else{
//
//            viewHolder.layAdds.visibility = View.GONE
//        }
//
////        if (dataModel.type.equals("courses")) {
////
////            if (dataModel.courseData!!.isAds!!){
////                viewHolder.layAdds.visibility = View.VISIBLE
////                Glide.with(context)
////                    .load(dataModel.courseData!!.adUrl)
////                    .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
////                    .transition(DrawableTransitionOptions.withCrossFade())
////                    .diskCacheStrategy(DiskCacheStrategy.ALL)
////                    .into(viewHolder.ivAddBanner)
////
////                viewHolder.layAdds.layoutParams.width = dataModel.courseData!!.adWidth!!.toInt()
////                viewHolder.layAdds.layoutParams.height = dataModel.courseData!!.adHeight!!.toInt()
////            }else{
////
////                viewHolder.layAdds.visibility = View.GONE
////            }
////
////        }else if (dataModel.type.equals("moments")) {
////            if (dataModel.moment!!.isAds!!){
////                viewHolder.layAdds.visibility = View.VISIBLE
////                Glide.with(context)
////                    .load(dataModel.moment!!.adUrl)
////                    .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
////                    .transition(DrawableTransitionOptions.withCrossFade())
////                    .diskCacheStrategy(DiskCacheStrategy.ALL)
////                    .into(viewHolder.ivAddBanner)
////
////                viewHolder.layAdds.layoutParams.width = dataModel.moment!!.adWidth!!.toInt()
////                viewHolder.layAdds.layoutParams.height = dataModel.moment!!.adHeight!!.toInt()
////            }else{
////
////                viewHolder.layAdds.visibility = View.GONE
////            }
////        }else if (dataModel.type.equals("sounds")) {
////            if (dataModel.sounds!!.isAds!!){
////                viewHolder.layAdds.visibility = View.VISIBLE
////                Glide.with(context)
////                    .load(dataModel.sounds!!.adUrl)
////                    .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
////                    .transition(DrawableTransitionOptions.withCrossFade())
////                    .diskCacheStrategy(DiskCacheStrategy.ALL)
////                    .into(viewHolder.ivAddBanner)
////
////                viewHolder.layAdds.layoutParams.width = dataModel.sounds!!.adWidth!!.toInt()
////                viewHolder.layAdds.layoutParams.height = dataModel.sounds!!.adHeight!!.toInt()
////            }else{
////
////                viewHolder.layAdds.visibility = View.GONE
////            }
////        }else{
////            viewHolder.layAdds.visibility = View.GONE
////        }
//
//
//
//        viewHolder.tvClaim.setOnClickListener {
//
//            if (dataModel.claim.equals("true")) {
//                Toast.makeText(
//                    context,
//                    "Task Completed!",
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            } else {
//
//                if (dataModel.type.equals("moments")) {
//
//                    val gson = Gson()
//                    val jsonMoment = gson.toJson(dataModel.moment)
////                val jsonMeditation = gson.toJson(meditationStateModel)
//                    MyApplication.prefs!!.momentModel = jsonMoment
////                MyApplication.prefs!!.stateModel = jsonMeditation
//                    val intent = Intent(context, PlayTaskMomentsActivity::class.java)
//                    intent.putExtra("m", "")
//                    intent.putExtra("taskid", dataModel.id)
//                    context.startActivity(intent)
//                } else if (dataModel.type.equals("courses")) {
//
//                    val gson = Gson()
//                    val jsonChapter = gson.toJson(dataModel.courses)
//                    val jsonCourse = gson.toJson(dataModel.courseData)
//                    MyApplication.prefs!!.chapterModel = jsonChapter
//                    MyApplication.prefs!!.courseModel = jsonCourse
//
//                    val intent = Intent(context, PlayerExoTaskActivity::class.java)
//
//                    intent.putExtra("taskid", dataModel.id)
//                    context.startActivity(intent)
//                } else if (dataModel.type.equals("sounds")) {
//
//                    val momentModel = MomentListModel()
//
//                    momentModel.setMomentId(dataModel.sounds!!.getSoundId())
//                    momentModel.setTitle(dataModel.sounds!!.getTitle().toString())
//                    momentModel.setSubtitle(dataModel.sounds!!.getSubtitle().toString())
//                    momentModel.setImage(dataModel.sounds!!.image.toString())
//                    momentModel.setMomentId(dataModel.sounds!!.getSoundId())
//                    momentModel.setAudio(dataModel.sounds!!.getAudio().toString())
//                    momentModel.setFreePaid(dataModel.sounds!!.getFreePaid().toString())
//                    momentModel.purchased = dataModel.sounds!!.purchased
//                    momentModel.coins = dataModel.sounds!!.coins
//                    momentModel.coinForContent = dataModel.sounds!!.coinForContent
//                    momentModel.setMinutes(dataModel.sounds!!.getMinutes().toString())
//                    momentModel.setSeconds(dataModel.sounds!!.getSeconds().toString())
//                    momentModel.setSeconds(dataModel.sounds!!.getSeconds().toString())
//
//
//                    val gson = Gson()
//                    val jsonMoment = gson.toJson(momentModel)
//                    MyApplication.prefs!!.momentModel = jsonMoment
//                    val intent = Intent(context, PlayTaskSoundActivity::class.java)
//                    intent.putExtra("m", "")
//                    intent.putExtra("taskid", dataModel.id)
//                    context.startActivity(intent)
//
////
////                    if (context is MainActivity) {
////                        (context as MainActivity).ChangeToSoundFragment(
////                            dataModel.sounds!!.getSoundId().toString(), dataModel.id
////                        )
////                    }
//                }
//
//            }
//
//        }
//
//
//    }
//
//    fun String.toDate(
//        dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone(
//            "UTC"
//        )
//    ): Date {
//        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
//        parser.timeZone = timeZone
//        return parser.parse(this)
//    }
//
//    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
//
//        val tvPrice = view.tvPrice
//        val tvTitle = view.tvTitle
//        val tvClaim = view.tvClaim
//        val ivStatus = view.ivStatus
//        val tvEarn = view.tvEarn
//        val ivAddBanner = view.ivAddBanner
//        val layAdds = view.layAdds
//
//
//    }
//
//}