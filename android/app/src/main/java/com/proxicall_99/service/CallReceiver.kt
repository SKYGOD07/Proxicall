package com.proxicall_99.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

/**
 * CallReceiver - Broadcast Receiver for incoming calls
 * 
 * This is registered in the manifest for static listening.
 * When a call comes in, it ensures ProxiCallService is running.
 */
class CallReceiver : BroadcastReceiver() {
    
    companion object {
        const val TAG = "CallReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Log.d(TAG, "Phone ringing from: $number")
                    
                    // Ensure service is running
                    ensureServiceRunning(context)
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d(TAG, "Call answered")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d(TAG, "Call ended")
                }
            }
        }
    }
    
    private fun ensureServiceRunning(context: Context) {
        if (!ProxiCallService.isRunning) {
            Log.d(TAG, "Starting ProxiCallService")
            val serviceIntent = Intent(context, ProxiCallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
