package com.learning.android.chat

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.learning.android.R
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsNavigator: SettingsNavigator

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        settingsNavigator = SettingsNavigator(this, devicePolicyManager, adminComponent)

        // Set up click listeners for each button to open respective settings
        findViewById<Button>(R.id.btn_wifi).setOnClickListener { settingsNavigator.openWiFiSettings() }
        findViewById<Button>(R.id.btn_bluetooth).setOnClickListener { settingsNavigator.openBluetoothSettings() }
        findViewById<Button>(R.id.btn_location).setOnClickListener { settingsNavigator.openLocationSettings() }
        findViewById<Button>(R.id.btn_airplane).setOnClickListener { settingsNavigator.openAirplaneModeSettings() }
        findViewById<Button>(R.id.btn_data_usage).setOnClickListener { settingsNavigator.openDataUsageSettings() }
        findViewById<Button>(R.id.btn_display).setOnClickListener { settingsNavigator.openDisplaySettings() }
        findViewById<Button>(R.id.btn_battery_optimization).setOnClickListener { settingsNavigator.openBatteryOptimizationSettings() }
        findViewById<Button>(R.id.btn_application).setOnClickListener { settingsNavigator.openApplicationSettings() }
        findViewById<Button>(R.id.btn_notification).setOnClickListener { settingsNavigator.openNotificationSettings() }
        findViewById<Button>(R.id.btn_security).setOnClickListener { settingsNavigator.openSecuritySettings() }
        findViewById<Button>(R.id.btn_system).setOnClickListener { settingsNavigator.openSystemSettings() }
        findViewById<Button>(R.id.btn_developer_options).setOnClickListener { settingsNavigator.openDeveloperOptions() }
        findViewById<Button>(R.id.btn_sound).setOnClickListener { settingsNavigator.openSoundSettings() }
        findViewById<Button>(R.id.btn_date_time).setOnClickListener { settingsNavigator.openDateAndTimeSettings() }
        findViewById<Button>(R.id.btn_brightness).setOnClickListener { settingsNavigator.openBrightnessSettings() }
        findViewById<Button>(R.id.btn_accessibility).setOnClickListener { settingsNavigator.openAccessibilitySettings() }
        findViewById<Button>(R.id.btn_storage).setOnClickListener { settingsNavigator.openStorageSettings() }
        findViewById<Button>(R.id.btn_language).setOnClickListener { settingsNavigator.openLanguageSettings() }
        findViewById<Button>(R.id.btn_vpn).setOnClickListener { settingsNavigator.openVPNSettings() }
        findViewById<Button>(R.id.btn_cast).setOnClickListener { settingsNavigator.openCastSettings() }
        findViewById<Button>(R.id.btn_print).setOnClickListener { settingsNavigator.openPrintSettings() }
        findViewById<Button>(R.id.btn_usage_access).setOnClickListener { settingsNavigator.openUsageAccessSettings() }
        findViewById<Button>(R.id.btn_battery_saver).setOnClickListener { settingsNavigator.openBatterySaverSettings() }
        findViewById<Button>(R.id.btn_overlay).setOnClickListener { settingsNavigator.openOverlaySettings() }
        findViewById<Button>(R.id.btn_notification_listener).setOnClickListener { settingsNavigator.openNotificationListenerSettings() }
        findViewById<Button>(R.id.btn_do_not_disturb).setOnClickListener { settingsNavigator.openDoNotDisturbSettings() }
        findViewById<Button>(R.id.btn_mobile_network).setOnClickListener { settingsNavigator.openMobileNetworkSettings() }
        findViewById<Button>(R.id.btn_data_roaming).setOnClickListener { settingsNavigator.openDataRoamingSettings() }
        findViewById<Button>(R.id.btn_app_battery_optimization).setOnClickListener { settingsNavigator.openAppBatteryOptimizationSettings() }
        findViewById<Button>(R.id.btn_device_admin).setOnClickListener { settingsNavigator.openDeviceAdminSettings() }
        findViewById<Button>(R.id.btn_app_permissions).setOnClickListener { settingsNavigator.openAppPermissionsSettings() }
        findViewById<Button>(R.id.btn_write_settings).setOnClickListener { settingsNavigator.openWriteSettings() }
        findViewById<Button>(R.id.btn_notification_policy).setOnClickListener { settingsNavigator.openNotificationPolicyAccessSettings() }
        findViewById<Button>(R.id.btn_ignore_background_data_restriction).setOnClickListener { settingsNavigator.openBackgroundDataUsageSettings() }
        findViewById<Button>(R.id.btn_sim_lock).setOnClickListener {
            settingsNavigator.requestSimLock(
                this
            )
        }
        findViewById<Button>(R.id.btn_notification_shade_on).setOnClickListener {
            settingsNavigator.notificationShadeLock(
                this
            )
        }
        findViewById<Button>(R.id.btn_notification_shade_off).setOnClickListener {
            settingsNavigator.notificationShadeUnLock(
                this
            )
        }
    }
}
