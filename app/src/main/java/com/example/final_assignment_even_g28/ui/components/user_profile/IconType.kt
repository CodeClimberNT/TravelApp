package com.example.final_assignment_even_g28.ui.components.user_profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tram
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
enum class IconType {
    HOUSE,
    ACCOUNT_CIRCLE,
    DIRECTIONS_WALK,
    TRAIN,
    TRAM,
    AIRPLANE,
    DEFAULT;

    companion object {
        fun toImageVector(iconType: IconType): ImageVector {
            return when (iconType) {
                HOUSE -> Icons.Default.House
                ACCOUNT_CIRCLE -> Icons.Default.AccountCircle
                DIRECTIONS_WALK -> Icons.AutoMirrored.Default.DirectionsWalk
                TRAIN -> Icons.Default.Train
                TRAM -> Icons.Default.Tram
                AIRPLANE -> Icons.Default.AirplanemodeActive
                DEFAULT -> Icons.Default.AccountCircle // Default icon if none matches
            }
        }

        fun toList(): List<IconType> {
            return IconType.entries
        }
    }
}

fun IconType.toImageVector(): ImageVector {
    return IconType.toImageVector(this)
}

fun IconType.toList(): List<IconType> {
    return IconType.toList()
}

