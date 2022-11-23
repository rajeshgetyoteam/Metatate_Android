package com.getyoteam.budamind.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.activity_quotes.*

import android.view.ViewGroup
import android.widget.TextView
import com.mindfulness.greece.model.MeditationStateModel
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler

import androidx.core.content.ContextCompat
import android.util.Log
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class QuotesActivity : AppCompatActivity() {
    private var authToken: String? = ""
    private lateinit var userId: String
    var currentPage: Int = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 300;//delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000; // time in milliseconds between successive task executions.

    private lateinit var downloadModel: DownloadFileModel
    private var imgUrl: String? = ""
    private var chapterModel: ChapterListModel? = null
    private var strQuote: String? = ""
    private var meditationStateModel: MeditationStateModel? = null
    private lateinit var courseModel: CourseListModel
    private lateinit var momentModel: MomentListModel
    private var stringArray: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quotes)
        val density = resources.displayMetrics.density
//        indicator.setRadius(5 * density)
        meditationStateModel = MeditationStateModel()

//        if (!MyApplication.prefs!!.subPurchase) {
//            tvHome.setText(getString(R.string.str_subscribe_clarity))
//        }
        authToken = MyApplication.prefs!!.authToken
        userId = MyApplication.prefs!!.userId

        stringArray = ArrayList<String>()
//        getQuote()
        if (intent.extras!!.containsKey("momentModel")) {
            momentModel = intent.extras!!.get("momentModel") as MomentListModel
            imgUrl = momentModel.getImage()
        } else if (intent.extras!!.containsKey("chapterModel")) {
            chapterModel = intent.extras!!.get("chapterModel") as ChapterListModel
            courseModel = intent.extras!!.get("courseModel") as CourseListModel
            imgUrl = courseModel.getBanner()

        } else if (intent.extras!!.containsKey("downloadModel")) {
            downloadModel = intent.extras!!.get("downloadModel") as DownloadFileModel
//            strQuote = downloadModel.getQuote()
            imgUrl = downloadModel.getImageFile()
        }
        if (intent.extras!!.containsKey("meditationStateModel") && intent.extras!!.get("meditationStateModel") != null) {
            meditationStateModel = intent.extras!!.get("meditationStateModel") as MeditationStateModel
        }

        val image = findViewById<ImageView>(R.id.ivBanner)
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

        getCountDetail()

        Glide.with(this)
            .load(imgUrl)
            .placeholder(ColorDrawable(ContextCompat.getColor(this, R.color.color_blue)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivBanner)




//        tvHome.setOnClickListener {
//            if (MyApplication.prefs!!.subPurchase) {
//                val intent = Intent(this@QuotesActivity, MainActivity::class.java)
//                intent.clearTop()
//                intent.clearTask()
//                startActivity(intent)
//                finish()
//            } else {
//                val intent = Intent(this@QuotesActivity, SubscribeActivity::class.java)
//                intent.putExtra("fromScreen", "Home")
//                startActivity(intent)
//                finish()
//            }
//        }

        fbShare.setOnClickListener {
            val intent = Intent(this@QuotesActivity, ShareQuoteActivity::class.java)
            intent.putExtra("img", imgUrl)
            intent.putExtra("quote", strQuote)
            startActivity(intent)
            finish()
        }
        ivClose.setOnClickListener { finish() }

        val handler = Handler()
        val update = Runnable {
            viewPager.setCurrentItem(currentPage % 2, true);
            currentPage++
        }

        timer = Timer()// This will create a new Thread
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }

        }, DELAY_MS, PERIOD_MS)

        tvQuote.setText(MyApplication.prefs!!.songQuote)
        strQuote = tvQuote.text.toString()
    }

    private fun getQuote() {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(API::class.java)
        val call = ApiUtils.getAPIService().getRandomQuotes()

        call.enqueue(object : Callback<QuoteModel> {
            override fun onFailure(call: Call<QuoteModel>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<QuoteModel>,
                response: Response<QuoteModel>
            ) {
                if (response.code() == 200) {
                    val quoteModel = response.body()!!
                    if (quoteModel.getStatus().equals(
                            getString(R.string.str_success),
                            ignoreCase = true
                        )
                    ) {
                        strQuote = quoteModel.getQuote()
                        Log.e("quoteModel", strQuote.toString())
                        tvQuote.setText(strQuote)
                    }
                }
            }
        })
    }

    private fun getCountDetail() {

        val call = ApiUtils.getAPIService().getHomeDetail(authToken!!, userId)

        call.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                if (meditationStateModel != null) {
                    viewPager.setAdapter(MyPagesAdapter(applicationContext, meditationStateModel!!))
                    view_pager_indicator.setupWithViewPager(viewPager)
                }
            }

            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.code() == 200) {
                        MyApplication.isHomeAPI = false

                        val homeResponse = response.body()!!
                        if (homeResponse.getStatus().equals(getString(R.string.str_success))) {

                            val totalWeeklyMin = homeResponse.getProfile()?.getWeeklyMinuteMeditate()
                            val totalDailyMin = homeResponse.getProfile()?.getWeeklyMinuteMeditate()
                            val totalMeditateMinute = homeResponse.getProfile()?.getMinuteMeditate()
                            MyApplication.prefs!!.weeklyMinute  = totalWeeklyMin!!.toFloat()
                            MyApplication.prefs!!.dailyMinute  = totalDailyMin!!.toFloat()
                            MyApplication.prefs!!.totalMeditateMinute  = totalMeditateMinute!!.toFloat()

                            if (meditationStateModel != null) {
                                viewPager.setAdapter(MyPagesAdapter(applicationContext, meditationStateModel!!))
                                view_pager_indicator.setupWithViewPager(viewPager)
                            }

                    }else{
                            if (meditationStateModel != null) {
                                viewPager.setAdapter(MyPagesAdapter(applicationContext, meditationStateModel!!))
                                view_pager_indicator.setupWithViewPager(viewPager)
                            }
                    }
                }
            }
        })
    }



    class MyPagesAdapter(
        applicationContext: Context,
        meditationState: MeditationStateModel
    ) : PagerAdapter() {
        val context = applicationContext
        val meditationStateModel = meditationState
        override fun isViewFromObject(p0: View, p1: Any): Boolean {
            return p0 == p1 as View;
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.raw_quotes, null)
            val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
            val tvValye = view.findViewById<TextView>(R.id.tvValue)
            if (position == 0) {
                tvHeader.setText(context.getString(R.string.str_minutes_meditated))
                val count:Double = String.format("%.2f", MyApplication.prefs!!.totalMeditateMinute).toDouble()
                tvValye.setText(count.toString())
            } else {
                tvHeader.setText(context.getString(R.string.str_current_streak))
                tvValye.setText(meditationStateModel.getCurrentStreak().toString())
            }

            (container as ViewPager).addView(view, 0)
            return view
        }

        override fun getCount(): Int {
            return 2
        }
    }
}
