package com.example.final_assignment_even_g28.utils

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel
import com.example.final_assignment_even_g28.viewmodel.auth.SignInViewModel

object AppFactory : ViewModelProvider.Factory {

    val travelProposalModel = TravelProposalModel()
    val userProfileModel = UserProfileModel()
    val reviewModel = UserReviewModel()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) ->
                UserProfileViewModel(userProfileModel) as T

            modelClass.isAssignableFrom(TravelProposalViewModel::class.java) ->
                TravelProposalViewModel(travelProposalModel, userProfileModel) as T

            modelClass.isAssignableFrom(SignInViewModel::class.java) ->
                SignInViewModel() as T

            modelClass.isAssignableFrom(UserReviewViewModel::class.java) ->
                UserReviewViewModel(reviewModel) as T


            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}