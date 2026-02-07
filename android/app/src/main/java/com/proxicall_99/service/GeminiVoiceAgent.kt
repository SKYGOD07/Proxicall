package com.proxicall_99.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.telephony.SmsManager
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.proxicall_99.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/**
 * GeminiVoiceAgent - TTS/STT + Gemini for call handling
 * 
 * Features:
 * - Text-to-Speech for speaking to user via earbuds
 * - Gemini for generating polite SMS responses
 * - SMS sending capability
 */
class GeminiVoiceAgent(private val context: Context) {
    
    companion object {
        const val TAG = "GeminiVoiceAgent"
    }
    
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    
    private val geminiModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
    
    init {
        initTts()
    }
    
    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && 
                             result != TextToSpeech.LANG_NOT_SUPPORTED
                Log.d(TAG, "TTS initialized: $isTtsReady")
            }
        }
    }
    
    /**
     * Speak text via earbuds/speaker
     */
    suspend fun speak(text: String): Boolean = suspendCancellableCoroutine { continuation ->
        if (!isTtsReady || tts == null) {
            Log.e(TAG, "TTS not ready")
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        
        val utteranceId = "proxicall_${System.currentTimeMillis()}"
        
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            
            override fun onDone(id: String?) {
                if (id == utteranceId) {
                    continuation.resume(true)
                }
            }
            
            override fun onError(id: String?) {
                if (id == utteranceId) {
                    continuation.resume(false)
                }
            }
        })
        
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
    
    /**
     * Generate a polite SMS response using Gemini
     */
    suspend fun generatePoliteResponse(
        callerName: String,
        isUserUnavailable: Boolean = false,
        customInstructions: String? = null
    ): String {
        val prompt = if (isUserUnavailable) {
            """
            Generate a brief, polite SMS response for when someone cannot answer their phone.
            Caller: $callerName
            Situation: The phone owner is away from their device.
            
            Requirements:
            - Maximum 160 characters (SMS limit)
            - Professional but friendly tone
            - Mention they will be contacted when available
            ${customInstructions?.let { "- Additional instructions: $it" } ?: ""}
            
            Respond with ONLY the SMS text, no quotes or explanation.
            """.trimIndent()
        } else {
            """
            Generate a brief, polite SMS response declining a call.
            Caller: $callerName
            ${customInstructions?.let { "User's message: $it" } ?: "Situation: User chose not to answer."}
            
            Requirements:
            - Maximum 160 characters (SMS limit)
            - Polite and professional
            
            Respond with ONLY the SMS text, no quotes or explanation.
            """.trimIndent()
        }
        
        return try {
            val response = geminiModel.generateContent(prompt)
            response.text?.take(160) ?: getDefaultResponse(isUserUnavailable)
        } catch (e: Exception) {
            // Check for quota exhaustion (429 Resource Exhausted)
            if (e.message?.contains("429") == true || e.message?.contains("ResourceExhausted") == true) {
                Log.w(TAG, "Gemini Quota Exceeded - Using fallback response")
            } else {
                Log.e(TAG, "Gemini error: ${e.message}", e)
            }
            getDefaultResponse(isUserUnavailable)
        }
    }
    
    private fun getDefaultResponse(isUserUnavailable: Boolean): String {
        return if (isUserUnavailable) {
            "Hi, I'm currently away from my phone. I'll get back to you as soon as possible. - ProxiCall"
        } else {
            "Sorry I can't take your call right now. I'll call you back soon. - ProxiCall"
        }
    }
    
    /**
     * Send SMS to a phone number and log it
     */
    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent to $phoneNumber: $message")
            
            // Log to Firestore
            logActivity("SMS_SENT", phoneNumber, message)
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS", e)
            false
        }
    }

    private fun logActivity(action: String, caller: String, response: String) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser ?: return
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        
        val logEntry = hashMapOf(
            "action" to action,
            "caller" to caller,
            "response" to response,
            "timestamp" to System.currentTimeMillis()
        )
        
        db.collection("users")
            .document(user.uid)
            .collection("activity_logs")
            .add(logEntry)
            .addOnSuccessListener { Log.d(TAG, "Activity logged: $action") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to log activity", e) }
    }
    
    /**
     * Full flow: Announce caller, get response, send SMS if declined
     */
    fun handleIncomingCall(
        callerNumber: String,
        callerName: String?,
        isUserInRange: Boolean,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.Main) {
            val displayName = callerName ?: callerNumber
            
            if (isUserInRange) {
                // User has earbuds - speak to them
                speak("$displayName is calling. Say yes to answer, or no to decline.")
                
                // TODO: Implement STT to listen for response
                // For now, this is a placeholder
                // In production: use SpeechRecognizer to get user's voice response
                
            } else {
                // User is away - use STATIC response to save Gemini quota
                Log.d(TAG, "User out of range - using static SMS to save quota")
                val response = getDefaultResponse(isUserUnavailable = true)
                // speak("You have a missed call from $displayName. Sending automated response.") // Don't speak if out of range, no one is there!
                sendSms(callerNumber, response)
            }
        }
    }
    
    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
