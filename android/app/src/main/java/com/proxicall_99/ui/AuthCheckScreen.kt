package com.proxicall_99.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.IndigoGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TrustedDevice(
    val name: String = "",
    val address: String = "",
    val type: String = "",
    val status: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthCheckScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var devices by remember { mutableStateOf<List<TrustedDevice>>(emptyList()) }
    var connectedDeviceName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    fun fetchDevices() {
        scope.launch {
            isLoading = true
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    // 1. Get Trusted Devices
                    val snapshot = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .collection("devices")
                        .get()
                        .await()
                    devices = snapshot.documents.mapNotNull { doc ->
                        TrustedDevice(
                            name = doc.getString("name") ?: "Unknown",
                            address = doc.getString("address") ?: "",
                            type = doc.getString("type") ?: "Device",
                            status = doc.getString("status") ?: "Trusted"
                        )
                    }
                    
                    // 2. Check Connection Status
                    val checker = com.proxicall_99.service.ProximityChecker(context)
                    // We can't really call suspend function simply here if it wasn't suspend, 
                    // but isUserInRange is simpler. However, we want strict check.
                    // ProximityChecker.isUserInRange() does the reflection check now.
                    val (isConnected, name) = checker.isUserInRange()
                    connectedDeviceName = if (isConnected) name else null
                    
                } catch (e: Exception) { }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchDevices()
    }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Auth Check", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = CyanGlow,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                devices.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Security,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No trusted devices", color = Color.White, fontSize = 18.sp)
                        Text(
                            "Add devices from the Dashboard",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn {
                        // Current Status Header
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (connectedDeviceName != null) 
                                        Color(0xFF22C55E).copy(alpha = 0.15f) 
                                    else Color(0xFFEF4444).copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (connectedDeviceName != null) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (connectedDeviceName != null) Color(0xFF22C55E) else Color(0xFFEF4444),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            if (connectedDeviceName != null) "AUTHENTICATED" else "LOCKED",
                                            color = if (connectedDeviceName != null) Color(0xFF22C55E) else Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            if (connectedDeviceName != null) "Connected to $connectedDeviceName" else "No trusted device connected",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                            
                            Text(
                                "TRUSTED DEVICES", 
                                color = Color.Gray, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        
                        items(devices) { device ->
                             val isConnected = device.name == connectedDeviceName
                             
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isConnected) Color(0xFF22C55E).copy(alpha = 0.2f) 
                                                else CyanGlow.copy(alpha = 0.2f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when(device.type) {
                                                "Audio" -> Icons.Default.Headphones
                                                "Wearable" -> Icons.Default.Watch
                                                else -> Icons.Default.Bluetooth
                                            },
                                            null,
                                            tint = if (isConnected) Color(0xFF22C55E) else CyanGlow
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(device.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(device.type, color = Color.Gray, fontSize = 12.sp)
                                    }
                                    
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                val user = FirebaseAuth.getInstance().currentUser ?: return@launch
                                                try {
                                                    FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(user.uid)
                                                        .collection("devices")
                                                        .document(device.address.replace(":", "_")) // Ensure ID matches what we saved
                                                        .delete()
                                                        .await()
                                                    fetchDevices() // Refresh list
                                                } catch (e: Exception) { }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, "Remove", tint = Color.Gray)
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
