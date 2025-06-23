//package com.example.final_assignment_even_g28.viewmodel
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material.icons.filled.AirplanemodeActive
//import androidx.compose.material.icons.filled.House
//import androidx.compose.material.icons.filled.Train
//import androidx.compose.material.icons.filled.Tram
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.final_assignment_even_g28.data.Collections
//import com.example.final_assignment_even_g28.data_class.Badge
//import com.example.final_assignment_even_g28.data_class.BadgeType
//import com.example.final_assignment_even_g28.data_class.Notification
//import com.example.final_assignment_even_g28.data_class.NotificationPreferenceType
//import com.example.final_assignment_even_g28.data_class.NotificationType
//import com.example.final_assignment_even_g28.data_class.TravelProposal
//import com.example.final_assignment_even_g28.data_class.UserProfile
//import com.example.final_assignment_even_g28.model.TravelProposalModel
//import com.example.final_assignment_even_g28.model.UserProfileModel
//import com.example.final_assignment_even_g28.shared.EditableFieldDefinition
//import com.example.final_assignment_even_g28.shared.InfoFieldDefinition
//import com.example.final_assignment_even_g28.shared.validation.UserProfileError
//import com.example.final_assignment_even_g28.shared.validation.UserProfileValidator
//import com.example.final_assignment_even_g28.shared.validation.asList
//import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
//import com.example.final_assignment_even_g28.utils.UNKNOWN_USER
//import com.example.final_assignment_even_g28.utils.toDateFormat
//import com.google.firebase.Timestamp
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.launch
//import kotlin.collections.addAll
//import kotlin.text.contains
//
//class NotificationViewModel(
//    private val tripModel: TravelProposalModel,
//    private val userModel: UserProfileModel
//) : ViewModel() {
//
//    private val _allTravelProposals = MutableStateFlow<List<TravelProposal>>(emptyList())
//
//    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
//    val notifications: StateFlow<List<Notification>>
//        get() = _notifications
//
//    private val _snackbarNotification = MutableStateFlow<Notification>(Notification())
//    val snackbarNotification: StateFlow<Notification>
//        get() = _snackbarNotification
//
//    private val _notificationEvents = MutableSharedFlow<Notification>()
//    val notificationEvents: SharedFlow<Notification> = _notificationEvents.asSharedFlow()
//
//    private val _unreadNotificationCount = MutableStateFlow(0)
//    val unreadNotificationCount: StateFlow<Int>
//        get() = _unreadNotificationCount
//
//    private val existingNotificationIds = mutableSetOf<String>()
//
//    private val sentLastMinuteNotifications = mutableSetOf<String>()
//
//
//    init {
//        getNotification()
//        pollingNotifications()
//    }
//
//    private fun loadAllTravelProposals() {
//        viewModelScope.launch {
//            tripModel.getFilteredTravelProposals(filters).collect { proposals ->
//                Log.d(
//                    "TravelProposalViewModel", "Filtered Proposals: ${proposals.map { it.id }}"
//                )
//                _allTravelProposals.value = proposals
//            }
//        }
//    }
//
//    private fun getNotification() {
//        viewModelScope.launch {
//            val toggleToNotificationTypes = mapOf(
//                NotificationPreferenceType.LAST_MINUTE to listOf(NotificationType.LAST_MINUTE),
//                NotificationPreferenceType.NEW_APPLICATION to listOf(NotificationType.NEW_APPLICATION),
//                NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP to listOf(NotificationType.REVIEW_RECEIVED_FOR_PAST_TRIP),
//                NotificationPreferenceType.CHECK_RECOMMENDED to listOf(NotificationType.CHECK_RECOMMENDED),
//                NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION to listOf(
//                    NotificationType.PARTICIPANT_APPROVED,
//                    NotificationType.PARTICIPANT_REJECTED
//                )
//            )
//            val user = userModel.loggedUser.value
////            userModel.loggedUser.collect { user ->
//            if (user.uid.isNotEmpty()) {
//                val excludedNotificationTypes = user.notificationSettings
//                    .filter { !it.enabled }
//                    .flatMap { toggle -> toggleToNotificationTypes[toggle.type] ?: emptyList() }
//                Log.d("NotificationsExcluded", "Out: $excludedNotificationTypes")
//
//                tripModel.getNotificationsForUserUID(user, excludedNotificationTypes)
//                    .collect { notifications ->
//                        // Update the main notifications list
//                        _notifications.value = notifications
//                        _unreadNotificationCount.value =
//                            notifications.count { !it.isRead(user.uid) }
//
//                        val newNotifications = notifications.filter { notification ->
//                            !existingNotificationIds.contains(notification.id) &&
//                                    !notification.isRead(user.uid) &&
//                                    notification.isRecent()
//                        }
//
//                        // Update the set of existing IDs
//                        existingNotificationIds.addAll(notifications.map { it.id })
//
//                        // Emit new notifications as events
//                        newNotifications.forEach { notification ->
//                            Log.d(
//                                "NewNotificationEvent",
//                                "Emitting notification event: ${notification.title}"
//                            )
//                            _notificationEvents.emit(notification)
//                        }
//                    }
//            } else {
//                _notifications.value = emptyList()
//                _unreadNotificationCount.value = 0
//            }
////            }
//        }
//    }
//
//    private fun pollingNotifications() {
//        viewModelScope.launch {
//            while (true) {
//                delay(30 * 60 * 1000)
//                //delay(20 * 1000)
//
//                Log.d("Notification Polling ", "Checking for last minute proposals")
//                checkForLastMinuteProposals()
//                checkForRecommendedProposals()
//            }
//
//        }
//    }
//
//
//    fun getSortedNotifications(): List<Notification> {
//        return _notifications.value.sortedWith(compareBy<Notification> { notification ->
//            val isRead = notification.isRead(currentUser.value.uid)
//            val isRecent = notification.isRecent()
//
//            when {
//                isRecent && !isRead -> 0
//                !isRecent && !isRead -> 1
//                else -> 2
//            }
//        }.thenByDescending { it.timestamp.toDate().time })
//    }
//
//
//    private fun checkForLastMinuteProposals() {
//        Log.d("prova", "Starting Function")
//        val timeNow = System.currentTimeMillis()
//        val proposals = _allTravelProposals.value
//
//        proposals.forEach { proposal ->
//            Log.d(
//                "prova",
//                "Before if condition: ${proposal.tripPlannerId} (${currentUser.value.uid})"
//            )
//            if (proposal.tripPlannerId == currentUser.value.uid)
//                return@forEach
//            Log.d("prova", "After If")
//
//            if (proposal.participants.any { it.id == currentUser.value.uid })
//                return@forEach
//
//            val hasExistingLastMinuteNotification = _notifications.value.any { notification ->
//                notification.tripId == proposal.id &&
//                        notification.type == NotificationType.LAST_MINUTE &&
//                        notification.applicantId == currentUser.value.uid
//
//
//            }
//
//
//            val startDateConversion = proposal.tripStartDate.toDate().time
//            val isLastMinute = (startDateConversion - timeNow) <= 24 * 60 * 60 * 1000
//
//            Log.d("controllo", "Proposal: ${sentLastMinuteNotifications} = (${proposal.id})")
//            if (isLastMinute && !hasExistingLastMinuteNotification) {
//                tripModel.addNotification(
//                    tripId = proposal.id,
//                    title = proposal.title,
//                    type = NotificationType.LAST_MINUTE_AUTOMATIC,
//                    notificationOwnerId = "Automatic",
//                    applicantId = currentUser.value.uid,
//                    tripPlannerId = proposal.tripPlannerId,
//                )
//                Log.d("prova", "Checking for last minute proposals")
//                Log.d(
//                    "prova",
//                    "Last minute notification sent for trip: ${proposal.title} (${proposal.id})"
//                )
//            }
//        }
//    }
//
//    private fun checkForRecommendedProposals() {
//        proposals.forEach { proposal ->
//            Log.d(
//                "prova2",
//                "Before if condition: ${proposal.tripPlannerId} (${currentUser.value.uid})"
//            )
//            if (proposal.tripPlannerId == currentUser.value.uid)
//                return@forEach
//            Log.d("prova2", "After If")
//
//            if (proposal.participants.any { it.id == currentUser.value.uid })
//                return@forEach
//
//            val hasExistingRecommendedNotification = _notifications.value.any { notification ->
//                notification.tripId == proposal.id &&
//                        notification.type == NotificationType.CHECK_RECOMMENDED &&
//                        notification.applicantId == currentUser.value.uid
//            }
//
//            Log.d(
//                "prima del controllo importante",
//                "Proposal: ${proposal.title} --> (${currentUser.value.mostDesiredDestination})"
//            )
//            if (!proposal.title.contains(
//                    currentUser.value.mostDesiredDestination,
//                    ignoreCase = true
//                )
//            )
//                return@forEach
//            Log.d(
//                "dopo del controllo importante",
//                "Proposal: ${proposal.title} --> (${currentUser.value.mostDesiredDestination})"
//            )
//
//            if (!hasExistingRecommendedNotification) {
//                tripModel.addNotification(
//                    tripId = proposal.id,
//                    title = proposal.title,
//                    type = NotificationType.CHECK_RECOMMENDED,
//                    notificationOwnerId = "Automatic",
//                    applicantId = currentUser.value.uid,
//                    tripPlannerId = proposal.tripPlannerId,
//                )
//                Log.d(
//                    "prova",
//                    "Recommended notification sent for trip: ${proposal.title} (${proposal.id})"
//                )
//            }
//        }
//    }
//
//
//    fun deleteNotification() {
//        viewModelScope.launch {
//            try {
//                tripModel.deleteNotificationsForTrip(_travelProposal.value.id)
//                Log.d("TravelProposalViewModel", "Notification deleted successfully")
//            } catch (e: Exception) {
//                Log.e("TravelProposalViewModel", "Error deleting notification: ${e.message}")
//            }
//        }
//    }
//
//
//    fun markNotificationAsRead(notificationId: String) {
//        tripModel.markNotificationAsRead(notificationId, currentUser.value.uid)
//    }
//
//    //dynamic message based on the type
//    fun getNotificationMessage(
//        type: NotificationType,
//        tripTitle: String,
//        snackBar: Boolean
//    ): String {
//        if (snackBar) {
//            return when (type) {
//                NotificationType.NEW_PROPOSAL -> "New travel proposal added: $tripTitle"
//                NotificationType.NEW_APPLICATION -> "New application received for: $tripTitle"
//                NotificationType.PARTICIPANT_APPROVED -> "You have been approved for this trip: $tripTitle"
//                NotificationType.PARTICIPANT_REJECTED -> "Your application for the trip has been rejected: $tripTitle"
//                NotificationType.REVIEW_RECEIVED_FOR_PAST_TRIP -> "New review received for trip: $tripTitle"
//                NotificationType.LAST_MINUTE -> "Last minute proposal for trip: $tripTitle"
//                NotificationType.CHECK_RECOMMENDED -> "Recommended trip based on your preferences for trip: $tripTitle"
//                NotificationType.LAST_MINUTE_AUTOMATIC -> "Last minute proposal for trip: $tripTitle (automatic notification)"
//                NotificationType.USER_REVIEW_RECEIVED -> "A user has left a review for you"
//                NotificationType.BADGE_UNLOCKED -> "New Badge unlocked!"
//                else -> "Notification for trip: $tripTitle"
//            }
//        } else {
//            return when (type) {
//                NotificationType.NEW_PROPOSAL -> "$tripTitle: New travel proposal added"
//                NotificationType.NEW_APPLICATION -> "$tripTitle: New application received"
//                NotificationType.PARTICIPANT_APPROVED -> "$tripTitle: You have been approved for this trip "
//                NotificationType.PARTICIPANT_REJECTED -> "$tripTitle: Your application for the trip has been rejected"
//                NotificationType.REVIEW_RECEIVED_FOR_PAST_TRIP -> "$tripTitle: New review received for this trip"
//                NotificationType.LAST_MINUTE -> "$tripTitle: Last minute proposal for this trip"
//                NotificationType.CHECK_RECOMMENDED -> "$tripTitle: Recommended trip based on your preferences"
//                NotificationType.LAST_MINUTE_AUTOMATIC -> "$tripTitle: Last minute proposal for this trip (automatic notification)"
//                NotificationType.USER_REVIEW_RECEIVED -> "A user has left a review for you"
//                NotificationType.BADGE_UNLOCKED -> "You have unlocked a New Badge!"
//                else -> "$tripTitle: Notification for trip "
//            }
//        }
//    }
//
//}