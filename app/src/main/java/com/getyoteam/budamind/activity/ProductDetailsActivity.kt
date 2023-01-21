package com.getyoteam.budamind.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.getyoteam.budamind.Model.ProductListModel
import com.getyoteam.budamind.Model.SizeListModel
import com.getyoteam.budamind.R
import com.getyoteam.budamind.adapter.ImageListAdapter
import com.getyoteam.budamind.adapter.SizeListAdapter
import com.getyoteam.budamind.fragment.ImageFragment
import kotlinx.android.synthetic.main.activity_product_details.*


class ProductDetailsActivity : AppCompatActivity() ,ImageListAdapter.ItemClickListener,SizeListAdapter.ItemClickListener{

    private var productList: ArrayList<ProductListModel>? = null
    private var sizeList: ArrayList<SizeListModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productList = ArrayList()
        sizeList = ArrayList()

        val s1 = SizeListModel()
        s1.productId = "0"
        s1.productName = "6"
        sizeList!!.add(s1)

        val s2 = SizeListModel()
        s2.productId = "1"
        s2.productName = "7"
        sizeList!!.add(s2)

        val s3 = SizeListModel()
        s3.productId = "2"
        s3.productName = "8"
        sizeList!!.add(s3)

        val s4 = SizeListModel()
        s4.productId = "3"
        s4.productName = "9"
        sizeList!!.add(s4)

        val p1 = ProductListModel()
        p1.productId = "0"
        p1.productName = "Adidas"
        p1.productImage = R.drawable.s1

        productList!!.add(p1)

        val p2 = ProductListModel()
        p2.productId = "1"
        p2.productName = "Puma"
        p2.productImage = R.drawable.s2
        productList!!.add(p2)

        val p3 = ProductListModel()
        p3.productId = "2"
        p3.productName = "Nike"
        p3.productImage = R.drawable.s3
        productList!!.add(p3)


        val p4 = ProductListModel()
        p4.productId = "3"
        p4.productName = "Campus"
        p4.productImage = R.drawable.s4
        productList!!.add(p4)


        val p5 = ProductListModel()
        p5.productId = "4"
        p5.productName = "Footox"
        p5.productImage = R.drawable.s5
        productList!!.add(p5)

        val p6 = ProductListModel()
        p6.productId = "5"
        p6.productName = "Reebok"
        p6.productImage = R.drawable.s6
        productList!!.add(p6)

        val p7 = ProductListModel()
        p7.productId = "6"
        p7.productName = "Sparx"
        p7.productImage = R.drawable.s7
        productList!!.add(p7)

        val vpAdapter = ViewPagerAdapter(supportFragmentManager)
        for (i in 0 until productList!!.size){
            vpAdapter.addFrag(ImageFragment(productList!![i].productImage), "")
        }

        ivImagies.adapter = ImageListAdapter(this,productList,this,0)

        vpProductGuide.adapter = vpAdapter
        worm_dots_indicator.setViewPager(vpProductGuide)


        rvSize.adapter = SizeListAdapter(this,sizeList,this,0)
        ivHeaderLeft.setOnClickListener {
            finish()
        }

    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {

        private val mFragmentList = java.util.ArrayList<Fragment>()
        private val mFragmentTitleList = java.util.ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(
            fragment: Fragment,
            title: String
        ) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return null
        }
    }

    override fun itemCategoryClick(time: String) {
        TODO("Not yet implemented")
    }
}