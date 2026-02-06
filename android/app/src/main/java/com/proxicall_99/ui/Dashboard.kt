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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proxicall_99.data.DataSyncManager
import kotlinx.coroutines.launch

// Enhanced Visuals (Glassmorphism inspired colors)
val CyanGlow = Color(0xFF22D3EE)
val IndigoGlow = Color(0xFF4F46E5)
val GlassSurface = Color(0xFF1E293B).copy(alpha = 0.7f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(onInfoClick: () -> Unit) {
    var isConnected by remember { mutableStateOf(true) }
    var autoReplyEnabled by remember { mutableStateOf(true) }
    var whisperEnabled by remember { mutableStateOf(false) }
    
    // Real Data State
    // Ideally this would come from a ViewModel observing Firestore/Room
    // For now we simulate the "Live" aspect by connecting it to the sync status
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        // Trigger a background sync on dashboard load to ensure freshness
        val syncManager = DataSyncManager(context)
        syncManager.syncData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CyanGlow, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ProxiCall Agent Active", color = Color.White, fontSize = 18.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onInfoClick) {
                        Icon(Icons.Default.MoreVert, "Menu", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF0F172A))
                    )
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                
                // 1. Status Card (Glassmorphism)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BluetoothConnected,
                            contentDescription = null,
                            tint = if (isConnected) CyanGlow else Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isConnected) "System Online" else "Offline",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monitoring Incoming Signals",
                            color = CyanGlow,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Controls
                Text("Agent Controls", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                ControlItem(
                    title = "Auto-Reply",
                    subtitle = "Scenario A: When Away",
                    icon = Icons.Default.Message,
                    isActive = autoReplyEnabled,
                    onToggle = { autoReplyEnabled = it },
                    activeColor = CyanGlow
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ControlItem(
                    title = "Whisper Mode",
                    subtitle = "Scenario B: Real-time Voice",
                    icon = Icons.Default.Mic,
                    isActive = whisperEnabled,
                    onToggle = { whisperEnabled = it },
                    activeColor = IndigoGlow
                )
            }
        }
    }
}

@Composable
fun ControlItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    activeColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) activeColor.copy(alpha = 0.1f) else Color(0xFF1E293B)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isActive) activeColor else Color.DarkGray,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = activeColor,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Black
                )
            )
        }
    }
}
