package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Statement")
data class Statement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardType: String,
    val monthYear: String,
    val previousBalance: Double,
    val paymentsAndCredits: Double,
    val interestPaid: Double,
    val statementBalance: Double
)
