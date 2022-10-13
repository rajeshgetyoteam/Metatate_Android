package com.getyoteam.budamind.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.getyoteam.budamind.R

class DownloadsViewPagerAdapter(supportFragmentManager: FragmentManager, context: Context): FragmentStatePagerAdapter(supportFragmentManager) {
    val context=context
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return mFragmentList[0]
            }
            else -> {
                return mFragmentList[1]
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.str_corse)
            else -> {
                context.getString(R.string.str_moment)
            }
        }
    }

}