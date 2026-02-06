package com.example.proxicall.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.example.proxicall.BuildConfig
import android.util.Log

object GeminiClient {
    private const val TAG = "GeminiClient"
    
    // SAFETY: Initialize standard model (Gemini 1.5 Flash)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun classifyCaller(callerName: String): String {
        return try {
            val prompt = """
                Classify the relationship of this caller name: "$callerName".
                Return ONLY one of these labels: 'Family', 'Professional', 'Stranger'.
                Examples:
                - 'Mom' -> 'Family'
                - 'Dad' -> 'Family'
                - 'Boss' -> 'Professional'
                - 'Mr. Smith' -> 'Professional'
                - 'Unknown Number' -> 'Stranger'
                - 'Spam' -> 'Stranger'
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            // Cleanup response (trim whitespace/newlines)
            val result = response.text?.trim() ?: "Stranger"
            Log.d(TAG, "Classified $callerName as $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error classification", e)
            "Stranger" // Fallback
        }
    }

    suspend fun generateReply(callerName: String, relationship: String, context: String): String {
        return try {
            val prompt = """
                Generate a short SMS reply for a missed call.
                Caller: $callerName
                Relationship: $relationship
                My Context/Reason: $context
                
                Rules:
                - If Relationship is 'Family', be casual.
                - If Relationship is 'Professional', be formal and polite.
                - If context is missing, use a generic polite excuse.
                - Keep it under 160 characters.
                - Do not include hashtags or emojis unless casual.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val reply = response.text?.trim() ?: "I can't talk right now."
            Log.d(TAG, "Generated reply: $reply")
            reply
        } catch (e: Exception) {
             "I am currently unavailable. Will call back soon."
        }
    }
}
