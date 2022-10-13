package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindfulness.greece.model.MeditationStateModel


class ChapterResponse {
    @SerializedName("meditationState")
    @Expose
    private var meditationState: MeditationStateModel? = null

    @SerializedName("chapterList")
    @Expose
    private var chapterList: List<ChapterListModel>? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getMeditationState(): MeditationStateModel? {
        return meditationState
    }

    fun setMeditationState(meditationState: MeditationStateModel) {
        this.meditationState = meditationState
    }

    fun getChapterList(): List<ChapterListModel>? {
        return chapterList
    }

    fun setChapterList(chapterList: List<ChapterListModel>) {
        this.chapterList = chapterList
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }
}
