package com.example.final_assignment_even_g28.model

import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    var id: Int = 0,
    var uid: String = "", // Firebase UID used for Google Authentication
    var name: String = "",
    var surname: String = "",
    var fullName: String = "",
    var nickName: String = "",
    var typeOfExperiences: List<String> = emptyList(),
    var mostDesiredDestination: String = "",
    var phoneNumber: String = "",
    val email: String = "",
    @Contextual var dateOfBirth: Timestamp = Timestamp.now(),
    var pastExperiences: List<String> = emptyList(),
    var bio: String = "",
    var badge: String = "",
    var currentLevel: Int = 1,
    var rating: Float = 0.0f,
    @get:Exclude
    var profilePicture: ProfilePictureData = ProfilePictureData.Monogram(""),
) {
    constructor() : this(
        id = 0,
        uid = "",
        fullName = "",
        nickName = "",
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = ProfilePictureData.Monogram("")
    )

    constructor(id: Int, nickName: String, profilePicture: ProfilePictureData) : this(
        id = id,
        uid = "",
        fullName = "",
        nickName = nickName,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = profilePicture
    )

    constructor(id: Int, uid: String, nickName: String, profilePicture: ProfilePictureData) : this(
        id = id,
        uid = uid,
        fullName = "",
        nickName = nickName,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = profilePicture
    )

    constructor(fullName: String, rating: Float, description: String) : this(
        id = 0,
        uid = "",
        fullName = fullName,
        nickName = fullName,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        pastExperiences = emptyList(),
        bio = description,
        badge = "",
        currentLevel = 1,
        rating = rating,
        profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
    )

    constructor(name: String, rating: Float, avatar: ProfilePictureData, contact: String) : this(
        id = 4,
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
        badge = "",
        currentLevel = 1,
        rating = rating,
        profilePicture = avatar
    )

    constructor(uid: String, name: String, email: String) : this(
        id = 0,
        uid = uid,
        fullName = name,
        nickName = name,
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = email,
        pastExperiences = emptyList(),
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
    )
}

typealias Planner = UserProfile
