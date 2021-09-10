package com.sarftec.worldproverbs.data

import android.content.Context
import com.sarftec.worldproverbs.APP_CREATED
import com.sarftec.worldproverbs.data.json.JsonQuoteList
import com.sarftec.worldproverbs.data.model.Category
import com.sarftec.worldproverbs.data.model.Quote
import com.sarftec.worldproverbs.editSettings
import com.sarftec.worldproverbs.readSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject


/*
Top Quotes is given a category id of Zero for special reasons. As it should always remain so with
the knowledge that any queries made with a zero id produce consistent results.
 */
const val TOP_QUOTE_CATEGORY_ID = 0

class DatabaseSetup @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: Database
) {

    suspend fun create() {
        context.assets.open("quotes/proverbs.json").use { inputStream ->
            val jsonList: List<JsonQuoteList> = Json.decodeFromString(
                inputStream.bufferedReader().readText()
            )
            jsonList.forEachIndexed { index, list ->
                database.categoryDao().insert(Category(index + 1, list.title))
                database.quoteDao().insert(
                    list.quotes.map {
                        Quote(categoryId = index + 1, message = it.message, author = it.name)
                    }
                )
            }
        }

        context.assets.open("quotes/top.json").use { inputStream ->
            val json: JsonQuoteList = Json.decodeFromString(
                inputStream.bufferedReader().readText()
            )
            database.categoryDao().insert(Category(TOP_QUOTE_CATEGORY_ID, json.title))
            database.quoteDao().insert(
                json.quotes.map {
                    Quote(categoryId = TOP_QUOTE_CATEGORY_ID, message = it.message, author = it.name)
                }
            )
        }
        context.editSettings(APP_CREATED, true)
    }

    suspend fun isCreated(): Boolean {
        return context.readSettings(APP_CREATED, false).first()
    }
}