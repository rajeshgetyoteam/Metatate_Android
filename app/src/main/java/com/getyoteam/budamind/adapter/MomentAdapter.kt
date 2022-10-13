package com.getyoteam.budamind.adapter

import android.content.Context
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
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.Model.MomentListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.raw_moment_item.view.*


class MomentAdapter(
    val momentModelArraylist: ArrayList<MomentListModel>?,
    var onMomentAdapterInteractionListner: OnMomentAdapterInteractionListener,
    val context: Context
) : RecyclerView.Adapter<MomentAdapter.ViewHolder>() {


    private var downloadId: Int = 0
    lateinit var onMomentAdapterInteractionListener: OnMomentAdapterInteractionListener

    private lateinit var db: AppDatabase
    private var filename: String? = null
    private var downloadFileModelOld: DownloadFileModel? = null
    private var audioPath: String? = null
    private lateinit var downloadFileModel: DownloadFileModel


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onMomentAdapterInteractionListener = onMomentAdapterInteractionListner
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
        viewHolder.tvMomentSubTitle.text = momentModelArraylist!!.get(possion).getSubtitle()!!.trim()
        val min = momentModelArraylist.get(possion).getMinutes().toString()
        val isPaid = momentModelArraylist.get(possion).getFreePaid().toString()
        viewHolder.tvMomentMin.text = min + " mins"


        Glide.with(context)
            .load(momentModelArraylist.get(possion).getImage())
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.ivMomentImages)

        if (isPaid.equals("paid", ignoreCase = true)) {
            if(momentModelArraylist.get(possion).purchased!!) {
                viewHolder.ivMomentLock.visibility = View.GONE
                viewHolder.layCoin.visibility = View.GONE
            }else{
                viewHolder.ivMomentLock.visibility = View.VISIBLE
                viewHolder.layCoin.visibility = View.VISIBLE
            }
        } else {
            viewHolder.ivMomentLock.visibility = View.GONE
            viewHolder.layCoin.visibility = View.GONE
        }

        if (momentModelArraylist.get(possion).coins != null){
            val token = Utils.format(momentModelArraylist.get(possion).coins!!.toBigInteger())
            viewHolder.tvPrice.text = token.replace("$","$"+"CHI")
        }else{
            viewHolder.tvPrice.text = "0"
        }


    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvMomentSubTitle = view.tvMomentSubTitle
        val tvMomentMin = view.tvMomentMin
        val tvMomentTitle = view.tvMomentTitle
        val ivMomentImages = view.ivMomentImages
        val ivMomentLock = view.ivMomentLock
        val layCoin = view.layCoin
        val tvPrice = view.tvPrice
    }

    interface OnMomentAdapterInteractionListener {
        fun onMomentAdapterInteractionListener(
            courseModel: CourseListModel,
            wantToDelete: Boolean
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