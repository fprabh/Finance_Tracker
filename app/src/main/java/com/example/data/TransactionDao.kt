package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM `Transaction` ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM `Transaction`")
    suspend fun getAllTransactionsSuspend(): List<Transaction>

    @Query("SELECT * FROM `Transaction` WHERE statementId = :statementId ORDER BY date DESC")
    fun getTransactionsByStatementId(statementId: Long): Flow<List<Transaction>>

    @Query("SELECT DISTINCT description FROM `Transaction` WHERE categoryId IS NULL ORDER BY description ASC")
    fun getUncategorizedDescriptions(): Flow<List<String>>

    @Query("SELECT * FROM `Transaction` WHERE categoryId IS NULL")
    suspend fun getUncategorizedTransactions(): List<Transaction>

    @Query("SELECT COUNT(*) FROM `Transaction` WHERE statementId = :statementId AND categoryId IS NULL")
    fun getUncategorizedCountForStatement(statementId: Long): Flow<Int>

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Update
    suspend fun updateTransaction(transaction: Transaction)
}
