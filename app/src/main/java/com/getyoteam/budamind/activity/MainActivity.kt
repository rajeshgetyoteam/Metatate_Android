package com.getyoteam.budamind.activity

import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.Prefs
import com.getyoteam.budamind.R
import com.getyoteam.budamind.fragment.*
import com.getyoteam.budamind.utils.NotificationUtils
import com.getyoteam.budamind.utils.RingtonePlayingService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var alertDialog: AlertDialog? = null
    var prefs: Prefs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs(this)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1000)
        val stopIntent = Intent(this, RingtonePlayingService::class.java)
        stopService(stopIntent)
        val bundle = intent.extras
        if (bundle != null) {
            try {
                Log.d("Notifi action", bundle.get("click_action").toString())
                prefs!!.clickaction = bundle.get("click_action").toString()
                Log.d("Not", bundle.toString())
                //bundle must contain all info sent in "data" field of the notification
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val currunt = prefs!!.clickaction
        if (currunt.equals("COURSE")) {
            changeFragment(LibraryFragment(), 2)
            navigation.menu.getItem(1).isChecked = true
        } else if (currunt.equals("MOMENT")) {
            changeFragment(LibraryFragment(), 2)
            navigation.menu.getItem(1).isChecked = true
        } else if (currunt.equals("SOUND")) {
            changeFragment(LibraryFragment(), 2)
            navigation.menu.getItem(1).isChecked = true
        } else {
            changeFragment(HomeFragment(), 1)
            navigation.menu.getItem(0).isChecked = true
        }
        showDialog()

    }

    fun changeFragment(currentFragment: Fragment, curFragmentPos: Int) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        if (manager.backStackEntryCount > 0) {
            manager.popBackStack()
        }
        transaction.replace(R.id.fragment_container, currentFragment)
        transaction.commit()
    }


    fun ChangeToTask() {
        changeFragment(TaskFragmentNew(), 3)
        navigation.menu.getItem(2).isChecked = true
    }

    override fun onBackPressed() {
        alertDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
//        val jsonObj = JSONObject()
//        jsonObj.put("userId", MyApplication.prefs!!!!.userId)
//        jsonObj.put("totalMinutes",MyApplication.prefs!!!!.totTimeSpent)
//        Utils.callUpdateTimeSpentSeconds(jsonObj)

    }

    private fun showDialog() {
        // Late initialize an alert dialog object
        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)
        // Set a title for alert dialog
        builder.setTitle("Metatate")
        // Set a message for alert dialog
        builder.setMessage("Would you like to close this application?")
        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    finish()
                    MyApplication.isSoundAPI = true
                    MyApplication.isHomeAPI = true
                    MyApplication.isLibraryAPI = true
                    MyApplication.isProfileAPI = true
                    alertDialog!!.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> alertDialog!!.dismiss()
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Yes", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button

        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

    }

    override fun onResume() {
        super.onResume()
        NotificationUtils(this).clearNotifications(applicationContext)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    changeFragment(HomeFragment(), 1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_library -> {
                    prefs!!.clickaction = "COURSE"
                    changeFragment(LibraryFragment(), 2)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_track -> {
                    changeFragment(TaskFragmentNew(), 3)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_catalog -> {
                    changeFragment(CatalogFragment(), 4)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_profile -> {
                    changeFragment(ProfileFragment(), 5)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }
}
