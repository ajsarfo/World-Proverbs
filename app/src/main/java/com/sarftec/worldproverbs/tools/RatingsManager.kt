package com.sarftec.worldproverbs.tools

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.Interpolator
import com.sarftec.worldproverbs.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.cos

/*
Note: APP_START_COUNT should be increment on app start-up in the load or start activity where
necessary
 */
class RatingsManager(
private val coroutineScope: CoroutineScope,
private val ratingsDialog: RatingsDialog
) {

    private val ratingsInterval = 4

    private var ratingsMenuItem: View? = null

    private val rateAppAnimation by lazy {
        val valueTo = 1.1f
        val valueFrom = 0.9f
        val frequency = 1f
        val animatorSet = AnimatorSet()
        animatorSet.play(
            ObjectAnimator.ofFloat(
                ratingsMenuItem!!, "scaleX", valueFrom, valueTo
            ).apply {
                repeatCount = ObjectAnimator.INFINITE
                interpolator = getRateAppInterpolator(valueFrom, frequency)
            }
        ).with(
            ObjectAnimator.ofFloat(
                ratingsMenuItem!!, "scaleY", valueFrom, valueTo
            ).apply {
                repeatCount = ObjectAnimator.INFINITE
                interpolator = getRateAppInterpolator(valueFrom, frequency)
            }
        )
        animatorSet
    }

    suspend fun init() {
        ratingsDialog.context.let { context ->
            if (!context.readSettings(SHOW_RATINGS, true).first()) return@let
            if (context.readSettings(APP_START_COUNT, 1).first() >= 4) {
                setup(ratingsDialog.context)
                ratingsDialog.show()
            }
        }
    }

    fun startRateAppAnimation(view: View) {
        ratingsMenuItem = view
        coroutineScope.launch {
            with(ratingsDialog.context) {
                if (!readSettings(SHOW_RATINGS, true).first()) return@with
                if (readSettings(APP_START_COUNT, 1).first() > 4) {
                    rateAppAnimation.apply {
                        duration = 2500
                        start()
                    }
                }
            }
        }
    }

    fun onRate() {
        ratingsMenuItem?.let {
            rateAppAnimation.cancel()
        }
        coroutineScope.launch {
            ratingsDialog.context.apply {
                editSettings(SHOW_RATINGS, false)
                rateApp()
            }
        }
    }

    private fun setup(context: Context) {
        ratingsDialog.onReview = {
            context.rateApp()
            coroutineScope.launch {
                context.editSettings(SHOW_RATINGS, false)
            }
        }

        ratingsDialog.onNextTime = {
            coroutineScope.launch {
                val startCount = context.readSettings(APP_START_COUNT, 1).first()
                context.editSettings(APP_START_COUNT, startCount.minus(ratingsInterval))
            }
        }
    }

    private fun getRateAppInterpolator(yOffset: Float, frequency: Float): Interpolator {
        return Interpolator { time ->
            val value = cos(2 * Math.PI * frequency * time).toFloat() + yOffset
            if (value > 1f) 1f else value
        }
    }
}