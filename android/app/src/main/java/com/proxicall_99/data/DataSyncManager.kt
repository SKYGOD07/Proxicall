package com.proxicall_99.data

import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

data class SyncedContact(
    val name: String,
    val phoneNumber: String,
    val lookupKey: String
)

data class SyncedCallLog(
    val number: String,
    val type: Int, // INCOMING, OUTGOING, MISSED
    val date: Long,
    val duration: Long,
    val name: String? = null
)

class DataSyncManager(private val context: Context) {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun syncData() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // 1. Sync Devices (Prioritize this for "Online" status)
        try {
            syncDevices(uid)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Sync Contacts
        try {
            val contacts = fetchContacts()
            if (contacts.isNotEmpty()) {
                db.collection("users").document(uid).collection("contacts")
                    .document("unified_list")
                    .set(mapOf("lastUpdated" to Date(), "items" to contacts))
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Sync Call Logs
        try {
            val logs = fetchCallLogs()
            if (logs.isNotEmpty()) {
                db.collection("users").document(uid).collection("call_logs")
                    .document("recent_history")
                    .set(mapOf("lastUpdated" to Date(), "items" to logs))
                    .await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun syncDevices(uid: String) {
        // Safe Proximity Check
        val bondedDevices = try {
            val proximityManager = com.proxicall_99.logic.ProximityManager(context)
            // proximityManager.isDeviceNear checks internal adapter, but we need bonded list
            // If we don't have permission, this might throw or return empty
            // For now, let's assume we can get basic info or just skip bonded if fails
            emptyList<String>() // TODO: Expose bonded devices from ProximityManager safely
        } catch (e: Exception) {
            emptyList()
        }

        // Add current device (Always succeeds)
        val currentDeviceName = android.os.Build.MODEL
        
        // Sync Current Device
        val currentDeviceData = mapOf(
            "name" to currentDeviceName,
            "type" to "Phone",
            "status" to "Online", // Explicitly set to Online
            "battery" to "Unknown",
            "metered" to false,
            "lastSynced" to Date()
        )
        db.collection("users").document(uid).collection("devices")
            .document("android_host")
            .set(currentDeviceData)
            .await()

        // Sync Bonded Devices (if any found)
        /* 
        bondedDevices.forEach { name ->
             // ... logic ...
        }
        */
    }

    private suspend fun fetchContacts(): List<SyncedContact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<SyncedContact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
            ),
            null, null, null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val keyIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)

            while (it.moveToNext()) {
                contacts.add(
                    SyncedContact(
                        name = it.getString(nameIndex) ?: "Unknown",
                        phoneNumber = it.getString(numIndex) ?: "",
                        lookupKey = it.getString(keyIndex) ?: ""
                    )
                )
            }
        }
        return@withContext contacts
    }

    private suspend fun fetchCallLogs(): List<SyncedCallLog> = withContext(Dispatchers.IO) {
        val logs = mutableListOf<SyncedCallLog>()
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.CACHED_NAME
                ),
                null, null, "${CallLog.Calls.DATE} DESC LIMIT 50"
            )

            cursor?.use {
                val numIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                val nameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

                while (it.moveToNext()) {
                    logs.add(
                        SyncedCallLog(
                            number = it.getString(numIndex) ?: "Unknown",
                            type = it.getInt(typeIndex),
                            date = it.getLong(dateIndex),
                            duration = it.getLong(durIndex),
                            name = it.getString(nameIndex)
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }
        return@withContext logs
    }
}
