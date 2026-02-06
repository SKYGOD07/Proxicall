package com.proxicall_99.agent

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import com.proxicall_99.agent.ProximityState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

enum class ProximityState {
    NEAR,    // User is close often engaged
    AWAY,    // User is far, independent
    UNKNOWN
}

class ProximityManager(private val bluetoothAdapter: BluetoothAdapter?) {

    private val _proximityState = MutableStateFlow(UNKNOWN)
    val proximityState = _proximityState.asStateFlow()

    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    
    // Target Device UUID (e.g., User's Smartwatch)
    private val TARGET_DEVICE_NAME = "MySmartWatch" 

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val rssi = result.rssi
            val name = device.name ?: ""

            if (name == TARGET_DEVICE_NAME) {
                // Logic based on RSSI thresholds
                val newState = when {
                    rssi > -70 -> NEAR   // Strong signal
                    rssi < -90 -> AWAY   // Weak signal
                    else -> _proximityState.value // Maintain state in hysteresis zone
                }
                
                if (newState != _proximityState.value) {
                    Log.d("ProxiCall", "Proximity Changed: $newState (RSSI: $rssi)")
                    _proximityState.value = newState
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        scanner?.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        scanner?.stopScan(scanCallback)
    }
}
