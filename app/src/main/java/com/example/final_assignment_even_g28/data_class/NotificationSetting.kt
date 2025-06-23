package com.example.final_assignment_even_g28.data_class

data class NotificationItem(
    val title: String,
    val description: String,
    val type: NotificationPreferenceType,
    var status: Boolean,
)

val NOTIFICATION_ITEMS = listOf(
    NotificationItem(
        title = "Last-minute travel proposal",
        description = "Receive suggestions for Trips that start within the next 24 hours.",
        type = NotificationPreferenceType.LAST_MINUTE,
        status = true
    ),
    NotificationItem(
        title = "New Applicant",
        description = "Get notified when a new application is submitted for your trip proposals.",
        type = NotificationPreferenceType.NEW_APPLICATION,
        status = true

    ),
    NotificationItem(
        title = "Own Trips Reviews",
        description = "Get notified when someone reviews your trips.",
        type = NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP,
        status = true

    ),
    NotificationItem(
        title = "Status update on pending application",
        description = "Receive updates about the status of your travel applications.",
        type = NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
        status = true

    ),
    NotificationItem(
        title = "Recommended trips",
        description = "Trip recommendations based on your interests.",
        type = NotificationPreferenceType.CHECK_RECOMMENDED,
        status = true

    ),
    NotificationItem(
        title = "Badge Unlocked",
        description = "Be notified when a new badge is unlocked.",
        type = NotificationPreferenceType.BADGE_UNLOCKED,
        status = true

    )
)


