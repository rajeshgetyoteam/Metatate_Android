package com.getyoteam.budamind.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.utils.Utils
import kotlinx.android.synthetic.main.activity_choose_your_goal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ChooseYourGoalActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private var isFirstTime: Boolean = false
    private var wallateStatus: String = ""
    private var tokenCredited: String = ""
    private lateinit var goalArrayList: ArrayList<GoalModel>

    //    private lateinit var db: AppDatabase
    private var totalGoal: Int = 0
//    private var goalArray: ArrayList<GoalModel> = ArrayList<GoalModel>()

    //    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_your_goal)
//        db = AppDatabase.getDatabase(this)
        isFirstTime = intent.getBooleanExtra("isFirstTime", false)
        wallateStatus = intent.getStringExtra("wallateStatus").toString()
        tokenCredited = intent.getStringExtra("credited").toString()
//        totalGoal = MyApplication.prefs!!!!.totalGoal
//        wanCoinDialog("1")

        if (isFirstTime) {
            tvSkip.visibility = View.VISIBLE
//            tvCountinue.text = getString(R.string.str_continue)
        } else {
            tvSkip.visibility = View.GONE
//            tvCountinue.text = getString(R.string.str_save)
        }
        tvCountinue.text = getString(R.string.str_continue)
//        goalArrayList = db.goalDao().getAll() as ArrayList<GoalModel>

//        if (goalArrayList.size > 0) {
//            for (i in 0..goalArrayList.size - 1) {
//                if (1 == goalArrayList.get(i).getAnxiety()) {
//                    cbReduceAnxiety.isChecked = true
//                    cvReduceAnxiety.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivReduceAnxiety.setBackgroundColor(resources.getColor(R.color.color_card_7eb1e2))
//                    flReduceAnxiety.visibility = View.VISIBLE
//                } else if (1 == goalArrayList.get(i).getFocus()) {
//                    cbImproveFocus.isChecked = true
//                    cvImproveFocus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    flImproveFocus.visibility = View.VISIBLE
//                    ivImproveFocus.setBackgroundColor(resources.getColor(R.color.color_card_ebb96b))
//                } else if (1 == goalArrayList.get(i).getGratitute()) {
//                    cbDevelopGratitute.isChecked = true
//                    cvDevelopGratitute.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivDevelopGratitute.setBackgroundColor(resources.getColor(R.color.color_card_ec8c8c))
//                    flDevelopGratitute.visibility = View.VISIBLE
//                } else if (1 == goalArrayList.get(i).getHappiness()) {
//                    cbIncreaseHappiness.isChecked = true
//                    cvIncreaseHappiness.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivIncreaseHappiness.setBackgroundColor(resources.getColor(R.color.color_card_7eb1e2))
//                    flIncreaseHappiness.visibility = View.VISIBLE
//                } else if (1 == goalArrayList.get(i).getMeditate()) {
//                    cbLearnToMeditate.isChecked = true
//                    cvLearnToMeditate.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivLearnToMeditate.setBackgroundColor(resources.getColor(R.color.color_card_7eb1e2))
//                    flLearnToMeditate.visibility = View.VISIBLE
//                } else if (1 == goalArrayList.get(i).getSelfEsteem()) {
//                    cbBuildSelfEsteem.isChecked = true
//                    cvBuildSelfEsteem.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivBuildSelfEsteem.setBackgroundColor(resources.getColor(R.color.color_card_ebb96b))
//                    flBuildSelfEsteem.visibility = View.VISIBLE
//                } else if (1 == goalArrayList.get(i).getSleep()) {
//                    cbBetterSleep.isChecked = true
//                    cvBetterSleep.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    flBetterSleep.visibility = View.VISIBLE
//                    ivBetterSleep.setBackgroundColor(resources.getColor(R.color.color_card_ec8c8c))
//                } else if (1 == goalArrayList.get(i).getStress()) {
//                    cbReduceStress.isChecked = true
//                    cvReduceStress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
//                    ivReduceStress.setBackgroundColor(resources.getColor(R.color.color_card_ec8c8c))
//                    flReduceStress.visibility = View.VISIBLE
//                }
//            }
//        }


        if (1 == MyApplication.prefs!!!!.anxiety) {
            cbReduceAnxiety.isChecked = true
            cvReduceAnxiety.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivReduceAnxiety.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flReduceAnxiety.visibility = View.VISIBLE
        }

        if (1 == MyApplication.prefs!!!!.focus) {
            cbImproveFocus.isChecked = true
            cvImproveFocus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            flImproveFocus.visibility = View.VISIBLE
            ivImproveFocus.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }

        if (1 == MyApplication.prefs!!!!.gratitute) {
            cbDevelopGratitute.isChecked = true
            cvDevelopGratitute.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivDevelopGratitute.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flDevelopGratitute.visibility = View.VISIBLE
        }
        if (1 == MyApplication.prefs!!!!.happiness) {
            cbIncreaseHappiness.isChecked = true
            cvIncreaseHappiness.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivIncreaseHappiness.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flIncreaseHappiness.visibility = View.VISIBLE
        }
        if (1 == MyApplication.prefs!!!!.meditate) {
            cbLearnToMeditate.isChecked = true
            cvLearnToMeditate.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivLearnToMeditate.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flLearnToMeditate.visibility = View.VISIBLE
        }
        if (1 == MyApplication.prefs!!!!.selfEsteem) {
            cbBuildSelfEsteem.isChecked = true
            cvBuildSelfEsteem.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivBuildSelfEsteem.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flBuildSelfEsteem.visibility = View.VISIBLE
        }
        if (1 == MyApplication.prefs!!!!.sleep) {
            cbBetterSleep.isChecked = true
            cvBetterSleep.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            flBetterSleep.visibility = View.VISIBLE
            ivBetterSleep.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }
        if (1 == MyApplication.prefs!!!!.stress) {
            cbReduceStress.isChecked = true
            cvReduceStress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
            ivReduceStress.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            flReduceStress.visibility = View.VISIBLE
        }

        cbReduceStress.setOnCheckedChangeListener(this)
        cbBetterSleep.setOnCheckedChangeListener(this)
        cbBuildSelfEsteem.setOnCheckedChangeListener(this)
        cbDevelopGratitute.setOnCheckedChangeListener(this)
        cbImproveFocus.setOnCheckedChangeListener(this)
        cbIncreaseHappiness.setOnCheckedChangeListener(this)
        cbLearnToMeditate.setOnCheckedChangeListener(this)
        cbReduceAnxiety.setOnCheckedChangeListener(this)

        tvCountinue.setOnClickListener {
//            MyApplication.prefs!!!!.totalGoal = totalGoal
//            db.goalDao().deleteAll()
//            for (i in 0..goalArray.size - 1) {
//                db.goalDao().insertAll(goalArray.get(i))
//            }
            updateGoals()
        }
        tvSkip.setOnClickListener {
            if (wallateStatus.equals("created")) {
                wanCoinDialog(Utils.format(tokenCredited.toBigInteger()))
            } else {
                MyApplication.prefs!!!!.isFirstApp = false
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun updateGoals() {

        progressContinue.visibility = View.VISIBLE

        val dataModel = SetGoalModel(MyApplication.prefs!!.userId.toInt(),MyApplication.prefs!!.anxiety,MyApplication.prefs!!.focus,
            MyApplication.prefs!!.gratitute,MyApplication.prefs!!.happiness,MyApplication.prefs!!.meditate
            ,MyApplication.prefs!!.selfEsteem,MyApplication.prefs!!.sleep,MyApplication.prefs!!.stress
        )
        val call = ApiUtils.getAPIService().setGoal(dataModel)

        call.enqueue(object : Callback<responceSetGoalModel> {
            override fun onFailure(call: Call<responceSetGoalModel>, t: Throwable) {
                progressContinue.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<responceSetGoalModel>, response: Response<responceSetGoalModel>) {
                if (response.code() == 200) {
                    progressContinue.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.status.equals(getString(R.string.str_success))) {
                        Toast.makeText(
                            applicationContext,
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (wallateStatus.equals("created")) {

                            wanCoinDialog(Utils.format(tokenCredited.toBigInteger()))
                        } else {
                            if (isFirstTime) {
                                MyApplication.prefs!!!!.isFirstApp = false
                                val intent = Intent(this@ChooseYourGoalActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                finish()
                            }

                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }


    fun wanCoinDialog(coin: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_congratulation_view)
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
        val tvCoin: TextView
        val tvWallate: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvCoin = dialog.findViewById(R.id.tvCoin)
        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
        tvCoin.text = "You have won " + coin.replace("$","$"+"CHI")
        close.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
            finish()
        }

        tvWallate.setOnClickListener {
            val intent = Intent(applicationContext, WalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)

            finish()

        }
        dialog.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when (buttonView?.id) {
            R.id.cbReduceStress -> {
                if (isChecked) {
                    cvReduceStress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
                    ivReduceStress.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flReduceStress.visibility = View.VISIBLE
                    MyApplication.prefs!!.stress = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(1)
//                    goalModel.setStress(1)
//                    goalArray.add(goalModel)
                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvReduceStress.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out))
                    ivReduceStress.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flReduceStress.visibility = View.GONE
                    MyApplication.prefs!!.stress = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(1)
//                    goalModel.setStress(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }
            R.id.cbBetterSleep -> {
                if (isChecked) {
                    cvBetterSleep.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
                    flBetterSleep.visibility = View.VISIBLE
                    ivBetterSleep.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    MyApplication.prefs!!.sleep = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(2)
//                    goalModel.setSleep(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvBetterSleep.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out))
                    flBetterSleep.visibility = View.GONE
                    ivBetterSleep.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    MyApplication.prefs!!.sleep = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(2)
//                    goalModel.setSleep(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }

            R.id.cbImproveFocus -> {
                if (isChecked) {
                    cvImproveFocus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
                    flImproveFocus.visibility = View.VISIBLE
                    ivImproveFocus.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    MyApplication.prefs!!.focus = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(3)
//                    goalModel.setFocus(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvImproveFocus.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out))
                    flImproveFocus.visibility = View.GONE
                    ivImproveFocus.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    MyApplication.prefs!!.focus = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(3)
//                    goalModel.setSleep(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }
            R.id.cbLearnToMeditate -> {
                if (isChecked) {
                    cvLearnToMeditate.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_in
                        )
                    )
                    ivLearnToMeditate.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flLearnToMeditate.visibility = View.VISIBLE
                    MyApplication.prefs!!.meditate = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(4)
//                    goalModel.setMeditate(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvLearnToMeditate.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_out
                        )
                    )
                    ivLearnToMeditate.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flLearnToMeditate.visibility = View.GONE
                    MyApplication.prefs!!.meditate = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(4)
//                    goalModel.setMeditate(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }

            R.id.cbDevelopGratitute -> {
                if (isChecked) {
                    cvDevelopGratitute.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_in
                        )
                    )
                    ivDevelopGratitute.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flDevelopGratitute.visibility = View.VISIBLE
                    MyApplication.prefs!!.gratitute = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(5)
//                    goalModel.setGratitute(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvDevelopGratitute.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_out
                        )
                    )
                    ivDevelopGratitute.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flDevelopGratitute.visibility = View.GONE
                    MyApplication.prefs!!.gratitute = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(5)
//                    goalModel.setGratitute(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }
            R.id.cbReduceAnxiety -> {
                if (isChecked) {
                    cvReduceAnxiety.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
                    ivReduceAnxiety.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flReduceAnxiety.visibility = View.VISIBLE

                    MyApplication.prefs!!.anxiety = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(6)
//                    goalModel.setAnxiety(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvReduceAnxiety.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_out
                        )
                    )
                    ivReduceAnxiety.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flReduceAnxiety.visibility = View.GONE
                    MyApplication.prefs!!.anxiety = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(6)
//                    goalModel.setAnxiety(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }

            R.id.cbIncreaseHappiness -> {
                if (isChecked) {
                    cvIncreaseHappiness.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_in
                        )
                    )
                    ivIncreaseHappiness.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flIncreaseHappiness.visibility = View.VISIBLE
                    MyApplication.prefs!!.happiness = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(7)
//                    goalModel.setHappiness(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvIncreaseHappiness.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_out
                        )
                    )
                    ivIncreaseHappiness.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flIncreaseHappiness.visibility = View.GONE
                    MyApplication.prefs!!.happiness = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(7)
//                    goalModel.setHappiness(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }
            R.id.cbBuildSelfEsteem -> {
                if (isChecked) {
                    cvBuildSelfEsteem.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_in
                        )
                    )
                    ivBuildSelfEsteem.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                    flBuildSelfEsteem.visibility = View.VISIBLE
                    MyApplication.prefs!!.selfEsteem = 1
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(8)
//                    goalModel.setSelfEsteem(1)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal++
                } else {
                    cvBuildSelfEsteem.setAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.zoom_out
                        )
                    )
                    ivBuildSelfEsteem.setBackgroundColor(resources.getColor(R.color.color_icon_light))
                    flBuildSelfEsteem.visibility = View.GONE
                    MyApplication.prefs!!.selfEsteem = 0
//                    val goalModel = GoalModel()
//                    goalModel.setGoalId(8)
//                    goalModel.setSelfEsteem(0)
//                    goalArray.add(goalModel)
//                    //db.goalDao().insertAll(goalModel)
//                    totalGoal--
                }
            }
        }
    }
}
