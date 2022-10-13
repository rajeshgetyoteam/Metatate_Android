package com.getyoteam.budamind.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.getyoteam.budamind.Model.CourseDownloadModel
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.MyApplication

import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.ChapterActivity
import com.getyoteam.budamind.adapter.CourseDownloadAdapter
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_courses.*
import kotlinx.android.synthetic.main.fragment_courses.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CoursesDownloadFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */

class CoursesDownloadFragment : Fragment(), CourseDownloadAdapter.OnCourseDownloadAdapterInteractionListener {

    private var userId: String = ""
    private lateinit var db: AppDatabase
    private lateinit var coursArrayList: ArrayList<CourseDownloadModel>
    private lateinit var coursDBArrayList: ArrayList<CourseListModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = MyApplication.prefs!!.userId
        db = AppDatabase.getDatabase(requireContext())

        coursArrayList = ArrayList<CourseDownloadModel>()
        coursDBArrayList = ArrayList<CourseListModel>()
        rvCourseList!!.layoutManager = GridLayoutManager(requireContext(), 2) as RecyclerView.LayoutManager?
        rvCourseList.addItemDecoration(
            GridItemDecoration(
                resources.getDimension(R.dimen._35sdp).toInt(),
                2
            )
        )
        swipeToRefresh.setRefreshing(false)
        swipeToRefresh.setEnabled(false)
        coursDBArrayList.addAll(db.courseDao().getAll() as ArrayList<CourseListModel>)

    }


    override fun onResume() {
        super.onResume()
        loadDataFromDB()
    }

    override fun onCourseDownloadAdapterInteractionListener(courseModel: CourseDownloadModel) {
        val intent= Intent(requireActivity(),ChapterActivity::class.java)
        intent.putExtra("courseDownloadModel",courseModel);
        intent.putExtra("firstCourse",coursDBArrayList.get(0));
        startActivity(intent)
    }

    private fun loadDataFromDB() {
        if (coursArrayList.size > 0) {
            coursArrayList.clear()
        }
        coursArrayList.addAll(db.courseDownloadDao().getAll() as ArrayList<CourseDownloadModel>)

        if (coursArrayList.size > 0){
            rvCourseList.visibility = View.VISIBLE
            tvNodata.visibility = View.GONE
            rvCourseList.adapter =
                CourseDownloadAdapter(coursArrayList, this@CoursesDownloadFragment, requireContext())
        }else{
            tvNodata.visibility = View.VISIBLE
            rvCourseList.visibility = View.GONE
        }



    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }



}
