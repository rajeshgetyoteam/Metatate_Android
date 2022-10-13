package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Profile {
    @SerializedName("firstName")
    @Expose
    private var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    private var lastName: String? = null

    @SerializedName("currentStreak")
    @Expose
    private var currentStreak: Int? = null

    @SerializedName("totalSessions")
    @Expose
    private var totalSessions: Int? = null

    @SerializedName("dailyMinutes")
    @Expose
    private var dailyMinutes: Float? = null

    @SerializedName("weeklyMinutes")
    @Expose
    private var weeklyMinutes : Float? = null

    @SerializedName("profilePic")
    @Expose
    private var profilePic: String? = null

    @SerializedName("longestStreak")
    @Expose
    private var longestStreak: Int? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("minuteMeditate")
    @Expose
    private var minuteMeditate: Float? = null

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun getCurrentStreak(): Int? {
        return currentStreak
    }

    fun setCurrentStreak(currentStreak: Int?) {
        this.currentStreak = currentStreak
    }

    fun getTotalSessions(): Int? {
        return totalSessions
    }

    fun setTotalSessions(totalSessions: Int?) {
        this.totalSessions = totalSessions
    }

    fun getProfilePic(): String? {
        return profilePic
    }

    fun setProfilePic(profilePic: String) {
        this.profilePic = profilePic
    }

    fun getLongestStreak(): Int? {
        return longestStreak
    }

    fun setLongestStreak(longestStreak: Int?) {
        this.longestStreak = longestStreak
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getMinuteMeditate(): Float? {
        return minuteMeditate
    }

    fun setMinuteMeditate(minuteMeditate: Float?) {
        this.minuteMeditate = minuteMeditate
    }

    fun getDailyMinuteMeditate(): Float? {
        return dailyMinutes
    }

    fun setDailyMinuteMeditate(dailyMinutes: Float?) {
        this.dailyMinutes = dailyMinutes
    }

    fun getWeeklyMinuteMeditate(): Float? {
        return weeklyMinutes
    }

    fun setWeeklyMinuteMeditate(weeklyMinutes: Float?) {
        this.weeklyMinutes = weeklyMinutes
    }
}