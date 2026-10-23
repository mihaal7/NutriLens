package com.example.nutrilens.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM FoodItem WHERE name LIKE '%' || :query || '%'")
    fun searchFood(query: String): Flow<List<FoodItem>>

    @Insert
    suspend fun insertFood(food: FoodItem)

    @Insert
    suspend fun insertLog(log: DailyLog)

    @Query("SELECT * FROM DailyLog ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DailyLog>>
}