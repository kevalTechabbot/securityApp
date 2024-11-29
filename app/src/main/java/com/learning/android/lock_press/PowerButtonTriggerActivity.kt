package com.learning.android.lock_press

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learning.android.R

class PowerButtonTriggerActivity : AppCompatActivity() {

    private var powerButtonPressCount = 0
    private var lastScreenOffTime: Long = 0
    private var lastScreenOnTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val resetCounterRunnable = Runnable {
        powerButtonPressCount = 0 // Reset the count after timeout
    }
    private val requiredPressCount = 3 // Number of times power button needs to be pressed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register a broadcast receiver to listen for screen on/off events
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(screenReceiver, intentFilter)
    }

    // BroadcastReceiver to detect screen on/off events
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    lastScreenOffTime = System.currentTimeMillis()
                    powerButtonPressCount++
                    Log.d(
                        "PowerButtonTrigger",
                        "Power button pressed $powerButtonPressCount times (Screen OFF)"
                    )
                    Log.d("PowerButtonTrigger", "Screen OFF time: $lastScreenOffTime ms")

                    // Check if it's part of the rapid press
                    processPressEvent()
                }

                Intent.ACTION_SCREEN_ON -> {
                    lastScreenOnTime = System.currentTimeMillis()
                    Log.d("PowerButtonTrigger", "Screen ON time: $lastScreenOnTime ms")

                    // Optionally, check the time between ON and OFF events
                    val duration = lastScreenOnTime - lastScreenOffTime
                    Log.d("PowerButtonTrigger", "Time between OFF and ON: $duration ms")

                    // Check if it's part of the rapid press
                    processPressEvent()
                }
            }
        }
    }

    private fun processPressEvent() {
        // Reset the counter if no further presses within 2 seconds
        handler.removeCallbacks(resetCounterRunnable)
        handler.postDelayed(resetCounterRunnable, 2000)

        // Check if the power button has been pressed 3 times
        if (powerButtonPressCount >= requiredPressCount) {
            triggerEvent()
            powerButtonPressCount = 0 // Reset after triggering the event
        }
    }

    private fun triggerEvent() {
        // Action to perform after detecting 3 presses of the power button
        Toast.makeText(this, "Power button pressed 3 times! Event triggered!", Toast.LENGTH_LONG)
            .show()
        Log.d("PowerButtonTrigger", "Event Triggered!")
        // Add any action you want to trigger here
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        unregisterReceiver(screenReceiver)
    }
}
