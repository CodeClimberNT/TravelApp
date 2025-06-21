package com.example.final_assignment_even_g28.data_class

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.final_assignment_even_g28.data_class.BadgeIconType.ERROR
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Progress(
    val current: Int = 0,
    val total: Int = 0
)

@Serializable
enum class BadgeIconType {
    PEOPLE,
    TRAIN,
    CAMERA,
    COMMENT,
    ERROR;

    companion object {
        fun toImageVector(iconType: BadgeIconType): ImageVector {
            return when (iconType) {
                PEOPLE -> Icons.Default.People
                TRAIN -> Icons.Default.Train
                COMMENT -> Icons.Default.Recommend
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

enum class BadgeType(
    val displayName: String,
    val description: String,
    val icon: BadgeIconType,
    val requiredProgress: Int
) {
    TRAVEL_IN_PACK(
        displayName = "Travel In Pack",
        description = "Apply to 5 trips that have already at least 3 people approved.",
        icon = BadgeIconType.PEOPLE,
        requiredProgress = 5
    ),
    EXPLORER(
        displayName = "Explorer",
        description = "Be approved in 5 trips.",
        icon = BadgeIconType.TRAIN,
        requiredProgress = 5
    ),
    PHOTOGRAPHER(
        displayName = "Photographer",
        description = "Upload at least one photo in 3 different reviews.",
        icon = BadgeIconType.CAMERA,
        requiredProgress = 3
    ),
    YOU_KNOW_IT(
        displayName = "You Know It",
        description = "Write a 5 Star review to an awesome fellow traveler.",
        icon = BadgeIconType.COMMENT,
        requiredProgress = 1
    );

    fun createBadge(): Badge {
        return Badge(
            title = this.displayName,
            icon = this.icon,
            description = this.description,
            progress = Progress(0, this.requiredProgress)
        )
    }

    companion object {
        fun fromTitle(title: String): BadgeType? {
            return entries.find { it.displayName == title }
        }
    }
}


@Serializable
data class Badge(
    @get:Exclude
    var id: String = "",
    val title: String = "Error Loading Badge",
    val icon: BadgeIconType = ERROR,
    val description: String = "An error occurred while loading this badge.",
    val progress: Progress = Progress(0, 1),
    @Contextual var timeOfCompletion: Timestamp = Timestamp(Date(0))
) : Comparable<Badge> {

    override fun compareTo(other: Badge): Int {
        val thisCompleted = this.isCompleted()
        val otherCompleted = other.isCompleted()

        return when {
            // Both completed: sort by completion time (most recent first)
            thisCompleted && otherCompleted -> {
                other.timeOfCompletion.compareTo(this.timeOfCompletion)
            }

            // One completed, one not: completed badges come first
            thisCompleted && !otherCompleted -> -1
            !thisCompleted && otherCompleted -> 1

            // Both incomplete: sort by progress percentage (highest first)
            else -> {
                val thisProgress = this.getProgressPercentage()
                val otherProgress = other.getProgressPercentage()

                when {
                    thisProgress > otherProgress -> -1
                    thisProgress < otherProgress -> 1
                    // Same progress: sort alphabetically by title
                    else -> this.title.compareTo(other.title)
                }
            }
        }
    }
}

//---- Utility Functions for Badges ----//
fun Badge.isCompleted(): Boolean {
    return this.progress.current >= this.progress.total && this.progress.total > 0
}

fun Badge.getProgressPercentage(): Float {
    return if (this.progress.total > 0) {
        (this.progress.current.toFloat() / this.progress.total.toFloat()).coerceIn(0.0f, 1.0f)
    } else {
        Log.e(
            "RowBadge",
            "Badge total progress is zero for badge: ${this.title}, id: ${this.id}. This will cause division by zero."
        )
        0.0f
    }
}


// Extension function to match badge with type
fun Badge.matchesBadgeType(type: BadgeType): Boolean {
    return this.title == type.displayName
}

// Type of badge to be achieved by the user
object BadgeRepository {
    fun getAllBadgeTypes(): List<BadgeType> = BadgeType.entries

    fun createInitialBadges(): List<Badge> {
        return BadgeType.entries.map { it.createBadge() }
    }

    fun getBadgeByType(type: BadgeType): Badge {
        return type.createBadge()
    }

    fun findBadgeType(badge: Badge): BadgeType? {
        return BadgeType.fromTitle(badge.title)
    }
}
