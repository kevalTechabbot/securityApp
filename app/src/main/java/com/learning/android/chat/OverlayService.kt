//package com.learning.android.chat
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.graphics.PixelFormat
//import android.os.Build
//import android.os.IBinder
//import android.provider.Settings
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
//import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//import android.view.WindowManager
//import android.widget.Button
//import android.widget.TextView
//import androidx.core.app.NotificationCompat
//import com.learning.android.R
//
//class OverlayService : Service() {
//
//    private lateinit var windowManager: WindowManager
//    private var overlayView: View? = null
//    private val CHANNEL_ID = "OverlayServiceChannel"
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // Ensure overlay permission is granted
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            stopSelf()
//            return
//        }
//
//        // Create the notification channel
//        createNotificationChannel()
//
//        // Build the notification
//        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Overlay Service")
//            .setContentText("Overlay is running...")
//            .setSmallIcon(R.drawable.ic_notification_icon_white)
//            .build()
//
//        // Start the service in the foreground
//        startForeground(1, notification)
//
//        // Set up WindowManager and Layout parameters for the overlay
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        val layoutParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_PHONE
//            },
//            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            PixelFormat.TRANSLUCENT
//        )
//
//        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
//
//        // Inflate the overlay layout
//        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
//
//        // Set overlay UI elements and close button listener
//        overlayView?.apply {
//            findViewById<TextView>(R.id.overlayTextView).text = "Your Overlay Text Here"
//            findViewById<Button>(R.id.closeButton).setOnClickListener {
//                stopSelf()  // Stops the service, which will also remove the overlay
//            }
//
//            // Make the overlay fullscreen and immersive
//            systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                    SYSTEM_UI_FLAG_FULLSCREEN or
//                    SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        }
//
//        // Add the overlay view to the WindowManager
//        windowManager.addView(overlayView, layoutParams)
//
//        // Add touch listener to keep the overlay immersive
//        overlayView?.setOnTouchListener { _, _ ->
//            // Reset the UI flags on touch
//            overlayView?.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                    SYSTEM_UI_FLAG_FULLSCREEN or
//                    SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            false // Allow other touch events to be processed
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Remove the overlay view if it exists
//        overlayView?.let {
//            windowManager.removeView(it)
//            overlayView = null
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val serviceChannel = NotificationChannel(
//                CHANNEL_ID,
//                "Overlay Service Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager?.createNotificationChannel(serviceChannel)
//        }
//    }
//}


package com.learning.android.chat

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.learning.android.R

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val CHANNEL_ID = "OverlayServiceChannel"
    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            overlayView?.systemUiVisibility = (
                    SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            SYSTEM_UI_FLAG_FULLSCREEN or
                            SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    )
            handler.postDelayed(this, 100) // Repeat every 100 milliseconds
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Ensure overlay permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            stopSelf()
            return
        }

        // Create the notification channel
        createNotificationChannel()

        // Build the notification
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Overlay Service")
            .setContentText("Overlay is running...")
            .setSmallIcon(R.drawable.ic_notification_icon_white)
            .build()

        // Start the service in the foreground
        startForeground(1, notification)

        // Set up WindowManager and Layout parameters for the overlay
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

        // Inflate the overlay layout
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        // Set overlay UI elements and close button listener
        overlayView?.apply {
            findViewById<TextView>(R.id.overlayTextView).text = "Your Overlay Text Here"
            findViewById<Button>(R.id.closeButton).setOnClickListener {
                stopSelf()  // Stops the service, which will also remove the overlay
            }

            // Make the overlay fullscreen and immersive
            systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    SYSTEM_UI_FLAG_FULLSCREEN or
                    SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        // Add the overlay view to the WindowManager
        windowManager.addView(overlayView, layoutParams)

        // Start the runnable to maintain the immersive mode
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the overlay view if it exists
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
        handler.removeCallbacks(runnable) // Stop the handler
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Overlay Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}
