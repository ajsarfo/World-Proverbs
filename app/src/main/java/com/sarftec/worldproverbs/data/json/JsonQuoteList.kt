package com.sarftec.worldproverbs.data.json

import kotlinx.serialization.Serializable

@Serializable
class JsonQuoteList(
    val title: String,
    val quotes: List<JsonQuote>
)