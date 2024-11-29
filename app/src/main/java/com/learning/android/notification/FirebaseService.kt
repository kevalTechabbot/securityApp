package com.learning.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.learning.android.R

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Optionally, send the token to your server or use it as needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Build the notification
        val builder = NotificationCompat.Builder(this, "myNotification")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(message.notification?.title.toString())
            .setContentText(message.notification?.body.toString())
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line...")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Handle Android O and later for notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                "myNotification",
                "My Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Message Notifications"
                lightColor = Color.CYAN
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, builder.build())
        } else {
            // For older versions, simply show the notification
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, builder.build())
        }
    }

    companion object {
        private const val TAG = "FirebaseService"
    }
}
