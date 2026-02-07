package com.proxicall_99.ui

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.proxicall_99.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.IndigoGlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoadingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Bluetooth State
    val bluetoothAdapter = remember { BluetoothAdapter.getDefaultAdapter() }
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    var userRefusedBluetooth by remember { mutableStateOf(false) }
    var bluetoothPromptShown by remember { mutableStateOf(false) }
    
    // Gemini State
    var geminiReady by remember { mutableStateOf(false) }
    var geminiStatus by remember { mutableStateOf("Waiting...") }
    
    // Progress State
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("INITIALIZING...") }
    
    // Bluetooth Enable Launcher
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
        if (!isBluetoothEnabled) {
            userRefusedBluetooth = true
        }
    }
    
    // Listen for Bluetooth state changes
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
                    if (isBluetoothEnabled) userRefusedBluetooth = false
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        onDispose { context.unregisterReceiver(receiver) }
    }
    
    // Main Loading Logic
    LaunchedEffect(isBluetoothEnabled) {
        if (!isBluetoothEnabled && !bluetoothPromptShown) {
            // Prompt user to enable Bluetooth
            bluetoothPromptShown = true
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else if (isBluetoothEnabled) {
            // Start loading sequence
            statusText = "✓ Bluetooth Active"
            delay(500)
            
            // Phase 1: System Check (0-30%)
            statusText = "Scanning Systems..."
            while (progress < 30f) {
                progress += 2f
                delay(50)
            }
            
            // Phase 2: Gemini Init (30-70%)
            statusText = "Connecting to Gemini 3..."
            geminiStatus = "Initializing..."
            scope.launch {
                try {
                    val model = GenerativeModel(
                        modelName = "gemini-1.5-flash",
                        apiKey = BuildConfig.GEMINI_API_KEY
                    )
                    // Warm up with a simple ping
                    val response = model.generateContent("Respond with OK")
                    geminiReady = response.text?.contains("OK", ignoreCase = true) == true
                    geminiStatus = if (geminiReady) "✓ Connected" else "⚠ Fallback Mode"
                } catch (e: Exception) {
                    geminiStatus = "⚠ Offline Mode"
                }
            }
            while (progress < 70f) {
                progress += 1.5f
                delay(50)
            }
            
            // Phase 3: Final Setup (70-100%)
            statusText = "Activating Agent..."
            while (progress < 100f) {
                progress += 2f
                delay(50)
            }
            
            delay(300)
            onComplete()
        }
    }

    // Animation for rings
    val infiniteTransition = rememberInfiniteTransition(label = "nebula")
    val rotateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Nebula Rings
        Box(modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = CyanGlow.copy(alpha = 0.2f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 8f)
                )
                drawArc(
                    color = CyanGlow,
                    startAngle = rotateAnim,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 8f)
                )
                drawArc(
                    color = IndigoGlow,
                    startAngle = -rotateAnim * 1.5f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 8f),
                    topLeft = Offset(20f, 20f),
                    size = androidx.compose.ui.geometry.Size(size.width - 40f, size.height - 40f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${progress.toInt()}%",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bluetooth Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isBluetoothEnabled) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                    contentDescription = null,
                    tint = if (isBluetoothEnabled) CyanGlow else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBluetoothEnabled) "Bluetooth Active" else "Bluetooth Required",
                    color = if (isBluetoothEnabled) CyanGlow else Color.Red,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Gemini Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (geminiReady) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (geminiReady) CyanGlow else Color.Yellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gemini: $geminiStatus",
                    color = if (geminiReady) CyanGlow else Color.Yellow,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = statusText,
                color = CyanGlow,
                letterSpacing = 4.sp,
                fontSize = 12.sp
            )
        }
        
        // Progress Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .background(Color.DarkGray, RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress / 100f)
                    .fillMaxHeight()
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(CyanGlow, IndigoGlow)
                        ),
                        RoundedCornerShape(50)
                    )
            )
        }
        
        // Enable Bluetooth Button (shown if BT off and not in prompt)
        if (!isBluetoothEnabled && bluetoothPromptShown && !userRefusedBluetooth) {
            Button(
                onClick = {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
            ) {
                Text("Turn On Bluetooth", color = Color.Black)
            }
        }
    }
    
    // Blocking Dialog if user refuses
    if (userRefusedBluetooth) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.BluetoothDisabled,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Bluetooth is Mandatory",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ProxiCall requires Bluetooth to detect your proximity and manage calls intelligently.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            userRefusedBluetooth = false
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            enableBluetoothLauncher.launch(enableBtIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanGlow)
                    ) {
                        Text("Enable Bluetooth", color = Color.Black)
                    }
                }
            }
        }
    }
}

