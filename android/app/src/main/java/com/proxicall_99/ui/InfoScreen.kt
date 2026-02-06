package com.proxicall_99.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("How it Works", "Features")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "System Info",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Black,
            contentColor = Color(0xFF22D3EE)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                HowItWorksContent()
            } else {
                FeaturesContent()
            }
        }
    }
}

@Composable
fun HowItWorksContent() {
    val steps = listOf(
        Triple("1. Signal Detection", "Monitors BLE proximity & phone state.", Color(0xFF22D3EE)), // Cyan
        Triple("2. Context Analysis", "Gemini 3 analyzes calendar & driving status.", Color(0xFF818CF8)), // Indigo
        Triple("3. Smart Action", "Auto-replies via SMS or synthesizes voice whispers.", Color(0xFFA78BFA)) // Purple
    )

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = "Autonomous Workflow",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        steps.forEachIndexed { index, (title, desc, color) ->
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color, CircleShape)
                    )
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(60.dp)
                                .background(Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(desc, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun FeaturesContent() {
    val features = listOf(
        "Gemini 3 Core" to "Latest multimodal model for deep context understanding.",
        "Whisper Mode (BLE)" to "Voice interception via Bluetooth earbuds.",
        "Device Fencing" to "Security via trusted hardware tokens.",
        "On-Device Privacy" to "Ethical data processing.",
        "Battery Optimized" to "<2% drain with BLE Low Energy scanning."
    )

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = "System Capabilities",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        features.forEach { (title, desc) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, color = Color(0xFF22D3EE), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(desc, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
