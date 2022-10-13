package com.getyoteam.budamind.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.getyoteam.budamind.R
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.viewpager.widget.ViewPager
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.adapter.DownloadsViewPagerAdapter
import com.getyoteam.budamind.adapter.LibraryViewPagerAdapter
import com.getyoteam.budamind.fragment.CoursesDownloadFragment
import com.getyoteam.budamind.fragment.MomentsDownloadFragment
import com.getyoteam.budamind.utils.AppDatabase
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_download.*


class DownloadActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        db = AppDatabase.getDatabase(this)

        setupViewPager(viewpager)
        tabs?.setupWithViewPager(viewpager)
        tvHeader.text = getText(R.string.str_downloads)
//        ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
        val typefaceBold = Typeface.createFromAsset(assets, "nunitosans_bold.ttf")
        val typefaceRegular = Typeface.createFromAsset(assets, "nunitosans_semibold.ttf")

        val vg = tabs.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (i in 0 until tabsCount) {
            val tv = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
            if (i == 0) {
                tv.typeface = typefaceBold
                tv.setTextColor(ContextCompat.getColor(this, R.color.app_pink_color))
            } else {
                tv.typeface = typefaceRegular
                tv.setTextColor(ContextCompat.getColor(this, R.color.color_black))
            }
            tabs.getTabAt(i)?.customView = tv
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab?.customView as TextView
                view.typeface = typefaceRegular
                view.setTextColor(ContextCompat.getColor(this@DownloadActivity, R.color.color_black))
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val view = tab?.customView as TextView
                view.typeface = typefaceBold
                view.setTextColor(ContextCompat.getColor(this@DownloadActivity, R.color.app_pink_color))
            }

        })

        ivHeaderLeft.setOnClickListener {
            finish()
        }

        cwAutoDownload.setOnCheckedChangeListener(this)
        val autoDownload = MyApplication.prefs!!.autoDownload
        cwAutoDownload.isChecked=autoDownload!!
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        MyApplication.prefs!!.autoDownload = isChecked
    }

    private fun createCustomFontTextViewForTab(): AppCompatTextView {
        val customFontTextView = AppCompatTextView(this)
        customFontTextView.gravity = Gravity.CENTER
        TextViewCompat.setTextAppearance(customFontTextView, R.style.TabTextAppearance)
        return customFontTextView
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = DownloadsViewPagerAdapter(
            supportFragmentManager, this)
        adapter.addFragment(CoursesDownloadFragment(), "Courses")
        adapter.addFragment(MomentsDownloadFragment(), "Moments")
        viewPager.adapter = adapter
    }

}