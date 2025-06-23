package com.example.final_assignment_even_g28.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data_class.BadgeType
import com.example.final_assignment_even_g28.data_class.NotificationType
import com.example.final_assignment_even_g28.data_class.UserReview
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UserReviewViewModel(
    private val reviewModel: UserReviewModel,
    private val userModel: UserProfileModel,
    private val travelProposalModel: TravelProposalModel,
) : ViewModel() {

    private val _myReviews = MutableStateFlow<List<UserReview>>(emptyList())
    val myReviews: StateFlow<List<UserReview>>
        get() = _myReviews

    private val _othersReviews = MutableStateFlow<List<UserReview>>(emptyList())
    val othersReviews: StateFlow<List<UserReview>>
        get() = _othersReviews

    init {
        // Observe user changes and manage review data accordingly
        loadReviewsForUser()
    }

//    private fun observeUserAndLoadReviews() {
//        viewModelScope.launch {
//            userModel.loggedUser.collect { user ->
//                if (user.uid.isNotEmpty()) {
//                    Log.d("UserReviewViewModel", "User logged in: ${user.uid}, loading reviews")
//                    loadReviewsForUser(user.uid)
//                } else {
//                    Log.d("UserReviewViewModel", "User logged out, clearing reviews")
//                    clearReviews()
//                }
//            }
//        }
//    }

    private fun loadReviewsForUser() {
        // Load reviews written by user
        viewModelScope.launch {
            reviewModel.getMyReviews().collect { reviews ->
                _myReviews.value = reviews
                Log.d("UserReviewViewModel", "Updated my reviews: ${reviews.size}")
            }
        }

        // Load reviews about user
        viewModelScope.launch {
            reviewModel.getOtherReviews().collect { reviews ->
                _othersReviews.value = reviews
                Log.d("UserReviewViewModel", "Updated reviews about user: ${reviews.size}")
            }
        }
    }

    private fun clearReviews() {
        _myReviews.value = emptyList()
        _othersReviews.value = emptyList()
    }

    fun writeReview(review: UserReview) {
        viewModelScope.launch {
            try {

                val result = reviewModel.writeReview(review)
                if (result.isSuccess) {
                    travelProposalModel.addNotification(
                        tripId = "",
                        title = review.title,
                        type = NotificationType.USER_REVIEW_RECEIVED,
                        notificationOwnerId = review.reviewerUID.toString(),
                        applicantId = review.reviewedUserUID.toString(),
                        tripPlannerId = null,
                        reviewedUser = review.reviewedUserUID.toString()
                    )
                    if (review.rating >= 5) {
                        userModel.triggerBadgeProgress(
                            targetUserUID = review.reviewerUID,
                            badgeType = BadgeType.YOU_KNOW_IT,
                            incrementBy = 1
                        )
                        Log.d("UserReviewViewModel", "Review written successfully")
                    }
                }

            } catch (e: Exception) {
                Log.e("UserReviewViewModel", "Error writing review: ${e.message}")
            }
        }
    }
}