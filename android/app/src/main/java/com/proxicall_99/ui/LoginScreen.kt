package com.proxicall_99.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proxicall_99.auth.GoogleAuthClient
import com.proxicall_99.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthClient = remember { GoogleAuthClient(context) }

    // Check if already signed in
    LaunchedEffect(key1 = Unit) {
        if (googleAuthClient.getSignedInUser() != null) {
            navController.navigate(Screen.Landing.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val intent = result.data ?: return@rememberLauncherForActivityResult
            scope.launch {
                val signInResult = googleAuthClient.signInWithIntent(intent)
                if (signInResult.errorMessage != null) {
                    Toast.makeText(
                        context,
                        "Sign in failed: ${signInResult.errorMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (signInResult.userData != null) {
                    // Success
                    Toast.makeText(
                        context,
                        "Signed in as ${signInResult.userData.username}",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ProxiCall",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    scope.launch {
                        val signInIntent = googleAuthClient.signIn()
                        launcher.launch(signInIntent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Text("Connect with Google")
            }
        }
    }
}
