package com.learning.android.lock_press

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.learning.android.chat.BootReceiver

class PowerButtonDetectionService : Service() {

    private val channelId = "PowerButtonChannel"
    private val notificationId = 1001
    private lateinit var powerButtonReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()

        // Create and register the broadcast receiver for screen on/off events
        powerButtonReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("TAG", "Device rebooted. Restarting service...123")
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> handlePowerButtonPress(
                        "Screen OFF",
                        this@PowerButtonDetectionService
                    )

                    Intent.ACTION_SCREEN_ON -> handlePowerButtonPress(
                        "Screen ON",
                        this@PowerButtonDetectionService
                    )
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(powerButtonReceiver, filter)
    }

    private fun handlePowerButtonPress(
        event: String,
        activity: PowerButtonDetectionService
    ) {
        // Handle power button press (screen on/off)
        // You can trigger actions here based on the event
        Log.d("TAG", "handlePowerButtonPress: Pressed button")
        Toast.makeText(activity, "Pressed button", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        // Build the notification for the foreground service
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_power_off)
            .setContentTitle("Power Button Detection")
            .setContentText("Monitoring power button presses")
            .setPriority(NotificationCompat.PRIORITY_LOW).build()

//        startForeground(notificationId, notification)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Power Button Detection", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the service is destroyed
        unregisterReceiver(powerButtonReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
