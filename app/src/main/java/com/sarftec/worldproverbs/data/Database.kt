package com.sarftec.worldproverbs.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sarftec.worldproverbs.data.dao.CategoryDao
import com.sarftec.worldproverbs.data.dao.QuoteDao
import com.sarftec.worldproverbs.data.model.Category
import com.sarftec.worldproverbs.data.model.Quote

@Database(entities = [Quote::class, Category::class], exportSchema = false, version = 1)
abstract class Database : RoomDatabase() {
   abstract fun quoteDao() : QuoteDao
   abstract fun categoryDao() : CategoryDao
}