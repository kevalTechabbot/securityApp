package com.learning.android.chat.preferences

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import java.text.DecimalFormat
import kotlin.math.abs

/**
 * Class for user-configurable scanf-style formatting of tile text.
 */
class TileTextFormatter(context: Context) {
    private val formatters = HashMap<String, Any>()

    init {
        val bm = context.getSystemService(BatteryManager::class.java)
        val batteryIntent =
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val numberFormat = DecimalFormat("#.##")
        assert(batteryIntent != null)
        val instantaneousCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val voltage = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) // provided as mV

        // CURRENT_NOW and CURRENT_AVERAGE are positive when charging and negative when discharging
        if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
            // Samsung devices, for some reason, returns this in milli-amperes instead of microamperes like the Android documentation specifies
            formatters["c"] = instantaneousCurrent
            formatters["a"] = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        } else {
            // Android provides these as microamperes, so divide by 1000 to convert to mA
            formatters["c"] = instantaneousCurrent / 1000
            formatters["a"] =
                bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) / 1000
        }

        // Temperature of the battery
        formatters["t"] =
            batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f // Celsius
        formatters["f"] =
            (batteryIntent.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                0
            ) / 10 * 1.8 + 32).toInt() // Fahrenheit

        // Voltage of the battery - provided as mV by Android, so divide by 1000 to get volts
        formatters["v"] = numberFormat.format((voltage / 1000f).toDouble())

        // Wattage of the battery - Power = I * V (divide instantaneousCurrent by 1,000,000 to get amps)
        formatters["w"] =
            numberFormat.format(abs((instantaneousCurrent / 1000000f * voltage / 1000f).toDouble()))

        // Battery level
        formatters["l"] = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
    }

    fun format(format: String): String {
        val formatted = StringBuilder()

        var i = 0
        while (i < format.length) {
            if (format[i] == '%') {
                if (i + 1 < format.length) {
                    val formatter = format.substring(i + 1, i + 2)
                    if (formatters.containsKey(formatter)) {
                        formatted.append(formatters[formatter])
                        i++
                    } else {
                        formatted.append('%')
                    }
                } else {
                    formatted.append('%')
                }
            } else {
                formatted.append(format[i])
            }
            i++
        }

        return formatted.toString()
    }
}
