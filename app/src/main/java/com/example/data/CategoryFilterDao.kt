package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryFilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: CategoryFilter): Long

    @Query("SELECT * FROM CategoryFilter")
    fun getAllFilters(): Flow<List<CategoryFilter>>

    @Query("SELECT * FROM CategoryFilter WHERE categoryId = :categoryId")
    fun getFiltersForCategory(categoryId: Int): Flow<List<CategoryFilter>>

    @Query("SELECT * FROM CategoryFilter WHERE categoryId = :categoryId AND keyword = :keyword LIMIT 1")
    suspend fun getFilterByKeyword(categoryId: Int, keyword: String): CategoryFilter?

    @Query("SELECT * FROM CategoryFilter")
    suspend fun getAllFiltersSuspend(): List<CategoryFilter>

    @Delete
    suspend fun deleteFilter(filter: CategoryFilter)
}
