package com.example.proxicall.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onComplete: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 3000L // 3 seconds
        
        while (progress < 100f) {
             val elapsedTime = System.currentTimeMillis() - startTime
             progress = (elapsedTime.toFloat() / duration.toFloat() * 100f).coerceAtMost(100f)
             delay(50) // Update every 50ms
        }
        delay(500) // Small delay at 100%
        onComplete()
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
                     color = Color(0xFF22D3EE).copy(alpha = 0.2f), // Cyan
                     radius = size.minDimension / 2,
                     style = Stroke(width = 8f)
                 )
                 
                 // Rotating Arc
                 drawArc(
                     color = Color(0xFF22D3EE),
                     startAngle = rotateAnim,
                     sweepAngle = 90f,
                     useCenter = false,
                     style = Stroke(width = 8f)
                 )
                 
                 // Counter Rotating Arc
                 drawArc(
                     color = Color(0xFF6366F1), // Indigo
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
             Spacer(modifier = Modifier.height(32.dp))
             Text(
                 text = "INITIALIZING...",
                 color = Color(0xFF22D3EE),
                 letterSpacing = 4.sp,
                 fontSize = 12.sp
             )
        }
        
        // Linear Progress Bar at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .fillMaxWidth(0.6f)
                .height(4.dp)
                .background(Color.DarkGray, androidx.compose.foundation.shape.CircleShape)
        ) {
            Box(
                 modifier = Modifier
                     .fillMaxWidth(progress / 100f)
                     .fillMaxHeight()
                     .background(
                         androidx.compose.ui.graphics.Brush.horizontalGradient(
                             listOf(Color(0xFF22D3EE), Color(0xFF6366F1))
                         ),
                         androidx.compose.foundation.shape.CircleShape
                     )
            )
        }
    }
}
