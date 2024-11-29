package com.learning.android.chat

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SMSReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        try {
            Log.d("TAG", "onReceive: Camera")
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<*>
                for (i in pdus.indices) {
                    val format = bundle.getString("format")
                    val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                    val sender = smsMessage.displayOriginatingAddress
                    val messageBody = smsMessage.messageBody

                    Log.d("SMSReceiver", "SMS received from $sender: $messageBody")

                    // Check for trigger keywords
                    if (messageBody.contains(
                            MediaCaptureService.CAPTURE_VIDEO_BACK,
                            ignoreCase = true
                        )
                    ) {
                        startVideoService(context, MediaCaptureService.CAMERA_BACK)
                    } else if (messageBody.contains(
                            MediaCaptureService.CAPTURE_VIDEO_FRONT,
                            ignoreCase = true
                        )
                    ) {
                        startVideoService(context, MediaCaptureService.CAMERA_FRONT)
                    } else if (messageBody.contains(
                            MediaCaptureService.CAPTURE_BACK,
                            ignoreCase = true
                        )
                    ) {
                        startNewCameraService(context, MediaCaptureService.CAMERA_BACK)
                    } else if (messageBody.contains(
                            MediaCaptureService.CAPTURE_FRONT,
                            ignoreCase = true
                        )
                    ) {
                        startNewCameraService(context, MediaCaptureService.CAMERA_FRONT)
                    } else if (messageBody.contains(
                            MediaCaptureService.CAPTURE_AUDIO,
                            ignoreCase = true
                        )
                    ) {
                        startAudioService(context)
                    } else if (messageBody.contains(
                            MediaCaptureService.SHOW_LOST_MESSAGE,
                            ignoreCase = true
                        )
                    ) {
                        showLostMessage(context)
                    } else if (messageBody.contains(
                            MediaCaptureService.START_ALERT_SOUND,
                            ignoreCase = true
                        )
                    ) {
                        startAlertSound(context)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SMSReceiver", "Exception caught $e")
        }
    }
}
