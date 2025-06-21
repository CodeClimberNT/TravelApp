package com.example.final_assignment_even_g28.utils

import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Timestamp?.toDateFormat(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this?.toDate() ?: 0)
}

val UNKNOWN_USER = UserProfile(
    uid = "0",
    name = "Unknown User",
    profilePicture = ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
)

// https://www.epochconverter.com/
// Epoch timestamp: 32535215999
//     Timestamp in milliseconds: 32535215999000
// Date and time (GMT): Wednesday, December 31, 3000 11:59:59 PM
//     Date and time (your time zone): Thursday, January 1, 3001 12:59:59 AM GMT+01:00
val MAX_TIMESTAMP: Timestamp = Timestamp(Date(32_535_215_999_000))
