package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity
class CourseListModel : Serializable {
    @SerializedName("courseId")
    @Expose
    @PrimaryKey
    private var courseId: Int? = null

    @SerializedName("courseName")
    @Expose
    @ColumnInfo(name = "courseName")
    private var courseName: String? = null



    @SerializedName("genre")
    @Expose
    @ColumnInfo(name = "genre")
    var genre: String? = null

    @SerializedName("description")
    @Expose
    @ColumnInfo(name = "description")
    private var description: String? = null

    @SerializedName("fromMinutes")
    @Expose
    @ColumnInfo(name = "fromMinutes")
    private var fromMinutes: String? = null

    @SerializedName("toMinutes")
    @Expose
    @ColumnInfo(name = "toMinutes")
    private var toMinutes: String? = null

    @SerializedName("colorCode")
    @Expose
    @ColumnInfo(name = "colorCode")
    private var colorCode: String? = null

    @SerializedName("banner")
    @Expose
    @ColumnInfo(name = "banner")
    private var banner: String? = null

    @SerializedName("coinForContent")
    @Expose
    @ColumnInfo(name = "coinForContent")
     var coinForContent: String? = null

    @SerializedName("coins")
    @Expose
    @ColumnInfo(name = "coins")
    var coins: String? = null

    @SerializedName("rewarded")
    @Expose
    @ColumnInfo(name = "rewarded")
    var rewarded: Boolean? = null

    @SerializedName("freePaid")
    @Expose
    @ColumnInfo(name = "freePaid")
    var freePaid: String? = null

    @SerializedName("purchased")
    @Expose
    @ColumnInfo(name = "purchased")
     var purchased: Boolean? = null

    @SerializedName("stress")
    @Expose
    @ColumnInfo(name = "stress")
    private var stress: Int? = 0

    @SerializedName("focus")
    @Expose
    @ColumnInfo(name = "focus")
    private var focus: Int? = 0

    @SerializedName("gratitute")
    @Expose
    @ColumnInfo(name = "gratitute")
    private var gratitute: Int? = 0

    @SerializedName("happiness")
    @Expose
    @ColumnInfo(name = "happiness")
    private var happiness: Int? = 0

    @SerializedName("sleep")
    @Expose
    @ColumnInfo(name = "sleep")
    private var sleep: Int? = 0

    @SerializedName("meditate")
    @Expose
    @ColumnInfo(name = "meditate")
    private var meditate: Int? = 0

    @SerializedName("anxiety")
    @Expose
    @ColumnInfo(name = "anxiety")
    private var anxiety: Int? = 0

    @SerializedName("selfEsteem")
    @Expose
    @ColumnInfo(name = "selfEsteem")
    private var selfEsteem: Int? = 0

    @Expose
    @ColumnInfo(name = "isDownloaded")
    private var isDownloaded: Boolean? = null

    @Expose
    @ColumnInfo(name = "totalChapters")
    var totalChapters: String? = null

    @SerializedName("ads")
    @Expose
    @ColumnInfo(name = "ads")
    var isAds: Boolean? = false

    @SerializedName("adLink")
    @Expose
    @ColumnInfo(name = "adLink")
    var adLink: String? = null

    @SerializedName("adUrl")
    @Expose
    @ColumnInfo(name = "adUrl")
    var adUrl: String? = null

    @SerializedName("adHeight")
    @Expose
    @ColumnInfo(name = "adHeight")
    var adHeight: String? = null

    @SerializedName("adWidth")
    @Expose
    @ColumnInfo(name = "adWidth")
    var adWidth: String? = null


    fun getCourseId(): Int? {
        return courseId
    }

    fun setCourseId(courseId: Int?) {
        this.courseId = courseId
    }

    fun getIsDownloaded(): Boolean? {
        return isDownloaded
    }

    fun setIsDownloaded(isDownloaded: Boolean?) {
        this.isDownloaded = isDownloaded
    }


    fun getCourseName(): String? {
        return courseName
    }

    fun setCourseName(courseName: String) {
        this.courseName = courseName
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getFromMinutes(): String? {
        return fromMinutes
    }

    fun setFromMinutes(fromMinutes: String) {
        this.fromMinutes = fromMinutes
    }

    fun getToMinutes(): String? {
        return toMinutes
    }
    fun setToMinutes(toMinutes: String) {
        this.toMinutes = toMinutes
    }

    fun getColorCode(): String? {
        return colorCode
    }

    fun setColorCode(colorCode: String) {
        this.colorCode = colorCode
    }

    fun getBanner(): String? {
        return banner
    }

    fun setBanner(banner: String) {
        this.banner = banner
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