package com.proxicall_99.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class ActivityLog(
    val action: String = "",
    val caller: String = "",
    val timestamp: Long = 0,
    val response: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogsScreen(onBack: () -> Unit) {
    var logs by remember { mutableStateOf<List<ActivityLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .collection("activity_logs")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()
                logs = snapshot.documents.mapNotNull { doc ->
                    ActivityLog(
                        action = doc.getString("action") ?: "",
                        caller = doc.getString("caller") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0,
                        response = doc.getString("response") ?: ""
                    )
                }
            } catch (e: Exception) { }
        }
        isLoading = false
    }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Activity Logs", color = Color.White, fontWeight = FontWeight.Bold) },
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
                    CircularProgressIndicator(color = CyanGlow, modifier = Modifier.align(Alignment.Center))
                }
                logs.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.History, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No activity yet", color = Color.White, fontSize = 18.sp)
                        Text("Actions taken by ProxiCall will appear here", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn {
                        items(logs) { log ->
                            val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            when(log.action) {
                                                "SMS_SENT" -> Icons.Default.Sms
                                                "CALL_ANSWERED" -> Icons.Default.Call
                                                else -> Icons.Default.Info
                                            },
                                            null,
                                            tint = CyanGlow
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(log.action.replace("_", " "), color = Color.White, fontWeight = FontWeight.Bold)
                                            Text("From: ${log.caller}", color = Color.Gray, fontSize = 12.sp)
                                        }
                                        Text(dateFormat.format(Date(log.timestamp)), color = Color.Gray, fontSize = 11.sp)
                                    }
                                    if (log.response.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("\"${log.response}\"", color = Color.Gray.copy(alpha = 0.7f), fontSize = 12.sp)
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
