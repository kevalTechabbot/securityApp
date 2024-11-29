package com.learning.android.chat

import android.content.Context
import android.os.PowerManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Handle the notification here
        val packageName = sbn.packageName
        val message = sbn.notification.extras.getString("android.text")
        Log.e("Message_notification 1->", message.toString())

        val messageBody = message.toString()

        try {
            // Check for specific commands in the notification's message
            when {
                messageBody.contains(MediaCaptureService.CAPTURE_VIDEO_BACK, ignoreCase = true) -> {
                    startVideoService(this, MediaCaptureService.CAMERA_BACK)
                }

                messageBody.contains(
                    MediaCaptureService.CAPTURE_VIDEO_FRONT,
                    ignoreCase = true
                ) -> {
                    startVideoService(this, MediaCaptureService.CAMERA_FRONT)
                }

                messageBody.contains(MediaCaptureService.CAPTURE_BACK, ignoreCase = true) -> {
                    startNewCameraService(this, MediaCaptureService.CAMERA_BACK)
                }

                messageBody.contains(MediaCaptureService.CAPTURE_FRONT, ignoreCase = true) -> {
                    startNewCameraService(this, MediaCaptureService.CAMERA_FRONT)
                }

                messageBody.contains(MediaCaptureService.CAPTURE_AUDIO, ignoreCase = true) -> {
                    startAudioService(this)
                }

                messageBody.contains(MediaCaptureService.SHOW_LOST_MESSAGE, ignoreCase = true) -> {
                    showLostMessage(this)
                }
                messageBody.contains(MediaCaptureService.START_ALERT_SOUND, ignoreCase = true) -> {
                    startAlertSound(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Wake the screen
        wakeScreen()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal, if needed
    }

    private fun wakeScreen() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp::MyWakelockTag"
        )
        wakeLock.acquire(3000) // Wake the screen for 3 seconds
    }

}
