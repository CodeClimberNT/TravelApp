package com.example.final_assignment_even_g28.model

import android.util.Log
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.UserReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class UserReviewModel {
    fun getMyReviews(userUID: String): Flow<List<UserReview>> = callbackFlow {
        val collection = Collections.userReview.whereEqualTo("reviewerUID", userUID)

        val listener = collection.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Log.e("UserReviewModel", "Error fetching reviews: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                val reviews = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserReview::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(reviews)
            } else {
                Log.d("UserReviewModel", "No reviews found for user: $userUID")
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getOtherReviews(userUID: String): Flow<List<UserReview>> = callbackFlow {
        val collection = Collections.userReview.whereEqualTo("reviewedUserUID", userUID)

        val listener = collection.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Log.e("UserReviewModel", "Error fetching reviews: ${error.message}")
                close(error)
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                val reviews = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserReview::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(reviews)
            } else {
                Log.d("UserReviewModel", "No reviews found for user: $userUID")
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun writeReview(review: UserReview): Result<String> {
        return try {
            val docRef = Collections.userReview.add(review).await()
            Log.d("UserReviewModel", "Review written successfully: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("UserReviewModel", "Error writing review: ${e.message}")
            Result.failure(e)
        }
    }
}
