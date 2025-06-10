package com.example.final_assignment_even_g28.model

import com.example.final_assignment_even_g28.model.UserProfile
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.google.firebase.Timestamp
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserToSave(
    var id: Int,
    var uid: String, // Firebase UID used for Google Authentication
    var name: String,
    var surname: String,
    var typeOfExperiences: List<String>,
    var mostDesiredDestination: String,
    var phoneNumber: String,
    val email: String,
    @Contextual var dateOfBirth: Timestamp,
    var pastExperiences: List<String>,
    var bio: String,
    var badge: String,
    var currentLevel: Int,
    var rating: Float,
){/*
    constructor(id: Int, uid: String, name: String, surname: String, typeOfExperiences: List<String>,
                mostDesiredDestination: String, phoneNumber: String, email: String, dateOfBirth: Timestamp, pastExperiences: List<String>,
                bio: String, badge: String, currentLevel: Int, rating: Float) : this(
        id = id,
        uid = uid,
        name = name,
        surname = surname,
        typeOfExperiences = typeOfExperiences,
        mostDesiredDestination = mostDesiredDestination,
        email = email,
        dateOfBirth = dateOfBirth,
        phoneNumber = phoneNumber,
        pastExperiences = pastExperiences,
        bio = bio,
        badge = badge,
        currentLevel = currentLevel,
        rating = rating)*/
}