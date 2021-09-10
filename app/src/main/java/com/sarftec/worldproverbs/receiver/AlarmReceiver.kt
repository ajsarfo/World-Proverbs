package com.sarftec.worldproverbs.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sarftec.worldproverbs.data.repository.Repository
import com.sarftec.worldproverbs.notification.NotificationMaker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var notificationMaker: NotificationMaker

    override fun onReceive(context: Context, intent: Intent) {
        runBlocking {
            repository.random(100)
                .filter { it.message.length in 100..250 }
                .randomOrNull()
                ?.let { notificationMaker.notify(it) }
        }
    }
}