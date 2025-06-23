package com.example.final_assignment_even_g28.data_class

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserToSave(
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
    @get:Exclude
    var badge: Badge?,
    var currentLevel: Int,
    var rating: Float,
    var isProfileImage: String,
    var profilePicture: String,
    var exp: Int
)