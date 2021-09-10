package com.sarftec.worldproverbs.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.sarftec.worldproverbs.notification.NotificationMaker
import com.sarftec.worldproverbs.share

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra(ACTION)?.let { action ->
            when(action) {
                SHARE -> {
                    val name = intent.getStringExtra(NAME)
                    val message = intent.getStringExtra(MESSAGE)
                    if(name == null || message == null) return
                    context.share("\"${message}\"\n\n_${name}", "Share", true)
                }
                DISMISS -> { }
            }
            NotificationManagerCompat.from(context).cancel(NotificationMaker.NOTIFICATION_ID)
        }
    }

    companion object {
        const val ACTION = "action"
        const val SHARE = "share"
        const val DISMISS = "dismiss"
        const val MESSAGE = "message"
        const val NAME = "name"
    }
}