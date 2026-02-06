package com.example.proxicall.logic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

class ProximityManager(private val context: Context) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter

    @SuppressLint("MissingPermission")
    fun isDeviceNear(deviceAddress: String): Boolean {
        if (adapter == null || !adapter.isEnabled) return false
        
        // In a real app, we'd scan for the specific device and check RSSI > -75dBm
        // For Hackathon/Demo, we might assume if we are connected to *any* paired device 
        // (like a watch) we are "Near". 
        
        // This is a simplified check: Is the specific device connected?
        val devices = adapter.bondedDevices
        val targetDevice = devices.find { it.address == deviceAddress }
        
        if (targetDevice != null) {
            // Note: Checking actual connection state requires PROFILE_HEADSET or similar
            // or using registerReceiver for ACL_CONNECTED.
            // For now, let's return TRUE if Bluetooth is ON and we have bonded devices,
            // acting as a simulation for "Watch is likely near".
            return true
        }
        
        return false
    }
}
