package com.example.final_assignment_even_g28.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.getProgressPercentage
import com.example.final_assignment_even_g28.data_class.isCompleted
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.NotificationBell
import com.example.final_assignment_even_g28.ui.components.badge.BadgeIconWithInfo
import com.example.final_assignment_even_g28.ui.components.sign_in.FailedLoginForm
import com.example.final_assignment_even_g28.ui.components.sign_in.UserNotExistForm
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.theme.ProfileAccentLight
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightPrimary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightSecondary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightTertiary
import com.example.final_assignment_even_g28.ui.theme.ProfileCardLight
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import kotlinx.coroutines.delay
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
    val profile by viewModel.editingProfile.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBadgesBottomSheet by remember { mutableStateOf(isOpeningBadge) }
    val isPasswordError by viewModel.isPasswordError.collectAsState()
    val isUserError by viewModel.isUserWrong.collectAsState()

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isSystemInDarkTheme()) {
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha= 0.1f),
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        } else {
                            listOf(
                                ProfileBackgroundLightPrimary,
                                ProfileBackgroundLightSecondary,
                                ProfileBackgroundLightTertiary
                            )
                        }
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Log.d("INIT", "User inside Profile Screen: $profile")
                if (profile.uid.isEmpty()) {
                    SignInScreen(navActions)
                } else {
                    ProfileHeader(profile, navActions = navActions)
                    Spacer(modifier = Modifier.height(24.dp))
                    ProfileActions(
                        navActions = navActions,
                        showBadges = { showBadges() },
                        viewModel = viewModel
                    )
                }
            }
        }

        if (leveledUp)
            LevelUpCard(onDismissRequest = { viewModel.editLevelUp() })

        if (showBadgesBottomSheet)
            BadgesBottomSheet(sheetState = sheetState, onDismiss = { hideBadges() })

        if (isPasswordError)
            FailedLoginForm(onDismissRequest = { viewModel.setIsPasswordWrong() })

        if (isUserError)
            UserNotExistForm(onDismissRequest = { viewModel.setAccountWrong() })
    }
}

@Composable
fun ProfileHeader(
    profile: UserProfile,
    navActions: Navigation,
    userProfileModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                ProfileCardLight
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(
                        animationSpec = tween(600, delayMillis = 300),
                        initialScale = 0f
                    ) + fadeIn(tween(600, delayMillis = 300))
                ) {
                    NotificationBell(navActions)
                }
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    animationSpec = tween(600, delayMillis = 100),
                    initialScale = 0.8f
                ) + fadeIn(tween(600, delayMillis = 100))
            ) {
                val levelRange = userProfileModel.getLevelRange()
                EnhancedLevelProgressBar(levelRange.first, levelRange.second)
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    animationSpec = tween(600, delayMillis = 200),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(tween(600, delayMillis = 200))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${profile.name} ${profile.surname}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = StarColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "%.1f".format(profile.rating),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                ProfileAccentLight.copy(alpha = 0.15f)
                            },

                        ) {
                            Text(
                                text = "Level ${profile.currentLevel}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isSystemInDarkTheme()) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    ProfileAccentLight
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    animationSpec = tween(600, delayMillis = 400),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(tween(600, delayMillis = 400))
            ) {
                InterestChips(interests = profile.typeOfExperiences)
            }
        }
    }
}

@Composable
fun EnhancedLevelProgressBar(
    exp: Float,
    nextLevelExp: Float,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    val userProfile by userVm.loggedUser.collectAsState()
    val progress = if (nextLevelExp > 0) exp / nextLevelExp else 0f

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        val isSystemInDarkTheme = isSystemInDarkTheme()
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val size = size.minDimension
            val strokeWidth = 8f
            val backgroundColor = if (isSystemInDarkTheme) {
                Color.Gray.copy(alpha = 0.3f)
            } else {
                Color.Gray.copy(alpha = 0.2f)
            }
            val progressColor = if (isSystemInDarkTheme) {
                Color(0xFFD0BCFF)
            } else {
                ProfileAccentLight
            }

            drawArc(
                color = backgroundColor,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(size, size),
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = progressColor,
                startAngle = 270f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                size = Size(size, size),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)

        ) {
            ProfilePicture(
                userProfile = userProfile,
                isLandScape = false,
                isDashboard = true
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestChips(interests: List<String>) {
    if (interests.isNotEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            interests.take(4).forEach { tag ->
                AssistChip(
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {},
                    enabled = false,
                    border = BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
            if (interests.size > 4) {
                AssistChip(
                    label = {
                        Text(
                            text = "+${interests.size - 4}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    onClick = {},
                    enabled = false,
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileActions(
    navActions: Navigation,
    showBadges: () -> Unit,
    viewModel: UserProfileViewModel
) {
    val actions = listOf(
        Triple("Personal Info", Icons.Outlined.Info, ProfileEvent.ProfileInfo),
        Triple("Your Badges", Icons.Outlined.BookmarkBorder, ProfileEvent.BadgesClicked),
        Triple("Reviews", Icons.Outlined.Mail, ProfileEvent.ReviewsClicked),
        Triple("Settings", Icons.Filled.Settings, ProfileEvent.SettingsClicked)
    )

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { it / 4 }
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        ProfileCardLight
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actions.forEach { (label, icon, evt) ->
                        ActionButton(
                            label = label,
                            icon = icon,
                            onClick = {
                                when (evt) {
                                    ProfileEvent.ProfileInfo -> navActions.navigateToProfile()
                                    ProfileEvent.BadgesClicked -> showBadges()
                                    ProfileEvent.ReviewsClicked -> navActions.navigateToUserReview()
                                    ProfileEvent.SettingsClicked -> navActions.navigateToSettings()
                                }
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(
                animationSpec = tween(600, delayMillis = 300),
                initialOffsetY = { it / 4 }
            )
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.logOut()
                    navActions.navigateToTravelList()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Log Out",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
            }
            .shadow(2.dp, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    ProfileAccentLight
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(50)
            isPressed = false
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
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surface
        } else {
            ProfileCardLight
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                stringResource(R.string.your_badges),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                badges.forEachIndexed { index, badge ->
                    var isVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 50L)
                        isVisible = true
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = { it / 3 }
                        ) + fadeIn(tween(400))
                    ) {
                        BadgeCard(badge)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun BadgeCard(badge: Badge, userVm: UserProfileViewModel = viewModel(factory = AppFactory)) {
    val progressPercentage = badge.getProgressPercentage()
    val isCompleted = badge.isCompleted()
    val loggedUser by userVm.loggedUser.collectAsState()

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgeIconWithInfo(badge, isMiniBadge = false)

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    strokeCap = StrokeCap.Round,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCompleted) {
                            stringResource(R.string.completed) + "!"
                        } else {
                            "${(progressPercentage * 100).toInt()}% ${stringResource(R.string.completed)}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    if (isCompleted) {
                        val isEquipped = loggedUser.badge?.title?.contains(badge.title) == true
                        Button(
                            onClick = {
                                if (isEquipped) {
                                    userVm.removeBadge()
                                } else {
                                    userVm.updateBadge(badge)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                if (isEquipped) {
                                    stringResource(R.string.unequip_this_badge)
                                } else {
                                    stringResource(R.string.equip_this_badge)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelUpCard(
    userProfileModel: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismissRequest: () -> Unit
) {
    val lvl = userProfileModel.loggedUser.collectAsState().value.currentLevel

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.surface
                } else {
                    ProfileCardLight
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Level Up!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        ProfileAccentLight
                    }
                )

                Text(
                    text = "You reached level $lvl",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            ProfileAccentLight
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}