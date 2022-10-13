package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.ChapterListPlayedModel

@Dao
interface ChapterPlayedDao {

  @Query("SELECT * FROM chapterListPlayedModel")
  fun getAll(): List<ChapterListPlayedModel>

  @Query("SELECT * FROM chapterListPlayedModel WHERE courseId IN (:courseId)")
  fun loadAllByIds(courseId: Int): List<ChapterListPlayedModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(courseListModel: ChapterListPlayedModel)

  @Delete
  fun delete(courseListModel: ChapterListPlayedModel)

  @Query("DELETE FROM chapterListPlayedModel WHERE courseId IN (:courseId) ")
  fun deleteAll(courseId: Int)

  @Query("DELETE FROM chapterListPlayedModel")
  fun deleteAllData()
}