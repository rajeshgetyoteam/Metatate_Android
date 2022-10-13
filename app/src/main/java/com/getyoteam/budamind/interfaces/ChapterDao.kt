package com.getyoteam.budamind.interfaces


import androidx.room.*
import com.getyoteam.budamind.Model.ChapterListModel

@Dao
interface ChapterDao {

  @Query("SELECT * FROM chapterlistmodel")
  fun getAll(): List<ChapterListModel>

  @Query("SELECT * FROM chapterlistmodel WHERE courseId IN (:courseId)")
  fun loadAllByIds(courseId: Int): List<ChapterListModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(courseListModel: ChapterListModel)

  @Delete
  fun delete(courseListModel: ChapterListModel)

  @Query("DELETE FROM chapterlistmodel WHERE courseId IN (:courseId) ")
  fun deleteAll(courseId: Int)
}