package com.example.final_assignment_even_g28.ui.components.sign_in

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@Composable
fun LogInForm(
    navActions: Navigation,
    model: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismissRequest: () -> Unit
) {
    var tempMail by remember { mutableStateOf("") }
    var tempPassword by remember { mutableStateOf("") }
    var isMailEmpty by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = tempMail,
                    onValueChange = { tempMail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = isMailEmpty,
                    supportingText = {
                        if (isMailEmpty) Text(
                            text = "Email Cannot be Empty",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )
                OutlinedTextField(
                    value = tempPassword,
                    onValueChange = { tempPassword = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPasswordEmpty,
                    supportingText = {
                        if (isPasswordEmpty) Text(
                            text = "Password Cannot be Empty",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (tempMail.isEmpty()) {
                            isMailEmpty = true
                        }
                        if (tempPassword.isEmpty()) {
                            isPasswordEmpty = true
                        }
                        if (isMailEmpty == false && isPasswordEmpty == false) {
                            model.login(tempMail, tempPassword)
                            navActions.navigateToUserMainPage()
                        }
                    }, modifier = Modifier
                        .width(240.dp)
                        .height(48.dp)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("Login")
                }
            }
        }

    }

}