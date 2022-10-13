package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SetGoalModel(pUserId: Int?,
    pAnxiety: Int?, pFocus: Int?, pGratitute: Int?, pHappiness: Int?,
    pMeditate: Int?, pSelfEsteem: Int?, pSleep: Int?, pStress: Int?
) {



    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("anxiety")
    @Expose
    var anxiety: Int? = null

    @SerializedName("focus")
    @Expose
    var focus: Int? = null

    @SerializedName("gratitute")
    @Expose
    var gratitute: Int? = null

    @SerializedName("happiness")
    @Expose
    var happiness: Int? = null

    @SerializedName("meditate")
    @Expose
    var meditate: Int? = null

    @SerializedName("selfEsteem")
    @Expose
    var selfEsteem: Int? = null

    @SerializedName("sleep")
    @Expose
    var sleep: Int? = null

    @SerializedName("stress")
    @Expose
    var stress: Int? = null

    init {
        userId = pUserId
        anxiety = pAnxiety
        focus = pFocus
        gratitute = pGratitute
        happiness = pHappiness
        meditate = pMeditate
        selfEsteem = pSelfEsteem
        sleep = pSleep
        stress = pStress
    }

}