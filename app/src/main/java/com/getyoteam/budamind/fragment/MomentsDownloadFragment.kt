package com.getyoteam.budamind.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.Model.MomentListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.PlayActivity
import com.getyoteam.budamind.adapter.MomentAdapter
import com.getyoteam.budamind.adapter.MomentDownloadAdapter
import com.getyoteam.budamind.interfaces.CustomTouchListener
import com.getyoteam.budamind.interfaces.onItemClickListener
import com.getyoteam.budamind.utils.AppDatabase
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.fragment_moments.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MomentsDownloadFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class MomentsDownloadFragment : Fragment(),
    MomentAdapter.OnMomentAdapterInteractionListener {


    private var momentModel: MomentListModel? = null
    private var meditationStateModel: MeditationStateModel? = null
    private var listener: OnFragmentInteractionListener? = null

    private var userId: String = ""
    private lateinit var db: AppDatabase
    private lateinit var momentArrayList: ArrayList<DownloadFileModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = MyApplication.prefs!!.userId
        db = AppDatabase.getDatabase(requireContext())

        tvMomentHeader.visibility = View.GONE
        momentArrayList = ArrayList<DownloadFileModel>()
        rvMomentList!!.layoutManager = LinearLayoutManager(requireContext())

        rvMomentList.addOnItemTouchListener(object : CustomTouchListener(requireContext(), object :
            onItemClickListener {
            override fun onClick(view: View?, index: Int) {
                val downloadModel = momentArrayList.get(index)
//                momentModel = MomentListModel()
                if (momentModel == null) {
                    momentModel = MomentListModel()
                    momentModel!!.setImage(downloadModel.getImageFile()!!)
                    momentModel!!.setMomentId(downloadModel.getFileId()!!)
                    momentModel!!.setTitle(downloadModel.getTitle()!!)
                    momentModel!!.setSubtitle(downloadModel.getSubTitle()!!)
                    momentModel!!.setMinutes(downloadModel.getMinute()!!)
                    momentModel!!.setAudio(downloadModel.getAudioFilePath()!!)
                }
                if (meditationStateModel == null) {
                    val meditationStateModelList = db.meditationStateDao().getAll()
                    if (meditationStateModelList.size > 0) {
                        meditationStateModel = meditationStateModelList.get(0)
                    }
                }

                val intent = Intent(requireContext(), PlayActivity::class.java)

                var momentListModel = MomentListModel()
                momentListModel.setImage(downloadModel.getImageFile()!!)
                momentListModel.setMomentId(downloadModel.getFileId()!!)
                momentListModel.setTitle(downloadModel.getTitle()!!)
                momentListModel.setSubtitle(downloadModel.getSubTitle()!!)
                momentListModel.setMinutes(downloadModel.getMinute()!!)
                momentListModel.setAudio(downloadModel.getAudioFilePath()!!)
                val gson = Gson()
                val jsonMoment = gson.toJson(momentModel)
                val jsonMeditation = gson.toJson(meditationStateModel)
                MyApplication.prefs!!.momentModel = jsonMoment
                MyApplication.prefs!!.stateModel = jsonMeditation

                intent.putExtra("meditationStateModel", meditationStateModel)
                intent.putExtra("downloadModel", downloadModel)
                startActivity(intent)
            }
        }) {})

        swipeToRefresh.setRefreshing(false);
        swipeToRefresh.setEnabled(false);
    }

    override fun onMomentAdapterInteractionListener(
        courseModel: CourseListModel,
        wantToDelete: Boolean
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDB()
    }


    private fun loadDataFromDB() {
        if (momentArrayList.size > 0) {
            momentArrayList.clear()
        }

        val downloadArrayList = db.downloadDao().getAll() as ArrayList<DownloadFileModel>

        for (i in 0..downloadArrayList.size - 1) {
            val downloadFileModel = downloadArrayList.get(i)
            if (downloadFileModel.getModelName()!!.contains("moment")) {
                momentArrayList.add(downloadFileModel)
            }
        }


        if (momentArrayList.size > 0) {
            rvMomentList.visibility = View.VISIBLE
            tvNodata.visibility = View.GONE
            rvMomentList.adapter =
                MomentDownloadAdapter(momentArrayList, requireContext())
        }else{
            tvNodata.visibility = View.VISIBLE
            rvMomentList.visibility = View.GONE
        }

    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}
