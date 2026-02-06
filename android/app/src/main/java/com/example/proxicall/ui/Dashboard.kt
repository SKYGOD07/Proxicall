package com.example.proxicall.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ActivityItem(
    val id: Int,
    val type: String,
    val message: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard() {
    var isConnected by remember { mutableStateOf(true) }
    var autoReplyEnabled by remember { mutableStateOf(true) }
    var whisperEnabled by remember { mutableStateOf(false) }

    val activities = listOf(
        ActivityItem(1, "auto-reply", "Auto-replied to Mom: 'In a meeting'", "2m ago"),
        ActivityItem(2, "whisper", "Whispered: 'Call from Boss - Priority High'", "15m ago"),
        ActivityItem(3, "connect", "Reconnected to Pixel 8 Pro", "1h ago")
    )

    var isLoading by remember { mutableStateOf(true) }

    if (isLoading) {
        LoadingScreen(onComplete = { isLoading = false })
    } else {
        Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timeline, 
                            contentDescription = null,
                            tint = Color(0xFF22D3EE) // Cyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ProxiCall Agent Active", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A) // Slate-900
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            
            // Status Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected) Color(0xFF064E3B) else Color(0xFF451A03) // Green-900 vs Amber-900
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp)) {
                         Icon(
                             imageVector = Icons.Default.BluetoothConnected, // Fallback icon
                             contentDescription = null,
                             modifier = Modifier.align(Alignment.Center),
                             tint = if (isConnected) Color(0xFF4ADE80) else Color(0xFFFBBF24)
                         )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isConnected) "Connected" else "Away",
                            color = if (isConnected) Color(0xFF4ADE80) else Color(0xFFFBBF24),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isConnected) "-42 dBm (Strong)" else "-85 dBm (Weak)",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Controls
            Text("Controls", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            
            ControlRow(
                title = "Auto-Reply",
                subtitle = "Scenario A: When Away",
                icon = Icons.Default.PhoneCallback,
                checked = autoReplyEnabled,
                onCheckedChange = { autoReplyEnabled = it },
                color = Color(0xFF0891B2) // Cyan-600
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            ControlRow(
                title = "Whisper Agent",
                subtitle = "Scenario B: Gemini Live",
                icon = Icons.Default.Mic,
                checked = whisperEnabled,
                onCheckedChange = { whisperEnabled = it },
                color = Color(0xFF4F46E5) // Indigo-600
            )

            // Activity Log
            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Activity", color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityLogItem(activity)
                }
            }
        }
    }
}

@Composable
fun ControlRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)) // Slate-800
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = color
                )
            )
        }
    }
}

@Composable
fun ActivityLogItem(item: ActivityItem) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Box(
                 modifier = Modifier
                     .size(12.dp)
                     .background(
                         when (item.type) {
                             "auto-reply" -> Color(0xFF06B6D4) // Cyan
                             "whisper" -> Color(0xFF6366F1) // Indigo
                             else -> Color(0xFF22C55E) // Green
                         },
                         CircleShape
                     )
             )
             Box(modifier = Modifier
                 .width(2.dp)
                 .height(40.dp)
                 .background(Color.DarkGray))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(item.message, color = Color.White, fontSize = 14.sp)
            Text(item.time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
