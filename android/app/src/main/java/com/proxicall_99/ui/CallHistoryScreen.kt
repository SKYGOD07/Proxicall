package com.proxicall_99.ui

import android.Manifest
import android.content.pm.PackageManager
import android.provider.CallLog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import com.proxicall_99.data.DataSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class LocalCallLog(
    val number: String,
    val name: String?,
    val type: Int,
    val date: Long,
    val duration: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var callLogs by remember { mutableStateOf<List<LocalCallLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSyncing by remember { mutableStateOf(false) }
    var hasPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        ) 
    }

    fun fetchCallLogs() {
        if (!hasPermission) {
            isLoading = false
            return
        }
        
        scope.launch {
            isLoading = true
            val logs = withContext(Dispatchers.IO) {
                val list = mutableListOf<LocalCallLog>()
                try {
                    val cursor = context.contentResolver.query(
                        CallLog.Calls.CONTENT_URI,
                        arrayOf(
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.DATE,
                            CallLog.Calls.DURATION
                        ),
                        null, null, "${CallLog.Calls.DATE} DESC LIMIT 50"
                    )
                    
                    cursor?.use {
                        val numIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                        val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
                        val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                        val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                        val durIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                        
                        while (it.moveToNext()) {
                            list.add(
                                LocalCallLog(
                                    number = it.getString(numIndex) ?: "Unknown",
                                    name = it.getString(nameIndex),
                                    type = it.getInt(typeIndex),
                                    date = it.getLong(dateIndex),
                                    duration = it.getLong(durIndex)
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                list
            }
            callLogs = logs
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchCallLogs()
    }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Call History", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                val syncManager = DataSyncManager(context)
                                syncManager.syncData() // Syncs logs to Firestore
                                fetchCallLogs() // Refresh local list
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = CyanGlow, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Sync, "Sync Now", tint = CyanGlow)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when {
                !hasPermission -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Permission Required", color = Color.White, fontSize = 18.sp)
                        Text("Please grant Call Log permission in settings", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                isLoading && callLogs.isEmpty() -> CircularProgressIndicator(color = CyanGlow, modifier = Modifier.align(Alignment.Center))
                callLogs.isEmpty() -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PhoneMissed, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No recent calls", color = Color.White, fontSize = 18.sp)
                    }
                }
                else -> {
                    LazyColumn {
                        items(callLogs) { log ->
                            val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (log.type) {
                                            CallLog.Calls.INCOMING_TYPE -> Icons.Default.CallReceived
                                            CallLog.Calls.OUTGOING_TYPE -> Icons.Default.CallMade
                                            CallLog.Calls.MISSED_TYPE -> Icons.Default.CallMissed
                                            CallLog.Calls.REJECTED_TYPE -> Icons.Default.CallEnd
                                            else -> Icons.Default.Call
                                        },
                                        contentDescription = null,
                                        tint = when (log.type) {
                                            CallLog.Calls.MISSED_TYPE, CallLog.Calls.REJECTED_TYPE -> Color(0xFFFF5252)
                                            CallLog.Calls.OUTGOING_TYPE -> Color(0xFF4CAF50)
                                            else -> CyanGlow
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = log.name ?: log.number,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        if (log.name != null) {
                                            Text(log.number, color = Color.Gray, fontSize = 12.sp)
                                        }
                                        Text(
                                            dateFormat.format(Date(log.date)),
                                            color = Color.Gray.copy(alpha = 0.7f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    
                                    Text(
                                        formatDuration(log.duration),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(seconds: Long): String {
    if (seconds == 0L) return ""
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}
