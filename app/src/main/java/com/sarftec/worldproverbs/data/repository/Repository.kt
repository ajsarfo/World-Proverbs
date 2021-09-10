package com.sarftec.worldproverbs.data.repository

import com.sarftec.worldproverbs.data.model.Category
import com.sarftec.worldproverbs.data.model.Quote

interface Repository {
    suspend fun categories() : List<Category>
    suspend fun quotes(categoryId: Int) : List<Quote>
    suspend fun quoteOfTheDay() : Quote
    suspend fun random(limit: Int) : List<Quote>
    suspend fun favorites() : List<Quote>
    suspend fun topQuotes() : List<Quote>
    suspend fun updateQuote(id: Int, isFavorite: Boolean)
}