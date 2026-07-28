package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatement(statement: Statement): Long

    @androidx.room.Update
    suspend fun updateStatement(statement: Statement)

    @androidx.room.Delete
    suspend fun deleteStatement(statement: Statement)

    @Query("SELECT * FROM Statement")
    fun getAllStatements(): Flow<List<Statement>>
}
