package com.example.final_assignment_even_g28.data_class

import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreference(
    var type: String = "",
    var enabled: Boolean = false
) {
    constructor(type: String) : this(type, true)

    constructor(type: String, enabled: Boolean, uid: String) : this(type, enabled) {
        this.type = type
        this.enabled = enabled
    }
}

@Serializable
data class UserProfile(
    var uid: String = "", // Firebase UID used for Google Authentication
    var name: String = "",
    var surname: String = "",
    var fullName: String = "",
    var nickName: String = "",
    var typeOfExperiences: List<String> = emptyList(),
    var mostDesiredDestination: String = "",
    var phoneNumber: String = "",
    val email: String = "",
    @Contextual var dateOfBirth: Timestamp = Timestamp.Companion.now(),
    var pastExperiences: List<String> = emptyList(),
    var bio: String = "",
    @get:Exclude
    @set:Exclude
    var badge: Badge? = null,
    var currentLevel: Int = 1,
    var rating: Float = 0.0f,
    var isProfileImage: String = "Monogram",
    var notificationSettings: List<NotificationPreference> = listOf(
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
    @get:Exclude
    var profilePicture: ProfilePictureData = ProfilePictureData.Monogram(""),
) {
    constructor() : this(
        uid = "",
        fullName = "",
        nickName = "",
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
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = ProfilePictureData.Monogram(""),

    )

    constructor(nickName: String, profilePicture: ProfilePictureData) : this(
        uid = "",
        fullName = "",
        nickName = nickName,
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
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = profilePicture
    )

    constructor(uid: String, nickName: String, profilePicture: ProfilePictureData) : this(
        uid = uid,
        fullName = "",
        nickName = nickName,
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
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = profilePicture
    )

    constructor(fullName: String, rating: Float, description: String) : this(
        uid = "",
        fullName = fullName,
        nickName = fullName,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = description,
        badge = null,
        currentLevel = 1,
        rating = rating,
        notificationSettings = listOf(
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
    )

    constructor(name: String, rating: Float, avatar: ProfilePictureData, contact: String) : this(
        uid = "4",
//        uid = "13",
        fullName = name,
        nickName = name,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "Alice",
        phoneNumber = "",
        email = contact,
        pastExperiences = emptyList(),
        bio = "",
        badge = null,
        currentLevel = 1,
        rating = rating,
        notificationSettings = listOf(
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = avatar
    )

    constructor(uid: String, name: String, email: String) : this(
        uid = uid,
        fullName = name,
        nickName = name,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = email,
        pastExperiences = emptyList(),
        bio = "",
        badge = null,
        currentLevel = 1,
        rating = 0.0F,
        notificationSettings = listOf(
        NotificationPreference("lastMinute", true),
        NotificationPreference("newApplication", true),
        NotificationPreference("reviewReceivedForPastTrip", true),
        NotificationPreference("statusUpdateOnPendingApplication", true),
        NotificationPreference("checkRecommended", true)
    ),
        profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
    )


}

typealias Planner = UserProfile