package com.learning.android.chat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.learning.android.R

class AlarmService : Service() {

    private lateinit var audioManager: AudioManager
    private val CHANNEL_ID = "AlarmServiceChannel"
    private var maxVolumeSet = false
    private lateinit var volumeObserver: ContentObserver
    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        // Initialize AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set up the notification channel
        createNotificationChannel()

        // Build the notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm Service")
            .setContentText("Alarm is running at maximum volume...")
            .setSmallIcon(R.drawable.ic_notification_icon_white)
            .build()

        // Start the service in the foreground
        startForeground(1, notification)

        // Set volume to max and start alarm
        setVolumeToMax()
        playAlarmSound()

        // Observe volume changes
        registerVolumeObserver()

        // Stop monitoring after 10 seconds
        handler.postDelayed({
            stopSelf()
        }, 10000)
    }

    private fun setVolumeToMax() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
    }

    private fun playAlarmSound() {
        // Initialize MediaPlayer with an alarm sound
        mediaPlayer =
            MediaPlayer.create(this, R.raw.alarm_sound)  // Place your alarm sound in res/raw folder

        // Set MediaPlayer volume to max
        mediaPlayer?.setVolume(1.0f, 1.0f)
        mediaPlayer?.isLooping = true  // Optional: make it loop until service stops

        // Start playing
        mediaPlayer?.start()
    }

    private fun registerVolumeObserver() {
        val resolver: ContentResolver = contentResolver
        volumeObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                setVolumeToMax()
            }
        }
        resolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(volumeObserver)
        mediaPlayer?.stop()       // Stop playing sound when the service is destroyed
        mediaPlayer?.release()    // Release resources used by MediaPlayer
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
