package com.sarftec.worldproverbs.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.sarftec.worldproverbs.data.model.Category

@Dao
interface CategoryDao : BaseDao<Category> {

    @Query("select * from CATEGORY_TABLE")
    suspend fun categories() : List<Category>
}