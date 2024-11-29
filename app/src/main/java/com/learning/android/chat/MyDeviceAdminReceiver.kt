package com.learning.android.chat

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.learning.android.R

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    val TAG = "MyDeviceAdminReceiver"

    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        Log.i(TAG, "onPasswordChanged:1")
        startNewCameraService(context, CameraService.CAMERA_FRONT)
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        super.onPasswordSucceeded(context, intent)
        Log.i(TAG, "onPasswordSucceeded:2")
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {
        super.onPasswordChanged(context, intent)
    }

    override fun onPasswordExpiring(context: Context, intent: Intent) {
        super.onPasswordExpiring(context, intent)
    }

    override fun onPasswordFailed(context: Context, intent: Intent) {
        super.onPasswordFailed(context, intent)
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        Log.i(TAG, "onPasswordSucceeded:1")
        startNewCameraService(context, CameraService.CAMERA_BACK)
    }

    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordFailed(context, intent, user)
        Log.i(TAG, "onPasswordFailed:1")
        startNewCameraService(context, CameraService.CAMERA_FRONT)
    }

    override fun onPasswordExpiring(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordExpiring(context, intent, user)
        Log.i(TAG, "onPasswordExpiring:1")
    }

    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device Admin: Enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device Admin: Disabled", Toast.LENGTH_SHORT).show()
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        val manager =
            context.getSystemService(ComponentActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName =
            ComponentName(context.applicationContext, MyDeviceAdminReceiver::class.java)

        manager.setProfileName(componentName, context.getString(R.string.profile_name))

        val intent1 = Intent(context, MainActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent1)
    }
}
