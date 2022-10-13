package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.DownloadFileModel

@Dao
interface DownloadDao {

  @Query("SELECT * FROM downloadfilemodel")
  fun getAll(): List<DownloadFileModel>

  @Query("SELECT * FROM downloadfilemodel WHERE fileName = :fileName limit 1")
  fun loadDownloadFile(fileName: String):DownloadFileModel

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertDownloadFile(downloadFileModel: DownloadFileModel)

  @Delete
  fun delete(downloadFileModel: DownloadFileModel)


  @Query("DELETE FROM downloadfilemodel")
  fun deleteAll()
}