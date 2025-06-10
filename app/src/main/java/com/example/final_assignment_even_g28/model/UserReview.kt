package com.example.final_assignment_even_g28.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


/*
IN DATABASE:
user_review:
id: Automatic,
reviewedUserId: number,
reviewerId: number,
title: String
rating: number,
description: String,
timestamp: timestamp,
 */

@Serializable
data class UserReview(
    val reviewedUserId: Int = 0,
    val reviewerId: Int = 0,
    val reviewerName: String = "",
    val reviewedName: String = "",
    val title: String = "",
    val images: List<String> = emptyList(),
    @get:Exclude val localImages: List<String> = emptyList(),
    val rating: Float = 0f,
    val description: String = "",
    @Contextual val timestamp: Timestamp = Timestamp.now(),
)
