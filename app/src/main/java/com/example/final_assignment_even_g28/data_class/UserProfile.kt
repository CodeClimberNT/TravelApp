package com.example.final_assignment_even_g28.data_class

import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
enum class NotificationPreferenceType {
    NEW_APPLICATION,
    STATUS_UPDATE_ON_PENDING_APPLICATION,
    REVIEW_RECEIVED_FOR_PAST_TRIP,
    LAST_MINUTE,
    CHECK_RECOMMENDED,
    NULL
}

@Serializable
data class NotificationPreference(
    var type: NotificationPreferenceType = NotificationPreferenceType.NULL,
    var enabled: Boolean = false
) {
    constructor(type: NotificationPreferenceType) : this(type, true)

    constructor(type: NotificationPreferenceType, enabled: Boolean, uid: String) : this(
        type,
        enabled
    ) {
        this.type = type
        this.enabled = enabled
    }
}
import kotlin.math.exp

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
        NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
        NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
        NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
        NotificationPreference(
            NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
            true
        ),
        NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
    ),
    var profilePicture: String = "",
    var exp: Int = 0
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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0,

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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0
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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0
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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0
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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0
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
            NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
            NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
            NotificationPreference(NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP, true),
            NotificationPreference(
                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                true
            ),
            NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true)
        ),
        profilePicture = "",
        exp = 0
    )


}

typealias Planner = UserProfile