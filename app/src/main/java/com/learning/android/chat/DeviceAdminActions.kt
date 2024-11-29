package com.learning.android.chat

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.ComponentName
import android.location.LocationManager
import android.provider.Settings
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.net.ConnectivityManager

class DeviceAdminActions(private val context: Context, private val adminComponent: ComponentName) {
    private val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    // Lock the device screen
    fun lockDeviceScreen() {
        dpm.lockNow()
    }

    // Set a new password
    fun setNewPassword(newPassword: String) {
        dpm.resetPassword(newPassword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY)
    }

    fun setCameraEnabled(enable: Boolean) {
        dpm.setCameraDisabled(adminComponent, !enable)
    }

    fun factoryReset() {
        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun promptEnableLocationSettings() {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    fun restrictAppInstallations(restrict: Boolean) {
//        dpm.addUserRestriction(adminComponent, DevicePolicyManager.DISALLOW_INSTALL_UNKNOWN_SOURCES)
    }

    fun setAppUninstallDisabled(packageName: String, disable: Boolean) {
        dpm.setUninstallBlocked(adminComponent, packageName, disable)
    }

    fun enableKioskMode() {
        if (dpm.isDeviceOwnerApp(context.packageName)) {
            dpm.setLockTaskPackages(adminComponent, arrayOf(context.packageName))
            // Enable lock task mode on the activity:
            // activity.startLockTask()
        }
    }

    // Enable or disable Bluetooth
    fun setBluetoothEnabled(context: Context, enable: Boolean) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (enable) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.enable()
        } else {
            bluetoothAdapter.disable()
        }
    }

    // Enable or disable Wi-Fi
    fun setWifiEnabled(enable: Boolean) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = enable
    }

    fun startScreenPinning(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val lockTaskMode =
                activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            activity.startLockTask()
        }
    }

    fun rebootDevice() {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
        process.waitFor()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun clearAppData(packageName: String) {
//        dpm.clearApplicationUserData(adminComponent, packageName) { _, succeeded ->
//            if (succeeded) {
//                // Data cleared successfully
//            } else {
//                // Data clear failed
//            }
//        }
    }

    fun setUsbDataDisabled(disable: Boolean) {
//        dpm.addUserRestriction(adminComponent, DevicePolicyManager.DISALLOW_USB_FILE_TRANSFER)
    }

    fun setFactoryResetDisabled(disable: Boolean) {
//        dpm.addUserRestriction(adminComponent, DevicePolicyManager.DISALLOW_FACTORY_RESET)
    }

    fun disableKeyguard() {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val lock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE)
        lock.disableKeyguard()
    }

    fun toggleMobileData(enable: Boolean) {
        val command = if (enable) "svc data enable" else "svc data disable"
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        process.waitFor()
    }
    fun openAirplaneModeSettings() {
        context.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
    }
    fun getDataUsageStats() {
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", 0, System.currentTimeMillis())
        val rxBytes = bucket.rxBytes
        val txBytes = bucket.txBytes
    }
}
