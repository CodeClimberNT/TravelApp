package com.example.final_assignment_even_g28.model

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.UserToSave
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom


class UserProfileModel() {
    private var _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> get() = _allUsers

    private var _loggedUser = MutableStateFlow<UserProfile>(UserProfile())
    val loggedUser: StateFlow<UserProfile> get() = _loggedUser

    private val _userProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val userProfiles: StateFlow<List<UserProfile>> = _userProfiles


    private val _selectedUserProfile =
        MutableStateFlow<UserProfile?>(UserProfile(uid = "11", name = "Nick"))
    val selectedUserProfile: StateFlow<UserProfile?> = _selectedUserProfile

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private var _leveledUp = MutableStateFlow<Boolean>(false)
    val leveledUp: StateFlow<Boolean> get() = _leveledUp


    init {

        Collections.users.get().addOnSuccessListener { querySnapshot ->
            val userList = mutableListOf<UserProfile>()
            for(document in querySnapshot){
                val user = document.toObject(UserProfile::class.java)
                userList.add(user)
            }
            _userProfiles.value = userList

            Log.d("User Profile","Load correctly ${userList.size} users")

        }.addOnFailureListener { e ->
            Log.e("User Profile","Error retrieving the users: $e")
        }
    }

    fun login(email: String, password: String){
        val auth = Collections.auth

        //login con username and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Firebase user
                    val user = auth.currentUser

                    if(user!=null){
                        Collections.users.document(user.uid).get()
                            .addOnSuccessListener { documentSnapshot ->
                                val userProfile = documentSnapshot.toObject(UserProfile::class.java)

                                if (userProfile?.uid != "")
                                    loadUser(userProfile!!)

                            }.addOnFailureListener {
                                //an user with that UID does not exists
                                Log.e("Login","Impossible to retrieve a user with this UID: ${user.uid}")
                            }
                        Log.d("Login", "User logged in successfully: ${user.email}")
                    } else {
                        Log.e("Login", "Authentication failed: ${task.exception?.message}")
                    }
                }
            }.addOnFailureListener {
                Log.e("Login","Impossible to login with this mail and password")
            }
    }

    fun deleteAccount(){
        val auth = Collections.auth
        val user = auth.currentUser


        if(user != null){
            user.delete()
            Log.d("User Model","User eliminated")
        }else{
            Log.e("User Model","Impossible to eliminate the user")
        }
    }

    fun loadUserByUID(uid: String){
        Collections.users.document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()){
                    Log.d("Load User","Loading User with UID: $uid")
                    val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                    _loggedUser.value = userProfile!!
                }
            }.addOnFailureListener {
                Log.e("Load User","Failed to load the user with UID: $uid")
            }
    }
    fun loadUser(user: UserProfile) {
        _loggedUser.value = user
    }

    fun logOut(){
        Collections.auth.signOut()
        _loggedUser.value = UserProfile()
    }


    suspend fun signUpWithGoogle(context: Context) {
        val activity = context as? Activity ?: return
        val auth = Collections.auth
        _isSigningIn.value = true

        try {
                Log.d("Sign Up", "Start try")
                val credManager = CredentialManager.create(activity)

                val googleOption =
                    GetSignInWithGoogleOption.Builder(activity.getString(R.string.default_web_client_id))
                        .setNonce(generateNonce())
                        .build()
                Log.d("Sign Up", "After Option")

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()
                Log.d("Sign Up", "After request")

                val result = credManager.getCredential(activity, request)
                Log.d("Sign Up", "After Result")

                val credential = result.credential
                Log.d("Sign Up", "After Credential")

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCred =
                        GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                    auth.signInWithCredential(firebaseCred).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Firebase user
                            val user = auth.currentUser

                            if(user!=null){
                                Collections.users.document(user.uid).get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                                        Log.d("Login with Google", "User: $userProfile")

                                        if (userProfile!= null){
                                            loadUser(userProfile)
                                        }else{
                                            Collections.users.document(user.uid).set(
                                                UserProfile(uid = user.uid,
                                                            email = user.email.toString(),
                                                            name = user.displayName.toString(),
                                                            surname = "",
                                                            rating = 0.0f,
                                                            fullName = "",
                                                            nickName = "",
                                                            typeOfExperiences = emptyList(),
                                                            mostDesiredDestination = "",
                                                            phoneNumber = "",
                                                            bio = "",
                                                            badge = "",
                                                            currentLevel = 0
                                                )
                                            )
                                            loadUserByUID(user.uid)
                                        }
                                        Log.d("Login with Google", "User was registered: ${user.email}")
                                    }.addOnFailureListener {
                                        Log.e("Login with Google", "it was not possible to make the sign in")
                                    }
                            } else {
                                Log.e("Login", "Authentication failed: ${task.exception?.message}")
                            }
                        }
                    }
                } else {
                    Log.e(
                        "Sign Up",
                        "Unexpected credential type ${credential::class.java.simpleName}"
                    )
                }
            } catch (e: Exception) {
                Log.e("Sign Up", "Google sign‑in failed: ${e.message}", e)
            } finally {
                _isSigningIn.value = false
            }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun signUp(userToSign: UserProfile, password: String){
        val auth = Collections.auth

        auth.createUserWithEmailAndPassword(userToSign.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        Collections.users.get()
                            .addOnSuccessListener { query ->
                                val userCount = query.size()
                                val savingUser = UserToSave(
                                    uid = user.uid,
                                    name = userToSign.name,
                                    surname = userToSign.surname,
                                    typeOfExperiences = userToSign.typeOfExperiences,
                                    mostDesiredDestination = userToSign.mostDesiredDestination,
                                    phoneNumber = userToSign.phoneNumber,
                                    email = userToSign.email,
                                    dateOfBirth = userToSign.dateOfBirth,
                                    bio = userToSign.bio,
                                    badge = userToSign.badge,
                                    currentLevel = 1,
                                    rating = 0.0f,
                                    isProfileImage = "Monogram",
                                    profilePicture = "",
                                    exp = 0
                                    )
                                Collections.users.document(user.uid).set(savingUser)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "User successfully added with uid: ${user.uid}")
                                        login(userToSign.email, password)
                                    }.addOnFailureListener { e ->
                                        Log.e("Firestore", "Error adding user to the server: $e")
                                    }

                            }.addOnFailureListener { e ->
                                Log.e("Firestore", "Error getting User Number: $e")
                            }
                    }
                }
            }.addOnFailureListener {
                Log.e("SignUp","Impossible to SignUp")
            }
    }

    suspend fun editProfile(userToSave: UserProfile, context: Context) {
    try {
            val snapshot = Collections.users.document(userToSave.uid).set(
                UserToSave(
                    name = userToSave.name,
                    surname = userToSave.surname,
                    uid = userToSave.uid,
                    email = userToSave.email,
                    dateOfBirth = userToSave.dateOfBirth,
                    bio = userToSave.bio,
                    phoneNumber = userToSave.phoneNumber,
                    mostDesiredDestination = userToSave.mostDesiredDestination,
                    typeOfExperiences = userToSave.typeOfExperiences,
                    profilePicture = userToSave.profilePicture.toString(),
                    isProfileImage = userToSave.isProfileImage,
                    badge = userToSave.badge,
                    currentLevel = userToSave.currentLevel,
                    rating = userToSave.rating,
                    exp = userToSave.exp
                ),
        ).await()
            Log.d("Edit User", "User with uid ${userToSave.uid} correctly edited")
            Log.d("Edit User", "changed saved")

            _loggedUser.value = userToSave
            uploadUserProfileImage(loggedUser.value.uid, userToSave.profilePicture, context)
            Log.d("Edit User", "Try to save uid: ${loggedUser.value.uid}, uri: ${userToSave.profilePicture}")

        }catch (e: Exception){
            Log.e("Edit Profile","Error editing Profile: $e")
        }
    }


    fun getAllUsers() {
        Collections.users.get()
            .addOnSuccessListener { collections ->
                var users = mutableListOf<UserProfile>()
                collections.forEach { document ->
                    val user = document.toObject(UserProfile::class.java)
                    users.add(user)
                }

                _allUsers.value = users

            }
    }

      fun getUserById(id: String): UserProfile {
        //get user by id from the list of all users
        return _allUsers.value.find { it.uid == id } ?: UserProfile()
     }

    fun getAvailableIcons(): List<IconType> {
        return IconType.toList()

    }

    fun getUserByUid(uid: String): UserProfile? {
        return _allUsers.value.firstOrNull { it.uid == uid }
    }

    fun getNicknameByUID(userId: String): String? {
        return _userProfiles.value.firstOrNull { it.uid == userId }?.nickName
    }
    fun getNicknameById(userId: String?): String? {
        return _userProfiles.value.firstOrNull { it.uid == userId }?.nickName
    }

    fun getNameByUID(userUID: String): String{
        return _userProfiles.value.firstOrNull(){ it.uid == userUID }?.name ?: "Unknow"
    }

    fun updateUserProfile(updatedProfile: UserProfile) {
        _userProfiles.value =
            _userProfiles.value.map { if (it.uid == updatedProfile.uid) updatedProfile else it }
        _selectedUserProfile.value = updatedProfile

    }

    fun fromStringToUri(uriString: String): Uri{
        return uriString.removePrefix("UriData(uri=").removeSuffix(")").toUri()
    }

    fun getImageFromUID(userUID: String): String{
        val url = Collections.userImagesBucket.publicUrl("$userUID/ProfileImage.jpg")

        Log.d("Image","Recovering from url: $url")

        return url
    }

    suspend fun uploadUserProfileImage(userUID: String, imageUri: String, context: Context) : Result<String> {
        return try {
            val fileName =
                "ProfileImage.jpg"
            val filePath = "$userUID/$fileName"

            Log.d("Edit User", "Uploading image to path: $filePath")

            val inputStream = context.contentResolver.openInputStream(fromStringToUri(imageUri))
            val bytes = inputStream?.readBytes() ?: throw Exception("Failed to read image")
            inputStream.close()

            // Upload to Supabase
            if(Collections.userImagesBucket.exists(filePath)){
                Collections.userImagesBucket.update(filePath, bytes)
            }else{
                Collections.userImagesBucket.upload(filePath, bytes)
            }

            val publicUrl = Collections.userImagesBucket.publicUrl(filePath)

            Log.d("Edit User", "Image uploaded successfully to \"$publicUrl\"")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("Edit User", "Upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    fun getImageUrlFromSupabase(userUID: String): String {
        val storage = Collections.storage

        val publicUrl = storage.from(Collections.userImagesBucket.toString()).publicUrl("$userUID/ProfileImage.jpg")

        return publicUrl
    }

    suspend fun deleteUserProfileImage(){
        try {
            val url = Collections.userImagesBucket.publicUrl("${loggedUser.value.uid}/ProfileImage.jpg")
            Log.e("Delete Image","Try to delete Image from url: $url")
            Collections.userImagesBucket.delete("${loggedUser.value.uid}/ProfileImage.jpg")
            Log.e("Delete Image","Image deleted")

        }catch (e: Exception){
            Log.e("Delete Image","Impossible to delete image: $e")
        }
    }

    private fun extractUserProfileFilePathFromUrl(url: String): String {
        // Extract file path from Supabase public URL
        return url.substringAfter(Collections.USER_IMAGES_BUCKET_PREFIX)
    }

    suspend fun gainExp(expValue: Int, context: Context){
        val newExp = loggedUser.value.exp + expValue
        _loggedUser.value = _loggedUser.value.copy(exp = newExp)
        editLevel(expValue)
        editProfile(loggedUser.value, context)
    }

    fun editLevel(oldExp: Int){
        var oldLvl: Int = 0;

        when(oldExp){
            in 0 .. 20 -> {oldLvl = 1}
            in 21 .. 50 -> {oldLvl = 2}
            in 51 .. 100 -> {oldLvl = 3}
            in 101 .. 200 -> {oldLvl = 4}
            in 201 .. 400 -> {oldLvl = 5}
            else -> {oldLvl = 6}
        }

        when(loggedUser.value.exp){
            in 0 .. 20 -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 1)}
            in 21 .. 50 -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 2)}
            in 51 .. 100 -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 3)}
            in 101 .. 200 -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 4)}
            in 201 .. 400 -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 5)}
            else -> {_loggedUser.value = _loggedUser.value.copy(currentLevel = 6)}
        }

        if(oldLvl < _loggedUser.value.currentLevel)
            _leveledUp.value = true
    }

    fun editLevelUp(){
        _leveledUp.value = false
    }
}
