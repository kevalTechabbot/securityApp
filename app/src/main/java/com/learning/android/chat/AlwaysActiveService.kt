package com.learning.android.chat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.learning.android.R

class AlwaysActiveService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your background task or monitoring logic here
        Log.d(TAG, "Service is running...")
        adminPermission()

        // Return START_STICKY to ensure the service restarts if the system kills it
        return START_STICKY
    }

    private fun adminPermission() {
        val deviceAdminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        if (!dpm.isAdminActive(deviceAdminComponent)) {
            Log.d("ReinitializeService", "Device admin is not active!")
        } else {
            // Perform any admin-required operations here
            Log.d("ReinitializeService", "Device admin active, performing tasks.")
        }

        // Stop the service once done
//        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed. Restarting...")
        restartService() // Ensure the service restarts
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This is a non-bound service, return null
        return null
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "persistent_service_channel"
        val channelName = "Persistent Service"

        // Check API level to create NotificationChannel only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "This is a persistent service"
            notificationManager.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId).setContentTitle("Service Running")
                .setContentText("This service is always active.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true) // Ensures the notification cannot be dismissed
                .build()
        } else {
            NotificationCompat.Builder(this, channelId).setContentTitle("Service Running")
                .setContentText("This service is always active.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true) // Ensures the notification cannot be dismissed
                .build()
        }

//        startForeground(1, notification)
    }

    private fun restartService() {
        val restartIntent = Intent(this, AlwaysActiveService::class.java)
        startService(restartIntent)
    }

    companion object {
        private const val TAG = "AlwaysActiveService"
    }
}
