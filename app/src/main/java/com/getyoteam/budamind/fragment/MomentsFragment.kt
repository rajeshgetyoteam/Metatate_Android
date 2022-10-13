package com.getyoteam.budamind.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.Model.LibraryModel
import com.getyoteam.budamind.Model.MomentListModel
import com.getyoteam.budamind.MyApplication

import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.PlayActivity
import com.getyoteam.budamind.adapter.MomentAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.getyoteam.budamind.interfaces.CustomTouchListener
import com.getyoteam.budamind.interfaces.onItemClickListener
import com.getyoteam.budamind.utils.AppDatabase
import com.facebook.login.LoginManager
import com.getyoteam.budamind.Model.responcePurchaseModel
import com.getyoteam.budamind.activity.SignInActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.fragment_moments.*
import kotlinx.android.synthetic.main.fragment_moments.cvInternetToast
import kotlinx.android.synthetic.main.fragment_moments.swipeToRefresh
import kotlinx.android.synthetic.main.fragment_sound.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MomentsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class MomentsFragment : Fragment(),
    MomentAdapter.OnMomentAdapterInteractionListener ,SwipeRefreshLayout.OnRefreshListener{


    private var authToken: String? = ""
    private var meditationStateModel: MeditationStateModel? = null
    private var listener: OnFragmentInteractionListener? = null

    private var userId: String = ""
    private lateinit var db: AppDatabase
    private lateinit var momentArrayList: ArrayList<MomentListModel>
    lateinit var mGoogleApiClient: GoogleApiClient

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
        authToken = MyApplication.prefs!!.authToken
        db = AppDatabase.getDatabase(requireContext())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient.connect()


        val c1 = ContextCompat.getColor(requireContext(),R.color.app_pink_color)
        swipeToRefresh.setColorSchemeColors(c1)

        momentArrayList = ArrayList<MomentListModel>()
        rvMomentList!!.layoutManager = LinearLayoutManager(requireContext())
        swipeToRefresh.setOnRefreshListener(this)
//        swipeToRefresh.setEnabled(false);

        rvMomentList.addOnItemTouchListener(object : CustomTouchListener(requireContext(), object :
            onItemClickListener {
            override fun onClick(view: View?, index: Int) {
                val momentListModel = momentArrayList.get(index)
                if (momentListModel.getFreePaid().equals("Free", ignoreCase = true)) {
                    loadMoment(index, momentListModel)
                } else {


                    if (momentListModel.purchased!!) {
                        loadMoment(index, momentListModel)
                    } else {
                        wanCoinDialog(momentListModel.getMomentId())
                    }
                }
            }
        }) {})

        cvInternetToast.setOnClickListener {
            cvInternetToast.visibility = View.GONE
        }

    }
    fun wanCoinDialog(momentId: Int?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_purchase_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                android.R.color.transparent
            )
        )
        val close: ImageView
        val tvYes: TextView
        val tvNo: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvYes = dialog.findViewById(R.id.tvyes)
        tvNo = dialog.findViewById(R.id.tvNo)
        close.setOnClickListener {
            dialog.dismiss()
        }

        tvYes.setOnClickListener {
            callApiForPurchase(momentId!!)
            dialog.dismiss()
        }

        tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }
    private fun loadMoment(index: Int, momentListModel: MomentListModel) {
        val gson = Gson()
        val jsonMoment = gson.toJson(momentListModel)
        val jsonMeditation = gson.toJson(meditationStateModel)
        MyApplication.prefs!!.momentModel = jsonMoment
        MyApplication.prefs!!.stateModel = jsonMeditation
        val intent = Intent(requireContext(), PlayActivity::class.java)
        intent.putExtra("m", "")
        startActivity(intent)
    }
    fun callApiForPurchase(momentId: Int) {
        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().purchaseMoments(userId, momentId.toString())

        call.enqueue(object : Callback<responcePurchaseModel> {
            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<responcePurchaseModel>,
                response: Response<responcePurchaseModel>
            ) {
                if (response.code() == 200) {

                    val commonModel = response.body()
                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
                        Toast.makeText(
                            requireContext(),
                            commonModel!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        getMomentDetail()

                    } else {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }
    override fun onRefresh() {
        if (isAdded()) {
            if (checkInternetConnection())
                getMomentDetail()
            else
                cvInternetToast.visibility = View.VISIBLE
        }
    }

    override fun onMomentAdapterInteractionListener(courseModel: CourseListModel, wantToDelete: Boolean) {
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDB()

//        swipeToRefresh.post(object : Runnable {
//            override fun run() {
//                if (isAdded()) {
//                    if (checkInternetConnection())
//                        getMomentDetail()
//                    else
//                        cvInternetToast.visibility = View.VISIBLE
//                }
//
//            }
//        })
    }



    private fun getMomentDetail() {
        swipeToRefresh.setRefreshing(true);
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(ClarityAPI::class.java)
        val call = ApiUtils.getAPIService().getLibraryList(authToken!!,userId)

        call.enqueue(object : Callback<LibraryModel> {
            override fun onFailure(call: Call<LibraryModel>, t: Throwable) {
                if (isAdded()) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.setRefreshing(false);

                    Toast.makeText(activity, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call<LibraryModel>, response: Response<LibraryModel>) {
                if (response.code() == 200) {
                    if (isAdded()) {
                        if (swipeToRefresh != null) {
                            swipeToRefresh.setRefreshing(false);

                            val libraryModel = response.body()!!
                            if (libraryModel.getStatus().equals(getString(R.string.str_success))) {
                                momentArrayList = libraryModel.getMomentList() as ArrayList<MomentListModel>
                                meditationStateModel = libraryModel.getMeditationState()

                                if (momentArrayList.size > 0) {
                                    db.momentDao().deleteAll()
                                }


                                for (i in 0..momentArrayList.size - 1) {
                                    val momentListModel = momentArrayList.get(i)
                                    db.momentDao().insertAll(momentListModel)
                                }

                                loadDataFromDB()
                            }else if(libraryModel.getStatus().equals(getString(R.string.str_invalid_token),ignoreCase = true)){
                                logOutAfterDialog()
                            } else {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.str_something_went_wrong),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun signOut() {
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun logOutAfterDialog() {
        MyApplication.prefs?.userId = ""
        MyApplication.isSoundAPI = true
        MyApplication.isHomeAPI = true
        MyApplication.isLibraryAPI = true
        MyApplication.isProfileAPI = true
        MyApplication.prefs!!.profilePic = ""
        MyApplication.prefs!!.first_name = ""
        MyApplication.prefs!!.last_name = ""

        db.meditationStateDao().deleteAll()
        db.chapterPlayedDao().deleteAllData()
        db.goalDao().deleteAll()

        val loginThrough = MyApplication.prefs!!.loginType.toString()

        if (loginThrough.equals("Facebook", ignoreCase = true)) {
            LoginManager.getInstance().logOut()
            signOut()
        } else if (loginThrough.equals("gmail", ignoreCase = true)) {
            googleSignOut()
        } else {
            signOut()
        }
        requireActivity().finish()
    }

    private fun googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            object : ResultCallback<Status> {
                override fun onResult(p0: Status) {
                    signOut()
                }
            })
    }

    private fun loadDataFromDB() {
        if (momentArrayList.size > 0) {
            momentArrayList.clear()
        }
        momentArrayList.addAll(db.momentDao().getAll() as ArrayList<MomentListModel>)

        rvMomentList.adapter =
            MomentAdapter(momentArrayList, this@MomentsFragment, requireContext())

    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
