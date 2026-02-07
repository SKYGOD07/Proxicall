package com.proxicall_99.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proxicall_99.ui.theme.CyanGlow
import com.proxicall_99.ui.theme.DarkBg
import com.proxicall_99.ui.theme.CardBg
import com.google.ai.client.generativeai.GenerativeModel
import com.proxicall_99.BuildConfig
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Initialize Gemini (In a real app, inject this or use a ViewModel)
    val geminiModel = remember {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Assistant", color = Color.White, fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Chat History
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(msg)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = CyanGlow
                )
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask Gemini...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isLoading) {
                            val userMsg = messageText
                            messages = messages + ChatMessage(userMsg, true)
                            messageText = ""
                            isLoading = true
                            
                            scope.launch {
                                try {
                                    val response = geminiModel.generateContent(userMsg)
                                    val aiText = response.text ?: "I couldn't generate a response."
                                    messages = messages + ChatMessage(aiText, false)
                                } catch (e: Exception) {
                                    messages = messages + ChatMessage("Error: ${e.message}", false)
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .background(CyanGlow, androidx.compose.foundation.shape.CircleShape)
                        .size(48.dp)
                ) {
                    Icon(Icons.Default.Send, "Send", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) CyanGlow.copy(alpha = 0.2f) else CardBg
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp, 
                topEnd = 16.dp, 
                bottomStart = if (message.isUser) 16.dp else 4.dp, 
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp
            )
        }
    }
}
