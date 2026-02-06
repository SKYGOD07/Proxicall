package com.proxicall_99.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proxicall_99.ui.navigation.Screen

@Composable
fun LandingScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Radar Animation
        Canvas(modifier = Modifier.size(300.dp)) {
            drawCircle(
                color = Color(0xFF22D3EE).copy(alpha = alpha),
                radius = size.minDimension / 2 * scale,
                style = Stroke(width = 4f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            Text(
                text = "Searching for Signals...",
                color = Color(0xFF22D3EE),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate(Screen.Dashboard.route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE))
            ) {
                Text("Launch Agent", color = Color.Black)
            }
        }
    }
}
