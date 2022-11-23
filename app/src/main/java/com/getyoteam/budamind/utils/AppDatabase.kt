package com.getyoteam.budamind.utils


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.interfaces.*
import com.mindfulness.greece.model.MeditationStateModel

@Database(
    entities = arrayOf(
        CourseListModel::class,
        CourseDownloadModel::class,
        ChapterListModel::class,
        ChapterListPlayedModel::class,
        MomentListModel::class,
        DownloadFileModel::class,
        MeditationStateModel::class,
        GoalModel::class,
        PurchaseModel::class,
        SoundListModel::class
    ), version = 1,exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseDownloadDao(): CourseDownloadDao
    abstract fun chapterDao(): ChapterDao
    abstract fun chapterPlayedDao(): ChapterPlayedDao
    abstract fun momentDao(): MomentDao
    abstract fun soundDao(): SoundDao
    abstract fun goalDao(): GoalDao
    abstract fun meditationStateDao(): MeditationStateDao
    abstract fun downloadDao(): DownloadDao
    abstract fun purchaseModelDao(): PurchaseModelDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Metatate"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}



