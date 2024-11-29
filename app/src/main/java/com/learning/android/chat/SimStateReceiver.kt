package com.learning.android.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SimStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        /*if (intent.action == TelephonyManager.ACTION_SIM_CARD_STATE_CHANGED ||
            intent.action == TelephonyManager.ACTION_SIM_STATE_CHANGED) {

            val simState = intent.getIntExtra(TelephonyManager.EXTRA_SIM_STATE, TelephonyManager.SIM_STATE_UNKNOWN)

            when (simState) {
                TelephonyManager.SIM_STATE_ABSENT -> Log.d("SimStateReceiver", "SIM card removed")
                TelephonyManager.SIM_STATE_READY -> Log.d("SimStateReceiver", "SIM card is ready")
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> Log.d("SimStateReceiver", "SIM card requires PIN")
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> Log.d("SimStateReceiver", "SIM card requires PUK")
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> Log.d("SimStateReceiver", "SIM card is network locked")
                else -> Log.d("SimStateReceiver", "SIM state changed: $simState")
            }
        }*/
    }
}
