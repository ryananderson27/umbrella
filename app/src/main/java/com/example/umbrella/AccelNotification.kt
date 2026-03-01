package com.example.umbrella

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object AccelerometerNotificationHelper {
    const val CHANNEL_ID = "accelerometer_service_channel"
    const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Accelerometer Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun buildNotification(context: Context, contentText: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Accelerometer Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    fun updateNotification(context: Context, contentText: String) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(context, contentText))
    }
}