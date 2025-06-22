package com.example.final_assignment_even_g28.ui.components.user_profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.ui.components.badge.BadgeIconWithInfo
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

/*
@Serializable
@Polymorphic
sealed class ProfilePictureData {
    @Serializable
    data class Monogram(val initials: String = "") : ProfilePictureData()

    @Serializable
    data class Icon(val icon: IconType = IconType.ACCOUNT_CIRCLE) : ProfilePictureData()

    @Serializable
    data class UriData(val uri: String = "") : ProfilePictureData()

    companion object {
        val DEFAULT: ProfilePictureData = Icon(IconType.ACCOUNT_CIRCLE)
    }

}
*/

@Composable
fun ProfilePicture(
    userProfile: UserProfile,
    isLandScape: Boolean,
    showCameraButton: Boolean = false,
    userProfileViewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    isDashboard: Boolean = false,
    isCandidate: Boolean = false,
    onCameraClick: (() -> Unit)? = null,
    onRemoveClick: () -> Unit = {},
) {
    val avatarSize = if (isCandidate) 56.dp else if (isDashboard) 72.dp else 150.dp
    val isEditing by remember { mutableStateOf(showCameraButton && onCameraClick != null) }
    val badge = userProfile.badge
    val profilePicture = userProfile.profilePicture

    // For some reason the type must be explicitly declared
    val boxModifier: MutableState<Modifier> = remember {
        if (userProfile.isProfileImage == "Uri" || !isEditing) {
            mutableStateOf(Modifier)
        } else {
            when (isLandScape) {
                true -> mutableStateOf(Modifier.fillMaxHeight())
                false -> mutableStateOf(Modifier.fillMaxWidth())
            }
        }
    }

    LaunchedEffect(userProfile.profilePicture) {
        boxModifier.value =
            if (userProfile.isProfileImage == "Uri" || !isEditing) {
                Modifier
            } else {
                when (isLandScape) {
                    true -> Modifier.fillMaxHeight()
                    false -> Modifier.fillMaxWidth()
                }

            }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = boxModifier.value
            .size(avatarSize)
    ) {
        if (!(showCameraButton && onCameraClick != null)) {
            when (userProfile.isProfileImage) {
                "Monogram" -> {
                    MakeMonogramFromInitials(
                        userProfileViewModel.getInitialsFromUser(userProfile),
                        isPrimary = true,
                        isDashboard = isDashboard,
                        isCandidate = isCandidate,
                        modifier = if (!isLandScape) Modifier else Modifier.size(100.dp)
                    )
                }

                "Icon" -> {
                    Icon(
                        imageVector = userProfileViewModel.getIcon(profilePicture),
                        contentDescription = "Profile Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = if (!isLandScape) Modifier
                            .clip(shape = CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxSize()
                        else Modifier
                            .clip(shape = CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .size(100.dp)
                    )
                }

                "Uri" -> {
                    Image(
                        painter =
                            rememberAsyncImagePainter(userProfileViewModel.getImageFromUID(profile)),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = if (!isLandScape) Modifier
                            .clip(shape = CircleShape)
                            .size(avatarSize)
                        else Modifier
                            .clip(shape = CircleShape)
                            .size(100.dp)

                    )
                }
            }
            if (badge != null && isDashboard) {
                Box(
                    modifier = Modifier.matchParentSize()
                ) {
                    BadgeIconWithInfo(
                        badge = badge, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .matchParentSize(), isMiniBadge = true
                    )
                }
            }

        } else {
            // Editing Profile Picture
            when (userProfile.isProfileImage) {
                "Icon", "Monogram" -> {
                    IconCarousel(userProfileViewModel, isLandScape = isLandScape)
                }

                "Uri" -> {
                    Image(
                        painter =
                            rememberAsyncImagePainter(userProfileViewModel.getImageFromUID(profile)),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = if (!isLandScape) Modifier
                            .clip(shape = CircleShape)
                            .size(avatarSize)
                        else Modifier
                            .clip(shape = CircleShape)
                            .size(100.dp)
                    )
                }
            }
            Box(
                modifier = if (!isLandScape) Modifier.size(avatarSize)
                else Modifier.size(130.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .align(Alignment.BottomEnd)
                            .background(
                                MaterialTheme.colorScheme
                                    .primaryContainer,
                                shape = CircleShape
                            )
                            .border(
                                1.5.dp,
                                Color.Black,
                                shape = CircleShape
                            )
                ) {
                    IconButton(
                        onClick = onCameraClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                }

                if (userProfile.isProfileImage == "Uri") {
                    Box(
                        modifier =
                            Modifier
                                .size(48.dp)
                                .align(Alignment.TopEnd)
                                .background(
                                    MaterialTheme.colorScheme
                                        .errorContainer,
                                    shape = CircleShape
                                )
                                .border(
                                    1.5.dp,
                                    Color.Black,
                                    shape = CircleShape
                                )
                    ) {

                        IconButton(
                            onClick = onRemoveClick,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )


                        }
                    }
                }
            }
        }
    }
}


@Composable
fun IconCarousel(viewModel: UserProfileViewModel, isLandScape: Boolean) {
    val icons = remember { mutableStateOf(viewModel.getIconsList()) }

    var selectedIndex by remember { mutableIntStateOf(0) }

    if (!isLandScape) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val leftIndex = (selectedIndex - 1 + icons.value.size) % icons.value.size
            val rightIndex = (selectedIndex + 1) % icons.value.size

            IconButton(onClick = {
                selectedIndex = leftIndex
                val icon = icons.value[selectedIndex]
                when (icon) {
                    is IconType -> {
                        viewModel.updateProfilePicture(icon)
                    }

                    is String -> {
                        viewModel.updateProfilePicture()
                    }
                }
            }) {
                IconOrText(
                    item = icons.value[leftIndex],
                    isSelected = false
                )
            }

            IconButton(
                onClick = {
                    selectedIndex = leftIndex
                    val icon = icons.value[selectedIndex]
                    when (icon) {
                        is IconType -> {
                            viewModel.updateProfilePicture(icon)
                        }

                        is String -> {
                            viewModel.updateProfilePicture()
                        }
                    }
                },
            ) {
                Text(
                    "<",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            IconOrText(
                item = icons.value[selectedIndex],
                isSelected = true
            )

            IconButton(
                onClick = {
                    selectedIndex = rightIndex
                    val icon = icons.value[selectedIndex]
                    when (icon) {
                        is IconType -> {
                            viewModel.updateProfilePicture(icon)
                        }

                        is String -> {
                            viewModel.updateProfilePicture()
                        }
                    }
                },
            ) {
                Text(
                    ">",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            IconButton(onClick = {
                selectedIndex = rightIndex
                val icon = icons.value[selectedIndex]
                when (icon) {
                    is IconType -> {
                        viewModel.updateProfilePicture(icon)
                    }

                    is String -> {
                        viewModel.updateProfilePicture()
                    }
                }
            }) {
                IconOrText(
                    item = icons.value[rightIndex],
                    isSelected = false
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val leftIndex = (selectedIndex - 1 + icons.value.size) % icons.value.size
            val rightIndex = (selectedIndex + 1) % icons.value.size


            IconButton(onClick = {
                selectedIndex = leftIndex
                val icon = icons.value[selectedIndex]
                when (icon) {
                    is IconType -> {
                        viewModel.updateProfilePicture(icon)
                    }

                    is String -> {
                        viewModel.updateProfilePicture()
                    }
                }
            }) {
                IconOrText(
                    item = icons.value[leftIndex],
                    isSelected = false
                )
            }

            IconButton(
                onClick = {
                    selectedIndex = leftIndex
                    val icon = icons.value[selectedIndex]
                    when (icon) {
                        is IconType -> {
                            viewModel.updateProfilePicture(icon)
                        }

                        is String -> {
                            viewModel.updateProfilePicture()
                        }
                    }
                },
            ) {
                Text(
                    "^",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            IconOrText(
                item = icons.value[selectedIndex],
                isSelected = true,
                modifier = Modifier
                    .size(100.dp)
            )

            IconButton(
                onClick = {
                    selectedIndex = rightIndex
                    val icon = icons.value[selectedIndex]
                    when (icon) {
                        is IconType -> {
                            viewModel.updateProfilePicture(icon)
                        }

                        is String -> {
                            viewModel.updateProfilePicture()
                        }
                    }
                },
            ) {
                Text(
                    "v",
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            IconButton(onClick = {
                selectedIndex = rightIndex
                val icon = icons.value[selectedIndex]
                when (icon) {
                    is IconType -> {
                        viewModel.updateProfilePicture(icon)
                    }

                    is String -> {
                        viewModel.updateProfilePicture()
                    }
                }
            }) {
                IconOrText(
                    item = icons.value[rightIndex],
                    isSelected = false,
                )
            }
        }
    }

}


@Composable
fun IconOrText(
    item: Any,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isDashboard: Boolean = false,
    isCandidate: Boolean = false
) {
    val ctx = LocalContext.current

    when (item) {
        is IconType -> {
            Icon(
                imageVector = item.toImageVector(),
                contentDescription = if (isSelected) "Selected Icon" else "Icon",
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                modifier = if (isSelected) modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary) else modifier.size(48.dp)
            )
        }

        is String -> {
            MakeMonogramFromInitials(item, isSelected, isDashboard, isCandidate, modifier)
        }

        else -> {
            Toast.makeText(
                ctx,
                "Unknown item type",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


@Composable
fun MakeMonogramFromInitials(
    text: String,
    isPrimary: Boolean,
    isDashboard: Boolean,
    isCandidate: Boolean,
    modifier: Modifier = Modifier
) {
    when {
        isDashboard -> {
            Text(
                text,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
                    .size(72.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }

        isPrimary && !isCandidate -> {
            Text(
                text,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
                    .size(150.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }

        isCandidate -> {
            Text(
                text,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
                    .size(72.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }

        else -> {
            Text(
                text,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = MaterialTheme.typography.displayLarge.fontWeight,
                color = Color.Gray,
                modifier = modifier
                    .size(48.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}