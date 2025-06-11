package com.example.final_assignment_even_g28

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.model.UserProfile
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.NotificationBell
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.screens.SignInScreen
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel


sealed interface ProfileEvent {
    object ProfileInfo : ProfileEvent
    object BadgesClicked : ProfileEvent
    object PastTripsClicked : ProfileEvent
    object ExperiencesClicked : ProfileEvent
    object ReviewsClicked : ProfileEvent
    object SettingsClicked : ProfileEvent
    object LogoutClicked : ProfileEvent
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProfileScreen(
    viewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState
) {
    val profile by viewModel.loggedUser.collectAsState()

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            bottomBar = { CustomBottomBar(navActions, bottomBarItem) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profile.uid.isEmpty()){
                    SignInScreen(navActions)
                }else{
                    ProfileHeader(profile, navActions = navActions)
                    Spacer(modifier = Modifier.height(40.dp))
                    ProfileButtonList(
                        navActions = navActions,
                        viewModel
                    )
                }
            }
        }
    }

@Composable
fun ProfileHeader(
    profile: UserProfile,
    navActions: Navigation
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 12.dp)
        ) {

            /* --- avatar (or initials fallback) --- */
//            val avatarSize = 72.dp
            ProfilePicture(
                profilePicture = profile.profilePicture,
                isLandScape = false,
                isDashboard = true
            )

            Spacer(Modifier.width(16.dp))

            /* --- name, rating, level --- */
            Column(Modifier.weight(1f)) {
                Text(
                    text = ("${profile.name}  ${profile.surname} "),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "%.2f".format(profile.rating),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            /* --- Notification Bell Icon --- */

            NotificationBell(navActions)
        }

        LevelAndChips(
            level = profile.currentLevel,
            interests = profile.typeOfExperiences
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileButtonList(
    navActions: Navigation,
    viewModel: UserProfileViewModel
) {

    val actions = listOf(
        Triple("Personal Info", Icons.Outlined.Info, ProfileEvent.ProfileInfo),
        Triple("Your Badges", Icons.Outlined.BookmarkBorder, ProfileEvent.BadgesClicked),
        Triple("Reviews", Icons.Outlined.Mail, ProfileEvent.ReviewsClicked),
        Triple("System Setting", Icons.Filled.Settings, ProfileEvent.SettingsClicked)
    )

    val commonShape = RoundedCornerShape(12.dp)
    val commonColors = ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        FlowRow(
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            actions.forEach { (label, icon, evt) ->
                FilledTonalButton(
                    onClick = {
                        when (evt) {
                            ProfileEvent.ProfileInfo -> navActions.navigateToProfile()
                            ProfileEvent.BadgesClicked -> {} //navActions.navigateToBadgesScreen()
                            ProfileEvent.ReviewsClicked -> navActions.navigateToUserReview()
                            ProfileEvent.SettingsClicked -> {navActions.navigateToSettings()} //navActions.navigateToSettingsScreen()
                            else -> {} // Handle other events as needed
                        }
                    },
                    shape = commonShape,
                    colors = commonColors,
                    modifier = Modifier
                        .height(75.dp)
                        .weight(1f)
                ) {
                    Icon(icon, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(label)
                }
            }
        }

        Spacer(Modifier.height(42.dp))
        FilledTonalButton(
            onClick = {
                viewModel.logOut()
                navActions.navigateToTravelList()
            },
            shape = commonShape,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LevelAndChips(level: Int, interests: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        /* plain level text */
        Text(
            text = "Lvl. $level",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp)
        )
        Spacer(Modifier.width(16.dp))
        /* interest chips */
        interests.forEach { tag ->
            AssistChip(
                label = { Text(tag) },
                // The chips are non-interactive in this context
                onClick = {},
                enabled = false,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProfileScreenPreview() {
//    val navActions = Navigation(rememberNavController())
//    ProfileScreen(navActions = navActions, bottomBarItem = BottomBarItem.Profile)
//}