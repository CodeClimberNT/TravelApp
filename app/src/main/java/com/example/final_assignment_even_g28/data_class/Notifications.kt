package com.example.final_assignment_even_g28.data_class


import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.Locale

data class Notification(
    @get:Exclude var id: String = "",
    val tripId: String = "",
    val title: String = "",
    val type: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val read: List<String> = emptyList(),
    val notificationOwnerId: String = "",
    val applicantId: String? = null,
    val tripPlannerId: String? = null,
    val reviewedUser: String? = null
) {
    // Metodi di utilità
    fun isRead(userId: String): Boolean {
        return read.contains(userId)
    }

    fun isRecent(): Boolean {
        val currentTime = System.currentTimeMillis()
        val notificationTime = timestamp.toDate().time
        return (currentTime - notificationTime) < (60 * 1000) // 1 minuto
    }

    fun getFormattedTimestamp(): String {
        return if (isRecent()) {
            "now"
        } else {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(timestamp.toDate())
        }
    }
}