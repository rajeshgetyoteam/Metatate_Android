package com.getyoteam.budamind.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.getyoteam.budamind.R

class LibraryViewPagerAdapter(supportFragmentManager: FragmentManager,context: Context): FragmentStatePagerAdapter(supportFragmentManager) {
    val context=context
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return mFragmentList[0]
            }
            1-> {
                return mFragmentList[1]
            }
            else -> {
                return mFragmentList[2]
            }
        }
    }

    override fun getCount(): Int {
        return 3
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
            1 -> context.getString(R.string.str_moment)
            else -> {
                return context.getString(R.string.str_sounds)
            }
        }
    }

}