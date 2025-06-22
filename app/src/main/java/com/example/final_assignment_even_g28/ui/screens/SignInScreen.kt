package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.sign_in.LogInForm
import com.example.final_assignment_even_g28.ui.components.sign_in.RegistrationForm
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@Composable
fun SignInScreen(
    navActions: Navigation,
    userVM: UserProfileViewModel = viewModel(factory = AppFactory),
) {
    val context = LocalContext.current
    var loading = userVM.isLoading.collectAsState()

    var showRegisterModule by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                stringResource(id = R.string.login_prompt),
                style = MaterialTheme.typography.bodyMedium
            )

            if (loading.value)
                CircularProgressIndicator()
            else {
                GoogleSignInButton { userVM.signUpWithGoogle(context) }
                Button(
                    onClick = { showRegisterModule = true }, modifier = Modifier
                        .width(240.dp)
                        .height(48.dp)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("Register")
                }
                Button(
                    onClick = {
                        showLogin = true

                    }, modifier = Modifier
                        .width(240.dp)
                        .height(48.dp)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("Log In")
                }

                if (showRegisterModule) {
                    RegistrationForm(navActions) { showRegisterModule = false }
                }
                if (showLogin) {
                    LogInForm(navActions) { showLogin = false }
                }
            }


        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier
            .width(240.dp)
            .height(48.dp)
    ) {
        Image(
            painterResource(id = R.drawable.ic_google_logo),
            contentDescription = null,
            modifier = Modifier.size(25.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Sign in with Google", color = MaterialTheme.colorScheme.primary)
    }
}


/*@Composable @Preview(showBackground = true)
private fun PreviewSignIn() { SignInScreen({}) }*/