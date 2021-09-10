package com.sarftec.worldproverbs.data.repository

import com.sarftec.worldproverbs.data.Database
import com.sarftec.worldproverbs.data.TOP_QUOTE_CATEGORY_ID
import com.sarftec.worldproverbs.data.model.Category
import com.sarftec.worldproverbs.data.model.Quote
import javax.inject.Inject
import javax.inject.Singleton

/*
Note Top Quotes has a category id of Zero for special reasons.
Always make queries for top quotes knowing that provision of Zero category id produces
consistent results
 */

class DiskRepositoryImpl @Inject constructor(
    private val database: Database
    ) : Repository {

    private var ofTheDay: Quote? = null

    override suspend fun categories(): List<Category> {
        return database.categoryDao().categories()
    }

    override suspend fun quotes(categoryId: Int): List<Quote> {
        return database.quoteDao().quotes(categoryId)
    }

    override suspend fun random(limit: Int): List<Quote> {
        return database.quoteDao().random(limit)
    }

    override suspend fun quoteOfTheDay(): Quote {
        return ofTheDay ?: kotlin.run {
            ofTheDay = random(20).first()
            ofTheDay!!
        }
    }

    override suspend fun favorites(): List<Quote> {
       return database.quoteDao().favorites()
    }

    override suspend fun topQuotes(): List<Quote> {
        return database.quoteDao().quotes(TOP_QUOTE_CATEGORY_ID)
    }

    override suspend fun updateQuote(id: Int, isFavorite: Boolean) {
       database.quoteDao().update(id, isFavorite)
    }
}