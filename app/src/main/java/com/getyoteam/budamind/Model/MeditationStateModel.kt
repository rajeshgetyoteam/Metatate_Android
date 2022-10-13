package com.mindfulness.greece.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
class MeditationStateModel : Serializable {
    @SerializedName("userId")
    @Expose
    @PrimaryKey
    private var userId: Int? = null

    @SerializedName("minuteMeditate")
    @Expose
    @ColumnInfo(name = "minuteMeditate")
    private var minuteMeditated: Float? = null

    @SerializedName("dailyMinutes")
    @Expose
    @ColumnInfo(name = "dailyMinutes")
    private var dailyMinutes : Float? = null

    @SerializedName("weeklyMinutes")
    @Expose
    @ColumnInfo(name = "weeklyMinutes")
    private var weeklyMinutes : Float? = null

    @SerializedName("totalSessions")
    @Expose
    @ColumnInfo(name = "totalSessions")
    private var totalSessions: Int? = null

    @SerializedName("currentStreak")
    @Expose
    @ColumnInfo(name = "currentStreak")
    private var currentStreak: Int? = null

    @SerializedName("longestStreak")
    @Expose
    @ColumnInfo(name = "longestStreak")
    private var longestStreak: Int? = null

    fun getUserId(): Int? {
        return userId
    }

    fun setUserId(userId: Int?) {
        this.userId = userId
    }

    fun getMinuteMeditated(): Float? {
        return minuteMeditated
    }

    fun setMinuteMeditated(minuteMeditated: Float?) {
        this.minuteMeditated = minuteMeditated
    }



    fun getDailyMinutes(): Float? {
        return dailyMinutes
    }

    fun setDailyMinutes(dailyMinutes: Float?) {
        this.dailyMinutes = dailyMinutes
    }

    fun getWeeklyMinutes(): Float? {
        return weeklyMinutes
    }

    fun setWeeklyMinutes(weeklyMinutes: Float?) {
        this.weeklyMinutes = weeklyMinutes
    }



    fun getTotalSessions(): Int? {
        return totalSessions
    }

    fun setTotalSessions(totalSessions: Int?) {
        this.totalSessions = totalSessions
    }

    fun getCurrentStreak(): Int? {
        return currentStreak
    }

    fun setCurrentStreak(currentStreak: Int?) {
        this.currentStreak = currentStreak
    }

    fun getLongestStreak(): Int? {
        return longestStreak
    }

    fun setLongestStreak(longestStreak: Int?) {
        this.longestStreak = longestStreak
    }
}