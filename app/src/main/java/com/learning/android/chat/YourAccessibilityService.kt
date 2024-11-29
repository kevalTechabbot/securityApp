package com.learning.android.chat

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class YourAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i("TAG", "onAccessibilityEvent: $event")
        if (event != null) {
            Log.i("TAG", "onAccessibilityEvent: ${event.eventType}")
            Log.i("TAG", "onAccessibilityEvent: ${event.className}")
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    // This can indicate the notification shade being opened
                    if (event.className == "com.android.systemui.statusbar.StatusBar") {
                        // Notification shade opened
                        // Do something here
                        Log.i("TAG", "onAccessibilityEvent: here")
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        // Handle interrupt
    }

    // Optional: You might want to override this to monitor the service status
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("YourAccessibilityService", "Accessibility Service Connected")
    }
}
