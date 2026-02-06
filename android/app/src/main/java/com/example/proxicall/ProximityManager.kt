package com.example.proxicall

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProximityManager(context: Context) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val scanner = bluetoothAdapter?.bluetoothLeScanner

    private val _proximityState = MutableStateFlow(ProximityState.UNKNOWN)
    val proximityState: StateFlow<ProximityState> = _proximityState.asStateFlow()

    // Configuration
    private val targetDeviceName = "SmartWatch_Simulator" // Replace with specific UUID if needed
    // private val SERVICE_UUID = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB") 
    
    private val RSSI_THRESHOLD_NEAR = -70
    private val RSSI_THRESHOLD_AWAY = -90

    @SuppressLint("MissingPermission") // Permissions should be handled in UI/Activity
    fun startScanning() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("ProximityManager", "Bluetooth not enabled")
            return
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(null, settings, scanCallback)
        Log.d("ProximityManager", "Started scanning")
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        scanner?.stopScan(scanCallback)
        Log.d("ProximityManager", "Stopped scanning")
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                val name = it.device.name
                val rssi = it.rssi

                // In a real scenario, filter by Service UUID or MAC address
                // For simulation, we check for a device name or accept all for debug if needed
                // Here assuming we match a simulated device or just logging logic
                
                // Logic based on RSSI
                val newState = when {
                    rssi > RSSI_THRESHOLD_NEAR -> ProximityState.NEAR
                    rssi < RSSI_THRESHOLD_AWAY -> ProximityState.AWAY
                    else -> _proximityState.value // Keep previous state (hysteresis)
                }

                if (newState != _proximityState.value) {
                    _proximityState.value = newState
                    Log.d("ProximityManager", "State changed to: $newState (RSSI: $rssi)")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("ProximityManager", "Scan failed: $errorCode")
        }
    }

    enum class ProximityState {
        NEAR, AWAY, UNKNOWN
    }
}
