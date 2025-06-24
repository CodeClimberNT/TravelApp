package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.NotificationPreferenceType
import com.example.final_assignment_even_g28.data_class.notificationItems
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.theme.DimColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    userModel: UserProfileViewModel = viewModel(factory = AppFactory),
    navActions: Navigation
) {
    val toggleStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            notificationItems.forEach {
                put(
                    it.title, userModel.getNotificationSetting(
                        when (it.title) {
                            "Last-minute travel proposal" -> NotificationPreferenceType.LAST_MINUTE
                            "New Applicant" -> NotificationPreferenceType.NEW_APPLICATION
                            "Own Trips Reviews" -> NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP
                            "Status update on pending application" -> NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION
                            "Recommended trips" -> NotificationPreferenceType.CHECK_RECOMMENDED
                            "Badge unlocked" -> NotificationPreferenceType.BADGE_UNLOCKED
                            else -> NotificationPreferenceType.NULL
                        }
                    )
                )
            }
        }
    }
    var showDeleteCompletion by remember { mutableStateOf(false) }

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
                            text = "Settings",
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
        bottomBar = {
            CustomBottomBar(
                navActions = navActions,
                selectedItem = BottomBarItem.Profile
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Notification", style = MaterialTheme.typography.headlineSmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            notificationItems.forEach { item ->
                NotificationToggle(
                    title = item.title,
                    description = item.description,
                    isChecked = toggleStates[item.title] == true,
                    onCheckedChange = { isEnabled ->

                        toggleStates[item.title] = isEnabled


                        val key = when (item.title) {
                            "Last-minute travel proposal" -> NotificationPreferenceType.LAST_MINUTE
                            "New Applicant" -> NotificationPreferenceType.NEW_APPLICATION
                            "Own Trips Reviews" -> NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP
                            "Status update on pending application" -> NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION
                            "Recommended trips" -> NotificationPreferenceType.CHECK_RECOMMENDED
                            "Badge unlocked" -> NotificationPreferenceType.BADGE_UNLOCKED
                            else -> NotificationPreferenceType.NULL
                        }

                        key.let {
                            userModel.updateNotificationSetting(it, isEnabled)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Delete Account",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = {
                    showDeleteCompletion = true
                },
                enabled = true,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Delete Account", color = MaterialTheme.colorScheme.onError)
            }
        }

        if (showDeleteCompletion) {
            Dialog(
                onDismissRequest = { showDeleteCompletion = false }
            ) {
                Card(
                    modifier = Modifier.height(320.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = DimColor
                    ),
                    elevation = CardDefaults.cardElevation(12.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Are you sure you want to delete your account?",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                        Button(
                            onClick = {
                                showDeleteCompletion = false
                                userModel.deleteAccount()
                                    navActions.back()
                            },
                            enabled = true,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(12.dp)
                        ) {
                            Text("Delete Account")
                        }
                        Button(
                            onClick = {
                                showDeleteCompletion = false
                            },
                            enabled = true,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationToggle(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, end = 16.dp)
                .weight(1f)
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Switch(
            checked = isChecked, onCheckedChange = onCheckedChange,
        )
    }
}

