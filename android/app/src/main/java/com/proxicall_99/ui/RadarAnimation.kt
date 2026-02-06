package com.proxicall_99.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun scanningRadar() {
    var foundSignal by remember { mutableStateOf(false) }

    // Simulate finding a signal
    LaunchedEffect(Unit) {
        delay(3500)
        foundSignal = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Radar Loop")

    // Rotation Animation for Scanner
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "Rotation"
    )

    // Pulse Animation for Rings
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing =  FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse Scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse Alpha"
    )

    // Scanner Gradient Colors
    val scannerColors = listOf(
        Color.Transparent,
        Color.Transparent,
        Color(0x1A06B6D4), // Cyan with low alpha
        Color(0x6606B6D4)  // Cyan with higher alpha
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color(0xFF020617), shape = RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(256.dp),
            contentAlignment = Alignment.Center
        ) {
            // Static Grid Lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center =  this.center
                val radius = size.minDimension / 2

                // Outer Ring
                drawCircle(
                    color = Color(0xFF334155),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Middle Ring
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = radius * 0.66f,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Inner Ring
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = radius * 0.33f,
                    style = Stroke(width = 1.dp.toPx())
                )
                
                // Crosshairs
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(center.x, 0f),
                    end = Offset(center.x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(0f, center.y),
                    end = Offset(size.width, center.y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Rotating Scanner Beam
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(rotation) {
                    drawCircle(
                        brush = Brush.sweepGradient(scannerColors),
                        radius = size.minDimension / 2
                    )
                }
            }

            // Pulsing Ripples (Only when scanning)
            if (!foundSignal) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFF06B6D4).copy(alpha = pulseAlpha),
                        radius = (size.minDimension / 2) * (pulseScale * 0.55f), // Adjust scale logic
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Detected Signal Dot
            if (foundSignal) {
                // Outer red ping
                Box(
                    modifier = Modifier
                        .offset(x = 40.dp, y = (-40).dp) // Position roughly top-right
                        .size(16.dp)
                        .background(Color.Red, CircleShape)
                )
            }

            // Center Core
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF22D3EE), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Status Text
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STATUS: ",
                color = Color(0xFF64748B),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = if (foundSignal) "LOCKED" else "SCANNING...",
                color = if (foundSignal) Color(0xFF34D399) else Color(0xFF22D3EE), // Emerald vs Cyan
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                // Simple opacity pulse for text if scanning could be added here
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(2.dp))
        ) {
            if (foundSignal) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF10B981), RoundedCornerShape(2.dp))
                )
            } else {
                 // Indeterminate loading bar
                 val infiniteTransitionBar = rememberInfiniteTransition(label = "Bar")
                 val xOffset by infiniteTransitionBar.animateFloat(
                     initialValue = -1f,
                     targetValue = 1f,
                     animationSpec = infiniteRepeatable(
                         animation = tween(1500, easing = LinearEasing)
                     ),
                    label = "Bar Offset"
                 )
                 
                 // Note: Ideally implemented with a draw modifier for true relative positioning, 
                 // but simple Box works for demo
            }
        }
    }
}

@Preview
@Composable
fun PreviewRadar() {
    scanningRadar()
}
