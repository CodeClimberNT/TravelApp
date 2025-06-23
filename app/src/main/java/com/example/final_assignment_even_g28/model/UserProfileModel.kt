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
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.BadgeRepository
import com.example.final_assignment_even_g28.data_class.BadgeType
import com.example.final_assignment_even_g28.data_class.Notification
import com.example.final_assignment_even_g28.data_class.NotificationPreference
import com.example.final_assignment_even_g28.data_class.NotificationPreferenceType
import com.example.final_assignment_even_g28.data_class.NotificationType
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.UserToSave
import com.example.final_assignment_even_g28.data_class.isCompleted
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.SecureRandom


class UserProfileModel() {
    private var _loggedUser = MutableStateFlow<UserProfile>(UserProfile())
    val loggedUser: StateFlow<UserProfile> get() = _loggedUser

    private val _userProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val userProfiles: StateFlow<List<UserProfile>> = _userProfiles

    private val _userBadges = MutableStateFlow<List<Badge>>(emptyList())
    val userBadges: StateFlow<List<Badge>> = _userBadges

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private var _leveledUp = MutableStateFlow<Boolean>(false)
    val leveledUp: StateFlow<Boolean> get() = _leveledUp

    private var _isPasswordError = MutableStateFlow<Boolean>(false)
    val isPasswordError: StateFlow<Boolean> get() = _isPasswordError

    private var _isAccountWrong = MutableStateFlow<Boolean>(false)
    val isAccountWrong: StateFlow<Boolean> get() = _isAccountWrong

    init {
        loadAllUsers()
    }

    private fun loadAllUsers() {

        try {
            Collections.users.get().addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<UserProfile>()
                for (document in querySnapshot) {
                    val user = document.toObject(UserProfile::class.java)
                    userList.add(user)
                }
                _userProfiles.value = userList
                Log.d("User Profile", "Load correctly ${userList.size} users")
            }.addOnFailureListener { e ->
                Log.e("User Profile", "Error retrieving the users: $e")
            }
            migrateNotificationSettings(Collections.auth.currentUser?.uid ?: "")
            Log.d(
                "UserProfileNotifications",
                "Current user: ${Collections.auth.currentUser?.uid ?: "No user"}"
            )
        } catch (e: Exception) {
            Log.e("UserProfileModel", "Error loading all users: ${e.message}")
        }

    }

    private suspend fun loadUserWithBadges(userProfile: UserProfile) {
        try {
            _loggedUser.value = userProfile

            // Initialize badges if they don't exist
            initializeUserBadgesIfNeeded(userProfile.uid)

            // Start listening to badges flow
            CoroutineScope(Dispatchers.Main).launch {
                getBadgesFlow(userProfile.uid).collect { badges ->
                    _userBadges.value = badges
                }
            }
        } catch (e: Exception) {
            Log.e("UserProfileModel", "Error loading user with badges: ${e.message}")
            _loggedUser.value = userProfile
            _userBadges.value = emptyList()
        }
    }

    fun login(email: String, password: String) {
        val auth = Collections.auth

        //login con username and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Firebase user
                    val user = auth.currentUser

                    if (user != null) {
                        Collections.users.document(user.uid).get()
                            .addOnSuccessListener { documentSnapshot ->
                                val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                                if (userProfile != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        loadUserWithBadges(userProfile)
                                    }
                                }
                            }.addOnFailureListener {
                                //an user with that UID does not exists
                                Log.e(
                                    "Login",
                                    "Impossible to retrieve a user with this UID: ${user.uid}"
                                )
                            }
                        Log.d("Login", "User logged in successfully: ${user.email}")
                    } else {
                        Log.e("Login", "Authentication failed: ${task.exception?.message}")
                    }
                }else{
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            _isAccountWrong.value = true
                            Log.e("Login", "Email not exist")
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            _isPasswordError.value = true
                            Log.e("Login", "Wrong Password")
                        }
                        else -> {
                            Log.e("Login", "Error during login: ${exception?.message}")
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("Login", "Impossible to login with this mail and password")
            }
    }

    fun setPasswordError(){
        _isPasswordError.value = false
    }

    fun setAccountWrong(){
        _isPasswordError.value = false
    }

    fun deleteAccount() {
        val auth = Collections.auth
        val user = auth.currentUser


        if (user != null) {
            Collections.getBadgeCollection(user.uid).get()
                .addOnSuccessListener { snapshot ->
                    // Delete all badges for the user
                    for (badgeDoc in snapshot.documents) {
                        badgeDoc.reference.delete().addOnSuccessListener {
                            Log.d("User Model", "Badge ${badgeDoc.id} deleted successfully")
                        }.addOnFailureListener { e ->
                            Log.e("User Model", "Error deleting badge: ${e.message}")
                        }
                    }
                }
            user.delete()
            Log.d("User Model", "User eliminated")
        } else {
            Log.e("User Model", "Impossible to eliminate the user")
        }
    }

    fun loadUserByUID(uid: String) {
        Collections.users.document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("Load User", "Loading User with UID: $uid")
                    val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                    if (userProfile != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            loadUserWithBadges(userProfile)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("Load User", "Failed to load the user with UID: $uid")
            }
    }

//    private fun loadUser(user: UserProfile) {
//        _loggedUser.value = user
//    }

    fun logOut() {
        Collections.auth.signOut()
        _loggedUser.value = UserProfile()
        _userBadges.value = emptyList()
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

                        if (user != null) {
                            Collections.users.document(user.uid).get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val userProfile =
                                        documentSnapshot.toObject(UserProfile::class.java)
                                    Log.d("Login with Google", "User: $userProfile")

                                    if (userProfile != null) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            loadUserWithBadges(userProfile)
                                        }
                                    } else {
                                        val newUserProfile = UserProfile(
                                            uid = user.uid,
                                            email = user.email.toString(),
                                            name = user.displayName.toString(),
                                        )
                                        CoroutineScope(Dispatchers.Main).launch {
                                            createUserWithBadges(newUserProfile)
                                        }
//                                        Collections.users.document(user.uid).set(
//                                            UserProfile(
//                                                uid = user.uid,
//                                                email = user.email.toString(),
//                                                name = user.displayName.toString(),
//                                            )
//                                        ).addOnSuccessListener {
//                                            // Initialize badges for new Google user
//                                            CoroutineScope(Dispatchers.Main).launch {
//                                                initializeUserBadges(user.uid)
//                                            }
//                                            // load user only after the user is registered and the badges are initialized
//                                            loadUserByUID(user.uid)
//                                        }
                                    }
                                    Log.d("Login with Google", "User was registered: ${user.email}")
                                }.addOnFailureListener {
                                    Log.e(
                                        "Login with Google",
                                        "it was not possible to make the sign in"
                                    )
                                }
                        } else {
                            Log.e(
                                "Login",
                                "Authentication failed: ${task.exception?.message}"
                            )
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

    private suspend fun createUserWithBadges(userProfile: UserProfile) {
        try {
            // Create user document
            withContext(Dispatchers.IO) {
                Collections.users.document(userProfile.uid).set(userProfile).await()
                Log.d("UserProfile", "Created user profile for: ${userProfile.uid}")
            }

            // Initialize badges
            initializeUserBadges(userProfile.uid)

            // Load user with badges
            loadUserWithBadges(userProfile)

        } catch (e: Exception) {
            Log.e("UserProfile", "Error creating user with badges: ${e.message}")
        }
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun signUp(userToSign: UserProfile, password: String) {
        val auth = Collections.auth

        auth.createUserWithEmailAndPassword(userToSign.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val userProfile = userToSign.copy(uid = user.uid)
                            createUserWithBadges(userProfile)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("SignUp", "Impossible to SignUp")
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
                    pastExperiences = userToSave.pastExperiences,
                    bio = userToSave.bio,
                    phoneNumber = userToSave.phoneNumber,
                    mostDesiredDestination = userToSave.mostDesiredDestination,
                    typeOfExperiences = userToSave.typeOfExperiences,
                    profilePicture = userToSave.profilePicture,
                    isProfileImage = userToSave.isProfileImage,
                    badge = userToSave.badge,
                    currentLevel = userToSave.currentLevel,
                    rating = userToSave.rating,
                    exp = userToSave.exp,
                    notificationSettings = userToSave.notificationSettings
                ),
            ).await()
            Log.d("Edit User", "User with uid ${userToSave.uid} correctly edited")
            Log.d("Edit User", "changed saved")

            _loggedUser.value = userToSave
            Log.d(
                "Edit User",
                "Try to save uid: ${loggedUser.value.uid}, uri: ${userToSave.profilePicture}"
            )

        } catch (e: Exception) {
            Log.e("Edit Profile", "Error editing Profile: $e")
        }
    }

    suspend fun updateUserProfileBadge(userUID: String, newBadge: Badge?) {
        try {
            withContext(Dispatchers.IO) {
                try {
                    Log.d("Edit User", "Updating badge for user with UID: $userUID")
                    Log.d("Edit User", "New badge: $newBadge")
                    Log.d("Edit User", "Logged user badge updated: ${_loggedUser.value.badge}")
                    // FIXME: use the commented code when the code is merged with the others
                    val documentRef = Collections.users.document(userUID)
                    val userToEdit = _userProfiles.value.firstOrNull { it.uid == userUID }
                        ?: throw Exception("User with UID $userUID not found")
                    userToEdit.badge = newBadge
                    Tasks.await(documentRef.set(userToEdit))

                    Log.d("Edit User", "Edited User with UID: ${userToEdit.uid}")

                    loadUserByUID(userToEdit.uid)
                    _loggedUser.value = _loggedUser.value.copy(badge = newBadge)

                } catch (e: Exception) {
                    Log.e("Edit User", "Error editing user: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("Edit User", "Error in coroutine: ${e.message}")
        }
    }


//    fun getAllUsers() {
//        Collections.users.get()
//            .addOnSuccessListener { collections ->
//                var users = mutableListOf<UserProfile>()
//                collections.forEach { document ->
//                    val user = document.toObject(UserProfile::class.java)
//                    users.add(user)
//                }
//
//                _allUsers.value = users
//
//            }
//    }

    fun getUserById(id: String): UserProfile {
        //get user by id from the list of all users
        return _userProfiles.value.find { it.uid == id } ?: UserProfile()
    }

    fun getAvailableIcons(): List<IconType> {
        return IconType.toList()

    }

    fun getUserByUid(uid: String): UserProfile? {
        return _userProfiles.value.firstOrNull { it.uid == uid }
    }

    fun getNameByUID(userUID: String): String {
        return _userProfiles.value.firstOrNull() { it.uid == userUID }?.name ?: "Unknown"
    }

    fun updateUserProfile(updatedProfile: UserProfile) {
        _userProfiles.value =
            _userProfiles.value.map { if (it.uid == updatedProfile.uid) updatedProfile else it }
    }

    fun fromStringToUri(uriString: String): Uri {
        return uriString.removePrefix("UriData(uri=").removeSuffix(")").toUri()
    }
     
    fun getImageFromUser(user: UserProfile): String{
        val url = Collections.userImagesBucket.publicUrl("${user.uid}/${user.profilePicture}.jpg")

        Log.d("Image", "Recovering from url: $url")

        return url
    }

    fun makeImageUri(){
        _loggedUser.value = _loggedUser.value.copy(isProfileImage = "Uri")
    }

    suspend fun uploadUserProfileImage(userUID: String, imageUri: String, context: Context) : Result<String> {
        return try {
            val fileName = generateRandomString(10)
            val filePath = "$userUID/$fileName.jpg"

            Log.d("Edit User", "Uploading image to path: $filePath")

            val inputStream = context.contentResolver.openInputStream(imageUri.toUri())
            val bytes = inputStream?.readBytes() ?: throw Exception("Failed to read image")
            inputStream.close()

            // Upload to Supabase
            Collections.userImagesBucket.upload(filePath, bytes)

            val publicUrl = Collections.userImagesBucket.publicUrl(filePath)

            Log.d("Edit User", "Image uploaded successfully to \"$publicUrl\"")

            _loggedUser.value = _loggedUser.value.copy(profilePicture = fileName)
            _loggedUser.value = _loggedUser.value.copy(isProfileImage = "Uri")

            Result.success(fileName)
        } catch (e: Exception) {
            Log.e("Edit User", "Upload failed: ${e.message}")
            Result.failure(e)
        }
    }

    fun generateRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    fun getImageUrlFromSupabase(user: UserProfile): String {
        val storage = Collections.storage

        val publicUrl = storage.from(Collections.userImagesBucket.toString())
            .publicUrl("${user.uid}/${user.profilePicture}.jpg")

        return publicUrl
    }

    suspend fun deleteUserProfileImage() {
        try {

            val url = Collections.userImagesBucket.publicUrl("${loggedUser.value.uid}/${loggedUser.value.profilePicture}")
            Log.e("Delete Image","Try to delete Image from url: $url")
            Collections.userImagesBucket.delete("${loggedUser.value.uid}/${loggedUser.value.profilePicture}")
            Log.e("Delete Image","Image deleted")

        } catch (e: Exception) {
            Log.e("Delete Image", "Impossible to delete image: $e")
        }
    }

    private fun extractUserProfileFilePathFromUrl(url: String): String {
        // Extract file path from Supabase public URL
        return url.substringAfter(Collections.USER_IMAGES_BUCKET_PREFIX)
    }

    suspend fun gainExp(expValue: Int, context: Context) {
        val newExp = loggedUser.value.exp + expValue
        editLevel(loggedUser.value.exp, newExp)
        _loggedUser.value = _loggedUser.value.copy(exp = newExp)
        editProfile(loggedUser.value, context)
    }

    fun editLevel(oldExp: Int, newExp: Int) {
        var oldLvl: Int = 0;

        when (oldExp) {
            in 0..20 -> {
                oldLvl = 1
            }

            in 21..50 -> {
                oldLvl = 2
            }

            in 51..100 -> {
                oldLvl = 3
            }

            in 101..200 -> {
                oldLvl = 4
            }

            in 201..400 -> {
                oldLvl = 5
            }

            else -> {
                oldLvl = 6
            }
        }

        when (newExp) {
            in 0..20 -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 1)
            }

            in 21..50 -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 2)
            }

            in 51..100 -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 3)
            }

            in 101..200 -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 4)
            }

            in 201..400 -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 5)
            }

            else -> {
                _loggedUser.value = _loggedUser.value.copy(currentLevel = 6)
            }
        }

        if (oldLvl < _loggedUser.value.currentLevel)
            _leveledUp.value = true
    }

    fun editLevelUp() {
        _leveledUp.value = false
    }


    private suspend fun initializeUserBadgesIfNeeded(userUID: String) {
        try {
            withContext(Dispatchers.IO) {
                val badgeCollection = Collections.getBadgeCollection(userUID)
                val existingBadges = badgeCollection.get().await()

                if (existingBadges.isEmpty) {
                    initializeUserBadges(userUID)
                }
            }
        } catch (e: Exception) {
            Log.e("UserBadges", "Error checking badge initialization: ${e.message}")
        }
    }

    suspend fun initializeUserBadges(userUID: String) {
        try {
            withContext(Dispatchers.IO) {
                val badgeCollection = Collections.getBadgeCollection(userUID)

                BadgeRepository.createInitialBadges().forEach { badge ->
                    badgeCollection.add(badge).await()
                }

                Log.d(
                    "UserBadges",
                    "Successfully initialized ${BadgeType.entries.size} badges for user: $userUID"
                )
            }
        } catch (e: Exception) {
            Log.e("UserBadges", "Error initializing badges for user $userUID: ${e.message}")
        }
    }

    private fun getBadgesFlow(userUID: String) = callbackFlow {
        val listener = Collections.getBadgeCollection(userUID)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserBadges", "Error listening to badges: ${error.message}")
                    trySend(emptyList<Badge>())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val badges = snapshot.documents.mapNotNull { document ->
                        document.toObject(Badge::class.java)?.apply {
                            id = document.id
                        }
                    }.sorted() // Use the Comparable implementation from Badge

                    trySend(badges)
                    Log.d("UserBadges", "Retrieved ${badges.size} badges for user: $userUID")
                } else {
                    trySend(emptyList<Badge>())
                }
            }

        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun triggerBadgeProgress(
        targetUserUID: String,
        badgeType: BadgeType,
        // Currently always incrementing by 1,
        // but if a badge requires other conditions
        // (e.g. meet with 10 people, and a travel has already 5 it can be incremented by 5)
        // this can come in handy
        incrementBy: Int = 1,
    ) {
        try {
            withContext(Dispatchers.IO) {
                val badgeCollection = Collections.getBadgeCollection(targetUserUID)
                val snapshot = badgeCollection.get().await()

                // Find badge by matching title (cleaner than type matching)
                val badgeDoc = snapshot.documents.find { doc ->
                    val badge = doc.toObject(Badge::class.java)
                    badge?.title == badgeType.displayName
                }

                if (badgeDoc != null) {
                    val currentBadge = badgeDoc.toObject(Badge::class.java)
                    if (currentBadge != null && !currentBadge.isCompleted()) {
                        updateUserBadgeProgress(targetUserUID, badgeDoc.id, incrementBy)
                        Log.d(
                            "BadgeProgress",
                            "Updated badge ${badgeType.displayName} for user $targetUserUID by $incrementBy"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BadgeProgress", "Error triggering badge progress: ${e.message}")
        }
    }

    suspend fun updateUserBadgeProgress(userUID: String, badgeId: String, incrementBy: Int = 1) {
        try {
            withContext(Dispatchers.IO) {
                val badgeRef = Collections.getBadgeCollection(userUID).document(badgeId)

                // Get current badge to check if it will be completed
                val currentBadge = badgeRef.get().await().toObject(Badge::class.java)

                if (currentBadge != null) {
                    val newCurrentProgress = currentBadge.progress.current + incrementBy
                    val isCompleting = newCurrentProgress >= currentBadge.progress.total &&
                            !currentBadge.isCompleted()

                    val updateMap = mutableMapOf<String, Any>(
                        "progress.current" to newCurrentProgress
                    )

                    // Only update completion time if badge is being completed now
                    if (isCompleting) {
                        updateMap["timeOfCompletion"] = Timestamp.now()
                    }

                    badgeRef.update(updateMap).await()

                    if (isCompleting) {
                        addNotification(
                            tripId = "",
                            title = currentBadge.title,
                            type = NotificationType.BADGE_UNLOCKED,
                            notificationOwnerId = userUID,
                        )
                    }

                    Log.d(
                        "UserBadges",
                        "Updated badge $badgeId progress to $newCurrentProgress/${currentBadge.progress.total}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("UserBadges", "Error updating badge progress: ${e.message}")
        }
    }

    suspend fun deleteAllBadges() {
        try {
            withContext(Dispatchers.IO) {
                val userDocs = Collections.users.get().await()
                for (userDoc in userDocs) {
                    val userUID = userDoc.id
                    val badgeCollection = Collections.getBadgeCollection(userUID)

                    // Delete all badges for the user
                    val badgesSnapshot = badgeCollection.get().await()
                    for (badgeDoc in badgesSnapshot.documents) {
                        badgeDoc.reference.delete().await()
                        Log.d(
                            "User Model",
                            "Badge ${badgeDoc.id} deleted successfully for user $userUID"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("User Model", "Error deleting all badges: ${e.message}")
        }
    }

    suspend fun initializeBadgesToAllUsers() {
        try {
            withContext(Dispatchers.IO) {
                val userDocs = Collections.users.get().await()
                for (userDoc in userDocs) {
                    val userUID = userDoc.id
                    initializeUserBadgesIfNeeded(userUID)
                }
            }
        } catch (e: Exception) {
            Log.e("User Model", "Error initializing badges for all users: ${e.message}")
        }
    }

    fun updateNotificationSettings(userId: String, settings: List<NotificationPreference>) {
        Collections.users.document(userId)
            .update("notificationSettings", settings)
            .addOnSuccessListener {
                Log.d("UserProfileNotifications", "Notification settings updated successfully")
                _loggedUser.value = _loggedUser.value.copy(notificationSettings = settings)
            }
            .addOnFailureListener { error ->
                Log.e(
                    "UserProfileNotifications",
                    "Failed to update notification settings: ${error.message}"
                )
            }
    }

    fun migrateNotificationSettings(userId: String) {
        if (!userId.isEmpty()) {
            Log.e("Migration", "User ID is empty, skipping migration")

            val defaultNotificationSettings = listOf(
                NotificationPreference(NotificationPreferenceType.LAST_MINUTE, true),
                NotificationPreference(NotificationPreferenceType.NEW_APPLICATION, true),
                NotificationPreference(
                    NotificationPreferenceType.REVIEW_RECEIVED_FOR_PAST_TRIP,
                    true
                ),
                NotificationPreference(
                    NotificationPreferenceType.STATUS_UPDATE_ON_PENDING_APPLICATION,
                    true
                ),
                NotificationPreference(NotificationPreferenceType.CHECK_RECOMMENDED, true),
                NotificationPreference(NotificationPreferenceType.BADGE_UNLOCKED, true)
            )

            Collections.users.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val data = documentSnapshot.data
                        if (data != null && !data.containsKey("notificationSettings")) {
                            Collections.users.document(userId)
                                .update("notificationSettings", defaultNotificationSettings)
                                .addOnSuccessListener {
                                    Log.d(
                                        "Migration",
                                        "Added notificationSettings for user $userId"
                                    )
                                }
                                .addOnFailureListener { error ->
                                    Log.e(
                                        "Migration",
                                        "Failed to add notificationSettings: ${error.message}"
                                    )
                                }
                        }
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("Migration", "Failed to retrieve user $userId: ${error.message}")
                }
        } else {
            Log.e("Migration", "User ID is empty, skipping migration")
        }
    }


    // FIXME: duplicate code, should be moved to a common place
    private fun addNotification(
        tripId: String,
        title: String,
        type: NotificationType,
        notificationOwnerId: String,
        applicantId: String? = null,
        tripPlannerId: String? = null,
        reviewedUser: String? = null
    ) {
        val notification = Notification(
            tripId = tripId,
            title = title,
            type = type,
            timestamp = Timestamp.now(),
            read = emptyList(),
            notificationOwnerId = notificationOwnerId,
            applicantId = applicantId,
            tripPlannerId = tripPlannerId,
            reviewedUser = reviewedUser
        )

        Collections.notifications.add(notification)
            .addOnSuccessListener {
                Log.d("Notifications", "Notification added for trip: $title, type: $type")
            }
            .addOnFailureListener { error ->
                Log.e("Notifications", "Failed to add notification: ${error.message}")
            }
    }
}
