package com.example.final_assignment_even_g28.data_class

import androidx.compose.material.icons.Icons
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
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
    var badge: String = "",
    var currentLevel: Int = 1,
    var rating: Float = 0.0f,
    var isProfileImage: String = "Monogram",
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
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = "",
        exp = 0
    )

    constructor(nickName: String) : this(
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
        profilePicture = "",
        exp = 0
    )

    constructor(uid: String, nickName: String) : this(
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
        badge = "",
        currentLevel = 1,
        rating = rating,
        profilePicture = "",
        exp = 0
    )

    constructor(name: String, rating: Float, avatar: Icons, contact: String) : this(
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
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = "",
        exp = 0
    )
}

typealias Planner = UserProfile