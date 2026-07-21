package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CategoryFilter")
data class CategoryFilter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val keyword: String
)
