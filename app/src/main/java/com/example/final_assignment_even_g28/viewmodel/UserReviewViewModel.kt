package com.example.final_assignment_even_g28.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.model.Planner
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfile
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.model.UserReview
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserReviewViewModel : ViewModel() {
    //UserReviewViewModel variables
    private val userId: Int = 1

    private val model = UserProfileModel()
    //private var currentUser2 = model.selectedUserProfile.value
    
    private val _myReviews = MutableLiveData<List<UserReview>>()
    val myReviews: LiveData<List<UserReview>> get() = _myReviews

    private val _othersReview = MutableLiveData<List<UserReview>>()
    val othersReview: LiveData<List<UserReview>> get() = _othersReview

    init {
            getReviews()
    }
    /*
    UserReviews Functions
     */


    //Function that reads all of the reviews on the db and save the onesuser-related
     fun getReviews(){
        viewModelScope.launch {
            val querySnapshot = Collections.userReview.get().await()

            if (querySnapshot != null && !querySnapshot.isEmpty) {
                val reviews = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    val userReview = documentSnapshot.toObject(UserReview::class.java)

                    val reviewerName = userReview?.reviewerId?.let { getUserName(it.toString()) }
                    val reviewedName = userReview?.reviewedUserId?.let { getUserName(it.toString()) }

                    userReview?.copy(
                        reviewerName = reviewerName ?: "Unknown Reviewer",
                        reviewedName = reviewedName ?: "Unknown User"
                    )
                }
                val myReviewsList = mutableListOf<UserReview>()
                val othersReviewList = mutableListOf<UserReview>()

                for (review in reviews){
                    if(review.reviewerId == userId){
                        myReviewsList.add(review)
                    }else{
                        if (review.reviewedUserId == userId){
                            othersReviewList.add(review)
                        }
                    }
                }
                _myReviews.value = myReviewsList
                _othersReview.value = othersReviewList
            }
            else {
                Log.d("UserReviewViewModel", "No reviews found")
                _myReviews.value = emptyList()
                _othersReview.value = emptyList()

            }
        }
    }



    suspend fun getUserName(id: String): String? {
        return suspendCoroutine { continuation ->
            Collections.users.document(id).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("name")
                        continuation.resume(userName)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    fun writeReview(review: UserReview) {
        viewModelScope.launch {
            try {
                Collections.userReview.add(review).await()
                Log.d("Write Review", "Review added successfully")

                val travelProposalModel = TravelProposalModel()
                travelProposalModel.addNotification(
                    tripId = "",
                    title = review.title,
                    type = "userReviewReceived",
                    notificationOwnerId = review.reviewerId.toString(),
                    applicantId = review.reviewedUserId.toString(),
                    tripPlannerId = null,
                    reviewedUser = review.reviewedUserId.toString()
                )

                getReviews()
            } catch (e: Exception) {
                Log.d("Write Review", "Error adding review: $e")
            }
        }
    }

    fun getReviewerName(id: Int): String {
        var reviewerName = ""
        Collections.users.document(id.toString()).get()
            .addOnSuccessListener { user ->
                if (user.exists()) {
                    val reviewerName = user.getString("name")
                }
            }

        return reviewerName
    }

}