package com.example.proxicall

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.proxicall.ui.Dashboard
import com.example.proxicall.ui.InfoScreen
// import com.example.proxicall.ui.theme.ProxiCallTheme // Assuming Theme exists, if not usage MaterialTheme directly
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permissionsState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )

                    LaunchedEffect(Unit) {
                        permissionsState.launchMultiplePermissionRequest()
                    }

                    var currentScreen by remember { mutableStateOf("dashboard") }

                    if (currentScreen == "dashboard") {
                        Dashboard(onInfoClick = { currentScreen = "info" })
                    } else {
                        InfoScreen(onBack = { currentScreen = "dashboard" })
                    }
                }
            }
        }
    }
}
