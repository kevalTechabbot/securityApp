package com.learning.android.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.learning.android.R
import com.learning.android.chat.preferences.TileTextFormatter
import java.time.Duration

/**
 * This service handles all interaction in Quick Settings panel. It shows "Start Listening" and
 * "Stop listening" when the Quick Settings panel is shown/hidden. It shows the dialog, if clicked.
 * It asks you to unlock, if you click it from locked screen.
 */

class WifiTileService : TileService() {
    private var isTappableTileEnabled = false
    private var shouldEmulatePowerSaveTile = false
    private var isCharging = false

    private fun setActiveLabelText(text: String) {
        if (getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("infoInTitle", false)) {
            qsTile.label = text
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = getString(R.string.battery_tile_label)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle = text
            }
        }
    }

    private fun setBatteryInfo(intent: Intent?) {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
        val batteryLevel = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val plugState = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val batteryState = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        val isPluggedIn =
            plugState == BatteryManager.BATTERY_PLUGGED_AC || plugState == BatteryManager.BATTERY_PLUGGED_USB || plugState == BatteryManager.BATTERY_PLUGGED_WIRELESS
        val isFullyCharged = isPluggedIn && batteryState == BatteryManager.BATTERY_STATUS_FULL
        isCharging = batteryState == BatteryManager.BATTERY_STATUS_CHARGING

        if (isTappableTileEnabled) {
            qsTile.state = if (isCharging) Tile.STATE_INACTIVE else (if (getSystemService(
                    PowerManager::class.java
                ).isPowerSaveMode
            ) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE)
        }

        if (isPluggedIn && getSharedPreferences(
                "preferences",
                MODE_PRIVATE
            ).getBoolean("dynamic_tile_icon", true)
        ) {
            when (plugState) {
                BatteryManager.BATTERY_PLUGGED_AC -> qsTile.icon =
                    Icon.createWithResource(this, android.R.drawable.star_on)

                BatteryManager.BATTERY_PLUGGED_USB -> qsTile.icon =
                    Icon.createWithResource(this, android.R.drawable.star_off)

                BatteryManager.BATTERY_PLUGGED_WIRELESS -> qsTile.icon =
                    Icon.createWithResource(this, android.R.drawable.alert_dark_frame)

                else -> qsTile.icon =
                    Icon.createWithResource(this, R.drawable.ic_qs_battery)
            }
        }

        if (isFullyCharged) {
            val customTileText =
                getSharedPreferences("preferences", MODE_PRIVATE).getString("charging_text", "")
            setActiveLabelText(
                if (customTileText!!.isEmpty()) getString(R.string.fully_charged) else TileTextFormatter(
                    this
                ).format(customTileText)
            )
            if (!isTappableTileEnabled) qsTile.state = getTileState(true)
        } else if (isCharging) {
            val customTileText =
                getSharedPreferences("preferences", MODE_PRIVATE).getString("charging_text", "")
            if (!isTappableTileEnabled) qsTile.state = getTileState(true)

            if (customTileText!!.isNotEmpty()) {
                setActiveLabelText(TileTextFormatter(this).format(customTileText))
            } else {
                val remainingTime =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        getSystemService(BatteryManager::class.java).computeChargeTimeRemaining()
                    } else {
                        0
                    }

                // computeChargeTimeRemaining() returns 0 at times for some reason, so check for < 1, not -1
                if (remainingTime < 1) {
                    setActiveLabelText(getString(R.string.charging_no_time_estimate, batteryLevel))
                } else if (remainingTime <= 60000) {
                    // case for when less than 1m is remaining - duration returns 0 minutes if less than 1m which is undesirable
                    setActiveLabelText(
                        getString(
                            R.string.charging_less_than_one_hour_left,
                            batteryLevel,
                            1
                        )
                    )
                } else {
                    val duration = Duration.ofMillis(remainingTime)
                    val hours = duration.toHours()
                    val minutes = duration.minusHours(hours).toMinutes()

                    if (hours > 0) {
                        setActiveLabelText(
                            getString(
                                R.string.charging_more_than_one_hour_left,
                                batteryLevel,
                                hours,
                                minutes
                            )
                        )
                    } else {
                        setActiveLabelText(
                            getString(
                                R.string.charging_less_than_one_hour_left,
                                batteryLevel,
                                minutes
                            )
                        )
                    }
                }
            }
        } else {
            val customTileText =
                getSharedPreferences("preferences", MODE_PRIVATE).getString("discharging_text", "")
            setActiveLabelText(
                if (customTileText!!.isEmpty()) "$batteryLevel%" else TileTextFormatter(
                    this
                ).format(customTileText)
            )
            if (!isTappableTileEnabled) qsTile.state = getTileState(false)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_qs_battery)
        }

        qsTile.updateTile()
    }

    private fun setPowerSaveInfo() {
        val shouldEmulatePowerSaveTile =
            getSharedPreferences("preferences", MODE_PRIVATE).getBoolean(
                "emulatePowerSaveTile",
                false
            )

        if (getSystemService(PowerManager::class.java).isPowerSaveMode) {
            qsTile.state = Tile.STATE_ACTIVE
            if (shouldEmulatePowerSaveTile) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle =
                    getString(R.string.power_saver_tile_on_subtitle)
            }
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            if (shouldEmulatePowerSaveTile) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                qsTile.subtitle =
                    getString(R.string.power_saver_tile_off_subtitle)
            }
        }

        qsTile.updateTile()
    }

    private var batteryStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setBatteryInfo(intent)
        }
    }

    private var powerSaveModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setPowerSaveInfo()
        }
    }

    override fun onStartListening() {
        shouldEmulatePowerSaveTile = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean(
            "emulatePowerSaveTile",
            false
        )
        isTappableTileEnabled = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean(
            "tappableTileEnabled",
            false
        )

        val batteryChangedIntent =
            registerReceiver(batteryStateReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))!!

        val status = batteryChangedIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        isCharging =
            status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        if (shouldEmulatePowerSaveTile) {
            unregisterReceiver(batteryStateReceiver)
            registerReceiver(
                powerSaveModeReceiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            )
            qsTile.label = getString(R.string.power_save_tile_label)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_battery_saver)

            if (isCharging) {
                qsTile.state = Tile.STATE_UNAVAILABLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    qsTile.subtitle = getString(R.string.power_save_tile_unavailable_subtitle)
                }
                qsTile.updateTile()
            } else {
                setPowerSaveInfo()
            }
        } else {
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_qs_battery)
            qsTile.label = getString(R.string.battery_tile_label)

            if (!isTappableTileEnabled) {
                qsTile.state = getTileState(isCharging)
            } else {
                val powerSaveChangedFilter =
                    IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
                registerReceiver(powerSaveModeReceiver, powerSaveChangedFilter)
                setPowerSaveInfo()
            }

            setBatteryInfo(batteryChangedIntent)
        }
    }

    private fun getTileState(isCharging: Boolean): Int {
        return when (getSharedPreferences("preferences", MODE_PRIVATE).getInt("tileState", 0)) {
            0 -> Tile.STATE_ACTIVE
            1 -> if (isCharging) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            else -> Tile.STATE_INACTIVE
        }
    }

    override fun onClick() {
        super.onClick()
        if (qsTile.state == Tile.STATE_INACTIVE) {
            qsTile.state = Tile.STATE_ACTIVE
            Toast.makeText(this, "Tile activated", Toast.LENGTH_SHORT).show()
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            Toast.makeText(this, "Tile deactivated", Toast.LENGTH_SHORT).show()
        }
        // Update the tile to reflect the new state
        qsTile.updateTile()


        Log.i("TAG", "onClick: comng here")
        if (!isTappableTileEnabled || isCharging) return

        val isInPowerSaveMode = getSystemService(PowerManager::class.java).isPowerSaveMode

        Settings.Global.putInt(contentResolver, "low_power", if (isInPowerSaveMode) 0 else 1)
        qsTile.state = if (isInPowerSaveMode) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onStopListening() {
        if (isTappableTileEnabled) {
            unregisterReceiver(powerSaveModeReceiver)
        }
        if (!shouldEmulatePowerSaveTile) {
            unregisterReceiver(batteryStateReceiver)
        }
    }
}