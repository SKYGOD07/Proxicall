package com.proxicall_99.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SyncedContact(
    val name: String = "",
    val phone: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var contacts by remember { mutableStateOf<List<SyncedContact>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSyncing by remember { mutableStateOf(false) }
    
    fun fetchContacts() {
        scope.launch {
            isLoading = true
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .collection("contacts")
                        .document("unified_list")
                        .get()
                        .await()
                    
                    if (doc.exists()) {
                        val items = doc.get("items") as? List<Map<String, String>>
                        contacts = items?.mapNotNull { item ->
                            SyncedContact(
                                name = item["name"] ?: "",
                                phone = item["phoneNumber"] ?: ""
                            )
                        }?.sortedBy { it.name } ?: emptyList()
                    }
                } catch (e: Exception) { }
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchContacts()
    }
    
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Synced Contacts", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                val syncManager = com.proxicall_99.data.DataSyncManager(context)
                                syncManager.syncData()
                                fetchContacts() // Refresh after sync
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = CyanGlow, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Sync, "Sync Now", tint = CyanGlow)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when {
                isLoading && contacts.isEmpty() -> CircularProgressIndicator(color = CyanGlow, modifier = Modifier.align(Alignment.Center))
                contacts.isEmpty() -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Contacts, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No contacts synced", color = Color.White, fontSize = 18.sp)
                        Text("Tap the sync icon to upload local contacts", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn {
                        item {
                            Text(
                                "${contacts.size} Contacts", 
                                color = CyanGlow, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        items(contacts) { contact ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(40.dp).clip(CircleShape).background(CyanGlow.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(contact.name.take(1).uppercase(), color = CyanGlow, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(contact.name, color = Color.White, fontWeight = FontWeight.Medium)
                                        Text(contact.phone, color = Color.Gray, fontSize = 12.sp)
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
