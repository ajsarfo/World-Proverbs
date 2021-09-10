package com.sarftec.worldproverbs.adapter

import com.sarftec.worldproverbs.Dependency
import com.sarftec.worldproverbs.viewmodel.SavableInterface

open class QuoteHolder(
    val dependency: Dependency,
    val viewModel: SavableInterface
)