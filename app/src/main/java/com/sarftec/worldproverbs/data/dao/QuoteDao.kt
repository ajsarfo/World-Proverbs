package com.sarftec.worldproverbs.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.sarftec.worldproverbs.data.model.QUOTE_TABLE
import com.sarftec.worldproverbs.data.model.Quote

@Dao
interface QuoteDao : BaseDao<Quote> {

    @Query("select * from $QUOTE_TABLE where categoryId = :categoryId")
    suspend fun quotes(categoryId: Int) : List<Quote>

    @Query("select * from $QUOTE_TABLE order by random() limit :limit")
    suspend fun random(limit: Int) : List<Quote>

    @Query("select * from $QUOTE_TABLE where favorite = 1")
    suspend fun favorites() : List<Quote>

    @Query("update $QUOTE_TABLE set favorite = :isFavorite where id = :id")
    suspend fun update(id: Int, isFavorite: Boolean)
}