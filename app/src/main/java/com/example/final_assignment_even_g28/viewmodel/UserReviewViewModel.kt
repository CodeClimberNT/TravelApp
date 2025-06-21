package com.example.final_assignment_even_g28.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data_class.BadgeType
import com.example.final_assignment_even_g28.data_class.UserReview
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserReviewViewModel(
    private val reviewModel: UserReviewModel,
    private val userModel: UserProfileModel
) : ViewModel() {

    private val loggedUser = userModel.loggedUser

    val myReviews: StateFlow<List<UserReview>> = reviewModel.myReviews
    val othersReview: StateFlow<List<UserReview>> = reviewModel.othersReview

    /*
    UserReviews Functions
     */

    fun getReviews(userUID: String){
        reviewModel.getReviews(userUID)
    }

    fun writeReview(review: UserReview) {
        viewModelScope.launch {
            val result = reviewModel.writeReview(review)
            if (result == true && review.rating >= 5) {
                userModel.triggerBadgeProgress(
                    targetUserUID = loggedUser.value.uid,
                    badgeType = BadgeType.YOU_KNOW_IT,
                    incrementBy = 1
                )
                Log.d("UserReviewViewModel", "Review written successfully")
            }
        }
    }
}