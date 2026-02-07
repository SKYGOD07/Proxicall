package com.proxicall_99.ui

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proxicall_99.data.DataSyncManager
import com.proxicall_99.service.ProxiCallService
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.IndigoGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    onInfoClick: () -> Unit,
    onAddDeviceClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onAuthCheckClick: () -> Unit = {},
    onActivityLogsClick: () -> Unit = {},
    onCallHistoryClick: () -> Unit = {},
    onContactsClick: () -> Unit = {},
    onBrainVerifyClick: () -> Unit = {},
    onAssistantClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    // Bluetooth State
    val bluetoothAdapter = remember { BluetoothAdapter.getDefaultAdapter() }
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    
    // Listen for Bluetooth changes
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        onDispose { context.unregisterReceiver(receiver) }
    }
    
    // Sync data and start service on load
    LaunchedEffect(Unit) {
        val syncManager = DataSyncManager(context)
        syncManager.syncData()
        
        // Start ProxiCallService
        if (!ProxiCallService.isRunning) {
            val serviceIntent = Intent(context, ProxiCallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
    
    // Pulse animation for BT button when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CardBg
            ) {
                SideMenuContent(
                    onItemClick = { route ->
                        scope.launch { drawerState.close() }
                        // Handle navigation based on route
                        when (route) {
                            "info" -> onInfoClick()
                            "auth" -> onAuthCheckClick()
                            "logs" -> onActivityLogsClick()
                            "history" -> onCallHistoryClick()
                            "brain" -> onBrainVerifyClick()
                            "contacts" -> onContactsClick()
                            "assistant" -> onAssistantClick()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = DarkBg,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "PROXICALL",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // Profile Icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(CyanGlow, IndigoGlow)
                                    )
                                )
                                .clickable { onAccountClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.weight(0.15f))
                    
                    // ===== LARGE BLUETOOTH TOGGLE =====
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(220.dp)
                    ) {
                        // Outer pulse ring (when active)
                        if (isBluetoothEnabled) {
                            Canvas(
                                modifier = Modifier
                                    .size(220.dp * pulseScale)
                            ) {
                                drawCircle(
                                    color = CyanGlow.copy(alpha = pulseAlpha),
                                    radius = size.minDimension / 2,
                                    style = Stroke(width = 4f)
                                )
                            }
                        }
                        
                        // Outer ring
                        Canvas(modifier = Modifier.size(200.dp)) {
                            drawCircle(
                                color = if (isBluetoothEnabled) CyanGlow.copy(alpha = 0.3f) 
                                        else Color.Gray.copy(alpha = 0.2f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 3f)
                            )
                        }
                        
                        // Inner ring
                        Canvas(modifier = Modifier.size(160.dp)) {
                            drawCircle(
                                color = if (isBluetoothEnabled) CyanGlow.copy(alpha = 0.5f) 
                                        else Color.Gray.copy(alpha = 0.3f),
                                radius = size.minDimension / 2,
                                style = Stroke(width = 2f)
                            )
                        }
                        
                        // Center BT Icon
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            if (isBluetoothEnabled) CyanGlow.copy(alpha = 0.2f) else Color.Transparent,
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isBluetoothEnabled) CyanGlow else Color.Gray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = "Bluetooth",
                                tint = if (isBluetoothEnabled) CyanGlow else Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Status Text
                    Text(
                        text = "Bluetooth: ${if (isBluetoothEnabled) "ON" else "OFF"}",
                        color = if (isBluetoothEnabled) CyanGlow else Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.weight(0.1f))
                    
                    // ===== ADD DEVICE BUTTON =====
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(64.dp)
                            .clickable { onAddDeviceClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBg
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Plus icon in circle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = CyanGlow.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = CyanGlow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "ADD DEVICE",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(0.2f))
                }
            }
        }
    }
}

@Composable
fun SideMenuContent(onItemClick: (String) -> Unit) {
    val menuItems = listOf(
        Triple(Icons.Default.Bluetooth, "Connection", "connection"),
        Triple(Icons.Default.Smartphone, "Auth Check", "auth"),
        Triple(Icons.Default.History, "Activity Logs", "logs"),
        Triple(Icons.Default.Call, "Call History", "history"),
        Triple(Icons.Default.Psychology, "Brain Verify", "brain"),
        Triple(Icons.Default.Contacts, "Contacts", "contacts"),
        Triple(Icons.Default.Face, "Assistant", "assistant")
    )
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 24.dp)
    ) {
        // Header
        Text(
            text = "PROXICALL",
            color = CyanGlow,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Menu Items
        menuItems.forEach { (icon, label, route) ->
            NavigationDrawerItem(
                icon = { Icon(icon, contentDescription = label, tint = Color.White) },
                label = { 
                    Text(
                        label, 
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ) 
                },
                selected = route == "connection",
                onClick = { onItemClick(route) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = CyanGlow.copy(alpha = 0.15f),
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Settings at bottom
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Gray) },
            label = { Text("Settings", color = Color.Gray) },
            selected = false,
            onClick = { onItemClick("info") },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
