package com.example.final_assignment_even_g28.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.ExperienceComposition
import com.example.final_assignment_even_g28.data_class.Filters
import com.example.final_assignment_even_g28.data_class.ItineraryStop
import com.example.final_assignment_even_g28.data_class.Notification
import com.example.final_assignment_even_g28.data_class.ParticipantDetailed
import com.example.final_assignment_even_g28.data_class.ParticipantStatus
import com.example.final_assignment_even_g28.data_class.Price
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.data_class.TravelReview
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.model.TravelProposalModel
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.validation.FilterError
import com.example.final_assignment_even_g28.shared.validation.FilterValidator
import com.example.final_assignment_even_g28.shared.validation.ReviewError
import com.example.final_assignment_even_g28.shared.validation.ReviewValidator
import com.example.final_assignment_even_g28.shared.validation.TravelProposalFirstScreenError
import com.example.final_assignment_even_g28.shared.validation.TravelProposalSecondScreenError
import com.example.final_assignment_even_g28.shared.validation.TravelProposalValidator
import com.example.final_assignment_even_g28.utils.UNKNOWN_USER
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class TravelProposalViewModel(
    private val tripModel: TravelProposalModel,
    private val userModel: UserProfileModel
) : ViewModel() {
    private val _travelProposal = MutableStateFlow<TravelProposal>(TravelProposal())
    val travelProposal: StateFlow<TravelProposal>
        get() = _travelProposal

    var tempTravelProposal by mutableStateOf(TravelProposal())

    var isTravelListLoaded by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
        private set

    var firstScreenValidationError by mutableStateOf(TravelProposalFirstScreenError())
    var secondScreenValidationError by mutableStateOf(TravelProposalSecondScreenError())


    var isExpanded by mutableStateOf(false)
        private set

    var groupSizeOptions = (1..15).toList()


    private val currentUser = userModel.loggedUser

    var filterErrors by mutableStateOf(FilterError())
        private set

    var tempReview by mutableStateOf(TravelReview())

    var reviewErrors by mutableStateOf(ReviewError())

    var isFilterBarExtended = mutableStateOf(false)
        private set
    var filters by mutableStateOf(Filters())


    private val _allTravelProposals = MutableStateFlow<List<TravelProposal>>(emptyList())
    val allTravelProposals: StateFlow<List<TravelProposal>>
        get() = _allTravelProposals


    val myTravelProposals: Flow<List<TravelProposal>> =
        tripModel.getMyTravelProposals(currentUser.value.uid)

    //    private val _pastTravelProposals = MutableStateFlow<List<TravelProposal>>(emptyList())
    val pastTravelProposals: Flow<List<TravelProposal>> =
        tripModel.getPastTravelProposals(currentUser.value.uid)


    private val _currentReviews = MutableStateFlow<List<TravelReview>>(emptyList())
    val currentReviews: StateFlow<List<TravelReview>>
        get() = _currentReviews

    private val _currentParticipants = MutableStateFlow<List<ParticipantDetailed>>(emptyList())
    val currentParticipants: StateFlow<List<ParticipantDetailed>>
        get() = _currentParticipants

    private val _currentTripPlanner = MutableStateFlow<UserProfile>(UNKNOWN_USER)
    val currentTripPlanner: StateFlow<UserProfile>
        get() = _currentTripPlanner

    //list of notification
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>>
        get() = _notifications

    private val _newTravelProposalNotification = MutableStateFlow<Notification?>(null)
    val newTravelProposalNotification: StateFlow<Notification?>
        get() = _newTravelProposalNotification


    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int>
        get() = _unreadCount

    private val existingNotificationIds = mutableSetOf<String>()

    private val sentLastMinuteNotifications = mutableSetOf<String>()


    init {
        loadAllTravelProposals()
        getNotification()
        pollingNotifications()
    }

    private fun loadAllTravelProposals() {
        viewModelScope.launch {
            tripModel.getFilteredTravelProposals(filters).collect { proposals ->
                Log.d(
                    "TravelProposalViewModel", "Filtered Proposals: ${proposals.map { it.id }}"
                )
                _allTravelProposals.value = proposals
            }
        }


//        viewModelScope.launch {
//            tripModel.getMyTravelProposals(currentUser.value.uid).collect { myProposals ->
//                Log.d("TravelProposalViewModel", "Owned Proposals: ${myProposals.map { it.id }}")
//                _myTravelProposals.value = myProposals
//            }
//        }

//        viewModelScope.launch {
//            tripModel.getPastTravelProposals(currentUser.value.uid).collect { pastProposals ->
//                Log.d("TravelProposalViewModel", "Past Proposals: ${pastProposals.map { it.id }}")
//                _pastTravelProposals.value = pastProposals
//            }
//        }
    }

    private fun getNotification() {
        viewModelScope.launch {
            val toggleToNotificationTypes = mapOf(
                "lastMinute" to listOf("lastMinute"),
                "newApplication" to listOf("newApplication"),
                "reviewReceivedForPastTrip" to listOf("reviewReceivedForPastTrip"),
                "checkRecommended" to listOf("checkRecommended"),
                "statusUpdateOnPendingApplication" to listOf("participantApproved", "participantRejected")
            )

            val excludedNotificationTypes = userModel.loggedUser.value.notificationSettings
                .filter { !it.enabled }
                .flatMap { toggle -> toggleToNotificationTypes[toggle.type] ?: emptyList() }
            Log.d("NotificationsExcluded", "Out: $excludedNotificationTypes")
            tripModel.getNotifications(currentUser.value.uid, excludedNotificationTypes).collect { notifications ->
                // NOTIFICATION BELL
                _notifications.value = notifications

                val unreadCount = notifications.count { !it.isRead(currentUser.value.uid) }
                _unreadCount.value = unreadCount

                //SNACKBAR
                notifications.forEach { notification ->
                    if (notification.isRecent() &&
                        !notification.isRead(currentUser.value.uid) &&
                        !existingNotificationIds.contains(notification.id)
                    ) {

                        existingNotificationIds.add(notification.id)
                        _newTravelProposalNotification.value = notification
                        Log.d("New Notification", "New notification: ${notification.title}")
                        Log.d("New Notification", "New notification: ${_newTravelProposalNotification.value}")
                    }
                }
            }
        }
    }

    private fun pollingNotifications() {
        viewModelScope.launch {
            while (true) {

                delay(30 * 60 * 1000)
                //delay(20 * 1000)

                Log.d("prova ", "Checking for last minute proposals")
                checkForLastMinuteProposals()
                checkForRecommendedProposals()
            }

        }

    }

    fun addImageFromGallery(uri: Uri) {
        tempTravelProposal = tempTravelProposal.copy(
            tempImages = (tempTravelProposal.tempImages + uri.toString()).toMutableList()
        )
    }

    fun removeImageFromGallery(index: Int) {
        if (index < 0) {
            Log.w("TravelProposalViewModel", "Index cannot be negative: $index")
            return
        }
        val imagesSize = tempTravelProposal.images.size
        val tempImagesSize = tempTravelProposal.tempImages.size
        val imageListSize = imagesSize + tempImagesSize

        if (index >= imageListSize) {
            Log.w("TravelProposalViewModel", "Index out of bounds for images list: $index")
            return
        }

        if (index < imagesSize) {
            // Removing from existing images - create new list without this image
            val updatedImages = tempTravelProposal.images.toMutableList()
            updatedImages.removeAt(index)
            tempTravelProposal = tempTravelProposal.copy(images = updatedImages)
        } else { //index point to temp images
            // Removing from temp images - create new list without this image
            val updatedTempImages = tempTravelProposal.tempImages.toMutableList()
            updatedTempImages.removeAt(index - imagesSize)
            tempTravelProposal = tempTravelProposal.copy(tempImages = updatedTempImages)
        }
    }

    private fun getPendingImageUris(): List<Uri> {
        return tempTravelProposal.tempImages.mapNotNull {
            try {
                it.toUri()
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getAllActivityTags(): List<Pair<ActivityTag, Boolean>> {
        return ActivityTag.entries.map {
            it to tempTravelProposal.activities.contains(it)
        }
    }

    fun updateActivityTags(activities: List<Pair<ActivityTag, Boolean>>) {
        tempTravelProposal =
            tempTravelProposal.copy(activities = activities.filter { it.second }.map { it.first })
    }

    fun updateAdventureComposition(
        adventure: String,
    ) {
        // Transform the Integer values entered by the user into float in the range [0,1]
        var adventure = adventure.toIntOrNull()?.coerceIn(0, 100) ?: 0

        tempTravelProposal = tempTravelProposal.copy(
            experienceComposition = ExperienceComposition(
                adventure,
                tempTravelProposal.experienceComposition.culture,
                tempTravelProposal.experienceComposition.relax,
                tempTravelProposal.experienceComposition.party
            )
        )
    }

    fun updateRelaxComposition(
        relax: String,
    ) {
        // Transform the Integer values entered by the user into float in the range [0,1]
        val relax = relax.toIntOrNull()?.coerceIn(0, 100) ?: 0

        tempTravelProposal = tempTravelProposal.copy(
            experienceComposition = ExperienceComposition(
                tempTravelProposal.experienceComposition.adventure,
                tempTravelProposal.experienceComposition.culture,
                relax,
                tempTravelProposal.experienceComposition.party
            )
        )
    }

    fun updateCultureComposition(
        culture: String,
    ) {
        // Transform the Integer values entered by the user into float in the range [0,1]
        val culture = culture.toIntOrNull()?.coerceIn(0, 100) ?: 0

        tempTravelProposal = tempTravelProposal.copy(
            experienceComposition = ExperienceComposition(
                tempTravelProposal.experienceComposition.adventure,
                culture,
                tempTravelProposal.experienceComposition.relax,
                tempTravelProposal.experienceComposition.party
            )
        )
    }

    fun updatePartyComposition(
        party: String,
    ) {
        // Transform the Integer values entered by the user into float in the range [0,1]
        val party = party.toIntOrNull()?.coerceIn(0, 100) ?: 0

        tempTravelProposal = tempTravelProposal.copy(
            experienceComposition = ExperienceComposition(
                tempTravelProposal.experienceComposition.adventure,
                tempTravelProposal.experienceComposition.culture,
                tempTravelProposal.experienceComposition.relax,
                party,
            )
        )
    }

    fun validateFirstScreenFields(): Boolean {
        val (errors, isValid) = TravelProposalValidator.validateFirstScreen(
            title = tempTravelProposal.title,
            maxParticipant = tempTravelProposal.maxParticipant,
            price = tempTravelProposal.price,
            tripStartDate = tempTravelProposal.tripStartDate,
            tripEndDate = tempTravelProposal.tripEndDate,
            itinerary = tempTravelProposal.itinerary,
            tripDescription = tempTravelProposal.description,
            numTripImages = tempTravelProposal.images.size + tempTravelProposal.tempImages.size,
        )

        firstScreenValidationError = errors

        return isValid
    }

    fun validateSecondScreenFields(): Boolean {
        val (errors, isValid) = TravelProposalValidator.validateSecondScreen(
            title = tempTravelProposal.title,
            activities = tempTravelProposal.activities,
        )

        secondScreenValidationError = errors

        return isValid
    }

    fun clickPlanNewOwnTrip(userId: String) {
        Log.d(
            "TravelProposalViewModel",
            "creating trip with ID: ${userId}"
        )
        tempTravelProposal = TravelProposal(
            tripPlannerId = userId,
            // Start with at least one stop already present
            itinerary = listOf(
                ItineraryStop(
                    date = Timestamp(Date(0L)), title = "", description = "", mandatory = false
                )
            )
        )
    }

    fun clickEditOwnTrip() {
        tempTravelProposal = _travelProposal.value.copy()
        isEditing = true
    }

    fun clickCloneTrip(userId: String) {
        Log.d(
            "TravelProposalViewModel",
            "cloning trip with ID: ${userId}"
        )
        val originalTrip = _travelProposal.value
        tempTravelProposal = originalTrip.copy(
            tripPlannerId = userId,
            participants = mutableListOf(),
            images = emptyList(),
            id = ""
        )
        isEditing = false
    }

    fun exitEditingTravelProposal() {
        tempTravelProposal = TravelProposal()
        isEditing = false
    }

    fun updateTitle(newTitle: String) {
        Log.d("TravelProposalViewModel", "userID ${currentUser.value.uid}")
        tempTravelProposal = tempTravelProposal.copy(title = newTitle)
    }

    fun addTravelProposal(context: Context) {
        viewModelScope.launch {
            try {
                val pendingImageUris = getPendingImageUris()

                Log.d(
                    "TravelProposalViewModel",
                    "Saving travel proposal with ${pendingImageUris.size} images"
                )
                Log.d(
                    "TravelProposalViewModel",
                    "Folder structure will be: ${tempTravelProposal.tripPlannerId}/${tempTravelProposal.id}"
                )

                val tripId = tempTravelProposal.id.ifEmpty { UUID.randomUUID().toString() }
                val title = tempTravelProposal.title
                val tripStartDate = tempTravelProposal.tripStartDate.toDate().time
                val result = tripModel.addTravelProposal(
                    travelProposal = tempTravelProposal.copy(
                        id = tripId
                    ),
                    imageUris = pendingImageUris,
                    context = context
                )

                if (result.isSuccess) {
                    Log.d(
                        "TravelProposalViewModel",
                        "Travel proposal saved successfully with ID: ${result.getOrThrow()}"
                    )
                    val now = System.currentTimeMillis()
                    val isLastMinute = (tripStartDate - now) < (24 * 60 * 60 * 1000)
                    if (isLastMinute) {
                        tripModel.addNotification(
                            tripId,
                            title,
                            "lastMinute",
                            currentUser.value.uid,
                            applicantId = currentUser.value.uid
                        )
                    } else {
                        tripModel.addNotification(
                            tripId,
                            title,
                            "newProposal",
                            currentUser.value.uid
                        )
                    }
                    tempTravelProposal.tempImages = listOf()
                    // Navigate back or show success message
                } else {
                    Log.e(
                        "TravelProposalViewModel",
                        "Failed to save: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("TravelProposalViewModel", "Save failed: ${e.message}")
            }
        }
    }

    fun updateTravelProposal(context: Context, replaceExisting: Boolean = false) {
        viewModelScope.launch {
            try {
                val pendingImageUris = getPendingImageUris()

                val originalStartDate = travelProposal.value.tripStartDate.toDate().time

                val result = tripModel.updateTravelProposal(
                    updatedProposal = tempTravelProposal,
                    originalSavedImages = travelProposal.value.images,
                    newImageUris = pendingImageUris,
                    context = context,
                )

                if (result.isSuccess) {
                    val now = System.currentTimeMillis()
                    val tripStart = tempTravelProposal.tripStartDate.toDate().time
                    val isLastMinute = (tripStart - now) < (24 * 60 * 60 * 1000)
                    Log.d(
                        "ifresult",
                        "originalStartDate: $originalStartDate tripStart: $tripStart now: $now"
                    )
                    if (isLastMinute && originalStartDate != tripStart) {
                        tripModel.addNotification(
                            tempTravelProposal.id,
                            tempTravelProposal.title,
                            "lastMinute",
                            currentUser.value.uid,
                            applicantId = currentUser.value.uid
                        )
                    }
                    Log.d(
                        "TravelProposalViewModel",
                        "Travel proposal updated successfully with ID: ${result.getOrThrow()}"
                    )
                    tempTravelProposal.tempImages = listOf()
                } else {
                    val uploadError =
                        result.exceptionOrNull()?.message ?: "Failed to updated travel proposal"
                    Log.e(
                        "TravelProposalViewModel", "Update failed: ${uploadError}}"
                    )
                }
            } catch (e: Exception) {
                Log.e("TravelProposalViewModel", "Exception during update: ${e.message}")
            }
        }
    }

    fun deleteTravelProposal() {
        viewModelScope.launch {
            try {
                val currentTrip = _travelProposal.value
                val result =
                    tripModel.deleteTravelProposal(currentTrip.id, currentTrip.tripPlannerId)

                if (result.isSuccess) {
                    Log.d("TravelProposalViewModel", "Travel proposal deleted successfully")
                } else {
                    Log.e(
                        "TravelProposalViewModel",
                        "Delete failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("TravelProposalViewModel", "Delete failed: ${e.message}")
            }
        }
    }


    fun updateGroupSize(
        newGroupSize: Int,
    ) {
        tempTravelProposal = tempTravelProposal.copy(maxParticipant = newGroupSize)
    }

    fun toggleExpanded() {
        isExpanded = !isExpanded
    }

    var minValue by mutableIntStateOf(0)
    var maxValue by mutableIntStateOf(0)

    var selectedStartDate by mutableStateOf<Long?>(null)
    var selectedEndDate by mutableStateOf<Long?>(null)
    var selectedStopDate by mutableStateOf<Long?>(null)
    var showStartDate by mutableStateOf(false)
    var showEndDate by mutableStateOf(false)
    var showStopDate by mutableStateOf(false)

    fun startDateSelected(date: Long?) {
        selectedStartDate = date ?: 0L
        tempTravelProposal = tempTravelProposal.copy(tripStartDate = Timestamp(Date(date ?: 0L)))
        showStartDate = false
    }

    fun endDateSelected(date: Long?) {
        selectedEndDate = date ?: 0L
        tempTravelProposal = tempTravelProposal.copy(tripEndDate = Timestamp(Date(date ?: 0L)))
        showEndDate = false
    }

    fun toggleStartDate() {
        showStartDate = !showStartDate
    }

    fun toggleEndDate() {
        showEndDate = !showEndDate
    }

    fun updatePriceRange(
        newPriceRange: Price,
    ) {
        minValue = newPriceRange.min
        maxValue = newPriceRange.max
        tempTravelProposal = tempTravelProposal.copy(price = newPriceRange)
    }

    fun updateDescription(
        newDescription: String,
    ) {
        tempTravelProposal = tempTravelProposal.copy(description = newDescription)
    }

    fun updateStopTitle(
        newTripTitle: String,
        index: Int,
    ) {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary.mapIndexed { i, stop ->
                if (i == index) {
                    stop.copy(title = newTripTitle)
                } else {
                    stop
                }
            })
    }

    fun updateStopDate(
        newTripDate: Long?,
        index: Int,
    ) {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary.mapIndexed { i, stop ->
                if (i == index) {
                    stop.copy(date = Timestamp(Date(newTripDate ?: 0L)))
                } else {
                    stop
                }
            })
    }

    fun toggleStopDate() {
        showStopDate = !showStopDate
    }

    fun updateStopMandatory(index: Int, isMandatory: Boolean) {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary.mapIndexed { i, stop ->
                if (i == index) {
                    stop.copy(mandatory = !stop.mandatory)
                } else {
                    stop
                }
            })
    }

    fun updateStopDescription(index: Int, description: String) {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary.mapIndexed { i, stop ->
                if (i == index) {
                    stop.copy(description = description)
                } else {
                    stop
                }
            })
    }

    fun addStop() {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary + ItineraryStop(
                date = Timestamp(Date(0L)), title = "", description = "", mandatory = false
            )
        )
    }

    fun deleteStop(index: Int) {
        tempTravelProposal = tempTravelProposal.copy(
            itinerary = tempTravelProposal.itinerary.filterIndexed { i, _ -> i != index })
    }

    fun clearReview() {
        tempReview = TravelReview()
        reviewErrors = ReviewError()
    }

    fun updateReviewTitle(title: String) {
        tempReview = tempReview.copy(title = title)
    }

    fun updateReviewDescription(comment: String) {
        tempReview = tempReview.copy(description = comment)
    }

    fun addReviewImageFromGallery(uri: Uri) {
        tempReview = tempReview.copy(tempImages = (tempReview.tempImages + uri.toString()))
    }

    fun removeReviewImageFromGallery(index: Int) {
        if (index < 0 || index >= tempReview.tempImages.size) {
            Log.w("TravelProposalViewModel", "Index out of bounds for review images: $index")
            return
        }

        tempReview =
            tempReview.copy(tempImages = tempReview.tempImages.filterIndexed { i, _ -> i != index })
    }

    fun updateReviewRating(rating: Float) {
        tempReview = tempReview.copy(rating = rating)
    }

    fun submitReview(context: Context): Boolean {
        reviewErrors = ReviewValidator.validate(tempReview)

        if (reviewErrors.hasError) {
            return false
        }

        viewModelScope.launch {
            try {
                // Prepare review with current travel proposal ID
                val reviewToSubmit = tempReview.copy(
                    reviewerId = currentUser.value.uid
                )
                val tripId = _travelProposal.value.id
                val plannerId = _travelProposal.value.tripPlannerId
                val imageUris = tempReview.tempImages.map { it.toUri() }
                val tripTitle: String = _travelProposal.value.title
                // Submit review
                val result = tripModel.submitReview(
                    review = reviewToSubmit,
                    plannerId = plannerId,
                    tripId = tripId,
                    tripTitle = tripTitle,
                    imageUris = imageUris,
                    context = context
                )

                if (result.isSuccess) {
                    Log.d("TravelProposalViewModel", "Review submitted successfully")
                    clearReview()
                } else {
                    Log.e(
                        "TravelProposalViewModel",
                        "Failed to submit review: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("TravelProposalViewModel", "Error submitting review", e)
            }
        }

        return true
    }

    fun toggleFilterBar() {
        isFilterBarExtended.value = !isFilterBarExtended.value
    }


    fun showFromValue(from: Timestamp?): String {
        if (from != null) return from.toDateFormat()
        return "Select Start"
    }


    fun showToValue(to: Timestamp?): String {
        if (to != null) return to.toDateFormat()
        return "Select End"
    }

    fun updateWhere(where: String) {
        filters = filters.copy(title = where)
    }

    fun resetWhere() {
        if (filters.title.isEmpty()) filters = filters.copy(title = "Anywhere")
    }

    fun toggleActivity(activity: Pair<ActivityTag, Boolean>) {
        val activities = filters.activities
        val updatedActivities = if (activity.first in activities) activities - activity.first
        else activities + activity.first
        filters = filters.copy(activities = updatedActivities)
    }

    fun updateFilterGroupSize(groupSize: Int?) {
        filters = filters.copy(groupSize = groupSize)
    }

    fun updateFrom(from: Long?) {
        val timestamp = from?.let { Timestamp(Date(it)) }
        filters = filters.copy(startDate = timestamp)
        filterErrors = FilterValidator.validateFilter(
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            tripStartDate = filters.startDate,
            tripEndDate = filters.endDate,
        )
    }

    fun updateTo(to: Long?) {
        val timestamp = to?.let { Timestamp(Date(it)) }
        filters = filters.copy(endDate = timestamp)
        filterErrors = FilterValidator.validateFilter(
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            tripStartDate = filters.startDate,
            tripEndDate = filters.endDate,
        )
    }

    fun updateMinPrice(minPrice: Int) {
        filters = filters.copy(minPrice = minPrice)
        filterErrors = FilterValidator.validateFilter(
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            tripStartDate = filters.startDate,
            tripEndDate = filters.endDate,
        )
    }

    fun updateMaxPrice(maxPrice: Int) {
        filters = filters.copy(maxPrice = maxPrice)
        filterErrors = FilterValidator.validateFilter(
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            tripStartDate = filters.startDate,
            tripEndDate = filters.endDate,
        )
    }

    fun applyFilters() {
        val validationResults = FilterValidator.validateFilter(
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            tripStartDate = filters.startDate,
            tripEndDate = filters.endDate,
        )
        filterErrors = validationResults
        if (validationResults.hasError) {
            Log.w("TravelProposalViewModel", "Filter validation failed: $filterErrors")
            return
        }
        viewModelScope.launch {
            tripModel.getFilteredTravelProposals(filters).collect { proposals ->
                _allTravelProposals.value = proposals
            }
        }

        toggleFilterBar()
    }
    fun getFilteredNotifications(): List<Notification> {
        val toggleToNotificationTypes = mapOf(
            "lastMinute" to listOf("lastMinute"),
            "newApplication" to listOf("newApplication"),
            "reviewReceivedForPastTrip" to listOf("reviewReceivedForPastTrip"),
            "checkRecommended" to listOf("checkRecommended"),
            "statusUpdateOnPendingApplication" to listOf("participantApproved", "participantRejected") // Mapping per statusUpdate
        )

        val activeNotificationTypes = userModel.loggedUser.value.notificationSettings
            .filter { it.enabled }
            .flatMap { toggle -> toggleToNotificationTypes[toggle.type] ?: emptyList() }

        return _notifications.value.filter { notification ->
            activeNotificationTypes.contains(notification.type)
        }
    }

    private fun todayDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun tomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    fun clickTripInfo(travelId: String, isPast: Boolean = false) {
        viewModelScope.launch {
            Log.d("TravelProposalViewModel", "Fetching travel proposal with ID: $travelId")
            tripModel.getTravelProposalById(travelId).collect { trip ->
                if (trip == null) {
                    Log.w("TravelProposalViewModel", "No trip found with ID: $travelId")
                    return@collect
                }
                _travelProposal.value = trip
                val tripUser = userModel.getUserByUid(trip.tripPlannerId)
                Log.d(
                    "TravelProposalViewModel",
                    "Trip planner ID: ${tripUser}"
                )
                if (tripUser == null) {
                    Log.w(
                        "TravelProposalViewModel",
                        "No user found for trip planner ID: ${trip.tripPlannerId}"
                    )
                    _currentTripPlanner.value = UNKNOWN_USER
                } else {
                    _currentTripPlanner.value = tripUser
                    Log.d("TravelProposalViewModel", "Trip planner found: ${tripUser.name}")
                }
            }
        }

        viewModelScope.launch {
            tripModel.getParticipantWithDetails(travelId).collect { participants ->
                _currentParticipants.value = participants
            }
        }


        if (isPast) {
            viewModelScope.launch {
                tripModel.getReviewsForProposal(travelId).collect { reviews ->
                    Log.d(
                        "TravelProposalViewModel",
                        "Loaded ${reviews.size} reviews for trip ID: $travelId"
                    )
                    _currentReviews.value = reviews
                }
            }
        }
    }

    fun isMyTrip(): Boolean {
        return _travelProposal.value.tripPlannerId == currentUser.value.uid
    }


    fun showDatesInList(dateStart: Date, dateEnd: Date): String {
        val formatterProposal = SimpleDateFormat("dd MMM yy", Locale.getDefault())
        return formatterProposal.format(dateStart) + " - " + formatterProposal.format(dateEnd)
    }

    fun showDatesInTripInfo(dateStart: Date, dateEnd: Date): String {
        val formatterProposal = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatterProposal.format(dateStart) + " - " + formatterProposal.format(dateEnd)
    }

    fun getNumApprovedParticipants(trip: TravelProposal): Int {
        return trip.participants.filter { it.status == ParticipantStatus.APPROVED }
            .sumOf { 1 + it.invitedGuests.size }
    }


    fun applyToTrip(guests: List<String>) {
        viewModelScope.launch {
            tripModel.applyForTrip(currentUser.value.uid, _travelProposal.value, guests)
        }
    }

    fun approveParticipant(user: UserProfile, trip: TravelProposal): Boolean {
        val result = tripModel.approveParticipant(userId = user.uid, trip = trip)
        return result
    }

    fun rejectParticipant(user: UserProfile, trip: TravelProposal) {
        tripModel.rejectParticipant(userId = user.uid, trip = trip)
    }

    fun isUserParticipant(userId: String = currentUser.value.uid): Boolean {
        return _travelProposal.value.participants.any { it.id == userId }
    }

    fun getUserParticipantStatus(userId: String = currentUser.value.uid): ParticipantStatus? {
        return _travelProposal.value.participants.find { it.id == userId }?.status
    }


    fun markNotificationAsRead(notificationId: String) {
        tripModel.markNotificationAsRead(notificationId, currentUser.value.uid)
    }

    //dynamic message based on the type
    fun getNotificationMessage(type: String, tripTitle: String, snackBar: Boolean): String {
        if (snackBar) {
            return when (type) {
                "newProposal" -> "New travel proposal added: $tripTitle"
                "newApplication" -> "New application received for: $tripTitle"
                "participantApproved" -> "You have been approved for this trip: $tripTitle"
                "participantRejected" -> "Your application for the trip has been rejected: $tripTitle"
                "reviewReceivedForPastTrip" -> "New review received for trip: $tripTitle"
                "lastMinute" -> "Last minute proposal for trip: $tripTitle"
                "checkRecommended" -> "Recommended trip based on your preferences for trip: $tripTitle"
                "lastMinuteAutomatic" -> "Last minute proposal for trip: $tripTitle (automatic notification)"
                "userReviewReceived" -> "A user has left a review for you"
                else -> "Notification for trip: $tripTitle"
            }
        } else {
            return when (type) {
                "newProposal" -> "$tripTitle: New travel proposal added"
                "newApplication" -> "$tripTitle: New application received"
                "participantApproved" -> "$tripTitle: You have been approved for this trip "
                "participantRejected" -> "$tripTitle: Your application for the trip has been rejected"
                "reviewReceivedForPastTrip" -> "$tripTitle: New review received for this trip"
                "lastMinute" -> "$tripTitle: Last minute proposal for this trip"
                "lastMinuteAutomatic" -> "$tripTitle: Last minute proposal for this trip (automatic notification)"
                "checkRecommended" -> "$tripTitle: Recommended trip based on your preferences"
                "userReviewReceived" -> "A user has left a review for you"
                else -> "$tripTitle: Notification for trip "
            }
        }
    }

    fun getCurrentUserUId(): String {
        return currentUser.value.uid
    }

    fun getSortedNotifications(): List<Notification> {
        return _notifications.value.sortedWith(compareBy<Notification> { notification ->
            val isRead = notification.isRead(currentUser.value.uid)
            val isRecent = notification.isRecent()

            when {
                isRecent && !isRead -> 0
                !isRecent && !isRead -> 1
                else -> 2
            }
        }.thenByDescending { it.timestamp.toDate().time })
    }


    private fun checkForLastMinuteProposals() {
        Log.d("prova", "Starting Function")
        val timeNow = System.currentTimeMillis()
        val proposals = _allTravelProposals.value

        proposals.forEach { proposal ->
            Log.d(
                "prova",
                "Before if condition: ${proposal.tripPlannerId} (${currentUser.value.uid})"
            )
            if (proposal.tripPlannerId == currentUser.value.uid)
                return@forEach
            Log.d("prova", "After If")

            if (proposal.participants.any { it.id == currentUser.value.uid })
                return@forEach

            val hasExistingLastMinuteNotification = _notifications.value.any { notification ->
                notification.tripId == proposal.id &&
                        notification.type == "lastMinute" &&
                        notification.applicantId == currentUser.value.uid


            }


            val startDateConversion = proposal.tripStartDate.toDate().time
            val isLastMinute = (startDateConversion - timeNow) <= 24 * 60 * 60 * 1000

            Log.d("controllo", "Proposal: ${sentLastMinuteNotifications} = (${proposal.id})")
            if (isLastMinute && !hasExistingLastMinuteNotification) {
                tripModel.addNotification(
                    tripId = proposal.id,
                    title = proposal.title,
                    type = "lastMinuteAutomatic",
                    notificationOwnerId = "Automatic",
                    applicantId = currentUser.value.uid,
                    tripPlannerId = proposal.tripPlannerId,
                )
                Log.d("prova", "Checking for last minute proposals")
                Log.d(
                    "prova",
                    "Last minute notification sent for trip: ${proposal.title} (${proposal.id})"
                )
            }
        }
    }

    private fun checkForRecommendedProposals() {
        val proposals = _allTravelProposals.value

        proposals.forEach { proposal ->
            Log.d(
                "prova2",
                "Before if condition: ${proposal.tripPlannerId} (${currentUser.value.uid})"
            )
            if (proposal.tripPlannerId == currentUser.value.uid)
                return@forEach
            Log.d("prova2", "After If")

            if (proposal.participants.any { it.id == currentUser.value.uid })
                return@forEach

            val hasExistingRecommendedNotification = _notifications.value.any { notification ->
                notification.tripId == proposal.id &&
                        notification.type == "checkRecommended" &&
                        notification.applicantId == currentUser.value.uid
            }

            Log.d(
                "prima del controllo importante",
                "Proposal: ${proposal.title} --> (${currentUser.value.mostDesiredDestination})"
            )
            if (!proposal.title.contains(
                    currentUser.value.mostDesiredDestination,
                    ignoreCase = true
                )
            )
                return@forEach
            Log.d(
                "dopo del controllo importante",
                "Proposal: ${proposal.title} --> (${currentUser.value.mostDesiredDestination})"
            )

            if (!hasExistingRecommendedNotification) {
                tripModel.addNotification(
                    tripId = proposal.id,
                    title = proposal.title,
                    type = "checkRecommended",
                    notificationOwnerId = "Automatic",
                    applicantId = currentUser.value.uid,
                    tripPlannerId = proposal.tripPlannerId,
                )
                Log.d(
                    "prova",
                    "Recommended notification sent for trip: ${proposal.title} (${proposal.id})"
                )
            }
        }
    }


    fun deleteNotification() {
        viewModelScope.launch {
            try {
                tripModel.deleteNotificationsForTrip(_travelProposal.value.id)
                Log.d("TravelProposalViewModel", "Notification deleted successfully")
            } catch (e: Exception) {
                Log.e("TravelProposalViewModel", "Error deleting notification: ${e.message}")
            }
        }
    }

    fun handleNotificationNavigation(notification: Notification, navActions: Navigation) {
        when (notification.type) {
            "newApplication" -> {
                navActions.navigateToTripInfo(
                    tripId = notification.tripId,
                    fromMyTripTab = true,
                    showParticipants = true
                )
            }

            "reviewReceivedForPastTrip" -> {
                navActions.navigateToPastTravelProposalInfo(
                    tripId = notification.tripId,
                    fromMyTripTab = true,
                    showReviewsTab = true
                )
            }
            "userReviewReceived" -> {
                navActions.navigateToUserReview()
            }
            else -> {
                navActions.navigateToTripInfo(notification.tripId, false)
            }
        }
    }

    //-------------🚨EMERGENCY ONLY🚨-------------//
    fun deleteAllProposals() {
        viewModelScope.launch {
            tripModel.deleteAllTravelProposal()
        }
    }
    //-------------🚨EMERGENCY ONLY🚨-------------//
}



