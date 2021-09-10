package com.sarftec.worldproverbs.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao <T> {

    @Insert
    suspend fun insert(list: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T)

    @Update
    suspend fun update(item: T)
}