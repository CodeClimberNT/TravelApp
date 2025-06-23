package com.example.final_assignment_even_g28.ui.screens

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.utils.toMillis
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Timestamp


enum class CameraPopupState {
    GALLERY,
    CAMERA,
}

// The design was changed from the first laboratory
// to better adhere both to the M3 guidelines and to the Lab2 specifications
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


    // For system back navigation (e.g., swiping from the edge of the screen)
    BackHandler {
        viewModel.saveAndExitEditing(ctx)
        onBackClick()
    }


    if (!previewCamera) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.saveAndExitEditing(ctx)
                            onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            MaterialTheme.colorScheme.inversePrimary,
                        ),
                    modifier = Modifier.shadow(16.dp)
                )
            },

            ) { innerPadding ->

            if (!isLandScape) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp, 0.dp)
                        .verticalScroll(scrollable)
                ) {
                    Spacer(Modifier.height(16.dp))

                    ProfilePicture(
                        userProfile = profile,
                        showCameraButton = true,
                        userProfileViewModel = viewModel,
                        onCameraClick = {
                            showCameraPopup = true
                            needToTakePhoto.value = true
                        },
                        onRemoveClick = {
                            viewModel.updateProfilePicture(IconType.ACCOUNT_CIRCLE)
                        },
                        isLandScape = isLandScape
                    )

                    Spacer(Modifier.height(16.dp))

                    GenerateEditableFields(
                        fields = viewModel.getEditFieldDefinitionList(profile)
                    )


                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.cancelChanges()
                                onBackClick()
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onError)
                        }

                        Spacer(Modifier.width(24.dp))

                        Button(onClick = {
                            if (viewModel.validateFields()) {
                                viewModel.saveAndExitEditing(ctx)
                                onBackClick()
                            }
                        }) {
                            Text("Save")
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp, 0.dp)
                ) {
                    ProfilePicture(
                        userProfile = profile,
                        showCameraButton = true,
                        userProfileViewModel = viewModel,
                        onCameraClick = {
                            showCameraPopup = true
                            needToTakePhoto.value = true
                        },
                        onRemoveClick = {
                            viewModel.updateProfilePicture(IconType.ACCOUNT_CIRCLE)
                        },
                        isLandScape = isLandScape
                    )


                    Spacer(Modifier.padding(16.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .verticalScroll(scrollable)
                    ) {

                        Spacer(modifier = Modifier.height(8.dp))

                        GenerateEditableFields(
                            fields = viewModel.getEditFieldDefinitionList(profile)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                viewModel.cancelChanges()
                                onBackClick()
                            }) {
                                Text("Cancel")
                            }

                            Button(onClick = {
                                if (viewModel.validateFields()) {
                                    viewModel.saveAndExitEditing(ctx)
                                    onBackClick()
                                }
                            }) {
                                Text("Save")
                            }

                            Spacer(Modifier.padding(16.dp))
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
    } else
        CameraPreview(
            onDismissCameraPreview = { previewCamera = false },
            isLandScape = isLandScape
        ) { uri ->
            viewModel.updateProfilePicture(uri.toString(), ctx)
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
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()//.verticalScroll(scrollState),
    ) {
        fields.forEach {
            EditableTextField(
                value = it.value,
                onValueChange = it.onValueChange,
                label = it.label,
                isError = it.errorMessage.isNotEmpty(),
                errorMessage = it.errorMessage,
                keyboardType = it.keyboardType,
                enabled = it.editable
            )
        }

        OutlinedTextField(
            value = profile.dateOfBirth.toDateFormat(),
            onValueChange = { },
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { selectDate = true }) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Open Date Picker"
                    )
                }
            },
        )

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

    Text(
        text = "Activities preferences",
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(12.dp)
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
                label = { Text(activity.value) })
            Spacer(modifier = Modifier.width(8.dp))
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
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Following M3 Guidelines: https://m3.material.io/components/dialogs/specs
                // To Review: the guidelines suggest this measures using the second component
                // title description, not interactable Buttons, they may need more space
                // For now M3 do not specify our specific case, for this reason we are using
                // the only one provided by the guidelines

                Spacer(Modifier.height(24.dp))
                Text("Select an option", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ButtonWithLabel(
                        text = "Camera",
                        icon = Icons.Default.CameraAlt,
                        isEnabled = popupSelectionState == CameraPopupState.CAMERA,
                        onClick = {
                            updateSelectionStateTo(CameraPopupState.CAMERA)
                        }
                    )
                    ButtonWithLabel(
                        text = "Gallery",
                        icon = Icons.Default.Photo,
                        isEnabled = popupSelectionState == CameraPopupState.GALLERY,
                        onClick = {
                            updateSelectionStateTo(CameraPopupState.GALLERY)
                        }
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Dismiss", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { onConfirmation() }) {
                        Text("Confirm")
                    }
                }
                Spacer(Modifier.height(24.dp))
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
) {
    val buttonColor = if (isEnabled) {
        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    } else {
        ButtonDefaults.outlinedButtonColors()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ) {
        OutlinedButton(
            onClick = onClick,
            shape = CircleShape,
            colors = buttonColor,
            modifier = Modifier
                .size(110.dp)

        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    Modifier.size(48.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
