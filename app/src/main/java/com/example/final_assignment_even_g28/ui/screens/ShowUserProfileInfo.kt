package com.example.final_assignment_even_g28.ui.screens

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.InfoFieldDefinition
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.ui.theme.ProfileAccentLight
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightPrimary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightSecondary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightTertiary
import com.example.final_assignment_even_g28.ui.theme.ProfileBorderLight
import com.example.final_assignment_even_g28.ui.theme.ProfileCardLight
import com.example.final_assignment_even_g28.ui.theme.ProfileSurfaceBorderLight
import com.example.final_assignment_even_g28.ui.theme.ProfileSurfaceLight
import com.example.final_assignment_even_g28.ui.theme.ProfileTextMutedLight
import com.example.final_assignment_even_g28.ui.theme.ProfileTextPrimaryLight
import com.example.final_assignment_even_g28.ui.theme.ProfileTextSecondaryLight
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUserProfileInfo(
    viewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    onEditClick: () -> Unit,
    bottomBarItem: BottomBarItem,
    navActions: Navigation,
    snackBarHostState: SnackbarHostState
) {
    val profile by viewModel.loggedUser.collectAsState()
    val configuration = LocalConfiguration.current
    var isLandScape = configuration.orientation == ORIENTATION_LANDSCAPE


    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            navActions.navigateToUserMainPage()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Profile Information",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    )
                )
            }
        },
        bottomBar = { CustomBottomBar(navActions = navActions, selectedItem = bottomBarItem) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 600, delayMillis = 800),
                    initialScale = 0f
                ) + fadeIn(animationSpec = tween(600, delayMillis = 800)),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.startEditing()
                        onEditClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(16.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        val scrollable = rememberScrollState()


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isSystemInDarkTheme()) {
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
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
            if (!isLandScape) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scrollable)
                        .padding(20.dp)
                ) {
                    Spacer(Modifier.height(16.dp))


                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            animationSpec = tween(durationMillis = 800),
                            initialOffsetY = { -it }
                        ) + fadeIn(animationSpec = tween(800)),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ProfileHeaderCard(profile, isLandScape)
                    }

                    Spacer(Modifier.height(32.dp))


                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            animationSpec = tween(durationMillis = 800, delayMillis = 300),
                            initialOffsetY = { it }
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 300)),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        InformationCard(viewModel.getInfoFieldDefinitionList(profile),isLandScape)
                    }

                    Spacer(Modifier.height(40.dp))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp)
                ) {

                    AnimatedVisibility(
                        modifier = Modifier.weight(1f),
                        visible = isVisible,
                        enter = slideInHorizontally(
                            animationSpec = tween(800),
                            initialOffsetX = { -it }
                        ) + fadeIn(tween(800))
                    ) {
                        ProfileHeaderCard(
                            profile = profile,
                            isLandScape = isLandScape,
                            modifier = Modifier.weight(0.35f)
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    AnimatedVisibility(
                        modifier = Modifier.weight(2f),
                        visible = isVisible,
                        enter = slideInHorizontally(
                            animationSpec = tween(800, delayMillis = 300),
                            initialOffsetX = { it }
                        ) + fadeIn(tween(800, delayMillis = 300))
                    ) {
                        InformationCard(
                            fields = viewModel.getInfoFieldDefinitionList(profile),
                            isLandScape,
                            modifier = Modifier
                                .weight(0.65f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderCard(
    profile: UserProfile,
    isLandScape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.then(
            if (!isLandScape) Modifier.fillMaxWidth() else Modifier
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                ProfileCardLight
            }
        ),
        shape = RoundedCornerShape(24.dp),
        border = if (isSystemInDarkTheme()) null else BorderStroke(
            1.dp,
            ProfileBorderLight
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(21.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isSystemInDarkTheme()) {
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            } else {
                                listOf(
                                    ProfileAccentLight.copy(alpha = 0.2f),
                                    ProfileAccentLight.copy(alpha = 0.1f)
                                )
                            }
                        ),
                        shape = CircleShape
                    )
                    .animateContentSize(tween(500)),
                contentAlignment = Alignment.Center
            ) {
                ProfilePicture(
                    userProfile = profile,
                    isLandScape = isLandScape
                )
            }

            Spacer(Modifier.height(20.dp))


            Text(
                text = "${profile.name} ${profile.surname}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    ProfileTextPrimaryLight
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )




        }
    }
}

@Composable
fun InformationCard(
    fields: List<InfoFieldDefinition>,
    isLandScape: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                ProfileCardLight
            }
        ),
        shape = RoundedCornerShape(24.dp),
        border = if (isSystemInDarkTheme()) null else BorderStroke(
            1.dp,
            ProfileBorderLight
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        ProfileAccentLight
                    },
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        ProfileTextPrimaryLight
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = if(isLandScape) Modifier.verticalScroll(rememberScrollState()) else Modifier
            ) {
                fields.forEachIndexed { index, field ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            animationSpec = tween(400),
                            initialOffsetX = { it / 2 }
                        ) + fadeIn(tween(400))
                    ) {
                        EnhancedProfileInfoItem(
                            label = field.label,
                            value = field.value
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedProfileInfoItem(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isSystemInDarkTheme()) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        } else {
            ProfileSurfaceLight
        },
        border = if (isSystemInDarkTheme()) null else BorderStroke(
            0.5.dp,
            ProfileSurfaceBorderLight
        ),
        shadowElevation = if (isSystemInDarkTheme()) 0.dp else 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Surface(
                shape = CircleShape,
                color = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    ProfileAccentLight.copy(alpha = 0.1f)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = getIconForField(label),
                    contentDescription = label,
                    tint = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        ProfileAccentLight
                    },
                    modifier = Modifier
                        .size(20.dp)
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        ProfileTextSecondaryLight
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value.ifEmpty { "Not specified" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) {
                        if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        } else {
                            ProfileTextMutedLight
                        }
                    } else {
                        if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun getIconForField(label: String): ImageVector {
    return when (label.lowercase()) {
        "name" -> Icons.Default.Person
        "surname" -> Icons.Default.Person
        "phone number" -> Icons.Default.Phone
        "email" -> Icons.Default.Email
        "date of birth" -> Icons.Default.DateRange
        "bio" -> Icons.Default.Info
        "desired destination" -> Icons.Default.Place
        "past experiences destination", "past experiences destinations" -> Icons.Default.Map
        "activities preferences" -> Icons.Default.Star
        else -> Icons.Default.Info
    }
}