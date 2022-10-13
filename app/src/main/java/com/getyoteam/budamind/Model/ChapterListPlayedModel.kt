package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Entity
class ChapterListPlayedModel : Serializable {
    @SerializedName("chapterId")
    @Expose
    @PrimaryKey
    private var chapterId: Int? = null

    @SerializedName("courseId")
    @Expose
    @ColumnInfo(name = "courseId")
    private var courseId: Int? = null

    @SerializedName("chapterName")
    @Expose
    @ColumnInfo(name = "chapterName")
    private var chapterName: String? = null

    @SerializedName("courseName")
    @Expose
    @ColumnInfo(name = "courseName")
    private var courseName: String? = null

    @SerializedName("audioUrl")
    @Expose
    @ColumnInfo(name = "audioUrl")
    private var audioUrl: String? = null

    @SerializedName("freePaid")
    @Expose
    @ColumnInfo(name = "freePaid")
    private var freePaid: String? = null

    fun getChapterId(): Int? {
        return chapterId
    }

    fun setChapterId(chapterId: Int?) {
        this.chapterId = chapterId
    }

    fun getCourseId(): Int? {
        return courseId
    }

    fun setCourseId(courseId: Int?) {
        this.courseId = courseId
    }

    fun getChapterName(): String? {
        return chapterName
    }

    fun setChapterName(chapterName: String) {
        this.chapterName = chapterName
    }

    fun getCourseName(): String? {
        return courseName
    }

    fun setCourseName(courseName: String) {
        this.courseName = courseName
    }


    fun getAudioUrl(): String? {
        return audioUrl
    }

    fun setAudioUrl(audioUrl: String) {
        this.audioUrl = audioUrl
    }

    fun getFreePaid(): String? {
        return freePaid
    }

    fun setFreePaid(freePaid: String) {
        this.freePaid = freePaid
    }

}