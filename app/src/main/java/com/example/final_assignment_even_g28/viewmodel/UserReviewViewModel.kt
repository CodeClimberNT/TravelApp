package com.example.final_assignment_even_g28.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.data_class.UserReview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserReviewViewModel(private val model: UserReviewModel): ViewModel() {

    val myReviews: StateFlow<List<UserReview>> = model.myReviews
    val othersReview: StateFlow<List<UserReview>> = model.othersReview


    /*
    UserReviews Functions
     */

    fun getReviews(userUID: String){
        model.getReviews(userUID)
    }

    fun writeReview(review: UserReview) {
        model.writeReview(review)
    }
}