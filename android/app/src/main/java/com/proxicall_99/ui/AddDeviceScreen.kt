package com.proxicall_99.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proxicall_99.service.ProximityChecker
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.IndigoGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

data class DeviceWithStatus(
    val device: BluetoothDevice,
    val name: String,
    val type: String,
    val isConnected: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bluetoothAdapter = remember { BluetoothAdapter.getDefaultAdapter() }
    
    var devices by remember { mutableStateOf<List<DeviceWithStatus>>(emptyList()) }
    var addingDevice by remember { mutableStateOf<String?>(null) }
    var addedDevice by remember { mutableStateOf<String?>(null) }
    
    // Get paired devices and check connection status
    LaunchedEffect(Unit) {
        try {
            // 1. Fetch already trusted devices
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val trustedAddresses = if (user != null) {
                try {
                    val snapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .collection("devices")
                        .get()
                        .await()
                    snapshot.documents.mapNotNull { doc -> doc.getString("address") }.toSet()
                } catch (e: Exception) { emptySet() }
            } else { emptySet() }

            // 2. Get paired devices
            val pairedDevices = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
            
            devices = pairedDevices.map { device ->
                val deviceName = try { device.name ?: "Unknown" } catch (e: SecurityException) { "Unknown" }
                val deviceType = try {
                    when (device.bluetoothClass?.majorDeviceClass) {
                        android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO -> "Audio"
                        android.bluetooth.BluetoothClass.Device.Major.WEARABLE -> "Wearable"
                        android.bluetooth.BluetoothClass.Device.Major.PHONE -> "Phone"
                        else -> "Device"
                    }
                } catch (e: SecurityException) { "Device" }
                
                // Check if this specific device is connected via reflection
                val isConnected = try {
                    val method = device.javaClass.getMethod("isConnected")
                    method.invoke(device) as Boolean
                } catch (e: Exception) { false }
                
                // Mark as "Added" if in trusted list
                if (trustedAddresses.contains(device.address)) {
                    // We can either filter it out or show it as added. 
                    // To keep UI clean, let's show it but with "Added" state initially
                    // However, we can't easily set 'addedDevice' state for multiple devices.
                    // Instead, let's filter the list or add a property to DeviceWithStatus
                }
                
                DeviceWithStatus(device, deviceName, deviceType, isConnected)
            }.sortedByDescending { it.isConnected } // Connected devices first
             .filter { !trustedAddresses.contains(it.device.address) } // Remove already trusted devices from list?
             // User requested: "removed from that list"
             
        } catch (e: SecurityException) { }
    }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Add Trusted Device", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            // Info Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CyanGlow.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = CyanGlow, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Select a paired device to use as your proximity anchor. ProxiCall will activate when this device is connected.",
                        color = CyanGlow.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("PAIRED DEVICES", color = Color.Gray, fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            if (devices.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.BluetoothDisabled, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No paired devices found", color = Color.Gray, fontSize = 14.sp)
                        Text("Pair a device in Bluetooth settings first", color = Color.Gray.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn {
                    items(devices) { deviceInfo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    if (addedDevice != deviceInfo.device.address) {
                                        addingDevice = deviceInfo.device.address
                                        scope.launch {
                                            val checker = ProximityChecker(context)
                                            withContext(Dispatchers.IO) { 
                                                checker.addTrustedDevice(deviceInfo.device) 
                                            }
                                            addingDevice = null
                                            addedDevice = deviceInfo.device.address
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (addedDevice == deviceInfo.device.address) 
                                    Color(0xFF22C55E).copy(alpha = 0.1f) else CardBg
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (deviceInfo.isConnected) Color(0xFF22C55E).copy(alpha = 0.2f)
                                                else IndigoGlow.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when (deviceInfo.type) { 
                                                "Audio" -> Icons.Default.Headphones
                                                "Wearable" -> Icons.Default.Watch
                                                else -> Icons.Default.Bluetooth 
                                            },
                                            null, 
                                            tint = if (deviceInfo.isConnected) Color(0xFF22C55E) else IndigoGlow,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(deviceInfo.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(deviceInfo.type, color = Color.Gray, fontSize = 12.sp)
                                            if (deviceInfo.isConnected) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    "• Connected",
                                                    color = Color(0xFF22C55E),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                when {
                                    addingDevice == deviceInfo.device.address -> {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CyanGlow, strokeWidth = 2.dp)
                                    }
                                    addedDevice == deviceInfo.device.address -> {
                                        Icon(Icons.Default.Check, "Added", tint = Color(0xFF22C55E))
                                    }
                                    else -> {
                                        Icon(Icons.Default.Add, "Add", tint = CyanGlow)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
