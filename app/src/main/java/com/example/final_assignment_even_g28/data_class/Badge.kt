package com.example.final_assignment_even_g28.data_class

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.final_assignment_even_g28.data_class.BadgeIconType.ERROR
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Progress(
    val current: Int = 0,
    val total: Int = 0
)

@Serializable
enum class BadgeIconType {
    PEOPLE,
    WALKING,
    TRAIN,
    AIRPLANE,
    CAMERA,
    ERROR;

    companion object {
        fun toImageVector(iconType: BadgeIconType): ImageVector {
            return when (iconType) {
                PEOPLE -> Icons.Default.People
                WALKING -> Icons.AutoMirrored.Filled.DirectionsWalk
                TRAIN -> Icons.Default.Train
                AIRPLANE -> Icons.Default.AirplanemodeActive
                CAMERA -> Icons.Default.CameraAlt
                ERROR -> Icons.Default.Error
            }
        }
    }
}

fun BadgeIconType.toImageVector(): ImageVector {
    return BadgeIconType.toImageVector(this)
}

fun BadgeIconType.toList(): List<BadgeIconType> {
    return BadgeIconType.entries.filter { it != ERROR }
}


@Serializable
data class Badge(
    @get:Exclude
    var id: String = "",
    val title: String = "Error Loading Badge",
    val icon: BadgeIconType = ERROR,
    val description: String = "An error occurred while loading this badge.",
    val progress: Progress = Progress(0, 42),
    @Contextual var timeOfCompletion: Timestamp = Timestamp.now()
) : Comparable<Badge> {
    // Compare badges based on progress percentage, then by title
    override fun compareTo(other: Badge): Int {
        // Compare by time of completion first
        val timeComparison = other.timeOfCompletion.compareTo(this.timeOfCompletion)
        if (timeComparison != 0) return timeComparison

        // Avoid division by zero
        if (progress.total == 0 || other.progress.total == 0)
            return title.compareTo(other.title)

        val thisProgressPercentage = progress.current.toDouble() / progress.total
        val otherProgressPercentage = other.progress.current.toDouble() / other.progress.total
        // then compare by progress percentage
        return when {
            thisProgressPercentage > otherProgressPercentage -> -1
            thisProgressPercentage < otherProgressPercentage -> 1
            else -> title.compareTo(other.title)
        }

    }
}

object LocalBadgeRepository {
    val badges = listOf(
        Badge(
            id = "badge1",
            title = "Travel In Pack",
            icon = BadgeIconType.PEOPLE,
            description = "Apply to 5 trips that have at least 3 people approved.",
            progress = Progress(5, 5)
        ),
        Badge(
            id = "badge2",
            title = "Little Steps",
            icon = BadgeIconType.WALKING,
            description = "Be approved for 1 trip.",
            progress = Progress(0, 1)
        ),
        Badge(
            id = "badge3",
            title = "Explorer",
            icon = BadgeIconType.TRAIN,
            description = "Be approved in 5 trips.",
            progress = Progress(5, 5)
        ),
        Badge(
            id = "badge3",
            title = "Now You're Flying",
            icon = BadgeIconType.AIRPLANE,
            description = "Be approved for 10 trips.",
            progress = Progress(0, 10)
        ),
        Badge(
            id = "badge3",
            title = "Photographer",
            icon = BadgeIconType.CAMERA,
            description = "Upload at least one photo in 3 different reviews.",
            progress = Progress(0, 3)
        )
    )
}