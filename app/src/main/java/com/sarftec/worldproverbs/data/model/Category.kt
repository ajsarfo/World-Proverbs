package com.sarftec.worldproverbs.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CATEGORY_TABLE = "category_table"

@Entity(tableName = CATEGORY_TABLE)
class Category(
    @PrimaryKey val id: Int,
    val category: String
)