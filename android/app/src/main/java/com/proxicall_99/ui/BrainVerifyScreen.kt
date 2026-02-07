package com.proxicall_99.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.proxicall_99.BuildConfig
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.IndigoGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainVerifyScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var geminiStatus by remember { mutableStateOf<String?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    var isOnline by remember { mutableStateOf<Boolean?>(null) }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Brain Verify", color = Color.White, fontWeight = FontWeight.Bold) },
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Brain Icon
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape)
                    .background(if (isOnline == true) CyanGlow.copy(alpha = 0.2f) else CardBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Psychology,
                    null,
                    tint = when(isOnline) { true -> CyanGlow; false -> Color(0xFFEF4444); else -> Color.Gray },
                    modifier = Modifier.size(64.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Gemini AI Core",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                when(isOnline) {
                    true -> "Connected & Ready"
                    false -> "Connection Failed"
                    null -> "Status Unknown"
                },
                color = when(isOnline) { true -> CyanGlow; false -> Color(0xFFEF4444); else -> Color.Gray },
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Status Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    StatusRow("API Key", if (BuildConfig.GEMINI_API_KEY.isNotEmpty()) "Configured" else "Missing")
                    Spacer(modifier = Modifier.height(12.dp))
                    StatusRow("Model", "gemini-2.0-flash")
                    Spacer(modifier = Modifier.height(12.dp))
                    StatusRow("Last Check", geminiStatus ?: "Not checked")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Test Button
            Button(
                onClick = {
                    isChecking = true
                    scope.launch {
                        try {
                            val model = GenerativeModel(
                                modelName = "gemini-2.0-flash",
                                apiKey = BuildConfig.GEMINI_API_KEY
                            )
                            val response = model.generateContent("Say 'OK' if you can hear me.")
                            isOnline = response.text?.contains("OK", ignoreCase = true) == true
                            geminiStatus = if (isOnline == true) "Verified just now" else "Failed"
                        } catch (e: Exception) {
                            isOnline = false
                            geminiStatus = "Error: ${e.message?.take(30)}"
                        }
                        isChecking = false
                    }
                },
                enabled = !isChecking,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoGlow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isChecking) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Connection", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
