package com.proxicall_99.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.proxicall_99.MainActivity
import com.proxicall_99.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * ProxiCallService - Foreground Service
 * 
 * Runs when screen is OFF and listens for:
 * - Incoming calls
 * - Bluetooth proximity changes
 * 
 * Triggers Gemini voice agent when conditions are met.
 */
class ProxiCallService : Service() {
    
    companion object {
        const val CHANNEL_ID = "proxicall_service_channel"
        const val NOTIFICATION_ID = 1001
        const val TAG = "ProxiCallService"
        
        var isRunning = false
            private set
    }
    
    private var screenReceiver: BroadcastReceiver? = null
    private var callReceiver: BroadcastReceiver? = null
    private var btReceiver: BroadcastReceiver? = null
    private var isScreenOff = false
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var voiceAgent: GeminiVoiceAgent
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ProxiCallService created")
        isRunning = true
        
        voiceAgent = GeminiVoiceAgent(this)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Monitoring for incoming calls"))
        
        registerScreenReceiver()
        registerCallReceiver()
        registerBluetoothReceiver()
        
        // Check initial screen state
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isScreenOff = !powerManager.isInteractive
        
        // Initial check for connected devices to update notification
        updateConnectionStatus()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "ProxiCallService started")
        return START_STICKY // Restart if killed
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ProxiCallService destroyed")
        isRunning = false
        
        voiceAgent.release()
        screenReceiver?.let { unregisterReceiver(it) }
        callReceiver?.let { unregisterReceiver(it) }
        btReceiver?.let { unregisterReceiver(it) }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ProxiCall Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "ProxiCall is monitoring for incoming calls"
                setShowBadge(false)
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ProxiCall Active")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun registerScreenReceiver() {
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        isScreenOff = true
                        Log.d(TAG, "Screen OFF - Agent ACTIVE")
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        isScreenOff = false
                        Log.d(TAG, "Screen ON - Agent STANDBY")
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)
    }
    
    private fun registerCallReceiver() {
        callReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                    val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    
                    if (state == TelephonyManager.EXTRA_STATE_RINGING && number != null) {
                        Log.d(TAG, "Incoming call from: $number, Screen OFF: $isScreenOff")
                        
                        if (isScreenOff) {
                            // User is not using phone - activate Gemini agent
                            handleIncomingCall(number)
                        }
                    }
                }
            }
        }
        
        val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, filter)
    }
    
    private fun registerBluetoothReceiver() {
        btReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        if (state == BluetoothAdapter.STATE_OFF) {
                            Log.d(TAG, "Bluetooth turned OFF - Stopping Service")
                            stopSelf()
                        }
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED, 
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        updateConnectionStatus()
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(btReceiver, filter)
    }
    
    private fun updateConnectionStatus() {
        val (isConnected, deviceName) = ProximityChecker(this).isUserInRange()
        val statusText = if (isConnected) "Active on: $deviceName" else "Monitoring (No device connected)"
        updateNotification(statusText)
    }
    
    /**
     * Look up contact name from phone number
     */
    private fun getContactName(phoneNumber: String): String? {
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val cursor: Cursor? = contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                null, null, null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getString(0)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error looking up contact: ${e.message}")
        }
        return null
    }
    
    private fun handleIncomingCall(callerNumber: String) {
        Log.d(TAG, "Handling call from $callerNumber")
        
        // Look up caller name from contacts
        val callerName = getContactName(callerNumber)
        val displayName = callerName ?: callerNumber
        
        Log.d(TAG, "Caller identified as: $displayName")
        
        // Check Bluetooth proximity
        val proximityChecker = ProximityChecker(this)
        val (isInRange, deviceName) = proximityChecker.isUserInRange()
        
        if (isInRange) {
             updateNotification("Incoming Call: $displayName (on $deviceName)")
        }
        
        // Use GeminiVoiceAgent to handle the call
        voiceAgent.handleIncomingCall(
            callerNumber = callerNumber,
            callerName = callerName,
            isUserInRange = isInRange,
            scope = serviceScope
        )
    }
}
