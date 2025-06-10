package com.example.final_assignment_even_g28.viewmodel.auth

import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data.Collections
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom

class SignInViewModel : ViewModel() {
    private val auth = Collections.auth


    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser.asStateFlow()

    fun signIn(context: Context) {
        val activity = context as? Activity ?: return
        viewModelScope.launch {
            _isSigningIn.value = true
            try {
                val credManager = CredentialManager.create(activity)

                val googleOption = GetSignInWithGoogleOption.Builder(activity.getString(R.string.default_web_client_id))
                    .setNonce(generateNonce())
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val result = credManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCred = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                    auth.signInWithCredential(firebaseCred).await()
                    _firebaseUser.value = auth.currentUser!!
                    Log.d("SignInVM", "Signed in with Google: ${firebaseUser.value?.displayName} (${firebaseUser.value?.email}) ${firebaseUser.value?.uid}")
                    _success.value = true
                } else {
                    Log.e("SignInVM", "Unexpected credential type ${credential::class.java.simpleName}")
                }
            } catch (e: Exception) {
                Log.e("SignInVM", "Google sign‑in failed: ${e.message}", e)
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _firebaseUser.value = null

        Log.d("SignInVM", "Signed out")
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}