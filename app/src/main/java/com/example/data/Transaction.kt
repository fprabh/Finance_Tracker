package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val description: String,
    val cardMember: String,
    val amount: Double,
    val statementId: Long,
    val categoryId: Int? = null,
    val hasConflict: Boolean = false
)
