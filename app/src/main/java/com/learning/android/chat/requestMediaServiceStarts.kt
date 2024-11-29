package com.learning.android.chat

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
val serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or
        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA


fun startCameraService(context: Context, cameraType: String) {
    val captureIntent = Intent(context, CameraService::class.java)
    captureIntent.putExtra(
        CameraService.CAMERA_TYPE_EXTRA, cameraType
    )  // Pass camera type (front/back)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            captureIntent.putExtra("serviceType", serviceType)
        }

        context.startService(captureIntent)
    } else {
        context.startService(captureIntent)
    }
}

fun startVideoService(
    context: Context,
    cameraType: String,
) {
    val captureIntent = Intent(context, MediaCaptureService::class.java)
    captureIntent.putExtra(
        MediaCaptureService.MEDIA_TYPE_EXTRA,
        MediaCaptureService.MEDIA_TYPE_VIDEO
    )
    captureIntent.putExtra(
        MediaCaptureService.CAMERA_TYPE_EXTRA, cameraType
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            captureIntent.putExtra("serviceType", serviceType)
        }
        context.startService(captureIntent)
    } else {
        context.startService(captureIntent)
    }
}

fun startNewCameraService(
    context: Context,
    cameraType: String,
) {
    val captureIntent = Intent(context, MediaCaptureService::class.java)
    captureIntent.putExtra(
        MediaCaptureService.MEDIA_TYPE_EXTRA,
        MediaCaptureService.MEDIA_TYPE_IMAGE
    )
    captureIntent.putExtra(
        MediaCaptureService.CAMERA_TYPE_EXTRA, cameraType
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            captureIntent.putExtra("serviceType", serviceType)
        }
        context.startService(captureIntent)
    } else {
        context.startService(captureIntent)
    }
}

fun startAudioService(
    context: Context,
) {
    val captureIntent = Intent(context, MediaCaptureService::class.java)
    captureIntent.putExtra(
        MediaCaptureService.MEDIA_TYPE_EXTRA,
        MediaCaptureService.MEDIA_TYPE_AUDIO
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            captureIntent.putExtra("serviceType", serviceType)
        }
        context.startService(captureIntent)
    } else {
        context.startService(captureIntent)
    }
}

fun showLostMessage(context: Context) {
    context.startService(Intent(context, OverlayService::class.java))
}


fun startAlertSound(context: Context) {
    // Prompt user to enable accessibility service
    val serviceIntent = Intent(context, AlarmService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(serviceIntent)
    }
}
