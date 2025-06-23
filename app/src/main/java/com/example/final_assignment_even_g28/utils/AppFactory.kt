package com.example.final_assignment_even_g28.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReviewModel
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel

object AppFactory : ViewModelProvider.Factory {
    val userProfileModel = UserProfileModel()
    val travelProposalModel = TravelProposalModel()
    val reviewModel = UserReviewModel()

    private var userProfileViewModel: UserProfileViewModel? = null
    private var travelProposalViewModel: TravelProposalViewModel? = null
    private var userReviewViewModel: UserReviewViewModel? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserProfileViewModel::class.java) -> {
                // Return singleton instance
                if (userProfileViewModel == null) {
                    userProfileViewModel = UserProfileViewModel(userProfileModel)
                }
                userProfileViewModel as T
            }

            modelClass.isAssignableFrom(TravelProposalViewModel::class.java) -> {
                // Return singleton instance
                if (travelProposalViewModel == null) {
                    travelProposalViewModel =
                        TravelProposalViewModel(travelProposalModel, userProfileModel)
                }
                travelProposalViewModel as T
            }

            modelClass.isAssignableFrom(UserReviewViewModel::class.java) -> {
                // Return singleton instance
                if (userReviewViewModel == null) {
                    userReviewViewModel =
                        UserReviewViewModel(reviewModel, userProfileModel, travelProposalModel)
                }
                userReviewViewModel as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}