package com.proxicall_99

import android.util.Log
import com.proxicall_99.ProximityManager.ProximityState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GeminiManager(private val apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro", // Or gemini-1.5-pro if available/preferred
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        }
    )

    suspend fun generateResponseAction(
        callerId: String,
        proximity: ProximityState,
        calendarStatus: String
    ): ActionResponse? {
        return withContext(Dispatchers.IO) {
            val prompt = """
                You are an availability orchestrator for ProxiCall.
                
                Context:
                - Caller: $callerId
                - User Proximity: ${proximity.name} (NEAR means the user is near their phone, AWAY means they are away)
                - Calendar Status: $calendarStatus
                
                Instructions:
                - If the user is AWAY, draft a polite SMS explainng they are unavailable but will reply soon.
                - If the user is NEAR, output a command to start a Gemini Live session.
                
                Output Schema:
                Return ONLY a valid JSON object with the following structure:
                {
                    "action": "SMS" or "LIVE_SESSION",
                    "content": "Message body for SMS or empty for LIVE_SESSION",
                    "reasoning": "Short explanation of the decision"
                }
            """.trimIndent()

            try {
                val response = generativeModel.generateContent(prompt)
                parseResponse(response)
            } catch (e: Exception) {
                Log.e("GeminiManager", "Error generating response", e)
                null
            }
        }
    }

    private fun parseResponse(response: GenerateContentResponse): ActionResponse? {
        val text = response.text ?: return null
        // Basic cleanup for potential markdown fencing
        val cleanJson = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        
        return try {
            val json = JSONObject(cleanJson)
            ActionResponse(
                action = json.getString("action"),
                content = json.optString("content", ""),
                reasoning = json.optString("reasoning", "")
            )
        } catch (e: Exception) {
            Log.e("GeminiManager", "Error parsing JSON: $cleanJson", e)
            null
        }
    }

    data class ActionResponse(
        val action: String,
        val content: String,
        val reasoning: String
    )
}
