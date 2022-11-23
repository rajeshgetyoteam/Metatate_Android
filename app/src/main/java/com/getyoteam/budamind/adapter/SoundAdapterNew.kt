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
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.Model.SoundListModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.raw_sound_item.view.*


class SoundAdapterNew(
    val soundModelArraylist: List<SoundListModel>?,
    val context: Context, var onSoundAdapterInteraction: OnSoundAdapterInteractionListener
) : RecyclerView.Adapter<SoundAdapterNew.ViewHolder>() {

    //    var mediaPlayer = MediaPlayer()
    private var downloadId: Int = 0
    lateinit var onSoundAdapterInteractionListener: OnSoundAdapterInteractionListener
    private var soundRecyclerView: RecyclerView? = null
    private lateinit var db: AppDatabase
    private var filename: String? = null
    private var downloadFileModelOld: DownloadFileModel? = null
    private var audioPath: String? = null
    private lateinit var downloadFileModel: DownloadFileModel

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        this.onSoundAdapterInteractionListener = onSoundAdapterInteraction
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.raw_sound_item,
                parent,
                false
            ), context
        )

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        soundRecyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return soundModelArraylist!!.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {

        val dataModel = soundModelArraylist!![possion]
        viewHolder.tvSoundTitle.text = dataModel.getTitle()!!.trim()
        viewHolder.tvSoundSubTitle.text = dataModel.getSubtitle()!!.trim()

        val min = dataModel.getMinutes().toString()
        val sec = dataModel.getSeconds().toString()
        val isPaid = dataModel.getFreePaid().toString()
        viewHolder.tvSoundMin.text = "$min mins"


        Glide.with(context)
            .load(dataModel.image)
            .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.ivSoundImages)

        if (dataModel!!.isAds!!){
            viewHolder.layAdds.visibility = View.VISIBLE
            Glide.with(context)
                .load(dataModel!!.adUrl)
                .placeholder(ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivAddBanner)

//            viewHolder.layAdds.layoutParams.width = dataModel!!.adWidth!!.toInt()
//            if (dataModel.adHeight.isNullOrEmpty()){
//                viewHolder.layAdds.layoutParams.height =
//                    context.resources.getDimension(R.dimen._100sdp).toInt()
//            }else{
//                val density = context.resources.displayMetrics.density
//                val hight: Float = dataModel.adHeight!!.toInt() * density
//                viewHolder.layAdds.layoutParams.height = hight.toInt()
//            }
        }else{

            viewHolder.layAdds.visibility = View.GONE
        }

        viewHolder.layAdds.setOnClickListener {

        }

        if (isPaid.equals("paid", ignoreCase = true)) {

            if (dataModel.purchased!!) {
                viewHolder.ivMomentLock.visibility = View.GONE
                viewHolder.layCoin.visibility = View.INVISIBLE

            } else {
                viewHolder.ivMomentLock.visibility = View.VISIBLE
                viewHolder.layCoin.visibility = View.VISIBLE

                if (dataModel.coins != null) {
                    val token = Utils.format(dataModel.coins!!.toBigInteger())
                    viewHolder.tvPrice.text = token.replace("$","$"+"CHI")
                } else {
                    viewHolder.tvPrice.text = "0"
                }
            }


        } else {
            viewHolder.ivMomentLock.visibility = View.GONE
            viewHolder.layCoin.visibility = View.INVISIBLE

        }

        viewHolder.itemView.setOnClickListener {
            onSoundAdapterInteractionListener!!.onSoundAdapterInteractionListener(
                dataModel, possion,"Home"
            )
        }

        viewHolder.layAdds.setOnClickListener {
            try {
                val webpage: Uri = Uri.parse(dataModel.adLink)
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

//    fun performClick(soundId: Int?) {
//        if (soundModelArraylist != null) {
//            for (pos in 0 until soundModelArraylist.size) {
//                if (soundModelArraylist[pos].getSoundId() == soundId) {
//
////                    soundRecyclerView?.findViewHolderForAdapterPosition(pos)?.itemView!!.performClick()
//                    onSoundAdapterInteractionListener!!.onSoundAdapterInteractionListener(
//                        soundModelArraylist.get(pos), pos,"Task"
//                    )
//                }
//            }
//        }
//    }


    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        val tvSoundSubTitle = view.tvsoundSubTitle
        val tvSoundMin = view.tvSoundMin
        val tvSoundTitle = view.tvSoundtTitle
        val layCoin = view.layCoin
        val tvPrice = view.tvPrice
        val ivSoundImages = view.ivSoundImages
        val ivMomentLock = view.ivMomentLock
        val ivAddBanner = view.ivAddBanner
        val layAdds = view.layAdds

    }

    interface OnSoundAdapterInteractionListener {
        fun onSoundAdapterInteractionListener(
            courseModel: SoundListModel,
            possion: Int,
            s: String
        )
    }

}