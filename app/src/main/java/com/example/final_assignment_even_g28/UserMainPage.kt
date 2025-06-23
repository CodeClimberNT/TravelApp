package com.example.final_assignment_even_g28

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.getProgressPercentage
import com.example.final_assignment_even_g28.data_class.isCompleted
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.NotificationBell
import com.example.final_assignment_even_g28.ui.components.badge.BadgeIconWithInfo
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.screens.SignInScreen
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch


sealed interface ProfileEvent {
    object ProfileInfo : ProfileEvent
    object BadgesClicked : ProfileEvent
    object ReviewsClicked : ProfileEvent
    object SettingsClicked : ProfileEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    isOpeningBadge: Boolean = false,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBadgesBottomSheet by remember { mutableStateOf(isOpeningBadge) }

    val profile by viewModel.loggedUser.collectAsState()
    val isUserLoggedIn =
        profile.uid.isNotEmpty() && profile.uid != com.example.final_assignment_even_g28.utils.UNKNOWN_USER.uid

    fun showBadges() {
        showBadgesBottomSheet = true
        scope.launch { sheetState.show() }
    }

    fun hideBadges() {
        showBadgesBottomSheet = false
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBadgesBottomSheet = false
            }
        }
    }

    val leveledUp by viewModel.leveledUp.collectAsState()

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
            Log.d("INIT", "User inside Profile Screen: $profile")
            if (!isUserLoggedIn) {
                Log.d("INIT", "User is not logged in, showing SignInScreen")
                SignInScreen(navActions)
            } else {
                ProfileHeader(profile, navActions = navActions)
                Spacer(modifier = Modifier.height(40.dp))
                ProfileButtonList(
                    navActions = navActions, { showBadges() }, viewModel
                )
            }
        }
        if (leveledUp)
            LevelUpCard(onDismissRequest = { viewModel.dismissLevelUpDialog() })

        if (showBadgesBottomSheet) {
            BadgesBottomSheet(sheetState = sheetState, onDismiss = { hideBadges() })
        }
    }
}

@Composable
fun ProfileHeader(
    profile: UserProfile, navActions: Navigation,
    userProfileModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)
        ) {

            /* --- avatar (or initials fallback) --- */

            /*
//            val avatarSize = 72.dp
                ProfilePicture(
                isLandScape = false,
                isDashboard = true
            )

            */
            val levelRange = userProfileModel.getLevelRange()
            LevelProgressBar(levelRange.first, levelRange.second)



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
            level = profile.currentLevel, interests = profile.typeOfExperiences
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileButtonList(
    navActions: Navigation, showBadges: () -> Unit, viewModel: UserProfileViewModel
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
                            ProfileEvent.BadgesClicked -> {
                                showBadges()
                            }

                            ProfileEvent.ReviewsClicked -> navActions.navigateToUserReview()
                            ProfileEvent.SettingsClicked -> {
                                navActions.navigateToSettings()
                            }
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
            }, shape = commonShape, colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ), modifier = Modifier
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
        horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.Center
    ) {
        /* plain level text */
        Text(
            text = "Lvl. $level",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 16.dp)
        )
        Spacer(Modifier.width(16.dp))/* interest chips */
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesBottomSheet(
    sheetState: SheetState,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismiss: () -> Unit
) {
    val userBadges by userVm.userBadges.collectAsState()
    val badges = userBadges.sorted()
    val scrollState = rememberScrollState()
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                stringResource(R.string.your_badges),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                badges.forEach { badge ->
                    RowBadge(badge)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowBadge(badge: Badge, userVm: UserProfileViewModel = viewModel(factory = AppFactory)) {
    val progressPercentage = badge.getProgressPercentage()
    val isCompleted = badge.isCompleted()
    val loggedUser by userVm.loggedUser.collectAsState()

    Card(
        elevation = CardDefaults.cardElevation(6.dp), colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.primaryContainer
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(132.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(Modifier.height(16.dp))
                BadgeIconWithInfo(
                    badge,
                    isMiniBadge = false
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize()
            ) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Box {
                    LinearProgressIndicator(
                        progress = { progressPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            // background color as the trackColor to fill the gap
                            .background(
                                MaterialTheme.colorScheme.surface, RoundedCornerShape(36.dp)
                            )
                            .border(
                                (0.5).dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(36.dp)
                            ),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        trackColor = MaterialTheme.colorScheme.surface,
                        strokeCap = StrokeCap.Round,
                    )
                    if (isCompleted) {
                        Text(
                            text = stringResource(R.string.completed) + "!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "${(progressPercentage * 100).toInt()}% ${stringResource(R.string.completed)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (loggedUser.badge?.title?.contains(badge.title) == true) {
                    Button(onClick = { userVm.removeBadge() }, enabled = isCompleted) {
                        Text(stringResource(R.string.unequip_this_badge))
                    }
                } else {
                    Button(onClick = { userVm.updateBadge(badge) }, enabled = isCompleted) {
                        Text(
                            if (isCompleted) {
                                stringResource(R.string.equip_this_badge)
                            } else {
                                "${badge.progress.current}/${badge.progress.total} ${
                                    stringResource(
                                        R.string.to_unlock
                                    )
                                }"
                            }
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun LevelProgressBar(
    exp: Float,
    nextLevelExp: Float,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    val userProfile by userVm.loggedUser.collectAsState()
    val progress = exp / nextLevelExp
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val size = size.minDimension
            val strokeWidth = 8f

            drawArc(
                color = Color.Gray,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(size, size),
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = Color.Blue,
                startAngle = 270f,
                sweepAngle = 360f * progress,
                useCenter = false,
                size = Size(size, size),
                style = Stroke(width = strokeWidth)
            )
        }
        ProfilePicture(
            userProfile = userProfile,
            isLandScape = false,
            isDashboard = true
        )
    }
}

@Composable
fun LevelUpCard(
    userProfileModel: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismissRequest: () -> Unit
) {
    val lvl = userProfileModel.loggedUser.collectAsState().value.currentLevel

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "You have leveled Up!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.size(20.dp))

                Text(
                    text = "${lvl - 1} -> $lvl",
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "Thank you!"
                    )
                }
            }
        }
    }
}
