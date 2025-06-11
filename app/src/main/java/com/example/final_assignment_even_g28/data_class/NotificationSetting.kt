package com.example.final_assignment_even_g28.data_class

data class NotificationItem(
    val title: String,
    val description: String
)

val notificationItems = listOf(
    NotificationItem(
        title = "Last-minute travel proposal",
        description = "Receive suggestions for Trips that start within the next 24 hours."
    ),
    NotificationItem(
        title = "New Applicant",
        description = "Get notified when a new application is submitted for your trip proposals."
    ),
    NotificationItem(
        title = "Own Trips Reviews",
        description = "Get notified when someone reviews your trips."
    ),
    NotificationItem(
        title = "Status update on pending application",
        description = "Receive updates about the status of your travel applications."
    ),
    NotificationItem(
        title = "Recommended trips",
        description = "Trip recommendations based on your interests."
    )
)


