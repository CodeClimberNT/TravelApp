package com.example.final_assignment_even_g28.model

import android.util.Log
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.UserReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class UserReviewModel() {
    private val userProfileModel = UserProfileModel()
    private val travelProposalModel = TravelProposalModel()

    private val _myReviews = MutableStateFlow<List<UserReview>>(emptyList())
    val myReviews: StateFlow<List<UserReview>> get() = _myReviews

    private val _othersReview = MutableStateFlow<List<UserReview>>(emptyList())
    val othersReview: StateFlow<List<UserReview>> get() = _othersReview

    fun getReviews(userUID: String) {
        Collections.userReview.get().addOnSuccessListener { querySnapshot ->
            val reviews = querySnapshot.documents.mapNotNull { documentSnapshot ->
                val userReview = documentSnapshot.toObject(UserReview::class.java)

                val reviewerName = userProfileModel.loggedUser.value.name
                val reviewedName =
                    userReview?.reviewedUserUID?.let { userProfileModel.getNameByUID(it.toString()) }

                userReview?.copy(
                    reviewerName = reviewerName,
                    reviewedName = reviewedName ?: "Unknown User"
                )
            }
            val myReviewsList = mutableListOf<UserReview>()
            val othersReviewList = mutableListOf<UserReview>()

            for (review in reviews) {
                if (review.reviewerUID == userUID) {
                    myReviewsList.add(review)
                } else {
                    if (review.reviewedUserUID == userUID) {
                        othersReviewList.add(review)
                    }
                }
            }
            _myReviews.value = myReviewsList
            _othersReview.value = othersReviewList
        }
    }

    suspend fun writeReview(review: UserReview): Boolean {
        try {
            val result = Collections.userReview.add(review).await()
            if (result == null) {
                throw Error("Failed to add review: ${review.title}")
                return false
            }

            Log.d("Write Review", "Review added successfully")

            travelProposalModel.addNotification(
                tripId = "",
                title = review.title,
                type = "userReviewReceived",
                notificationOwnerId = review.reviewerUID.toString(),
                applicantId = review.reviewedUserUID.toString(),
                tripPlannerId = null,
                reviewedUser = review.reviewedUserUID.toString()
            )
            getReviews(review.reviewerUID)

            return true
        } catch (e: Exception) {
            Log.e("Write Review", "Error adding review: ${e.message}")
            return false
        }
    }
}
