package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity
class GoalModel : Serializable {
    @SerializedName("goalId")
    @Expose
    @PrimaryKey
    private var goalId: Int? = null

    @SerializedName("stress")
    @Expose
    @ColumnInfo(name = "stress")
    private var stress: Int? = null

    @SerializedName("focus")
    @Expose
    @ColumnInfo(name = "focus")
    private var focus: Int? = null

    @SerializedName("gratitute")
    @Expose
    @ColumnInfo(name = "gratitute")
    private var gratitute: Int? = null

    @SerializedName("happiness")
    @Expose
    @ColumnInfo(name = "happiness")
    private var happiness: Int? = null

    @SerializedName("sleep")
    @Expose
    @ColumnInfo(name = "sleep")
    private var sleep: Int? = null

    @SerializedName("meditate")
    @Expose
    @ColumnInfo(name = "meditate")
    private var meditate: Int? = null

    @SerializedName("anxiety")
    @Expose
    @ColumnInfo(name = "anxiety")
    private var anxiety: Int? = null

    @SerializedName("selfEsteem")
    @Expose
    @ColumnInfo(name = "selfEsteem")
    private var selfEsteem: Int? = null


    fun getGoalId(): Int? {
        return goalId
    }

    fun setGoalId(goalId: Int?) {
        this.goalId = goalId
    }

    fun getStress(): Int? {
        return stress
    }

    fun setStress(stress: Int?) {
        this.stress = stress
    }

    fun getFocus(): Int? {
        return focus
    }

    fun setFocus(focus: Int?) {
        this.focus = focus
    }

    fun getGratitute(): Int? {
        return gratitute
    }

    fun setGratitute(gratitute: Int?) {
        this.gratitute = gratitute
    }

    fun getHappiness(): Int? {
        return happiness
    }

    fun setHappiness(happiness: Int?) {
        this.happiness = happiness
    }

    fun getSleep(): Int? {
        return sleep
    }

    fun setSleep(sleep: Int?) {
        this.sleep = sleep
    }

    fun getMeditate(): Int? {
        return meditate
    }

    fun setMeditate(meditate: Int?) {
        this.meditate = meditate
    }

    fun getAnxiety(): Int? {
        return anxiety
    }

    fun setAnxiety(anxiety: Int?) {
        this.anxiety = anxiety
    }

    fun getSelfEsteem(): Int? {
        return selfEsteem
    }

    fun setSelfEsteem(selfEsteem: Int?) {
        this.selfEsteem = selfEsteem
    }
}