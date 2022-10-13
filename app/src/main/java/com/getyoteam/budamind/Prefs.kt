package com.getyoteam.budamind

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_FILENAME = "com.getyoteam.budamind.prefs"
    val EMAIL_ADDRESS = "email_address"
    val USER_ID = "user_id"
    val LOGIN_TYPE = "login_type"
    val PRE_DATE = "prev_date"
    val PRE_CHAPTER = "prev_chapter"
    val PRE_INDEX_SOUND = "prev_index_sound"
    val COURSE_ID = "course_id"
    val TOTAL_GOAL = "total_goal"
    val FIRST_MEDITATION_SIZE = "first_meditation_size"
    val FIRST_MEDITATION_ID = "first_meditation_id"
    val MINUTE = "minute"
    val HOUR = "hour"
    val IS_REMINDER= "is_reminder"
    val IS_PUSH_NOTIFICATION= "is_pushnotification"
    val IS_MOMENT_PUSH= "is_moment_push"
    val IS_SOUND_PUSH= "is_sound_push"
    val FB_TOKEN= "fb_token"
    val SONG_QUOTE= "song_quote"
    val IS_LIBRARY_PUSH= "is_library_push"
    val IS_FIRST_TIME= "is_first_time"
    val IS_FIRST_APP= "is_first_app"
    val MEDITATION_ID = "meditation_id"
    val AUTO_DOWNLOAD = "auto_download"
    val IS_PLAYING = "is_playing"
    val IS_ITEM_CLICK_PLAYING = "is_click"
    val FORGOT_PASSWORD = "forgot_password"
    val FIRST_NAME = "first_name"
    val LAST_NAME = "last_name"
    val EMAIL = "email"
    val PROFILE_PIC = "profile_pic"
    val ASK_PERMISSION_FIRST = "ask_permission_first"
    val PUR_DATE = "purchase_date"
    val SUB_PUR = "subscription_purchased"
    val SUB_TYPE = "subscription_type"
    val AUTH_TOKEN = "auth_token"
    val STATEMODEL="meditationStateModel"
    val COURSEMODEL="courseModel"
    val CHAPTERMODEL="chapterModel"
    val MOMENTMODEL="momentModel"
    val PREF_CLICK_ACTION="clickaction"
    val PREF_MY_WALLET_ADDRES="mywaddrsss"
    val PREF_MY_WALLET_ID="myid"
    val PREF_MY_WALLET_BALANCE="my_bal"
    val PREF_TOTAL_EARNED="tot_ern"
    val PREF_NEXT_WITHDROWDATE="nextdate"
    val PREF_WALLET_RESPONCE="w_responce"
    val PREF_TASK_RESPONCE="t_responce"
    val PREF_REFERALUSER_ID="e_id"

    val PREF_CATAGORY_ANXIETY="anxiety"
    val PREF_CATAGORY_FOCUS="focus"
    val PREF_CATAGORY_GRATITUDE="gratitute"
    val PREF_CATAGORY_HAPPINESS="happiness"
    val PREF_CATAGORY_MEDITATE="meditate"
    val PREF_CATAGORY_SELF_ESTEEN="selfEsteem"
    val PREF_CATAGORY_SLEEP="sleep"
    val PREF_CATAGORY_STRESS="stress"

    val PREF_DAILY_MINUTE="d_mimute"
    val PREF_WEEKLY_MINUTE="w_mimute"
    val PREF_TOT_MEDITATE_MINUTE="tot_m_mimute"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var weeklyMinute: Float?
        get() = prefs.getFloat(PREF_WEEKLY_MINUTE, 0f)
        set(value) = prefs.edit().putFloat(PREF_WEEKLY_MINUTE, value!!).apply()

    var dailyMinute: Float?
        get() = prefs.getFloat(PREF_DAILY_MINUTE, 0f)
        set(value) = prefs.edit().putFloat(PREF_DAILY_MINUTE, value!!).apply()

    var totalMeditateMinute: Float?
        get() = prefs.getFloat(PREF_TOT_MEDITATE_MINUTE, 0f)
        set(value) = prefs.edit().putFloat(PREF_TOT_MEDITATE_MINUTE, value!!).apply()



    var anxiety: Int?
        get() = prefs.getInt(PREF_CATAGORY_ANXIETY, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_ANXIETY, value!!).apply()

    var focus: Int?
        get() = prefs.getInt(PREF_CATAGORY_FOCUS, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_FOCUS, value!!).apply()

    var gratitute: Int?
        get() = prefs.getInt(PREF_CATAGORY_GRATITUDE, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_GRATITUDE, value!!).apply()

    var happiness: Int?
        get() = prefs.getInt(PREF_CATAGORY_HAPPINESS, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_HAPPINESS, value!!).apply()

    var meditate: Int?
        get() = prefs.getInt(PREF_CATAGORY_MEDITATE, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_MEDITATE, value!!).apply()

    var selfEsteem: Int?
        get() = prefs.getInt(PREF_CATAGORY_SELF_ESTEEN, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_SELF_ESTEEN, value!!).apply()

    var sleep: Int?
        get() = prefs.getInt(PREF_CATAGORY_SLEEP, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_SLEEP, value!!).apply()

    var stress: Int?
        get() = prefs.getInt(PREF_CATAGORY_STRESS, 0)
        set(value) = prefs.edit().putInt(PREF_CATAGORY_STRESS, value!!).apply()


    var referalId: String
        get() = prefs.getString(PREF_REFERALUSER_ID, "")!!
        set(value) = prefs.edit().putString(PREF_REFERALUSER_ID, value).apply()

    var wallateResponce: String
        get() = prefs.getString(PREF_WALLET_RESPONCE, "")!!
        set(value) = prefs.edit().putString(PREF_WALLET_RESPONCE, value).apply()

    var taskResponce: String
        get() = prefs.getString(PREF_TASK_RESPONCE, "")!!
        set(value) = prefs.edit().putString(PREF_TASK_RESPONCE, value).apply()

    var totRsrned: String
        get() = prefs.getString(PREF_TOTAL_EARNED, "")!!
        set(value) = prefs.edit().putString(PREF_TOTAL_EARNED, value).apply()

    var myBalance: String
        get() = prefs.getString(PREF_MY_WALLET_BALANCE, "0")!!
        set(value) = prefs.edit().putString(PREF_MY_WALLET_BALANCE, value).apply()

    var nextDate: String
        get() = prefs.getString(PREF_NEXT_WITHDROWDATE, "")!!
        set(value) = prefs.edit().putString(PREF_NEXT_WITHDROWDATE, value).apply()

    var stateModel: String
        get() = prefs.getString(STATEMODEL, "")!!
        set(value) = prefs.edit().putString(STATEMODEL, value).apply()

    var myWallateId: String
        get() = prefs.getString(PREF_MY_WALLET_ID, "")!!
        set(value) = prefs.edit().putString(PREF_MY_WALLET_ID, value).apply()

    var myWallateAddress: String
        get() = prefs.getString(PREF_MY_WALLET_ADDRES, "")!!
        set(value) = prefs.edit().putString(PREF_MY_WALLET_ADDRES, value).apply()
    var courseModel: String
        get() = prefs.getString(COURSEMODEL, "")!!
        set(value) = prefs.edit().putString(COURSEMODEL, value).apply()
    var chapterModel: String
        get() = prefs.getString(CHAPTERMODEL, "")!!
        set(value) = prefs.edit().putString(CHAPTERMODEL, value).apply()
    var momentModel: String
        get() = prefs.getString(MOMENTMODEL, "")!!
        set(value) = prefs.edit().putString(MOMENTMODEL, value).apply()
    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")!!
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()
    var subscriptionType: String
        get() = prefs.getString(SUB_TYPE, "")!!
        set(value) = prefs.edit().putString(SUB_TYPE, value).apply()
    var subPurchase: Boolean
        get() = prefs.getBoolean(SUB_PUR, false)
        set(value) = prefs.edit().putBoolean(SUB_PUR, value).apply()
    var purchaseDate: Long
        get() = prefs.getLong(PUR_DATE, 0)
        set(value) = prefs.edit().putLong(PUR_DATE, value).apply()
    var profilePic: String
        get() = prefs.getString(PROFILE_PIC, "")!!
        set(value) = prefs.edit().putString(PROFILE_PIC, value).apply()
    var first_name: String
        get() = prefs.getString(FIRST_NAME, "")!!
        set(value) = prefs.edit().putString(FIRST_NAME, value).apply()
    var last_name: String
        get() = prefs.getString(LAST_NAME, "")!!
        set(value) = prefs.edit().putString(LAST_NAME, value).apply()

    var email: String
        get() = prefs.getString(EMAIL, "")!!
        set(value) = prefs.edit().putString(EMAIL, value).apply()
    var emailAddress: String?
        get() = prefs.getString(EMAIL_ADDRESS, "")
        set(value) = prefs.edit().putString(EMAIL_ADDRESS, value).apply()
    var askPermissionFirst: Boolean
        get() = prefs.getBoolean(ASK_PERMISSION_FIRST, true)
        set(value) = prefs.edit().putBoolean(ASK_PERMISSION_FIRST, value).apply()
    var autoDownload: Boolean?
        get() = prefs.getBoolean(AUTO_DOWNLOAD, false)
        set(value) = prefs.edit().putBoolean(AUTO_DOWNLOAD, value!!).apply()
    var isPlaying: Boolean?
        get() = prefs.getBoolean(IS_PLAYING, false)
        set(value) = prefs.edit().putBoolean(IS_PLAYING, value!!).apply()

    var isItemClicked: Boolean?
        get() = prefs.getBoolean(IS_ITEM_CLICK_PLAYING, false)
        set(value) = prefs.edit().putBoolean(IS_ITEM_CLICK_PLAYING, value!!).apply()

    var isFirstTime: Boolean?
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(value) = prefs.edit().putBoolean(IS_FIRST_TIME, value!!).apply()

    var isFirstApp: Boolean?
        get() = prefs.getBoolean(IS_FIRST_APP, true)
        set(value) = prefs.edit().putBoolean(IS_FIRST_APP, value!!).apply()
    var isReminder: Boolean?
        get() = prefs.getBoolean(IS_REMINDER, true)
        set(value) = prefs.edit().putBoolean(IS_REMINDER, value!!).apply()
    var isPushNotification: Boolean?
        get() = prefs.getBoolean(IS_PUSH_NOTIFICATION, true)
        set(value) = prefs.edit().putBoolean(IS_PUSH_NOTIFICATION, value!!).apply()
    var isLibraryPush: Boolean?
        get() = prefs.getBoolean(IS_LIBRARY_PUSH, true)
        set(value) = prefs.edit().putBoolean(IS_LIBRARY_PUSH, value!!).apply()
    var isMomentPush: Boolean?
        get() = prefs.getBoolean(IS_MOMENT_PUSH, true)
        set(value) = prefs.edit().putBoolean(IS_MOMENT_PUSH, value!!).apply()
    var isSoundPush: Boolean?
        get() = prefs.getBoolean(IS_SOUND_PUSH, true)
        set(value) = prefs.edit().putBoolean(IS_SOUND_PUSH, value!!).apply()
    var forgotPassword: Boolean?
        get() = prefs.getBoolean(FORGOT_PASSWORD, true)
        set(value) = prefs.edit().putBoolean(FORGOT_PASSWORD, value!!).apply()
    var userId: String
        get() = prefs.getString(USER_ID, "")!!
        set(value) = prefs.edit().putString(USER_ID, value).apply()
    var firebaseToken: String
        get() = prefs.getString(FB_TOKEN, "")!!
        set(value) = prefs.edit().putString(FB_TOKEN, value).apply()
    var songQuote: String
        get() = prefs.getString(SONG_QUOTE, "")!!
        set(value) = prefs.edit().putString(SONG_QUOTE, value).apply()
    var totalGoal: Int
        get() = prefs.getInt(TOTAL_GOAL, 0)
        set(value) = prefs.edit().putInt(TOTAL_GOAL, value).apply()
    var firstMeditationSize: Int
        get() = prefs.getInt(FIRST_MEDITATION_SIZE, 0)
        set(value) = prefs.edit().putInt(FIRST_MEDITATION_SIZE, value).apply()
    var firstMeditationId: Int
        get() = prefs.getInt(FIRST_MEDITATION_ID, 0)
        set(value) = prefs.edit().putInt(FIRST_MEDITATION_ID, value).apply()
    var courseId: Int
        get() = prefs.getInt(COURSE_ID, 0)
        set(value) = prefs.edit().putInt(COURSE_ID, value).apply()
    var prevIndexSound: Int
        get() = prefs.getInt(PRE_INDEX_SOUND, 0)
        set(value) = prefs.edit().putInt(PRE_INDEX_SOUND, value).apply()
    var hour: Int
        get() = prefs.getInt(HOUR, 0)
        set(value) = prefs.edit().putInt(HOUR, value).apply()
    var minute: Int
        get() = prefs.getInt(MINUTE, 0)
        set(value) = prefs.edit().putInt(MINUTE, value).apply()
    var prevDate: String?
        get() = prefs.getString(PRE_DATE, "")
        set(value) = prefs.edit().putString(PRE_DATE, value).apply()
    var prevChapter: String?
        get() = prefs.getString(PRE_CHAPTER, "")
        set(value) = prefs.edit().putString(PRE_CHAPTER, value).apply()
    var loginType: String?
        get() = prefs.getString(LOGIN_TYPE, "")
        set(value) = prefs.edit().putString(LOGIN_TYPE, value).apply()

    var clickaction: String?
        get() = prefs.getString(PREF_CLICK_ACTION, "Home")
        set(value) = prefs.edit().putString(PREF_CLICK_ACTION, value).apply()
}