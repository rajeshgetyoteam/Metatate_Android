package com.getyoteam.budamind.activity

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.activity_reminder.*
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.getyoteam.budamind.Model.ChapterListModel
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.utils.AlarmReceiver
import com.mindfulness.greece.model.MeditationStateModel
import java.util.*


class ReminderActivity : AppCompatActivity(), View.OnClickListener {

    private var isFirtsTime: Boolean? = false
    private lateinit var meditationStateModel: MeditationStateModel
    private lateinit var chapterModel: ChapterListModel
    private lateinit var courseModel: CourseListModel
    private lateinit var calendar: Calendar
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        isFirtsTime = MyApplication.prefs!!.isFirstTime
        if (intent.extras != null) {
            if (isFirtsTime!!) {
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
                tvCountinue.visibility = View.VISIBLE
            }
        }

        setTime(8, 0)

        tvHeader.setText(getString(R.string.str_mindfulness_reminder))
//        if (isFirtsTime!!) {
//            ivHeaderLeft.setImageResource(R.drawable.ic_close_black)
//        } else {
//            ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
//        }
        tvTime.setOnClickListener(this)
        ivTime.setOnClickListener(this)
        tvCountinue.setOnClickListener(this)
        ivHeaderLeft.setOnClickListener(this)
        val hour = MyApplication.prefs!!.hour
        val minute = MyApplication.prefs!!.minute
        val isRemider = MyApplication.prefs!!.isReminder

        setTime(8, 0)

        if (hour > 0) {
            setTime(hour, minute)
        }

        if (isRemider!!) {
            swSaveReminder.isChecked = true
            tvCountinue.visibility = View.VISIBLE
        }

        swSaveReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvCountinue.visibility = View.VISIBLE
//                MyApplication.prefs!!.isReminder = true
            } else {
                alarmMgr?.cancel(alarmIntent);
                tvCountinue.visibility = View.INVISIBLE
                MyApplication.prefs!!.isReminder = false
            }
        }

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0 or PendingIntent.FLAG_IMMUTABLE)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.extras != null) {
            if (isFirtsTime!!) {
                MyApplication.prefs!!.isFirstTime = false
                val intent = Intent(this@ReminderActivity, QuotesActivity::class.java)
                intent.putExtra("chapterModel", chapterModel)
                intent.putExtra("courseModel", courseModel)
                intent.putExtra("meditationStateModel", meditationStateModel)
                startActivity(intent)
            }
        }
        finish()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvTime -> {
                loadTimePickerDialog()
            }
            R.id.ivTime -> {
                loadTimePickerDialog()
            }
            R.id.ivHeaderLeft -> {
                val isFirtsTime = MyApplication.prefs!!.isFirstTime
                if (intent.extras != null) {
                    if (isFirtsTime!!) {
                        val intent = Intent(this@ReminderActivity, QuotesActivity::class.java)
                        intent.putExtra("chapterModel", chapterModel)
                        intent.putExtra("courseModel", courseModel)
                        intent.putExtra("meditationStateModel", meditationStateModel)
                        startActivity(intent)
                    }
                }
                finish()
            }
            R.id.tvCountinue -> {
                MyApplication.prefs!!.isReminder = true

                    alarmMgr?.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent
                    )

//                alarmMgr!!.set(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(),
//                    alarmIntent);

//                alarmMgr?.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis+milisec.toLong(), milisec.toLong(), alarmIntent)
                    Toast.makeText(this, "Metatate Reminder Set.", Toast.LENGTH_SHORT).show()

                Log.d("Reminder",calendar.getTimeInMillis().toString())
//                    Toast.makeText(this, "Midfulness reminder canceled.", Toast.LENGTH_SHORT).show()

                if (intent.extras != null) {
                    if (MyApplication.prefs!!.isFirstTime!!) {
                        val intent = Intent(this@ReminderActivity, QuotesActivity::class.java)
                        intent.putExtra("chapterModel", chapterModel)
                        intent.putExtra("courseModel", courseModel)
                        intent.putExtra("meditationStateModel", meditationStateModel)
                        startActivity(intent)
                    }
                    finish()
                }
                finish()
            }
        }
    }

    private fun loadTimePickerDialog() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val ampm = mcurrentTime.get(Calendar.AM_PM)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(
            this@ReminderActivity,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                MyApplication.prefs!!.hour = selectedHour
                MyApplication.prefs!!.minute = selectedMinute

                setTime(selectedHour, selectedMinute)

            }, hour, minute, false
        )//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()

    }

    private fun setTime(selectedHour: Int, selectedMinute: Int) {
        calendar = Calendar.getInstance()
        var date = Date()
//        calendar.add(Calendar.DATE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)


//        calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
//        calendar.set(Calendar.MINUTE, selectedMinute)

        var hour = selectedHour
        val minute = selectedMinute
        var timeSet = ""
        if (hour > 12) {
            hour -= 12
            timeSet = "PM"
        } else if (hour == 0) {
            hour += 12
            timeSet = "AM"
        } else if (hour == 12) {
            timeSet = "PM"
        } else {
            timeSet = "AM"
        }

        var min = ""
        if (minute < 10)
            min = "0$minute"
        else
            min = minute.toString()
        // Append in a StringBuilder
        val aTime = StringBuilder().append(hour).append(':')
            .append(min).append(" ").append(timeSet).toString()
        tvTime.setText(aTime)
    }

}
