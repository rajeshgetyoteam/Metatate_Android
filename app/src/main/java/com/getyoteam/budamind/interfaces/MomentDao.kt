package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.MomentListModel

@Dao
interface MomentDao {

  @Query("SELECT * FROM momentlistmodel ORDER BY `index` ASC" )
  fun getAll(): List<MomentListModel>

  @Query("SELECT * FROM momentlistmodel WHERE momentId IN (:momentId)")
  fun loadAllByIds(momentId: IntArray): List<MomentListModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(momentlistmodel: MomentListModel)

  @Delete
  fun delete(momentlistmodel: MomentListModel)

  @Query("DELETE FROM momentListModel")
  fun deleteAll()
}