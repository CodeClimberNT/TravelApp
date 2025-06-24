package com.example.final_assignment_even_g28.ui.screens

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.shared.EditableFieldDefinition
import com.example.final_assignment_even_g28.shared.EditableTextField
import com.example.final_assignment_even_g28.ui.components.modal.DatePickerModal
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.screens.CameraPreview
import com.example.final_assignment_even_g28.ui.theme.ProfileAccentLight
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightPrimary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightSecondary
import com.example.final_assignment_even_g28.ui.theme.ProfileBackgroundLightTertiary
import com.example.final_assignment_even_g28.ui.theme.ProfileBorderLight
import com.example.final_assignment_even_g28.ui.theme.ProfileCardLight
import com.example.final_assignment_even_g28.ui.theme.ProfileSurfaceLight
import com.example.final_assignment_even_g28.ui.theme.ProfileSurfaceBorderLight
import com.example.final_assignment_even_g28.ui.theme.ProfileTextPrimaryLight
import com.example.final_assignment_even_g28.ui.theme.ProfileTextSecondaryLight
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.utils.toMillis
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay

enum class CameraPopupState {
    GALLERY,
    CAMERA,
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditUserProfileInfo(
    viewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    onBackClick: () -> Unit
) {
    val scrollable = rememberScrollState()
    var showCameraPopup by remember { mutableStateOf(false) }
    var popupSelectionState by remember { mutableStateOf(CameraPopupState.GALLERY) }
    var previewCamera by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val profile by viewModel.editingProfile.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri is Uri)
            viewModel.updateProfilePicture(uri.toString(), ctx)
        else
            Toast.makeText(
                ctx,
                "No image selected",
                Toast.LENGTH_SHORT
            ).show()
    }

    fun updateSelectionStateTo(newState: CameraPopupState) {
        if (popupSelectionState != newState) {
            popupSelectionState = newState
        }
    }

    val needToTakePhoto = remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted && needToTakePhoto.value) {
            previewCamera = true
            needToTakePhoto.value = false
        }
    }

    BackHandler {
        viewModel.handleBackNavigation(ctx)
        onBackClick()
    }

    if (!previewCamera) {
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.handleBackNavigation(ctx)
                                onBackClick()
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
                                text = "Edit Profile",
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
        ) { innerPadding ->


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
                            ProfileEditCard(
                                profile = profile,
                                viewModel = viewModel,
                                isLandScape = isLandScape,
                                onCameraClick = {
                                    showCameraPopup = true
                                    needToTakePhoto.value = true
                                },
                                onRemoveClick = {
                                    viewModel.updateProfilePicture(IconType.ACCOUNT_CIRCLE)
                                }
                            )
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
                            EditInformationCard(
                                fields = viewModel.getEditFieldDefinitionList(profile),
                                viewModel = viewModel,
                                isLandScape = isLandScape
                            )
                        }

                        Spacer(Modifier.height(32.dp))


                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                animationSpec = tween(durationMillis = 800, delayMillis = 600),
                                initialOffsetY = { it }
                            ) + fadeIn(animationSpec = tween(800, delayMillis = 600)),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.cancelChanges()
                                        onBackClick()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = {
                                        if (viewModel.validateFields()) {
                                            viewModel.saveAndExitEditing(ctx)
                                            onBackClick()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Save")
                                }
                            }
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
                            ProfileEditCard(
                                profile = profile,
                                viewModel = viewModel,
                                isLandScape = isLandScape,
                                onCameraClick = {
                                    showCameraPopup = true
                                    needToTakePhoto.value = true
                                },
                                onRemoveClick = {
                                    viewModel.updateProfilePicture(IconType.ACCOUNT_CIRCLE)
                                },
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
                            Column(
                                modifier = Modifier
                                    .weight(0.65f)
                                    .fillMaxHeight()
                                    .verticalScroll(scrollable)
                            ) {
                                EditInformationCard(
                                    fields = viewModel.getEditFieldDefinitionList(profile),
                                    viewModel = viewModel,
                                    isLandScape = isLandScape,
                                    modifier = Modifier.fillMaxHeight()
                                )

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.cancelChanges()
                                            onBackClick()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Cancel")
                                    }

                                    Button(
                                        onClick = {
                                            if (viewModel.validateFields()) {
                                                viewModel.handleBackNavigation(ctx)
                                                onBackClick()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("Save")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showCameraPopup) {
                CameraPopup(
                    onDismissRequest = { showCameraPopup = false },
                    onConfirmation = {
                        when (popupSelectionState) {
                            CameraPopupState.CAMERA -> {
                                if (permissionState.status.isGranted) {
                                    previewCamera = true
                                } else {
                                    permissionState.launchPermissionRequest()
                                    if (permissionState.status.isGranted) {
                                        previewCamera = true
                                    }
                                }
                            }
                            CameraPopupState.GALLERY -> {
                                galleryLauncher.launch("image/*")
                            }
                        }
                        showCameraPopup = false
                    },
                    popupSelectionState = popupSelectionState,
                    updateSelectionStateTo = { updateSelectionStateTo(it) }
                )
            }
        }
    } else {
        CameraPreview(
            onDismissCameraPreview = { previewCamera = false },
            isLandScape = isLandScape
        ) { uri ->
            viewModel.updateProfilePicture(uri.toString(), ctx)
        }
    }
}

@Composable
fun ProfileEditCard(
    profile: com.example.final_assignment_even_g28.data_class.UserProfile,
    viewModel: UserProfileViewModel,
    isLandScape: Boolean,
    onCameraClick: () -> Unit,
    onRemoveClick: () -> Unit,
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
                .padding(top= 16.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
        ) {

            ProfilePicture(
                userProfile = profile,
                showCameraButton = true,
                userProfileViewModel = viewModel,
                onCameraClick = onCameraClick,
                onRemoveClick = onRemoveClick,
                isLandScape = isLandScape
            )

            Spacer(Modifier.height(20.dp))


            Text(
                text = "${profile.name} ${profile.surname}".takeIf { it.trim().isNotEmpty() }
                    ?: "Your Name",
                style = MaterialTheme.typography.headlineMedium,
                modifier= Modifier.padding(top= 0.dp, bottom = 16.dp, start = 0.dp, end = 0.dp),
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    ProfileTextPrimaryLight
                }
            )

        }
    }
}

@Composable
fun EditInformationCard(
    fields: List<EditableFieldDefinition>,
    viewModel: UserProfileViewModel,
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
                    text = "Edit Information",
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


            GenerateEditableFields(fields = fields, viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateEditableFields(
    fields: List<EditableFieldDefinition>,
    viewModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    var selectDate by remember { mutableStateOf(false) }
    val profile by viewModel.editingProfile.collectAsState()

    Log.d("Edit Profile", "Current profile dob ${profile.dateOfBirth.toDateFormat()}")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        fields.forEach { field ->
            EditableTextField(
                value = field.value,
                onValueChange = field.onValueChange,
                label = field.label,
                isError = field.errorMessage.isNotEmpty(),
                errorMessage = field.errorMessage,
                keyboardType = field.keyboardType,
                enabled = field.editable
            )
        }

        OutlinedTextField(
            value = profile.dateOfBirth.toDateFormat(),
            onValueChange = { },
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { selectDate = true }) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Open Date Picker"
                    )
                }
            },
        )

        Spacer(Modifier.height(8.dp))

        ActivityList()

        if (selectDate) {
            DatePickerModal(
                initialDate = profile.dateOfBirth.toMillis(),
                onDateSelected = {
                    if (it != null) {
                        viewModel.updateDateOfBirth(Timestamp(it / 1000, 0))
                        Log.d("Date Selector", "Date selected: ${Timestamp(it / 1000, 0)}")
                    }
                },
                onDismiss = { selectDate = false },
            )
        }
    }
}

@Composable
fun ActivityList(userVm: UserProfileViewModel = viewModel(factory = AppFactory)) {
    val profile by userVm.editingProfile.collectAsState()
    val validationError =  userVm.validationErrors
    Column {
        Text(
            text = "Activity Preferences",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.primary
            } else {
                ProfileTextPrimaryLight
            },
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActivityTag.entries.forEach { activity ->
                FilterChip(
                    selected = profile.typeOfExperiences.contains(activity.toString()),
                    onClick = {
                        if (profile.typeOfExperiences.contains(activity.toString())) {
                            userVm.updateDeleteTypeOfExperiences(activity.toString())
                            Log.d("Edit Profile", "Removed Experience: ${activity.toString()}")
                        } else {
                            userVm.updateAddTypeOfExperiences(activity.toString())
                            Log.d("Edit Profile", "Added Experience: ${activity.toString()}")
                        }
                    },
                    label = { Text(activity.value) }
                )
            }
        }
        if (validationError.typeOfExperiences.isNotBlank()) {
            Text(
                text = validationError.typeOfExperiences,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CameraPopup(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    popupSelectionState: CameraPopupState,
    updateSelectionStateTo: (CameraPopupState) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.surface
                } else {
                    ProfileCardLight
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Select Image Source",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        ProfileTextPrimaryLight
                    }
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonWithLabel(
                        text = "Camera",
                        icon = Icons.Default.CameraAlt,
                        isEnabled = popupSelectionState == CameraPopupState.CAMERA,
                        onClick = { updateSelectionStateTo(CameraPopupState.CAMERA) },
                        modifier = Modifier.weight(1f)
                    )
                    ButtonWithLabel(
                        text = "Gallery",
                        icon = Icons.Default.Photo,
                        isEnabled = popupSelectionState == CameraPopupState.GALLERY,
                        onClick = { updateSelectionStateTo(CameraPopupState.GALLERY) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirmation() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonWithLabel(
    text: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColor = if (isEnabled) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (isEnabled) {
            Button(
                onClick = onClick,
                colors = buttonColor,
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            OutlinedButton(
                onClick = onClick,
                colors = buttonColor,
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isEnabled) {
                if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    ProfileTextPrimaryLight
                }
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
    }
}