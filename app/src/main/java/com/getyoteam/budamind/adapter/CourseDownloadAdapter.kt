package com.getyoteam.budamind.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.CourseDownloadModel
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.AppDatabase
import kotlinx.android.synthetic.main.raw_course_item.view.*


class CourseDownloadAdapter(
    val courseModelArraylist: ArrayList<CourseDownloadModel>?,
    var onCourseDownloadAdapterInteractionListner: OnCourseDownloadAdapterInteractionListener,
    val context: Context
) : RecyclerView.Adapter<CourseDownloadAdapter.ViewHolder>() {


    private var downloadId: Int = 0
    lateinit var onCourseDownloadAdapterInteractionListener: OnCourseDownloadAdapterInteractionListener

    private lateinit var db: AppDatabase
    private var filename: String? = null
    private var downloadFileModelOld: DownloadFileModel? = null
    private var audioPath: String? = null
    private lateinit var downloadFileModel: DownloadFileModel


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onCourseDownloadAdapterInteractionListener = onCourseDownloadAdapterInteractionListner
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_course_item,
                parent,
                false
            ), context
        )
    }

    override fun getItemCount(): Int {
        return courseModelArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
        viewHolder.tvSleepTitle.text = courseModelArraylist!!.get(possion).getCourseName()!!.trim()
        val min = courseModelArraylist.get(possion).getToMinutes().toString()
        viewHolder.tvSleepSubTitle.text = min + " mins"

        val color = Color.parseColor(courseModelArraylist!!.get(possion).getColorCode())
//        viewHolder.ivSleepImage.setBackgroundColor(color)

        Glide.with(context)
            .load(courseModelArraylist.get(possion).getBanner())
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.ivSleepImage)

        viewHolder.ivSleepImage.setOnClickListener {
            onCourseDownloadAdapterInteractionListener.onCourseDownloadAdapterInteractionListener(courseModelArraylist.get(possion))
        }

//        Glide.with(context)
//            .load(courseModelArraylist.get(possion).getBanner())
//            .into(viewHolder.ivSleepImage)
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvSleepSubTitle = view.tvCourseSubTitle
        val tvSleepTitle = view.tvCourseTitle
        val ivSleepImage = view.ivCourseImages
    }

    interface OnCourseDownloadAdapterInteractionListener {
        fun onCourseDownloadAdapterInteractionListener(
            courseModel: CourseDownloadModel
        )
    }

//    private fun downloadAudio(
//        viewHolder: ViewHolder,
//        possion: Int
//    ) {
////        TODO("/data/user/0/com.mindfulness.greece/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
//        val dirPath = context.getApplicationInfo().dataDir
//        viewHolder.circleProgressNormal.visibility = View.VISIBLE
//        viewHolder.ivSleepDownload.visibility = View.GONE
//        downloadId = PRDownloader.download(audioPath, dirPath, filename)
//            .build()
//            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
//                override fun onStartOrResume() {
//
//                }
//
//            })
//            .setOnPauseListener(object : OnPauseListener {
//                override fun onPause() {
//
//                }
//            })
//            .setOnCancelListener(object : OnCancelListener {
//                override fun onCancel() {
//
//                }
//            })
//            .setOnProgressListener(object : OnProgressListener {
//                override fun onProgress(progress: Progress) {
//                    val progressPercent = progress.currentBytes * 100 / progress.totalBytes;
//                    viewHolder.circleProgressNormal.progress = progressPercent.toInt()
//                }
//            })
//            .start(object : OnDownloadListener {
//                override fun onError(error: com.downloader.Error?) {
//                    Log.e("@@@", error.toString() + "@@@")
//                }
//
//                override fun onDownloadComplete() {
//                    viewHolder.circleProgressNormal.visibility = View.GONE
//                    viewHolder.ivSleepDownload.visibility = View.VISIBLE
////                    viewHolder.ivSleepDownload.setImageResource(R.drawable.ic_download_done_white)
////                    db.downloadDao().insertDownloadFile(downloadFileModel)
//                }
//
//            })
////        storyModelArraylist!!.get(possion).setDownloadId(downloadId)
//
//    }
}