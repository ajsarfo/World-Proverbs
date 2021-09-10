package com.sarftec.worldproverbs.data.json

import kotlinx.serialization.Serializable

@Serializable
class JsonQuote(
    val message: String,
    val name: String
)