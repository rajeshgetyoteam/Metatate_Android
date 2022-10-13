package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.CourseDownloadModel

@Dao
interface CourseDownloadDao {

  @Query("SELECT * FROM coursedownloadmodel")
  fun getAll(): List<CourseDownloadModel>

  @Query("SELECT * FROM coursedownloadmodel WHERE courseId IN (:courseId)")
  fun loadAllByIds(courseId: Int?): CourseDownloadModel

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(courseListModel: CourseDownloadModel)

  @Delete
  fun delete(courseListModel: CourseDownloadModel)

  @Query("DELETE FROM coursedownloadmodel")
  fun deleteAll()
}