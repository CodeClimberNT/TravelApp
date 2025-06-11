package com.example.final_assignment_even_g28.data_class

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class TravelReview(
    @get:Exclude var id: String = "",
    val reviewerId: String = "",
    @get:Exclude var reviewerName: String = "",
    val title: String = "",
    val images: List<String> = emptyList(),
    @get:Exclude val tempImages: List<String> = mutableListOf(),
    val rating: Float = 0f,
    val description: String = "",
    val timestamp: Timestamp = Timestamp.Companion.now()
)