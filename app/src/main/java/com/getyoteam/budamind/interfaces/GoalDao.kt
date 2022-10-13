package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.GoalModel

@Dao
interface GoalDao {

  @Query("SELECT * FROM goalmodel")
  fun getAll(): List<GoalModel>

  @Query("SELECT * FROM goalmodel WHERE goalId IN (:goalId)")
  fun loadAllByIds(goalId: Int): List<GoalModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(goalModel: GoalModel)

  @Delete
  fun delete(goalModel: GoalModel)

  @Query("DELETE FROM goalmodel")
  fun deleteAll()
}