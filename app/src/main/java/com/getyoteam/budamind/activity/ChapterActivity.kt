package com.getyoteam.budamind.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.downloader.*
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.adapter.ChapterAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.testaudioexohls.PlayerExoActivity
import com.getyoteam.budamind.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.activity_chapter.*
import kotlinx.android.synthetic.main.activity_chapter.cvInternetToast
import kotlinx.android.synthetic.main.activity_chapter.swipeToRefresh
import kotlinx.android.synthetic.main.activity_chapter.tvCourseMin
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ChapterActivity : AppCompatActivity(),
    ChapterAdapter.OnChapterAdapterInteractionListener, View.OnClickListener {
    private var isFree: Boolean = true
    private var firstCourseModel: CourseListModel? = null
    private var index: Int? = -1
    private var courseDownload: CourseDownloadModel? = null
    private var courseId: Int? = 0
    private var subTitle: String? = ""
    private var time: String = ""
    private var courseName: String? = ""
    private var imgUrl: String? = ""
    private var courseDownloadModel: CourseDownloadModel? = null
    private var meditationStateModel: MeditationStateModel? = null
    private var downloadPer: Int? = 0
    private var pos: Int = 0
    private var filename: String? = ""
    private var audioPath: String? = ""
    private var downloadId: Int = 0
    private lateinit var chapterArrayList: ArrayList<ChapterListModel>
    private lateinit var chapterPlayedArrayList: ArrayList<ChapterListPlayedModel>
    private lateinit var db: AppDatabase
    private var courseModel: CourseListModel? = null
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    private lateinit var downloadFileModel: DownloadFileModel
    private var downloadFileModelOld: DownloadFileModel? = null
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        if (intent.extras!!.containsKey("courseModel")) {
            courseModel = CourseListModel()
            courseModel = intent.extras!!.get("courseModel") as CourseListModel
            imgUrl = courseModel!!.getBanner()
            courseName = courseModel!!.getCourseName()
            time =
                courseModel!!.getFromMinutes() + " - " + courseModel!!.getToMinutes() + " - Course"
            subTitle = courseModel!!.getDescription()
            courseId = courseModel!!.getCourseId()

            if (courseModel!!.freePaid.equals("Free")) {
                layPrice.visibility = View.GONE
            } else {
                if (courseModel!!.purchased!!) {
                    layPrice.visibility = View.GONE
                } else {
                    if (courseModel!!.coins != null) {
                        val p = Utils.format(courseModel!!.coins!!.toBigInteger())

                        tvPrice.text = p.replace("$", "$" + "CHI")
                    } else {
                        tvPrice.text = "0"
                    }
                }
            }
        } else {
            courseDownloadModel = intent.extras!!.get("courseDownloadModel") as CourseDownloadModel
            imgUrl = courseDownloadModel!!.getBanner()
            tvTryYourFirstCourse.visibility = View.GONE
            courseName = courseDownloadModel!!.getCourseName()
            time =
                courseDownloadModel!!.getFromMinutes() + " - " + courseDownloadModel!!.getToMinutes() + " - Course"
            subTitle = courseDownloadModel!!.getDescription()
            courseId = courseDownloadModel!!.getCourseId()
        }

        if (intent.extras!!.containsKey("firstCourse")) {
            firstCourseModel = intent.extras!!.get("firstCourse") as CourseListModel
        }

        val list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        userId = MyApplication.prefs!!.userId
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        db = AppDatabase.getDatabase(this)

        val image = findViewById<ImageView>(R.id.ivCourseBanner)
        val curveRadius = resources.getDimension(R.dimen._25sdp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            image.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(
                        0,
                        0,
                        view!!.width,
                        (view.height + curveRadius).toInt(),
                        curveRadius
                    )
                }
            }

            image.clipToOutline = true

        }

//        Utils.wanCoinDialog(this,"2")
        Glide.with(this)
            .load(imgUrl)
            .placeholder(ColorDrawable(ContextCompat.getColor(this, R.color.color_blue)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(ivCourseBanner)

        tvTitle.text = courseName
        tvCourseMin.text = time
        tvSubTitle.text = subTitle

        ivBack.setOnClickListener { finish() }
        tvTryYourFirstCourse.setOnClickListener {
            if (index == -111) {
                val intent = Intent(this@ChapterActivity, ChapterActivity::class.java)
                intent.putExtra("courseModel", firstCourseModel);
                startActivity(intent)
                finish()
            } else if (index != -1) {
                onChapterAdapterInteractionListener(chapterArrayList.get(index!!))
            } else {
                onChapterAdapterInteractionListener(chapterArrayList.get(0))
            }
        }
        cvInternetToast.setOnClickListener { cvInternetToast.visibility = View.GONE }
        ivDownload.setOnClickListener(this)
        circleProgressNormal.setOnClickListener(this)
        chapterArrayList = ArrayList<ChapterListModel>()
        chapterPlayedArrayList = ArrayList<ChapterListPlayedModel>()
        rvChapterList!!.layoutManager = GridLayoutManager(this, 5) as RecyclerView.LayoutManager?
        rvChapterList.addItemDecoration(
            GridItemDecoration(
                resources.getDimension(R.dimen._0sdp).toInt(),
                5
            )
        )

//        swipeToRefresh.setOnRefreshListener(this)
        swipeToRefresh.setEnabled(false);


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivDownload -> {
                try {
                    if (courseModel!!.purchased!! || isFree) {
                        courseDownload = db.courseDownloadDao().loadAllByIds(courseId)
                        if (courseDownload != null) {
                            val snackbar = Snackbar.make(
                                parentLayout,
                                getString(R.string.str_delete_all_file),
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setAction(
                                    getString(R.string.str_yes),
                                    object : View.OnClickListener {
                                        override fun onClick(v: View?) {
                                            deleteFiles()
                                        }

                                    })
                            snackbar.view.setOnClickListener {
                                snackbar.dismiss()
                            }
                            snackbar.show();
                        } else {
                            if (checkInternetConnection()) {
                                checkDownloadPermmission()
                            } else {
                                Toast.makeText(
                                    this@ChapterActivity,
                                    getString(R.string.str_check_internet_connection),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } else {
//                    val intent = Intent(this, SubscribeActivity::class.java)
//                    intent.putExtra("isFirstTime", false)
//                    startActivity(intent)
                        purchaseDialogCourse(courseId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            R.id.circleProgressNormal -> {
                PRDownloader.cancel(downloadId)
                circleProgressNormal.visibility = View.INVISIBLE
                ivDownload.visibility = View.VISIBLE
                ivDownload.setImageResource(R.drawable.ic_download)
            }
        }
    }

    private fun checkDownloadPermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@ChapterActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (checkInternetConnection())
                    downloadAudio(chapterArrayList.get(pos))
            } else {
                managePermissions.checkPermissions()
            }
        } else {
            if (checkInternetConnection())
                downloadAudio(chapterArrayList.get(pos))
        }
    }

    private fun deleteFiles() {
        for (i in 0..chapterArrayList.size - 1) {
            val chapterListModel = chapterArrayList.get(i)
            val path = chapterListModel.getAudioUrl()
            if (path != null) {
                filename = path.substring(path.lastIndexOf('/') + 1);
            }
            val downloadFile = db.downloadDao().loadDownloadFile(filename!!)
            if (downloadFile != null) {
                val path = downloadFile.getAudioFilePath() + downloadFile.getFileName()
                val myFile = File(path)
                if (myFile.exists()) {
                    myFile.delete()
                    db.downloadDao().delete(downloadFile)
                }
            }
        }
        db.courseDownloadDao().delete(courseDownload!!)
        ivDownload.setImageResource(R.drawable.ic_download)
        tvAllDownloading.setText(chapterArrayList.size.toString() + " sessions")
    }

    private fun downloadAudio(chapterListModel: ChapterListModel) {

        audioPath = chapterListModel.getAudioUrl()
        if (audioPath != null) {
//            filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1);
            val fileSting = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
            filename = fileSting.replace("%20", " ")
        }

        Log.e("@@@!", filename.toString())
        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
        if (downloadFileModelOld != null) {
//            ivDownload.visibility = View.VISIBLE
//            ivDownload.setImageResource(R.drawable.ic_download_done)
            if (pos != chapterArrayList.size - 1) {
                pos++
                val curProcess = circleProgressNormal.progress
                circleProgressNormal.progress = curProcess + downloadPer!!
                downloadAudio(chapterArrayList.get(pos))
            }
        } else {
            downloadFileModel = DownloadFileModel()
            downloadFileModel.setFileId(chapterListModel.getChapterId())
            downloadFileModel.setFileName(filename!!)
            downloadFileModel.setFileType("audio")
            downloadFileModel.setImageFile("")
            downloadFileModel.setModelName("chapterModel")
            downloadFileModel.setAudioFilePath(getString(R.string.download_path) + packageName + "/")
            downloadFileModel.setMinute("")
            downloadFileModel.setSubTitle("")
            downloadFileModel.setTitle("")
        }


        val dirPath = getApplicationInfo().dataDir
        circleProgressNormal.visibility = View.VISIBLE
        ivDownload.visibility = View.INVISIBLE
        downloadId = PRDownloader.download(audioPath, dirPath, filename)
            .build()
            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
                override fun onStartOrResume() {
                }

            })
            .setOnPauseListener(object : OnPauseListener {
                override fun onPause() {

                }
            })
            .setOnCancelListener(object : OnCancelListener {
                override fun onCancel() {

                }
            })
            .setOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Progress) {
                }
            })
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    Log.e("@@@", error.toString() + "@@@")
                    if (pos != chapterArrayList.size - 1) {
                        pos++
                        downloadAudio(chapterArrayList.get(pos))
                    } else {
                        circleProgressNormal.visibility = View.INVISIBLE
                        ivDownload.visibility = View.VISIBLE
                        ivDownload.setImageResource(R.drawable.ic_download_done)
                    }
                }

                override fun onDownloadComplete() {
                    val curProcess = circleProgressNormal.progress
                    circleProgressNormal.progress = curProcess + downloadPer!!

                    db.downloadDao().insertDownloadFile(downloadFileModel)
                    Log.e("@@@==", downloadFileModel.getFileName().toString())

                    tvAllDownloading.setText("Downloading " + (pos + 1) + " out of " + chapterArrayList.size.toString() + "...")

                    downloadId = 0
                    if (pos != chapterArrayList.size - 1) {
                        pos++
                        downloadAudio(chapterArrayList.get(pos))
                    } else {
                        circleProgressNormal.progress = 100

                        //Todo: after download set course true to display in download
                        courseModel!!.setIsDownloaded(true)
                        courseDownloadModel = CourseDownloadModel()
                        courseDownloadModel!!.setBanner(imgUrl!!)
                        courseDownloadModel!!.setCourseId(courseId)
                        courseDownloadModel!!.setCourseName(courseName!!)
                        courseDownloadModel!!.setDescription(subTitle!!)
                        courseDownloadModel!!.setFromMinutes(courseModel!!.getFromMinutes()!!)
                        courseDownloadModel!!.setToMinutes(courseModel!!.getToMinutes()!!)
                        courseDownloadModel!!.setColorCode(courseModel!!.getColorCode()!!)
                        db.courseDownloadDao().insertAll(courseDownloadModel!!)

                        circleProgressNormal.visibility = View.INVISIBLE
                        ivDownload.visibility = View.VISIBLE
                        ivDownload.setImageResource(R.drawable.ic_download_done)
                        tvAllDownloading.setText(chapterArrayList.size.toString() + " sessions downloaded")
                    }
                }

            })
    }

    override fun onStop() {
        super.onStop()
        PRDownloader.cancel(downloadId)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    downloadAudio(chapterArrayList.get(0))
                    toast("Permissions granted.")
                } else {
                    toast("Permissions denied.")
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        swipeToRefresh.post(object : Runnable {
            override fun run() {
                if (checkInternetConnection()) {
                    getChapterList()
//                    if (courseModel != null) {
//                        getChapterList()
//                    } else {
//                        if (courseDownloadModel != null) {
//                            cvInternetToast.visibility = View.GONE
//                        } else {
//                            cvInternetToast.visibility = View.VISIBLE
//                        }
//                    }
                }
//                loadDataFromDB()
            }
        })

    }

    override fun onChapterAdapterInteractionListener(chapterListModel: ChapterListModel) {
        if (chapterListModel.getFreePaid().equals("paid", ignoreCase = true)) {


            if (chapterListModel.purchased!!) {
                playChapterSong(chapterListModel)
            } else {
                purchaseDialogCourse(courseId)
            }
        } else {
            playChapterSong(chapterListModel)
        }
    }

    fun purchaseDialogCourse(courseId: Int?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_purchase_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
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
            callApiForPurchaseCourse(courseId!!)
            dialog.dismiss()
        }

        tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }

    fun callApiForPurchaseCourse(courseId: Int) {
        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().purchaseCourse(userId, courseId.toString())

        call.enqueue(object : Callback<responcePurchaseModel> {
            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    this@ChapterActivity,
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
                        courseModel!!.purchased = true
                        layPrice.visibility = View.GONE
                        Toast.makeText(
                            this@ChapterActivity,
                            commonModel!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        getChapterList()

                    } else {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                        Toast.makeText(
                            this@ChapterActivity,
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        this@ChapterActivity,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    fun wanCoinDialog(chapterId: Int?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_purchase_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
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
            callApiForPurchase(chapterId!!)
            dialog.dismiss()
        }

        tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }

    fun callApiForPurchase(chapId: Int) {
        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().purchaseChapter(userId, chapId.toString())

        call.enqueue(object : Callback<responcePurchaseModel> {
            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    this@ChapterActivity,
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
                            this@ChapterActivity,
                            commonModel!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        courseModel!!.purchased = true
                        getChapterList()

                    } else {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                        Toast.makeText(
                            this@ChapterActivity,
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        this@ChapterActivity,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }

    private fun playChapterSong(chapterListModel: ChapterListModel) {
        if (courseModel == null) {
            courseModel = CourseListModel()
            courseModel!!.setIsDownloaded(true)
            courseModel!!.setBanner(imgUrl!!)
            courseModel!!.setCourseId(courseId)
            courseModel!!.setCourseName(courseName!!)
            courseModel!!.setDescription(subTitle!!)
            courseModel!!.setFromMinutes(courseDownloadModel!!.getFromMinutes()!!)
            courseModel!!.setToMinutes(courseDownloadModel!!.getToMinutes()!!)
            courseModel!!.setColorCode(courseDownloadModel!!.getColorCode()!!)


//            courseModel!!.setHappiness(courseDownloadModel!!.getHappiness())

            if (courseDownloadModel!!.getHappiness() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getHappiness()!!)
            }
//            courseModel!!.setGratitute(courseDownloadModel!!.getGratitute()!!)

            if (courseDownloadModel!!.getGratitute() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getGratitute()!!)
            }
//            courseModel!!.setMeditate(courseDownloadModel!!.getMeditate()!!)
            if (courseDownloadModel!!.getMeditate() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getMeditate()!!)
            }
//            courseModel!!.setSelfEsteem(courseDownloadModel!!.getSelfEsteem()!!)
            if (courseDownloadModel!!.getSelfEsteem() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getSelfEsteem()!!)
            }

//            courseModel!!.setSleep(courseDownloadModel!!.getSleep()!!)

            if (courseDownloadModel!!.getSleep() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getSleep()!!)
            }

//            courseModel!!.setStress(courseDownloadModel!!.getStress()!!)

            if (courseDownloadModel!!.getStress() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getStress()!!)
            }

            if (courseDownloadModel!!.getFocus() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getFocus()!!)
            }
            if (courseDownloadModel!!.getAnxiety() != null) {
                courseModel!!.setFocus(courseDownloadModel!!.getAnxiety())
            }

        }
        val meditationStateModelList = db.meditationStateDao().getAll()
        if (meditationStateModelList.size > 0) {
            meditationStateModel = meditationStateModelList.get(0)
        }

        val mMyPrefManager = MyPreferenceManager(this)

        val gson = Gson()
        val jsonChapter = gson.toJson(chapterListModel)
        val jsonCourse = gson.toJson(courseModel)
        val jsonMeditation = gson.toJson(meditationStateModel)
        MyApplication.prefs!!.chapterModel = jsonChapter
        MyApplication.prefs!!.courseModel = jsonCourse
        MyApplication.prefs!!.stateModel = jsonMeditation

        mMyPrefManager.chapterModel = jsonChapter
        mMyPrefManager.courseModel = jsonCourse
        mMyPrefManager.meditationState = jsonMeditation

        val intent = Intent(this, PlayerExoActivity::class.java)
//        intent.putExtra("chapterModel", chapterListModel)
//        intent.putExtra("courseModel", courseModel)
//        intent.putExtra("meditationStateModel", meditationStateModel)
        startActivity(intent)


    }

    private fun getChapterList() {
        if (chapterArrayList.size < 1) {
            swipeToRefresh.isRefreshing = true
        }
        val call = ApiUtils.getAPIService().getChapterList(courseId, userId)

        call.enqueue(object : Callback<ChapterResponse> {
            override fun onFailure(call: Call<ChapterResponse>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    this@ChapterActivity,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }


            override fun onResponse(
                call: Call<ChapterResponse>,
                response: Response<ChapterResponse>
            ) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false

                if (response.code() == 200) {
                    val chapterResponse = response.body()!!
                    if (chapterResponse.getStatus().equals(getString(R.string.str_success))) {
                        chapterArrayList =
                            chapterResponse.getChapterList() as ArrayList<ChapterListModel>
                        meditationStateModel = chapterResponse.getMeditationState()
                    }
                    if (chapterArrayList.size > 0) {
                        llDownload.visibility = View.VISIBLE
                        view1.visibility = View.VISIBLE
                        view3.visibility = View.VISIBLE
                        ivDownload.visibility = View.VISIBLE
                        db.chapterDao().deleteAll(courseId!!)
                    }


                    if (chapterArrayList.size > 0)
                        downloadPer = 100 / chapterArrayList.size


                    for (i in 0..chapterArrayList.size - 1) {
                        val chapterListModel = chapterArrayList.get(i)
                        if (courseModel != null) {
                            chapterListModel.setCourseName(courseModel!!.getCourseName().toString())
                        }
                        if (courseDownloadModel != null) {
                            chapterListModel.setCourseName(
                                courseDownloadModel!!.getCourseName().toString()
                            )
                        }

                        if (chapterListModel.getFreePaid().equals("paid", ignoreCase = true)) {
                            isFree = false
                        }
                        try {
                            db.chapterDao().insertAll(chapterListModel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    loadDataFromDB()

                }
            }
        })
    }

    private fun loadDataFromDB() {
        var fileNametemp: String = ""
        var totalChapterDownloaded: Int = 0
        var chapterListSize: Int = 0
        if (chapterArrayList.size > 0) {
            chapterArrayList.clear()
        }
        if (chapterPlayedArrayList.size > 0) {
            chapterPlayedArrayList.clear()
        }
        chapterArrayList.addAll(
            db.chapterDao().loadAllByIds(courseId!!) as ArrayList<ChapterListModel>
        )
        chapterPlayedArrayList.addAll(
            db.chapterPlayedDao().loadAllByIds(courseId!!) as List<ChapterListPlayedModel>
        )

        for (i in 0..chapterArrayList.size - 1) {
            val chapterListModel = chapterArrayList.get(i)
            if (chapterListModel.getFreePaid().equals("paid", ignoreCase = true)) {
                isFree = false
            }
        }

        tvAllDownloading.setText(chapterArrayList.size.toString() + " sessions")


        if (chapterArrayList.size > 0) {
            if (chapterPlayedArrayList.size > 0) {
                tvTryYourFirstCourse.visibility = View.VISIBLE
                tvTryYourFirstCourse.setText(getString(R.string.str_next_session))
                index = chapterPlayedArrayList.size
            } else if (chapterPlayedArrayList.size == chapterArrayList.size) {
                tvTryYourFirstCourse.visibility = View.GONE
                tvTryYourFirstCourse.setText(getString(R.string.str_repeat_course))
                index = 0
            } else if (MyApplication.prefs!!.firstMeditationId > 0) {
                tvTryYourFirstCourse.visibility = View.GONE
                tvTryYourFirstCourse.setText(getString(R.string.str_begin_course))
                index = 0
            } else {
                tvTryYourFirstCourse.visibility = View.GONE

                tvTryYourFirstCourse.setText(getString(R.string.str_try_your_first_course))
                index = chapterPlayedArrayList.size
            }
        }


        val downloadChapterList = ArrayList<DownloadFileModel>()
        downloadChapterList.addAll(db.downloadDao().getAll() as ArrayList<DownloadFileModel>)

        totalChapterDownloaded = 0
        for (i in 0 until chapterArrayList.size) {
            audioPath = chapterArrayList.get(i).getAudioUrl()
            if (audioPath != null) {
                val fileName = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
                fileNametemp = fileName.replace("%20", " ")
            }
            for (j in 0 until downloadChapterList.size) {
                if (fileNametemp.equals(downloadChapterList[j].getFileName(), ignoreCase = true)) {
                    totalChapterDownloaded++
                }
            }
        }

        chapterListSize = chapterArrayList.size

        if (totalChapterDownloaded == chapterListSize) {
            ivDownload.setImageResource(R.drawable.ic_download_done)
            tvAllDownloading.text = chapterArrayList.size.toString() + " sessions downloaded"
        } else {
            ivDownload.setImageResource(R.drawable.ic_download)
        }

        rvChapterList.adapter =
            ChapterAdapter(chapterArrayList, chapterPlayedArrayList, this@ChapterActivity, this)

    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
