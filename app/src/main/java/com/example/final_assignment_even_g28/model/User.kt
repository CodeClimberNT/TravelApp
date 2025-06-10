package com.example.final_assignment_even_g28.model

import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Int,
    var name: String,
    var surname: String,
    var nickName: String,
    var typeOfExperiences: List<String>,
    var mostDesiredDestination: String,
    var phoneNumber: String,
    val email: String,
    var dateOfBirth: String,
    var pastExperiences: String,
    var bio: String,
    var badge: String,
    var currentLevel: Int,
    var rating: Float,
    var profilePicture: ProfilePictureData,
    var logged: Boolean
) {
    constructor() : this(
        id = 0,
        name = "",
        surname = "",
        nickName = "",
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        dateOfBirth = "",
        pastExperiences = "",
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = ProfilePictureData.Monogram(""),
        logged = false
    )

    constructor(id: Int, nickName: String, profilePicture: ProfilePictureData) : this(
        id = id,
        name = "",
        surname = nickName,
        nickName = "",
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        dateOfBirth = "",
        pastExperiences = "",
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = 0.0F,
        profilePicture = profilePicture,
        logged = false
    )

    constructor(fullName: String, rating: Float, description: String) : this(
        id = 0,
        name = fullName,
        surname = fullName,
        nickName = "",
        typeOfExperiences = emptyList(),
        mostDesiredDestination = "",
        phoneNumber = "",
        email = "",
        dateOfBirth = "",
        pastExperiences = "",
        bio = description,
        badge = "",
        currentLevel = 1,
        rating = rating,
        profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE),
        logged = false
    )

    constructor(name: String, rating: Float, avatar: ProfilePictureData, contact: String) : this(
        id = 0,
        name = name,
        surname = name,
        typeOfExperiences = emptyList(),
        nickName = "",
        mostDesiredDestination = "",
        phoneNumber = "",
        email = contact,
        dateOfBirth = "",
        pastExperiences = "",
        bio = "",
        badge = "",
        currentLevel = 1,
        rating = rating,
        profilePicture = avatar,
        logged = false
    )
}