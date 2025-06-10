package com.example.final_assignment_even_g28.ui.screens
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.viewmodel.auth.SignInViewModel
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@Composable
fun SignInScreen(navActions: Navigation,
                 singInVM: SignInViewModel = viewModel(factory = AppFactory),
                 userVM: UserProfileViewModel = viewModel(factory = AppFactory),
                 ) {
    val context = LocalContext.current
    val loading by singInVM.isSigningIn.collectAsState()
    val success by singInVM.success.collectAsState()
    val firebaseUser by singInVM.firebaseUser.collectAsState()

    var showRegisterModule by remember{ mutableStateOf(false)}
    var showLogin by remember{ mutableStateOf(false)}

    LaunchedEffect(success) {
        if (success) {
            if(userVM.setCurrentUser(firebaseUser!!.uid, firebaseUser!!.displayName!!, firebaseUser!!.email!!))
                navActions.navigateToTravelList()
            else
                navActions.navigateToRegistrationForm()
        }
    }

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
            Text("Please login to continue", style = MaterialTheme.typography.bodyMedium)

            if(loading)
                CircularProgressIndicator()
            else{
                GoogleSignInButton { singInVM.signIn(context) }
                Button(onClick = { showRegisterModule = true }, modifier = Modifier
                    .width(240.dp)
                    .height(48.dp)) {
                    Spacer(Modifier.width(8.dp))
                    Text("Register")
                }
                Button(onClick = {
                    showLogin = true

                }, modifier = Modifier
                    .width(240.dp)
                    .height(48.dp)) {
                    Spacer(Modifier.width(8.dp))
                    Text("Log In")
                }

                if(showRegisterModule){
                    RegistrationForm(navActions)
                }
                if(showLogin){
                    LogInForm(navActions, userVM)
                }
            }


        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier
        .width(240.dp)
        .height(48.dp)) {
        Image(painterResource(id = R.drawable.ic_google_logo), contentDescription = null, modifier = Modifier.size(25.dp))
        Spacer(Modifier.width(8.dp))
        Text("Sign in with Google")
    }
}

@Composable
fun LogInForm(navActions: Navigation, model: UserProfileViewModel){
    var tempMail by remember { mutableStateOf("") }
    var tempPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = tempMail,
            onValueChange = { tempMail = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = tempPassword,
            onValueChange = { tempPassword = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            model.login(tempMail, tempPassword)
            navActions.navigateToUserMainPage()
            }, modifier = Modifier
            .width(240.dp)
            .height(48.dp)) {
            Spacer(Modifier.width(8.dp))
            Text("Sign in with Google")
        }
    }
}


/*@Composable @Preview(showBackground = true)
private fun PreviewSignIn() { SignInScreen({}) }*/