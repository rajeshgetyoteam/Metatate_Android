package com.getyoteam.budamind.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.widget.CompoundButton
import com.getyoteam.budamind.Model.ChapterListModel
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.google.firebase.messaging.FirebaseMessaging
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private var isFirtsTime: Boolean = false
    private lateinit var chapterModel: ChapterListModel
    private lateinit var courseModel: CourseListModel
    private lateinit var meditationStateModel: MeditationStateModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        isFirtsTime = MyApplication.prefs!!.isFirstTime!!
        if (intent.extras != null) {
            if (isFirtsTime) {
                if (intent.extras!!.containsKey("chapterModel")) {
                    chapterModel = intent.extras!!.get("chapterModel") as ChapterListModel
                }
                if (intent.extras!!.containsKey("courseModel")) {
                    courseModel = intent.extras!!.get("courseModel") as CourseListModel
                }
                if (intent.extras!!.containsKey("meditationStateModel")) {
                    meditationStateModel =
                        intent.extras!!.get("meditationStateModel") as MeditationStateModel
                }
            }
        }

        tvHeader.setText(getString(R.string.str_notifications))
//        if (isFirtsTime) {
//            ivHeaderLeft.setImageResource(R.drawable.ic_close_white)
//        } else {
//            ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
//        }

//        ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
        ivHeaderLeft.setOnClickListener {
//            if (intent.extras != null) {
//                if (isFirtsTime) {
//                    MyApplication.prefs!!.isFirstTime = false
//                    val intent = Intent(this@NotificationActivity, QuotesActivity::class.java)
//                    intent.putExtra("chapterModel", chapterModel)
//                    intent.putExtra("courseModel", courseModel)
//                    intent.putExtra("meditationStateModel", meditationStateModel)
//                    startActivity(intent)
//                }
//            }
            finish()
        }


        val isPush = MyApplication.prefs!!.isPushNotification
        val isLibraryPush = MyApplication.prefs!!.isLibraryPush
        val isMomentPush = MyApplication.prefs!!.isMomentPush
        val isSoundPush = MyApplication.prefs!!.isSoundPush

        if (isPush!!)
            swPushNoti.isChecked = true
        if (isLibraryPush!!)
            swLibrary.isChecked = true
        if (isMomentPush!!)
            swMoment.isChecked = true
        if (isSoundPush!!)
            swSound.isChecked = true

        swSound.setOnCheckedChangeListener(this)
        swMoment.setOnCheckedChangeListener(this)
        swLibrary.setOnCheckedChangeListener(this)
        swPushNoti.setOnCheckedChangeListener(this)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFirtsTime) MyApplication.prefs!!.isFirstTime = false
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.swLibrary -> {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("NEW_COURSE")
                    MyApplication.prefs!!.isLibraryPush = true
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("NEW_COURSE");
                    MyApplication.prefs!!.isLibraryPush = false
                }
            }
            R.id.swMoment -> {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOMENT")
                    MyApplication.prefs!!.isMomentPush = true
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("NEW_MOMENT");
                    MyApplication.prefs!!.isMomentPush = false
                }
            }
            R.id.swPushNoti -> {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATION")
                    MyApplication.prefs!!.isPushNotification = true
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("PUSH_NOTIFICATION")
                    MyApplication.prefs!!.isPushNotification = false
                }
            }
            R.id.swSound -> {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("NEW_SOUND")
                    MyApplication.prefs!!.isSoundPush = true
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("NEW_SOUND");
                    MyApplication.prefs!!.isSoundPush = false
                }
            }
        }
    }


}
