package com.example.final_assignment_even_g28.data_class

import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationPreferenceType {
    NEW_APPLICATION, STATUS_UPDATE_ON_PENDING_APPLICATION, REVIEW_RECEIVED_FOR_PAST_TRIP, LAST_MINUTE, CHECK_RECOMMENDED, NULL
}

@Serializable
data class NotificationPreference(
    var type: NotificationPreferenceType = NotificationPreferenceType.NULL,
    var enabled: Boolean = false
) {
    constructor(type: NotificationPreferenceType) : this(type, true)

    constructor(type: NotificationPreferenceType, enabled: Boolean, uid: String) : this(
        type, enabled
    ) {
        this.type = type
        this.enabled = enabled
    }
}

@Serializable
data class UserProfile(
    var uid: String = "", // Firebase UID used for Google Authentication
    var name: String = "",
    var surname: String = "",
    var typeOfExperiences: List<String> = emptyList(),
    var mostDesiredDestination: String = "",
    var phoneNumber: String = "",
    val email: String = "",
    @Contextual var dateOfBirth: Timestamp = Timestamp.Companion.now(),
    var pastExperiences: List<String> = emptyList(),
    var bio: String = "",
    var badge: Badge? = null,
    var currentLevel: Int = 1,
    var rating: Float = 0.0f,
    var isProfileImage: String = "Monogram",
    var notificationSettings: List<NotificationPreference> = listOf(
        NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
        NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
        NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
        NotificationPreference(
            NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION, true
        ),
        NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
    ),
    var profilePicture: String = "",
    var exp: Int = 0
) {
    constructor() : this(
        uid = "",
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = "",
        badge = null,
        currentLevel = 1,
        rating = 0.0F,
        notificationSettings = listOf(
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION, true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0,
    )
}

typealias Planner = UserProfile