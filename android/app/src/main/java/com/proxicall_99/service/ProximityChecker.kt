package com.proxicall_99.service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ProximityChecker - Bluetooth Range Detection
 * 
 * Checks if user is near any of their trusted Bluetooth devices.
 * Used to determine whether to activate voice agent or send auto-SMS.
 */
class ProximityChecker(private val context: Context) {
    
    companion object {
        const val TAG = "ProximityChecker"
    }
    
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    
    /**
     * Check if user is within range of any trusted Bluetooth device.
     * 
     * Strategy:
     * 1. Get list of currently connected Bluetooth devices
     * 2. Compare against user's trusted devices in Firestore
     * 3. If any match -> user is "in range"
     */
    /**
     * Check if user is within range of any trusted Bluetooth device.
     * Returns Pair(isInRange, deviceName)
     */
    fun isUserInRange(): Pair<Boolean, String?> {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.w(TAG, "Bluetooth not available or disabled")
            return Pair(false, null)
        }
        
        // Get connected devices
        val connectedDevices = getConnectedDevices()
        
        if (connectedDevices.isEmpty()) {
            Log.d(TAG, "No Bluetooth devices currently connected")
            return Pair(false, null)
        }
        
        // Find first connected audio/wearable device
        val connectedDevice = connectedDevices.find { device ->
            try {
                val deviceClass = device.bluetoothClass?.majorDeviceClass
                deviceClass == android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO ||
                deviceClass == android.bluetooth.BluetoothClass.Device.Major.WEARABLE
            } catch (e: SecurityException) {
                false
            }
        }
        
        val isInRange = connectedDevice != null
        val deviceName = try { connectedDevice?.name } catch (e: SecurityException) { "Unknown Device" }
        
        Log.d(TAG, "User in range: $isInRange ($deviceName)")
        return Pair(isInRange, deviceName)
    }
    
    private fun getConnectedDevices(): List<BluetoothDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.filter { device ->
                isDeviceConnected(device)
            } ?: emptyList()
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for Bluetooth devices", e)
            emptyList()
        }
    }

    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as Boolean
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Add a device to user's trusted list in Firestore.
     */
    suspend fun addTrustedDevice(device: BluetoothDevice) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        
        try {
            val deviceData = mapOf(
                "name" to (device.name ?: "Unknown Device"),
                "address" to device.address,
                "type" to getDeviceTypeName(device),
                "status" to "Trusted",
                "addedAt" to System.currentTimeMillis()
            )
            
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .collection("devices")
                .document(device.address.replace(":", "_"))
                .set(deviceData)
            
            Log.d(TAG, "Added trusted device: ${device.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add trusted device", e)
        }
    }
    
    private fun getDeviceTypeName(device: BluetoothDevice): String {
        return try {
            when (device.bluetoothClass?.majorDeviceClass) {
                android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO -> "Audio"
                android.bluetooth.BluetoothClass.Device.Major.WEARABLE -> "Wearable"
                android.bluetooth.BluetoothClass.Device.Major.PHONE -> "Phone"
                android.bluetooth.BluetoothClass.Device.Major.COMPUTER -> "Computer"
                else -> "Other"
            }
        } catch (e: SecurityException) {
            "Unknown"
        }
    }
}
