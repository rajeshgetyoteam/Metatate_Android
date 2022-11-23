//package com.getyoteam.budamind.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.getyoteam.budamind.Model.DownloadFileModel
//import com.getyoteam.budamind.Model.SoundListModel
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.utils.AppDatabase
//import com.getyoteam.budamind.utils.Utils
//import kotlinx.android.synthetic.main.raw_sound_item.view.*
//
//
//class SoundAdapter(
//    val soundModelArraylist: List<SoundListModel>?,
//    val context: Context, var onSoundAdapterInteraction: OnSoundAdapterInteractionListener
//) : RecyclerView.Adapter<SoundAdapter.ViewHolder>() {
//
//    //    var mediaPlayer = MediaPlayer()
//    private var downloadId: Int = 0
//    lateinit var onSoundAdapterInteractionListener: OnSoundAdapterInteractionListener
//    private var soundRecyclerView: RecyclerView? = null
//    private lateinit var db: AppDatabase
//    private var filename: String? = null
//    private var downloadFileModelOld: DownloadFileModel? = null
//    private var audioPath: String? = null
//    private lateinit var downloadFileModel: DownloadFileModel
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
//        this.onSoundAdapterInteractionListener = onSoundAdapterInteraction
//        return ViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.raw_sound_item,
//                parent,
//                false
//            ), context
//        )
//
//    }
//
//    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
//        super.onAttachedToRecyclerView(recyclerView)
//        soundRecyclerView = recyclerView
//    }
//
//    override fun getItemCount(): Int {
//        return soundModelArraylist!!.size
//    }
//
//    override fun onBindViewHolder(viewHolder: ViewHolder, possion: Int) {
//
//        viewHolder.tvSoundTitle.text = soundModelArraylist!!.get(possion).getTitle()!!.trim()
//        viewHolder.tvSoundSubTitle.text = soundModelArraylist.get(possion).getSubtitle()!!.trim()
//        val soundModel = soundModelArraylist.get(possion)
//        val min = soundModel.getMinutes().toString()
//        val sec = soundModel.getSeconds().toString()
//        val isPaid = soundModel.getFreePaid().toString()
//        viewHolder.tvSoundMin.text = min + ":" + sec
//
//
//        if (soundModel.isFirstTime()!!) {
//            viewHolder.progressBar.visibility = View.VISIBLE
//        } else {
////            viewHolder.progressBar.visibility = View.VISIBLE
//            viewHolder.progressBar.visibility = View.GONE
//        }
//
//        if (isPaid.equals("paid", ignoreCase = true)) {
//
//            if (soundModel.purchased!!) {
//                viewHolder.ivSoundLock.visibility = View.GONE
//                viewHolder.layCoin.visibility = View.INVISIBLE
//                viewHolder.ivSoundPlay.visibility = View.VISIBLE
//            } else {
//                viewHolder.ivSoundLock.visibility = View.VISIBLE
//                viewHolder.layCoin.visibility = View.VISIBLE
//                viewHolder.ivSoundPlay.visibility = View.GONE
//                if (soundModel.coins != null) {
//                    val token = Utils.format(soundModel.coins!!.toBigInteger())
//                    viewHolder.tvPrice.text = token.replace("$","$"+"CHI")
//                } else {
//                    viewHolder.tvPrice.text = "0"
//                }
//            }
//
//
//        } else {
//            viewHolder.ivSoundLock.visibility = View.GONE
//            viewHolder.layCoin.visibility = View.INVISIBLE
//            viewHolder.ivSoundPlay.visibility = View.VISIBLE
//        }
//
//        if (soundModelArraylist.get(possion).isPlaying()!!) {
//            viewHolder.ivSoundPlay.setImageResource(R.drawable.ic_pause_black)
//        } else {
//            viewHolder.ivSoundPlay.setImageResource(R.drawable.ic_play_black)
//        }
//
//        viewHolder.itemView.setOnClickListener {
//            onSoundAdapterInteractionListener!!.onSoundAdapterInteractionListener(
//                soundModel, possion,"Home"
//            )
//        }
//
//        viewHolder.itemView
//    }
//
//    fun performClick(soundId: Int?) {
//        if (soundModelArraylist != null) {
//            for (pos in 0 until soundModelArraylist.size) {
//                if (soundModelArraylist[pos].getSoundId() == soundId) {
//
////                    soundRecyclerView?.findViewHolderForAdapterPosition(pos)?.itemView!!.performClick()
//                    onSoundAdapterInteractionListener!!.onSoundAdapterInteractionListener(
//                        soundModelArraylist.get(pos), pos,"Task"
//                    )
//
//                }
//            }
//        }
//    }
//
//
//    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
//
//        val tvSoundSubTitle = view.tvsoundSubTitle
//        val tvSoundMin = view.tvSoundMin
//        val tvSoundTitle = view.tvSoundtTitle
//        val ivSoundPlay = view.ivSoundPlay
//        val ivSoundLock = view.ivSoundLock
//        val progressBar = view.progressBar
//        val layCoin = view.layCoin
//        val tvPrice = view.tvPrice
//
//    }
//
//    interface OnSoundAdapterInteractionListener {
//        fun onSoundAdapterInteractionListener(
//            courseModel: SoundListModel,
//            possion: Int,
//            s: String
//        )
//    }
//
//}