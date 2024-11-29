package com.learning.android.chat

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.learning.android.R

class AlternateMainActivityAlias : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternate_main)

        val lockScreenSwitchAppButton: Button = findViewById<Button>(R.id.lockScreenSwitchAppButton)

        lockScreenSwitchAppButton.setOnClickListener {
            toggleAppIcon()
        }
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

}