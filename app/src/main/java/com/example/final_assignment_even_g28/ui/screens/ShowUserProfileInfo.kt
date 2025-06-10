package com.example.final_assignment_even_g28.ui.screens

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.InfoFieldDefinition
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowUserProfileInfo(
    viewModel: UserProfileViewModel,
    onEditClick: () -> Unit,
    bottomBarItem: BottomBarItem,
    navActions: Navigation,
    snackBarHostState: SnackbarHostState
) {
    val ctx = LocalContext.current
    val profilePicture = viewModel.getProfilePicture()
    var isLandScape by remember {
        mutableStateOf(
            ctx.resources.configuration.orientation == ORIENTATION_LANDSCAPE
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile Info",
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.startEditing()
                        onEditClick()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(40.dp)
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
        bottomBar = { CustomBottomBar(navActions = navActions, selectedItem = bottomBarItem) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Content of the screen
        val scrollable = rememberScrollState()
        if (!isLandScape) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        // Initial padding to account both for the top bar another
                        // padding
                        // for breathing
                        .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scrollable)
            ) {
                Spacer(Modifier.height(16.dp))

                ProfilePicture(
                    profilePicture = profilePicture,
                    isLandScape = isLandScape
                )
                Spacer(Modifier.height(16.dp))

                GenerateInfoFields(viewModel.getInfoFieldDefinitionList())
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp)
                    .padding(innerPadding)
            ) {
                ProfilePicture(
                    profilePicture = profilePicture,
                    isLandScape = isLandScape
                )
                Spacer(Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .verticalScroll(scrollable)
                ) {
                    Spacer(Modifier.height(8.dp))
                    GenerateInfoFields(viewModel.getInfoFieldDefinitionList())
                }
            }
        }
    }
}

@Composable
fun GenerateInfoFields(
    fields: List<InfoFieldDefinition>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        fields.forEach {
            ProfileInfoItem(
                value = it.value,
                label = it.label,
            )
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
    }
}