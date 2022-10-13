package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.mindfulness.greece.model.MeditationStateModel

@Dao
interface MeditationStateDao {

  @Query("SELECT * FROM meditationStateModel")
  fun getAll(): List<MeditationStateModel>

//  @Query("SELECT * FROM meditationStateModel WHERE med IN (:stateId)")
//  fun loadAllByIds(stateId: IntArray): List<MeditationStateModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(meditationStateModel: MeditationStateModel)

  @Delete
  fun delete(meditationStateModel: MeditationStateModel)

  @Query("DELETE FROM meditationstatemodel")
  fun deleteAll()
}