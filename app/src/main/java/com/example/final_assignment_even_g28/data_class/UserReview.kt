package com.example.final_assignment_even_g28.data_class

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class UserReview(
    val reviewedUserUID: String = "",
    val reviewerUID: String = "",
    val reviewerName: String = "",
    val reviewedName: String = "",
    val title: String = "",
    val images: List<String> = emptyList(),
    @get:Exclude val localImages: List<String> = emptyList(),
    val rating: Float = 0f,
    val description: String = "",
    @Contextual val timestamp: Timestamp = Timestamp.Companion.now(),
)