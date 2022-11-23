package com.getyoteam.budamind.interfaces

import com.getyoteam.budamind.Model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface API {
    @POST("login")
    fun login(@Body loginResponseModel: LoginResponseModel): Call<CommonModel>

    @POST("socialLogin")
    fun socialLogin(@Body loginResponseModel: SocialLoginModel): Call<responceUserModel>

    @POST("sendVerificationMail?")
    fun sendVerificationMail(@Query("email") customerId: String): Call<StatusModel>

    @POST("loginViaOtp?")
    fun loginViaOtp(@Query("email") email: String,@Query("otp") otp: String,@Query("deviceToken") deviceToken: String): Call<responceUserModel>

    @POST("signup")
    fun signUp(@Body loginResponseModel: LoginResponseModel): Call<CommonModel>

    @POST("getLibrary")
    fun getLibraryList(@Header("authToken") token:String,@Query("userId") customerId: String): Call<LibraryModel>

    @POST("home")
    fun getHomeDetail(@Header("authToken") token:String,@Query("userId") customerId: String): Call<HomeResponse>

    @POST("home")
    fun getTaskDetail(@Header("authToken") token:String,@Query("userId") customerId: String): Call<TaskResponse>

    @POST("forgotPassword")
    fun forgotPassword(@Query("email") email: String?): Call<CommonModel>

    @POST("verifyOtp")
    fun verfiyOTP(@Query("email") email: String?, @Query("otp") otp: String?): Call<CommonModel>

    @POST("setPassword")
    fun setPassword(@Query("userId") userId: String?, @Query("deviceToken") deviceToken: String?, @Query("newPassword") newPassword: String?): Call<CommonModel>

    @POST("changePassword")
    fun changePassword(@Header("authToken") token:String,@Query("userId") email: String?, @Query("oldPassword") otp: String?, @Query("newPassword") password: String?): Call<CommonModel>

    @POST("getChapters")
    fun getChapterList(@Query("courseId") courseId: Int?,@Query("userId") userId: String): Call<ChapterResponse>

    @POST("getSounds")
    fun getSoundList(@Query("userId") userId: String?): Call<SoundResponse>

    @POST("randomQuotes")
    fun getRandomQuotes(): Call<QuoteModel>

    @POST("getProfile")
    fun getProfileDetail(
        @Header("authToken") token: String,
        @Query("userId") customerId: String
    ): Call<Profile>

    @POST("deleteUser")
    fun deleteUser(@Header("authToken") token:String,@Query("userId") userId: String?): Call<Status>


    @Multipart
    @POST("uploadFile")
    fun updateUserProfilePhoto(
        @Part file: MultipartBody.Part,
        @Part("folder") folderName: RequestBody
    ): Call<ProfileImage>

    @POST("updateProfile")
    fun updateProfile(@Body map: HashMap<String, String>): Call<CommanResponseModel>

    @POST("getWalletAndHistory")
    fun getWalletAndHistory(
        @Query("userId") userId: String?,
        @Query("byDate") byDate: String?
    ): Call<WallateResponceModel>


    @POST("withdrawalRequest")
    fun withdrawalRequest(
        @Query("userId") userId: String?, @Query("walletId") walletId: String?,
        @Query("receivedAddress") receivedAddress: String?,
        @Query("amoutToWithdraw") amoutToWithdraw: String?
    ): Call<CommonModel>


    @POST("getTask")
    fun getTask(@Query("userId") userId: String?): Call<TaskResponceModel>

    @POST("getRewardsOnPlay")
    fun getRewardsOnPlay(
        @Query("userId") userId: String?,
        @Query("type") type: String?,
        @Query("itemId") itemId: String?
    ): Call<responceRewarModel>

    @POST("purchaseMoments")
    fun purchaseMoments(
        @Query("userId") userId: String?,
        @Query("momentId") momentId: String?
    ): Call<responcePurchaseModel>

    @POST("purchaseSounds")
    fun purchaseSounds(
        @Query("userId") userId: String?,
        @Query("soundId") soundId: String?
    ): Call<responcePurchaseModel>

    @POST("purchaseCourse")
    fun purchaseCourse(
        @Query("userId") userId: String?,
        @Query("courseId") courseId: String?
    ): Call<responcePurchaseModel>

    @POST("purchaseChapter")
    fun purchaseChapter(
        @Query("userId") userId: String?,
        @Query("chapterId") chapterId: String?
    ): Call<responcePurchaseModel>

    @GET("ticker?")
    fun getTicker(
        @Query("key") token: String,
        @Query("ids") ids: String?,
        @Query("interval") interval: String?,
        @Query("convert") convert: String?,
        @Query("per-page") perpage: String?,
        @Query("page") page: String?
    ): Call<ArrayList<responceBudaPriceModel>>


    @DELETE("clearHistory")
    fun clearHistory(
        @Query("walletId") walletId: String?
    ): Call<CommanResponseModel>

    @DELETE("deleteTransHistory")
    fun deleteTransHistory(
        @Query("id") id: String?
    ): Call<CommanResponseModel>

    @POST("claimForTask")
    fun claimForTask(
        @Query("userId") userId: String?,
        @Query("taskId") taskId : String?
    ): Call<responceTaskModel>


    @POST("updateSeconds")
    fun updateSeconds(
        @Body body: JsonObject
    ): Call<Status>

    @POST("submitTimeSpent")
    fun submitTimeSpent (
        @Body body: JsonObject
    ): Call<Status>

    @POST("submitAppOpendTimes")
    fun submitAppOpendTimes(
        @Query("userId") userId: String?
    ): Call<Status>


    @POST("chooseMeditationAnalytics")
    fun coursesAnalytics(
        @Body body: JsonObject
    ): Call<Status>

    @POST("chooseMeditationAnalytics")
    fun momentsAnalytics(
        @Body body: JsonObject
    ): Call<Status>

    @POST("chooseMeditationAnalytics")
    fun soundAnalytics(
        @Body body: JsonObject
    ): Call<Status>

    @POST("setGoal")
    fun setGoal(@Body loginResponseModel: SetGoalModel): Call<responceSetGoalModel>

    @POST("referralRewards")
    fun referralRewards(@Query("userId") userId: String?,@Query("referTo") referTo: String?): Call<CommanResponseModel>

}