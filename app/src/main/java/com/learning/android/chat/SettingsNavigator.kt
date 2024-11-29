package com.learning.android.chat

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.ProxyInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Proxy

class SettingsNavigator(
    private val context: Context,
    private val devicePolicyManager: DevicePolicyManager,
    private val componentName: ComponentName
) {
    // Open Wi-Fi settings

    fun openWiFiSettings() {
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    // Open Bluetooth settings
    fun openBluetoothSettings() {
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    // Open Location settings
    fun openLocationSettings() {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    // Open Airplane mode settings
    fun openAirplaneModeSettings() {
        context.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
    }

    // Open Data usage settings
    fun openDataUsageSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS))
        }
    }

    // Open Display settings
    fun openDisplaySettings() {
        context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }

    // Open Battery optimization settings
    fun openBatteryOptimizationSettings() {
        context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
    }

    // Open Application settings
    fun openApplicationSettings() {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
    }

    // Open Notification settings
    fun openNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            })
        }
    }

    // Open Security settings
    fun openSecuritySettings() {
        context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    }

    // Open System settings
    fun openSystemSettings() {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    // Open Developer options (if enabled)
    fun openDeveloperOptions() {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
    }

    // Open Sound settings
    fun openSoundSettings() {
        context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS))
    }

    // Open Date and Time settings
    fun openDateAndTimeSettings() {
        context.startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
    }

    // Open Display - Brightness settings
    fun openBrightnessSettings() {
        context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
    }

    // Open Accessibility settings
    fun openAccessibilitySettings() {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    // Open Storage settings
    fun openStorageSettings() {
        context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
    }

    // Open Language and Input settings
    fun openLanguageSettings() {
        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

    // Open VPN settings
    fun openVPNSettings() {
        context.startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
    }

    // Open Cast settings (for screen mirroring)
    fun openCastSettings() {
        context.startActivity(Intent(Settings.ACTION_CAST_SETTINGS))
    }

    // Open Print settings
    fun openPrintSettings() {
        context.startActivity(Intent(Settings.ACTION_PRINT_SETTINGS))
    }

    // Open App-specific Usage Access settings
    fun openUsageAccessSettings() {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    // Open App-specific Battery Saver settings (Android 6.0+)
    fun openBatterySaverSettings() {
        context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
    }

    // Open App-specific Display over other apps settings
    fun openOverlaySettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    // Open App-specific Notification Listener Access settings
    fun openNotificationListenerSettings() {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    // Open App-specific Do Not Disturb Access settings (Android 6.0+)
    fun openDoNotDisturbSettings() {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    // Open Mobile Network settings
    fun openMobileNetworkSettings() {
        context.startActivity(Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS))
    }

    // Open Data Roaming settings
    fun openDataRoamingSettings() {
        context.startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
    }

    // Open Background Data Usage settings (App-specific)
    fun openBackgroundDataUsageSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    // Open App-specific Battery Optimization settings
    fun openAppBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    // Open device admin settings
    fun openDeviceAdminSettings() {
        context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    }

    // Open Manage App Permissions settings (Android 6.0+)
    fun openAppPermissionsSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        context.startActivity(intent)
    }

    // Open Write Settings Permission
    fun openWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }

    // Open Notification Policy Access settings (Do Not Disturb access)
    fun openNotificationPolicyAccessSettings() {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    fun lockScreen() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()
        }
    }

    fun setPasswordQuality() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setPasswordQuality(
                componentName,
                DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
            )
        }
    }

    fun wipeDeviceInternalData() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.wipeData(0)
        }
    }

    fun wipeDeviceExternalData() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE)
        }
    }

    fun setPasswordExpiration(timeoutMillis: Long) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setPasswordExpirationTimeout(componentName, timeoutMillis)
        }
    }

    fun setPasswordHistoryLength(length: Int) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setPasswordHistoryLength(componentName, length)
        }
    }

    fun setMaximumFailedPasswordsBeforeWipe(maxAttempts: Int) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setMaximumFailedPasswordsForWipe(componentName, maxAttempts)
        }
    }

    fun setCameraDisabled(disable: Boolean) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setCameraDisabled(componentName, disable)
        }
    }


    fun setKeyguardDisabledFeatures(features: Int) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setKeyguardDisabledFeatures(componentName, features)
        }
    }

    fun disableKeyguard() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.setKeyguardDisabled(componentName, true)
        }
    }


    fun enableNetworkLogging() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                devicePolicyManager.setNetworkLoggingEnabled(componentName, true)
            }
        }
    }

    fun addUserRestriction(restriction: String) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.addUserRestriction(componentName, restriction)
        }
    }

    fun clearUserRestriction(restriction: String) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.clearUserRestriction(componentName, restriction)
        }
    }

    fun setGlobalProxy(/*proxyHost: String, proxyPort: Int*/) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            val proxyHost = "your.proxy.host"
            val proxyPort = 8080
            val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort))
            System.setProperty("http.proxyHost", proxyHost)
            System.setProperty("http.proxyPort", proxyPort.toString())
//            devicePolicyManager.setRecommendedGlobalProxy(componentName, proxy)
        }
    }

    fun setTimeRestrictions(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        if (devicePolicyManager.isAdminActive(componentName)) {
//            devicePolicyManager.setDailyMaintenanceWindow(
//                componentName,
//                startHour,
//                startMinute,
//                endHour,
//                endMinute
//            )
        }
    }

    fun setUsbDataDisabled(disable: Boolean) {
        if (devicePolicyManager.isAdminActive(componentName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                devicePolicyManager.setUsbDataSignalingEnabled(!disable)
            }
        }
    }

    fun setSafeBootDisabled(disable: Boolean) {
        if (devicePolicyManager.isAdminActive(componentName)) {
//            devicePolicyManager.setSafeBootDisabled(componentName, disable)
        }
    }

    fun setSuperUserAccess() {
        try {
//            val process = Runtime.getRuntime().exec("pm list packages") // "su" is for superuser access
//            val process = Runtime.getRuntime().exec("su") // "su" is for superuser access
//            val process = Runtime.getRuntime().exec("am start -n com.learning.android.chat/.MainActivity") // "su" is for superuser access
            val process = Runtime.getRuntime()
                .exec("dpm set-device-owner com.learning.android/.chat.MyDeviceAdminReceiver") // "su" is for superuser access
            val outputStream = process.outputStream
            val inputStream = process.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val output = StringBuffer()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            Log.d("CMD Output", output.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hasRootPermission(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.write("exit\n".toByteArray())
            process.waitFor()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun requestNotificationAccess(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
        Toast.makeText(context, "Please enable notification access for this app", Toast.LENGTH_LONG)
            .show()
    }

    fun requestSimLock(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
            context.startActivity(intent)



//            val intent = Intent()
//            intent.setClassName(
//                "com.android.settings",
//                "com.android.settings.Settings\$FactoryResetActivity"
//            )
//            context.startActivity(intent)
//            val intent = Intent("android.settings.FACTORY_RESET_SETTINGS")
//            context.startActivity(intent)


//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.component = ComponentName(
//                "com.android.settings",
//                "com.android.settings.Settings\$FactoryResetActivity"
//            )
//            context.startActivity(intent)


//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.component = ComponentName("com.android.settings", "com.android.settings.Settings\$PrivacySettingsActivity")
//            context.startActivity(intent)

//            val intent = Intent(Settings.ACTION_PRIVACY_SETTINGS)
//            context.startActivity(intent)

//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.component = ComponentName(
//                "com.android.settings",
//                "com.android.settings.action.FACTORY_RESET"
//            )
//            context.startActivity(intent)

//            val intent = Intent()
//            intent.setComponent(ComponentName("com.android.settings", "com.android.settings.Settings$FactoryResetActivity"))
//            intent.action = "com.android.settings.action.FACTORY_RESET"
//            context.startActivity(intent)


        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("TAG", "requestSimLock: ${e.localizedMessage}")
            Toast.makeText(
                context,
                "SIM Lock Settings not accessible on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private lateinit var overlayView: View
    private lateinit var windowManager: WindowManager

    fun notificationShadeLock(context: Activity) {
//        devicePolicyManager.setLockTaskPackages(componentName, arrayOf(context.packageName))
//        context.startLockTask() // Starts kiosk mode
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        // Setting up the overlay view
        overlayView = View(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
            setOnTouchListener { _, _ -> true }  // Block touches
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            100,  // Height of the overlay
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP

        // Add the overlay view
        windowManager.addView(overlayView, params)

    }

    fun notificationShadeUnLock(context: Activity) {
//        devicePolicyManager.setLockTaskPackages(componentName, arrayOf(context.packageName))
//        context.stopLockTask() // Starts kiosk mode
        if (::overlayView.isInitialized && overlayView.isAttachedToWindow) {
            windowManager.removeView(overlayView)
        }
    }

    fun requestOverlayPermission(context: Context) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            context.startActivity(intent)
        }
    }

    fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        val packageName = context.packageName
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

    fun requestNotificationListenerPermission(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
    }
}
