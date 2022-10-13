package com.getyoteam.budamind.interfaces

import androidx.room.*
import com.getyoteam.budamind.Model.PurchaseModel

@Dao
interface PurchaseModelDao {

  @Query("SELECT * FROM purchasemodel")
  fun getAll(): List<PurchaseModel>

  @Query("SELECT * FROM purchaseModel WHERE orderId IN (:orderId)")
  fun loadAllByIds(orderId: IntArray): List<PurchaseModel>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(featuredModel: PurchaseModel)

  @Delete
  fun delete(featuredModel: PurchaseModel)

  @Query("DELETE FROM purchaseModel")
  fun deleteAll()

}