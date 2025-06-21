package com.example.final_assignment_even_g28.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.final_assignment_even_g28.model.ImageStorageModel
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel

object AppFactory : ViewModelProvider.Factory {
    val imageStorageModel = ImageStorageModel()
    val userProfileModel = UserProfileModel()

    val travelProposalModel =
        TravelProposalModel(userModel = userProfileModel, imageStorageModel = imageStorageModel)
    val reviewModel = UserReviewModel(
        userProfileModel = userProfileModel,
        travelProposalModel = travelProposalModel
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) ->
                UserProfileViewModel(userProfileModel) as T

            modelClass.isAssignableFrom(TravelProposalViewModel::class.java) ->
                TravelProposalViewModel(travelProposalModel, userProfileModel) as T

            modelClass.isAssignableFrom(UserReviewViewModel::class.java) ->
                UserReviewViewModel(reviewModel, userProfileModel) as T


            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}