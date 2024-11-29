package com.learning.android.chat

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.learning.android.R

class FakeShutdownActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DevicePolicyManager
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // Activate admin if not already activated
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            requestAdminPermission()
        }

        // Set the activity to full-screen immersive mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        // Set the content view to simulate a shutdown screen
        setContentView(R.layout.activity_fake_shutdown)

        // Lower the screen brightness
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0f
        window.attributes = layoutParams

        // Start lock task mode
        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ImageView>(R.id.shutdown_icon).isVisible = false
            findViewById<TextView>(R.id.shutdown_message).isVisible = false
        }, 1000)

        startLockTask()
    }

    override fun onBackPressed() {
        // Block back button
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true // Block all touch events
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true // Block all key events
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun requestAdminPermission() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Enable admin for shutdown simulation"
        )
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLockTask() // Ensure lock task mode stops if activity is destroyed
    }
}
