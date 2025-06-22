package com.example.final_assignment_even_g28.data_class


import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NotificationType {
    NEW_PROPOSAL,
    NEW_APPLICATION,
    PARTICIPANT_APPROVED,
    PARTICIPANT_REJECTED,
    REVIEW_RECEIVED_FOR_PAST_TRIP,
    LAST_MINUTE,
    CHECK_RECOMMENDED,
    LAST_MINUTE_AUTOMATIC,
    USER_REVIEW_RECEIVED,
    BADGE_UNLOCKED,
    NULL;
}

data class Notification(
    @get:Exclude var id: String = "",
    val tripId: String = "",
    val title: String = "",

    var type: NotificationType = NotificationType.NULL,

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

        val debugTime = Timestamp(Date(currentTime))
        Log.d(
            "NotificationDebug",
            "Notification $title Current time: ${debugTime.toDate()}, Notification time: ${timestamp.toDate()}"
        )
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