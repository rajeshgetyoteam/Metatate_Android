package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
class DownloadFileModel : Serializable {
    @SerializedName("fileId")
    @Expose
    @PrimaryKey
    private var fileId: Int? = 0

    @SerializedName("fileName")
    @Expose
    @ColumnInfo(name = "fileName")
    private var fileName: String? = ""

    @SerializedName("fileType")
    @Expose
    @ColumnInfo(name = "fileType")
    private var fileType: String? = ""

    @SerializedName("audioFilePath")
    @Expose
    @ColumnInfo(name = "audioFilePath")
    private var audioFilePath: String? = ""

    @SerializedName("minute")
    @Expose
    @ColumnInfo(name = "minute")
    private var minute: String? = ""

    @SerializedName("second")
    @Expose
    @ColumnInfo(name = "second")
    private var second: String? = ""

    @SerializedName("imageFile")
    @Expose
    @ColumnInfo(name = "imageFile")
    private var imageFile: String? = ""

    @SerializedName("modelName")
    @Expose
    @ColumnInfo(name = "modelName")
    private var modelName: String? = ""

    @SerializedName("title")
    @Expose
    @ColumnInfo(name = "title")
    private var title: String? = ""

    @SerializedName("subTitle")
    @Expose
    @ColumnInfo(name = "subTitle")
    private var subTitle: String? = ""

    @SerializedName("played")
    @Expose
    @ColumnInfo(name = "played")
    private var played: Boolean? = false
    fun isPlayed(): Boolean? {
        return played
    }

    fun setPlayed(played: Boolean?) {
        this.played = played
    }

    fun getSubTitle(): String? {
        return subTitle
    }

    fun setSubTitle(subTitle: String?) {
        this.subTitle = subTitle
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getFileId(): Int? {
        return fileId
    }

    fun setFileId(fileId: Int?) {
        this.fileId = fileId
    }

    fun getFileName(): String? {
        return fileName
    }

    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    fun getFileType(): String? {
        return fileType
    }

    fun setFileType(fileType: String) {
        this.fileType = fileType
    }

    fun getAudioFilePath(): String? {
        return audioFilePath
    }

    fun setAudioFilePath(audioFile: String) {
        this.audioFilePath = audioFile
    }

    fun getModelName(): String? {
        return modelName
    }

    fun setModelName(modelName: String) {
        this.modelName = modelName
    }

    fun getMinute(): String? {
        return minute
    }

    fun setMinute(minute: String) {
        this.minute = minute
    }

    fun getSecond(): String? {
        return second
    }

    fun setSecond(second: String) {
        this.second = second
    }

    fun getImageFile(): String? {
        return imageFile
    }

    fun setImageFile(imageFile: String) {
        this.imageFile = imageFile
    }

}