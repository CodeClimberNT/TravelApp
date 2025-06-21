package com.example.final_assignment_even_g28.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.Filters
import com.example.final_assignment_even_g28.data_class.Notification
import com.example.final_assignment_even_g28.data_class.NotificationType
import com.example.final_assignment_even_g28.data_class.Participant
import com.example.final_assignment_even_g28.data_class.ParticipantDetailed
import com.example.final_assignment_even_g28.data_class.ParticipantStatus
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.data_class.TravelReview
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.utils.MAX
import com.example.final_assignment_even_g28.utils.UNKNOWN_USER
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TravelProposalModel(
    private val imageStorageModel: ImageStorageModel,
    private val userModel: UserProfileModel
) {
    //TODO: add private member with mutableStateFlow and StateFlow

    fun getFilteredTravelProposals(
        filters: Filters
    ): Flow<List<TravelProposal>> = callbackFlow {
        val allActivities = ActivityTag.entries

        var query: Query = Collections.travelProposals.whereGreaterThanOrEqualTo(
            "tripStartDate", filters.startDate ?: Timestamp.now()
        ).whereLessThanOrEqualTo("tripEndDate", filters.endDate ?: Timestamp.MAX())
            .whereArrayContainsAny("activities", filters.activities.ifEmpty { allActivities })
            .whereGreaterThanOrEqualTo("price.min", filters.minPrice)
            .whereLessThanOrEqualTo("price.max", filters.maxPrice)

        if (filters.groupSize != null) {
            query = query.whereEqualTo("maxParticipant", filters.groupSize)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                val proposals = snapshot.toTravelProposalsWithIds()
                val filteredProposals =
                    if (filters.title.isNotEmpty() && filters.title != "Anywhere") {
                        proposals.filter { it.title.contains(filters.title, ignoreCase = true) }
                    } else {
                        proposals
                    }

                trySend(filteredProposals)
            } else {
                Log.e("TravelProposalModel", error.toString())
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    fun getTravelProposalById(id: String): Flow<TravelProposal?> = callbackFlow {
        val listener =
            Collections.travelProposals.document(id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TravelProposalModel", error.toString())
                    trySend(TravelProposal())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toTravelProposalWithId())
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }


//    fun getMyTravelProposals(userId: String): Flow<List<TravelProposal>> = callbackFlow {
//        val query = Collections.travelProposals.whereEqualTo("tripPlannerId", userId)
//            .whereGreaterThan("tripEndDate", Timestamp.now())
//            .orderBy("tripEndDate", Query.Direction.DESCENDING)
//
//        val listener = query.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                Log.e("TravelProposalModel", error.toString())
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                trySend(snapshot.toTravelProposalsWithIds())
//            }
//        }
//        awaitClose { listener.remove() }
//    }

    fun getMyTravelProposals(userId: String): Flow<List<TravelProposal>> = callbackFlow {
        val currentTime = Timestamp.now()
        val myTravelProposals = mutableMapOf<String, TravelProposal>()
        var ownedProposalsLoaded = false
        var participatingProposalsLoaded = false

        fun checkAndSendResults() {
            if (ownedProposalsLoaded && participatingProposalsLoaded) {
                // Convert map values to list and sort by end date (most recent first)
                val sortedProposals = myTravelProposals.values.toList()
                    .sortedBy { it.tripStartDate.seconds }
                Log.d(
                    "TravelProposalModel",
                    "Sending sorted personal proposals: ${sortedProposals.size} proposals"
                )
                trySend(sortedProposals)
            }
        }

        val ownedQuery = Collections.travelProposals
            .whereEqualTo("tripPlannerId", userId)
            .whereGreaterThan("tripEndDate", currentTime)
            .orderBy("tripStartDate", Query.Direction.ASCENDING)


        val ownedListener = ownedQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TravelProposalModel", "Error getting owned past proposals: $error")
                if (!ownedProposalsLoaded) {
                    ownedProposalsLoaded = true
                    checkAndSendResults()
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val ownedProposals = snapshot.toTravelProposalsWithIds()

                // Add owned proposals to the map
                ownedProposals.forEach { proposal ->
                    myTravelProposals[proposal.id] = proposal
                }

                ownedProposalsLoaded = true
                checkAndSendResults()
            }
        }


        // already got the past proposals made by the user
        val participatedQuery = Collections.travelProposals
            .whereNotEqualTo("tripPlannerId", userId)
            .whereGreaterThan("tripEndDate", currentTime)
            .orderBy("tripStartDate", Query.Direction.ASCENDING)

        val participatingListener = participatedQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TravelProposalModel", "Error getting participated past proposals: $error")
                if (!participatingProposalsLoaded) {
                    participatingProposalsLoaded = true
                    checkAndSendResults()
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val allProposals = snapshot.toTravelProposalsWithIds()

                // Filter proposals where user is an approved participant (but not the planner)
                val participatingProposals = allProposals.filter { proposal ->
                    proposal.participants.any { participant ->
                        //TODO: decide if the user should see proposals
                        // where they are rejected in my trips
                        participant.id == userId //&& participant.status != ParticipantStatus.REJECTED
                    }
                }

                // Add participated proposals to the map
                participatingProposals.forEach { proposal ->
                    myTravelProposals[proposal.id] = proposal
                }

                participatingProposalsLoaded = true
                checkAndSendResults()
            }
        }

        awaitClose {
            ownedListener.remove()
            participatingListener.remove()
        }
    }


    fun getPastTravelProposals(userId: String): Flow<List<TravelProposal>> = callbackFlow {
        val currentTime = Timestamp.now()
        val pastTravelProposals = mutableMapOf<String, TravelProposal>()
        var ownedProposalsLoaded = false
        var participatedProposalsLoaded = false

        fun checkAndSendResults() {
            if (ownedProposalsLoaded && participatedProposalsLoaded) {
                // Convert map values to list and sort by end date (most recent first)
                val sortedProposals = pastTravelProposals.values.toList()
//                    .sortedByDescending { it.tripEndDate.seconds }
                Log.d(
                    "TravelProposalModel",
                    "Sending sorted past proposals: ${sortedProposals.size} proposals"
                )
                trySend(sortedProposals)
            }
        }
        Log.d("TravelProposalModel", "Getting past travel proposals for user: $userId")

        val ownedQuery = Collections.travelProposals.whereEqualTo("tripPlannerId", userId)
            .whereLessThanOrEqualTo("tripEndDate", currentTime)
            .orderBy("tripEndDate", Query.Direction.DESCENDING)


        val ownedListener = ownedQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TravelProposalModel", "Error getting owned past proposals: $error")
                if (!ownedProposalsLoaded) {
                    ownedProposalsLoaded = true
                    checkAndSendResults()
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val ownedProposals = snapshot.toTravelProposalsWithIds()

                // Add owned proposals to the map
                ownedProposals.forEach { proposal ->
                    pastTravelProposals[proposal.id] = proposal
                }

                ownedProposalsLoaded = true
                checkAndSendResults()
            }
        }


        // already got the past proposals made by the user
        val participatedQuery = Collections.travelProposals.whereNotEqualTo("tripPlannerId", userId)
            .whereLessThanOrEqualTo("tripEndDate", currentTime)
            .orderBy("tripEndDate", Query.Direction.DESCENDING)

        val participatedListener = participatedQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TravelProposalModel", "Error getting participated past proposals: $error")
                if (!participatedProposalsLoaded) {
                    participatedProposalsLoaded = true
                    checkAndSendResults()
                }
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val allProposals = snapshot.toTravelProposalsWithIds()

                // Filter proposals where user is an approved participant (but not the planner)
                val participatedProposals = allProposals.filter { proposal ->
                    proposal.participants.any { participant ->
                        (participant.id == userId) && (participant.status == ParticipantStatus.APPROVED)
                    }
                }

                // Add participated proposals to the map
                participatedProposals.forEach { proposal ->
                    pastTravelProposals[proposal.id] = proposal
                }

                participatedProposalsLoaded = true
                checkAndSendResults()
            }
        }

        awaitClose {
            ownedListener.remove()
            participatedListener.remove()
        }
    }

    fun getReviewsForProposal(proposalId: String): Flow<List<TravelReview>> = callbackFlow {
        val query = Collections.getReviewCollection(proposalId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TravelProposalModel", "Error getting reviews: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val reviews = snapshot.documents.mapNotNull { document ->
                    document.toObject(TravelReview::class.java)?.apply {
                        Log.d("TravelProposalModel", "Review found: $this")
                        val userName =
                            userModel.getUserByUid(reviewerId)?.name ?: "Unknown User"
                        Log.d("TravelProposalModel", "Reviewer name: $userName")
                        id = document.id
                        reviewerName = userName
                    }
                }
                trySend(reviews)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { listener.remove() }
    }

    suspend fun submitReview(
        review: TravelReview,
        plannerId: String,
        tripId: String,
        tripTitle: String,
        imageUris: List<Uri>,
        context: Context
    ): Result<String> {
        return try {
            val reviewId = UUID.randomUUID().toString()

            if (!imageStorageModel.validateReviewFolderStructure(plannerId, tripId, reviewId)) {
                return Result.failure(Exception("Invalid folder structure for userId: $plannerId, tripId: $tripId, reviewId: $reviewId"))
            }

            // Upload review images if any
            val imageUrls = if (imageUris.isNotEmpty()) {
                val uploadResult = imageStorageModel.uploadMultipleReviewImages(
                    plannerId = plannerId,
                    tripId = tripId,
                    reviewId = reviewId,
                    imageUris = imageUris,
                    context = context
                )

                if (uploadResult.isSuccess) {
                    uploadResult.getOrThrow()
                } else {
                    throw uploadResult.exceptionOrNull()
                        ?: Exception("Failed to upload review images")
                }
            } else {
                emptyList()
            }

            // Create final review with uploaded images
            val finalReview = review.copy(
                images = imageUrls,
                tempImages = mutableListOf(),
                timestamp = Timestamp.now()
            )

            // Add review to subCollection
            Collections.getReviewCollection(tripId).document(reviewId).set(finalReview)

            Log.d("TravelProposalModel", "Review submitted successfully: $reviewId")
            addNotification(
                tripId = tripId,
                title = tripTitle,
                type = NotificationType.REVIEW_RECEIVED_FOR_PAST_TRIP,
                notificationOwnerId = review.reviewerId,
                tripPlannerId = plannerId,
                reviewedUser = review.reviewerId
            )
            Result.success(reviewId)
        } catch (e: Exception) {
            Log.e("TravelProposalModel", "Failed to submit review: ${e.message}")
            Result.failure(e)
        }
    }

//    fun submitReview(travelProposalId: String, review: TravelReview) {
//        Collections.travelProposals.document(travelProposalId)
//            .update("reviews", FieldValue.arrayUnion(review))
//    }

    fun getTripPlanner(travelProposalId: String): UserProfile? {
        return UserProfile()
    }


    fun approveParticipant(userId: String, trip: TravelProposal): Boolean {
        val approvedCount = getApprovedParticipantsCount(trip)
        if (approvedCount >= trip.maxParticipant) {
            return false
        }

        updateParticipantInList(trip, userId, ParticipantStatus.APPROVED)

        val tripUpdates = mapOf(
            "participants" to trip.participants
        )

        Collections.travelProposals.document(trip.id).update(tripUpdates)
            .addOnFailureListener { error ->
                Log.e("addParticipants", "Failed to update trip: ${error.message}")
            }.addOnSuccessListener {
                addNotification(
                    trip.id,
                    trip.title,
                    NotificationType.PARTICIPANT_APPROVED,
                    trip.tripPlannerId,
                    applicantId = userId,
                    trip.tripPlannerId
                )
            }

        addNotification(
            trip.id,
            trip.title,
            NotificationType.PARTICIPANT_APPROVED,
            trip.tripPlannerId,
            applicantId = userId,
            trip.tripPlannerId
        )
        return true
    }

    fun rejectParticipant(userId: String, trip: TravelProposal) {
        updateParticipantInList(trip, userId, ParticipantStatus.REJECTED)

        val tripUpdates = mapOf(
            "participants" to trip.participants
        )

        Collections.travelProposals.document(trip.id).update(tripUpdates)
            .addOnFailureListener { error ->
                Log.e("deleteParticipant", "Failed to update trip: ${error.message}")
            }
        addNotification(
            trip.id,
            trip.title,
            NotificationType.PARTICIPANT_REJECTED,
            trip.tripPlannerId,
            applicantId = userId,
            trip.tripPlannerId
        )
    }

//    fun getUsersProfiles(userIds: List<String>): Flow<List<UserProfile>> = callbackFlow {
//        val query = Collections.users.whereIn(FieldPath.documentId(), userIds)
//        val listener = query.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                Log.e("getUsersProfiles", error.toString())
//                trySend(emptyList())
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                val userProfiles =
//                    snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
//                trySend(userProfiles)
//            }
//        }
//        awaitClose { listener.remove() }
//    }

    fun getParticipantWithDetails(tripId: String): Flow<List<ParticipantDetailed>> =
        callbackFlow {
            val query = Collections.travelProposals.document(tripId)
            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("getParticipantWithDetails", error.toString())
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val trip = snapshot.toTravelProposalWithId()

                    val participants = trip.participants.map { participant ->
                        ParticipantDetailed(
                            user = userModel.getUserByUid(participant.id) ?: UNKNOWN_USER,
                            invitedGuests = participant.invitedGuests,
                            status = participant.status
                        )

                    }

                    Log.d("getParticipant", "participants: $participants")
                    trySend(participants)

                } else {
                    trySend(emptyList())
                }
            }
            awaitClose { listener }
        }

    fun applyForTrip(userId: String, trip: TravelProposal, guests: List<String>): Boolean {
        val existingParticipant = trip.participants.find { it.id == userId }

        if (existingParticipant != null) {
            // User already has a status for this trip
            return false
        }

        updateParticipantInList(trip, userId, ParticipantStatus.PENDING, guests)

        val tripUpdates = mapOf(
            "participants" to trip.participants
        )

        Collections.travelProposals.document(trip.id).update(tripUpdates)
            .addOnFailureListener { error ->
                Log.e("applyForTrip", "Failed to update trip: ${error.message}")
            }

        addNotification(
            trip.id,
            trip.title,
            NotificationType.NEW_APPLICATION,
            userId,
            userId,
            tripPlannerId = trip.tripPlannerId
        )

        return true
    }

    suspend fun addTravelProposal(
        travelProposal: TravelProposal, imageUris: List<Uri>, context: Context
    ): Result<String> {
        return try {
            // Generate ID if not provided
            val tripId = travelProposal.id.ifEmpty { UUID.randomUUID().toString() }
            val userId = travelProposal.tripPlannerId

            // Validate folder structure
            if (!imageStorageModel.validateTripFolderStructure(userId, tripId)) {
                return Result.failure(Exception("Invalid folder structure for userId: $userId, tripId: $tripId"))
            }

            Log.d(
                "TravelProposalModel",
                "Creating travel proposal with folder structure: $userId/$tripId"
            )

            // Upload images to Supabase with folder structure: userId/tripId/
            val imageUrls = if (imageUris.isNotEmpty()) {
                val uploadResult = imageStorageModel.uploadMultipleTripImages(
                    userId = userId, tripId = tripId, imageUris = imageUris, context = context
                )

                if (uploadResult.isFailure) {
                    return Result.failure(
                        uploadResult.exceptionOrNull() ?: Exception("Image upload failed")
                    )
                }

                uploadResult.getOrThrow()
            } else {
                emptyList()
            }

            // Update travel proposal with image URLs and ID
            val updatedProposal = travelProposal.copy(
                id = tripId, images = imageUrls
            )

            // Save to Firestore (using callback to match your pattern)
            Collections.travelProposals.document(tripId).set(updatedProposal)

            Log.d("TravelProposalModel", "Travel proposal saved successfully with ID: $tripId")
            Result.success(tripId)
        } catch (e: Exception) {
            Log.e("TravelProposalModel", "Failed to add travel proposal: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateTravelProposal(
        updatedProposal: TravelProposal,
        originalSavedImages: List<String>,
        newImageUris: List<Uri>,
        context: Context,
    ): Result<String> {
        return try {
            val tripId = updatedProposal.id
            val userId = updatedProposal.tripPlannerId

            val imagesToDelete = originalSavedImages.filter { originalImage ->
                !updatedProposal.images.contains(originalImage)
            }

            // if there are images to delete, remove them from Supabase storage
            if (imagesToDelete.isNotEmpty()) {
                Log.d("TravelProposalModel", "Deleting ${imagesToDelete.size} old images")
                imagesToDelete.forEach { imageUrl ->
                    val deleteResult = imageStorageModel.deleteTripImage(imageUrl)
                    if (deleteResult.isFailure) {
                        throw deleteResult.exceptionOrNull()
                            ?: Exception("Failed to delete image: $imageUrl")
                    }
                }
            }

            // Handle image updates
            val finalImageUrls = if (newImageUris.isNotEmpty()) {
                // if there are new images, upload them
                val uploadResult = imageStorageModel.uploadMultipleTripImages(
                    userId = userId, tripId = tripId, imageUris = newImageUris, context = context
                )
                if (uploadResult.isSuccess) {
                    val newImageUrls = uploadResult.getOrThrow()
                    // Combine existing images with new ones
                    updatedProposal.images + newImageUrls
                } else {
                    throw uploadResult.exceptionOrNull() ?: Exception("Failed to upload images")
                }

            } else {
                // Keep existing images if no new images to upload
                updatedProposal.images
            }

            // Update the travel proposal with final image URLs
            val finalProposal = updatedProposal.copy(
                images = finalImageUrls, tempImages = mutableListOf() // Clear temp images
            )

            // Update the complete document in Firestore
            Collections.travelProposals.document(tripId).set(finalProposal)

            Log.d("TravelProposalModel", "Travel proposal updated successfully with ID: $tripId")
            Result.success(tripId)
        } catch (e: Exception) {
            Log.e("TravelProposalModel", "Failed to update travel proposal: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteTravelProposal(travelProposalId: String, userId: String): Result<Unit> {
        return try {

            // Delete all reviews associated with the travel proposal(First from Supabase, then from Firestore)
            val reviewsQuery = Collections.getReviewCollection(travelProposalId)

            val reviewsSnapshot = reviewsQuery.get().await()

            reviewsSnapshot.documents.forEach { document ->
                val reviewId = document.id

                document.toObject(TravelReview::class.java)?.let {
                    imageStorageModel.deleteReviewImages(
                        userId, travelProposalId, reviewId
                    )
                }

                document.reference.delete()
            }

            // Delete images from Supabase storage first
            imageStorageModel.deleteTripImages(userId, travelProposalId)

            // Delete from Firestore
            Collections.travelProposals.document(travelProposalId).delete()

            Log.d("TravelProposalModel", "Travel proposal and images deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TravelProposalModel", "Failed to delete travel proposal: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteAllTravelProposal() {
        val query = Collections.travelProposals.get().await()

        query.documents.forEach { document ->
            deleteTravelProposal(
                travelProposalId = document.id,
                userId = document.getString("tripPlannerId") ?: ""
            )
        }
    }


    // ----- Helper functions ----- //
    //NOTIFICATION PART
    fun addNotification(
        tripId: String,
        title: String,
        type: NotificationType,
        notificationOwnerId: String,
        applicantId: String? = null,
        tripPlannerId: String? = null,
        reviewedUser: String? = null
    ) {
        val notification = Notification(
            tripId = tripId,
            title = title,
            type = type,
            timestamp = Timestamp.now(),
            read = emptyList(),
            notificationOwnerId = notificationOwnerId,
            applicantId = applicantId,
            tripPlannerId = tripPlannerId,
            reviewedUser = reviewedUser
        )

        Collections.notifications.add(notification)
            .addOnSuccessListener {
                Log.d("Notifications", "Notification added for trip: $title, type: $type")
            }
            .addOnFailureListener { error ->
                Log.e("Notifications", "Failed to add notification: ${error.message}")
            }
    }


    fun getNotifications(excludedNotificationTypes: List<NotificationType>): Flow<List<Notification>> =
        callbackFlow {
            val userId = userModel.loggedUser.value.uid
            Log.d("NotificationsExcluded2", "In: $excludedNotificationTypes, User ID: $userId")
            val listener = Collections.notifications
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Notifications", error.toString())
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        try {
                            val notifications = snapshot.documents.mapNotNull { document ->
                                Log.d("Notifications", "Exclude types: $excludedNotificationTypes")
                                val notification = document.toObject(Notification::class.java)
                                Log.d("CurrentUser", "User ID: $userId")
                                notification?.copy(id = document.id)


                            }.filter { notification ->
                                !excludedNotificationTypes.contains(notification.type)
                            }.filter { notification ->
                                // Filtra le notifiche per destinatario
                                when (notification.type) {
                                    NotificationType.NEW_PROPOSAL -> {
                                        notification.notificationOwnerId != userId
                                    }

                                    NotificationType.NEW_APPLICATION -> notification.tripPlannerId == userId
                                    NotificationType.PARTICIPANT_APPROVED, NotificationType.PARTICIPANT_REJECTED -> notification.applicantId == userId
                                    NotificationType.REVIEW_RECEIVED_FOR_PAST_TRIP -> {
                                        notification.tripPlannerId == userId && notification.reviewedUser != userId
                                    }

                                    NotificationType.USER_REVIEW_RECEIVED -> notification.reviewedUser == userId
                                    NotificationType.LAST_MINUTE -> notification.applicantId != userId
                                    NotificationType.LAST_MINUTE_AUTOMATIC -> notification.applicantId == userId
                                    NotificationType.CHECK_RECOMMENDED -> notification.applicantId == userId
                                    NotificationType.BADGE_UNLOCKED -> notification.notificationOwnerId == userId
                                    else -> false
                                }
                            }
                            trySend(notifications)
                        } catch (e: Exception) {
                            Log.e("Notifications", "Error parsing notification: ${e.message}")
                            trySend(emptyList())
                        }
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose { listener.remove() }
        }

    fun markNotificationAsRead(notificationId: String, userId: String) {
        Collections.notifications.document(notificationId)
            .update("read", FieldValue.arrayUnion(userId))
            .addOnFailureListener { error ->
                Log.e("Notifications", "Failed to mark notification as read: ${error.message}")
            }
    }

    //delete all the notifications for a tripId
    fun deleteNotificationsForTrip(tripId: String) {
        Collections.notifications.whereEqualTo("tripId", tripId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { document ->
                    document.reference.delete()
                        .addOnFailureListener { error ->
                            Log.e(
                                "Notifications",
                                "Failed to delete notification: ${error.message}"
                            )
                        }
                }
            }
            .addOnFailureListener { error ->
                Log.e("Notifications", "Failed to get notifications for trip: ${error.message}")
            }
    }

    private fun getApprovedParticipantsCount(trip: TravelProposal): Int {
        return trip.participants.count { it.status == ParticipantStatus.APPROVED }
    }

    private fun findParticipantIndex(trip: TravelProposal, userId: String): Int {
        return trip.participants.indexOfFirst { it.id == userId }
    }

    private fun updateParticipantInList(
        trip: TravelProposal,
        userId: String,
        newStatus: ParticipantStatus,
        guests: List<String> = emptyList()
    ): Boolean {
        val participantIndex = findParticipantIndex(trip, userId)

        if (participantIndex == -1) {
            // Add new participant if not found
            trip.participants.add(
                Participant(
                    id = userId, status = newStatus, invitedGuests = guests
                )
            )
        } else {

            trip.participants[participantIndex] =
                trip.participants[participantIndex].copy(status = newStatus)
        }

        return true
    }
}

// Iterate over the documents in the QuerySnapshot and convert them to TravelProposal objects
// extracting the document ID from the FirebaseDB and using it in TravelProposal ID
private fun QuerySnapshot.toTravelProposalsWithIds(): List<TravelProposal> {
    val travelProposalsWithIds = mutableListOf<TravelProposal>()
    for (document in this.documents) {
        try {
            val travelProposal = document.toObject(TravelProposal::class.java)
            travelProposal?.let { trip ->
                trip.id = document.id
                travelProposalsWithIds.add(trip)
            }
        } catch (e: Exception) {
            Log.e("TravelProposalModel", "Error processing document: ${document.id}, ${e.message}")
            continue
        }
    }
    return travelProposalsWithIds
}

private fun DocumentSnapshot.toTravelProposalWithId(): TravelProposal {
    var travelProposal = TravelProposal()
    try {
        val documentId = this.id
        travelProposal = this.toObject(TravelProposal::class.java)?.apply {
            id = documentId
        } ?: throw Exception("Failed to convert document to TravelProposal")

        travelProposal.id = this.id
    } catch (e: Exception) {
        Log.e(
            "TravelProposalModel",
            "Error processing travel proposal document: ${this.id}, ${e.message}"
        )
    }

    return travelProposal
}