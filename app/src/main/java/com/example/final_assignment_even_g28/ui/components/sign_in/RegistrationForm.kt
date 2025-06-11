package com.example.final_assignment_even_g28.ui.components.sign_in

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.ui.window.Dialog


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrationForm(
    navActions: Navigation,
    userVM: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismissRequest: () -> Unit
) {

    val profile = userVM.selectedUserProfile

    var tempName by remember { mutableStateOf("") }
    var tempSurname by remember { mutableStateOf("") }
    var tempMail by remember { mutableStateOf("") }
    var tempPassword by remember { mutableStateOf("") }

    val interestsOptions = listOf("Hiking", "Fun", "Culture", "Relax", "Adventure", "Food")
    val cityOptions = listOf("USA", "Italy", "Japan", "Thailand", "Spain", "Canada")

    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var selectedCities by remember { mutableStateOf(setOf<String>()) }

    Card (
        modifier = Modifier
            .height(550.dp),
        shape = RoundedCornerShape(16.dp)
    ){
        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(48.dp)).align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempSurname,
                    onValueChange = { tempSurname = it },
                    label = { Text("Surname") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempMail,
                    onValueChange = { tempMail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempPassword,
                    onValueChange = { tempPassword = it },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),

                    )

                Spacer(Modifier.height(24.dp))
                Text("Select your interest:", style = MaterialTheme.typography.titleSmall)
                Text("Activity preferences", style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    interestsOptions.forEach { option ->
                        FilterChip(
                            selected = option in selectedInterests,
                            onClick = {
                                selectedInterests = if (option in selectedInterests) selectedInterests - option
                                else selectedInterests + option
                            },
                            label = { Text(option) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                // --- Favourite City ---
                Text("Select your favourite city:", style = MaterialTheme.typography.titleSmall)
                Text("Country", style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    cityOptions.forEach { city ->
                        FilterChip(
                            selected = city in selectedCities,
                            onClick = {
                                selectedCities = if (city in selectedCities) selectedCities - city
                                else selectedCities + city
                            },
                            label = { Text(city) }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = {
                        userVM.signUp(
                            UserProfile(
                                name = tempName,
                                surname = tempSurname,
                                email = tempMail
                            ),
                            password = tempPassword
                        )

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Sign Up")
                }

            }
        }
    }

}

data class RegistrationData(
    val name: String,
    val surname: String,
    val email: String,
    val interests: List<String>,
    val favouriteCities: List<String>
)
