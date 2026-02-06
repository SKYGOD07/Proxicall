package com.example.proxicall.agent

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiAgent {

    // Using Gemini 3 model optimized for reasoning
    private val model = GenerativeModel(
        modelName = "gemini-1.5-pro", // Placeholder for Gemini 3
        apiKey = "YOUR_API_KEY_HERE"
    )

    private val systemInstruction = """
        You are an availability orchestrator for a context-aware agent.
        Your goal is to decide how to handle an incoming call based on the user's proximity to their phone.
        
        RULES:
        1. If proximity_state is "AWAY", you MUST draft a polite SMS auto-reply explaining the user is away.
        2. If proximity_state is "NEAR", you MUST output a command to start a "Whisper Agent" (Gemini Live) session to ask the user what to do in their ear.
        
        Output format: JSON ONLY.
        {
          "action": "AUTO_REPLY" | "WHISPER_AGENT",
          "content": "The SMS text or the initial whisper phrase"
        }
    """.trimIndent()

    suspend fun generateResponseAction(
        callerId: String,
        proximityState: String,
        calendarStatus: String
    ): JSONObject = withContext(Dispatchers.IO) {
        
        val inputJson = JSONObject().apply {
            put("caller_id", callerId)
            put("proximity_state", proximityState)
            put("calendar_status", calendarStatus)
        }

        val prompt = "$systemInstruction\n\nINPUT CONTEXT: $inputJson"

        try {
            val response = model.generateContent(prompt)
            val responseText = response.text ?: "{}"
            
            // Clean markdown code blocks if present
            val jsonString = responseText.replace("```json", "").replace("```", "").trim()
            
            return@withContext JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback safe mode
            return@withContext JSONObject().apply {
                put("action", "AUTO_REPLY")
                put("content", "I am currently unavailable.")
            }
        }
    }
}
