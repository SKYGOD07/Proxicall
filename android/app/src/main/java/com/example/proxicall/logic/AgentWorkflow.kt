package com.example.proxicall.logic

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import com.example.proxicall.data.GeminiClient

object AgentWorkflow {
    private const val TAG = "AgentWorkflow"

    suspend fun handleIncomingCall(context: Context, number: String) {
        Log.i(TAG, "Starting Agent Workflow for $number")

        // 1. Identify/Classify Caller
        // In a real app, we'd query ContactsProvider here to get a name.
        // For demo, we'll use a mocked lookup or just the number if unknown.
        val callerName = resolveContactName(context, number) ?: "Unknown Number"
        
        // 2. Gemini Classification
        val relationship = GeminiClient.classifyCaller(callerName)
        Log.i(TAG, "Relationship: $relationship")

        // 3. Check Proximity
        val proximityManager = com.example.proxicall.logic.ProximityManager(context)
        // For demo purposes, we can hardcode a device address or check if ANY device is connected
        // "00:00:00:00:00:00" is a placeholder. In reality, we'd store the user's watch address.
        val isUserNear = proximityManager.isDeviceNear("00:00:00:00:00:00") 
        Log.i(TAG, "User Proximity: ${if (isUserNear) "NEAR" else "AWAY"}")

        if (!isUserNear) {
            // Case A: Away Mode -> Auto-Reply
            Log.i(TAG, "Case A: Away Mode Activated due to weak signal/no connection.")
            val reply = GeminiClient.generateReply(callerName, relationship, "I am away from my phone.")
            sendSMS(number, reply)
        } else {
            // Case B: Busy Mode (Near but not answering)
            // Trigger Whisper Interaction immediately for demo (skipping 15s delay)
            Log.i(TAG, "Case B: Busy Mode Activated. Triggering Whisper Agent.")
            startWhisperInteraction(context, callerName)
        }
    }

    private fun checkProximity(): Boolean {
        return false // Deprecated, using ProximityManager directly
    }

    private fun resolveContactName(context: Context, number: String): String? {
        // TODO: Implement Contacts Query
        if (number.contains("5554")) return "Mom" // Emulator hack
        return null
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.i(TAG, "SMS Sent to $phoneNumber: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS", e)
        }
    }
    
    // --- Whisper Mode (TTS/STT) ---
    // Note: TTS initialization is asynchronous. For a background service/receiver, 
    // it's tricky. We usually need a foreground service or Activity bound to it.
    // For this prototype, we'll assume we can start a Service or Activity to handle the voice interaction.
    fun startWhisperInteraction(context: Context, callerName: String) {
        val intent = android.content.Intent(context, com.example.proxicall.ui.WhisperActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("CALLER_NAME", callerName)
        }
        context.startActivity(intent)
        Log.i(TAG, "Starting Whisper Interaction for $callerName")
    }
}
