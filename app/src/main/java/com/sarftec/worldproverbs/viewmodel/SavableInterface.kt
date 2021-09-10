package com.sarftec.worldproverbs.viewmodel

import com.sarftec.worldproverbs.model.Item

interface SavableInterface {
    fun save(quote: Item.Quote)
}