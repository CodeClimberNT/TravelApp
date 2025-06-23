package com.example.final_assignment_even_g28.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tram
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.BadgeType
import com.example.final_assignment_even_g28.data_class.NOTIFICATION_ITEMS
import com.example.final_assignment_even_g28.data_class.NotificationItem
import com.example.final_assignment_even_g28.data_class.NotificationPreferenceType
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.shared.EditableFieldDefinition
import com.example.final_assignment_even_g28.shared.InfoFieldDefinition
import com.example.final_assignment_even_g28.shared.validation.UserProfileError
import com.example.final_assignment_even_g28.shared.validation.UserProfileValidator
import com.example.final_assignment_even_g28.shared.validation.asList
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.utils.UNKNOWN_USER
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserProfileViewModel(private val model: UserProfileModel) : ViewModel() {
    val isLoading: StateFlow<Boolean> = model.isSigningIn

    private val _loggedUser = MutableStateFlow<UserProfile>(UserProfile())
    val loggedUser: StateFlow<UserProfile>
        get() = _loggedUser

    private val _userBadges = MutableStateFlow<List<Badge>>(emptyList())
    val userBadges: StateFlow<List<Badge>>
        get() = _userBadges

    private var _editingProfile = MutableStateFlow<UserProfile>(UserProfile())
    val editingProfile: StateFlow<UserProfile>
        get() = _editingProfile

    private var _validationErrors by mutableStateOf(UserProfileError())
    val validationErrors: UserProfileError
        get() = _validationErrors

    private var _leveledUp = MutableStateFlow<Boolean>(false)
    val leveledUp: StateFlow<Boolean>
        get() = _leveledUp

    val notificationItems: StateFlow<List<NotificationItem>> = loggedUser.map { user ->
        NOTIFICATION_ITEMS.map { item ->
            val isEnabled =
                user.notificationSettings.find { it.type == item.type }?.enabled ?: false
            item.copy(status = isEnabled)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NOTIFICATION_ITEMS.map { it.copy(status = false) }
    )

    var isEditing by mutableStateOf(false)

    init {
        initializeSession()
    }

    fun initializeSession() {
        viewModelScope.launch {
            model.loadCurrentUser().collect { (loggedUserValue, badges) ->
                _loggedUser.value = loggedUserValue
                _userBadges.value = badges
                _editingProfile.value = loggedUserValue
                Log.d("INIT", "loggedUser updated: $loggedUserValue")
            }
        }
    }


    fun logOut() {
        model.logOut()
        _loggedUser.value = UserProfile()
        _userBadges.value = emptyList()
        _editingProfile.value = UserProfile()
    }

    fun isUserLoggedIn(): Boolean {
        Log.d("LOGIN", "Checking if user is logged in: ${_loggedUser.value.uid}")
        return _loggedUser.value.uid.isNotEmpty() && _loggedUser.value.uid != UNKNOWN_USER.uid
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val userUID = model.login(email, password)
                model.loadUserWithBadgesFromDB(userUID).collect { (user, badges) ->
                    _loggedUser.value = user
                    _userBadges.value = badges
                    _editingProfile.value = user
                    Log.d("LOGIN", "Logged in user: $user")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Error during login: ${e.message}")
            }
        }
    }

    fun signUp(userToSign: UserProfile, password: String) {
        viewModelScope.launch {
            try {
                val userUID = model.signUp(userToSign, password)
                model.loadUserWithBadgesFromDB(userUID).collect { (user, badges) ->
                    _loggedUser.value = user
                    _userBadges.value = badges
                    _editingProfile.value = user
                    Log.d("LOGIN", "Signed up user: $user")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Error during sign up: ${e.message}")
            }
        }
    }

    fun signUpWithGoogle(context: Context) {
        viewModelScope.launch {
            try {
                val userUID = model.signUpWithGoogle(context)
                Log.d("LOGIN", "User UID from Google Sign Up: $userUID")

                // Stop the existing collection from initializeSession to avoid conflicts
                // Load user data directly without collect to avoid flow conflicts
                val userWithBadges = model.loadUserWithBadgesFromDB(userUID)
                userWithBadges.collect { (user, badges) ->
                    Log.d("LOGIN", "returned from Google login: $user")

                    // Update the state directly
                    _loggedUser.value = user
                    _userBadges.value = badges
                    _editingProfile.value = user

                    Log.d("LOGIN", "Logged in user updated: ${_loggedUser.value}")
                    Log.d("LOGIN", "editing profile updated: ${_editingProfile.value}")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Error during Google sign up: ${e.message}")
            }
        }
    }


    fun gainExp(expValue: Int, context: Context) {
        val newExp = loggedUser.value.exp + expValue
        val level = getLevelFromExp(newExp)

        _leveledUp.value = level > loggedUser.value.currentLevel

        viewModelScope.launch {
            model.updateUserLevel(loggedUser.value.uid, newExp, level)
        }
    }

    fun getLevelFromExp(newExp: Int): Int {
        return when (newExp) {
            in 0..20 -> 1

            in 21..50 -> 2

            in 51..100 -> 3

            in 101..200 -> 4

            in 201..400 -> 5

            else -> 6
        }
    }

    fun dismissLevelUpDialog() {
        _leveledUp.value = false
    }

    fun deleteAccount() {
        model.deleteAccount()
    }

    fun updateNotificationSetting(type: NotificationPreferenceType, enabled: Boolean) {
        Log.d(
            "Notification Settings",
            "outside"
        )
        viewModelScope.launch {
            try {
                Log.d(
                    "Notification Settings",
                    "inside"
                )
                if (!isUserLoggedIn()) {
                    Log.w("Notification Settings", "User is not logged in, cannot update settings.")
                    return@launch
                }

                val currentUserUid = loggedUser.value.uid
                val updatedSettings = loggedUser.value.notificationSettings.map { pref ->
                    if (pref.type == type) pref.copy(enabled = enabled) else pref
                }


                model.updateNotificationSettings(currentUserUid, updatedSettings)
                Log.d(
                    "Notification Settings",
                    "Updated settings for $currentUserUid: $updatedSettings"
                )
                _loggedUser.value = _loggedUser.value.copy(notificationSettings = updatedSettings)
                _editingProfile.value =
                    _editingProfile.value.copy(notificationSettings = updatedSettings)
            } catch (e: Exception) {
                Log.e("Notification Settings", "Error updating settings: ${e.message}")
            }
        }
    }

    fun startEditing() {
        if (isEditing) {
            return
        }
        Log.d("Edit User", "Starting to edit profile: ${_loggedUser.value}")
        _editingProfile.value = _loggedUser.value
        Log.d("Edit User", "Starting to edit profile: ${_editingProfile.value}")
        _validationErrors = UserProfileError()
        isEditing = true
    }

    fun cancelChanges() {
        isEditing = false
        _editingProfile.value = _loggedUser.value
    }


    fun saveAndExitEditing(context: Context) {
        viewModelScope.launch {
            isEditing = false
            model.editProfile(editingProfile.value, context)
            Log.d("Edit User", "Saving Profile: ${editingProfile.value}")
        }
    }

    fun getUserProfileByUID(userUID: String): Flow<UserProfile?> = model.getUserByUID(userUID)

    fun handleBackNavigation(context: Context) {
        if (validateFields()) {
            saveAndExitEditing(context)
        } else {
            cancelChanges()
        }
    }

    fun getIcon(iconName: String): ImageVector {
        when (iconName) {
            "DIRECTIONS_WALK" -> {
                return Icons.AutoMirrored.Default.DirectionsWalk
            }

            "HOUSE" -> {
                return Icons.Default.House
            }

            "ACCOUNT_CIRCLE" -> {
                return Icons.Default.AccountCircle
            }

            "TRAIN" -> {
                return Icons.Default.Train
            }

            "TRAM" -> {
                return Icons.Default.Tram
            }

            "AIRPLANE" -> {
                return Icons.Default.AirplanemodeActive
            }

            else -> {
                return Icons.Default.AccountCircle
            }
        }
    }

    fun getIconNameFromString(iconString: String): String? {
        val regex = """Icon\(icon=([A-Z_]+)\)""".toRegex()
        val matchResult = regex.find(iconString)

        return matchResult?.groups?.get(1)?.value
    }

    fun getIconsList(): List<Any> {
        // Temporary add the initials to the list
        val initials = getInitials()
        val iconsList = model.getAvailableIcons() + initials
        val profilePicture = _loggedUser.value.profilePicture

        return when (_loggedUser.value.isProfileImage) {
            "Monogram" -> {
                iconsList.toMutableList().apply {
                    // I hope the compiler optimize this
                    val target = initials
                    remove(target)
                    add(0, target)
                }
            }

            "Icon" -> {
                iconsList.toMutableList().apply {
                    val target = getIconNameFromString(_loggedUser.value.profilePicture)
                    if (target?.isNotEmpty() == true) {
                        remove(target)
                        add(0, target)
                    }
                }
            }

            "Uri" -> {
                iconsList
            }

            else -> {
                emptyList()
            }
        }
    }

    fun getInitials(): String {
        return getInitialsFromUser(editingProfile.value)
    }

    fun getInitialsFromUser(user: UserProfile): String {
        if (user.surname.isEmpty())
            return (user.name.firstOrNull()).toString().uppercase()
        return (user.name.firstOrNull().toString().uppercase() + user.surname.firstOrNull()
            .toString().uppercase())
    }

    fun updateName(name: String) {
        _editingProfile.value = _editingProfile.value.copy(name = name)
    }

    fun updateSurname(surname: String) {
        _editingProfile.value = _editingProfile.value.copy(surname = surname)
    }

    fun updateMostDesiredDestination(newDestination: String) {
        _editingProfile.value = _editingProfile.value.copy(mostDesiredDestination = newDestination)
    }

    fun updateBio(newBio: String) {
        _editingProfile.value = _editingProfile.value.copy(bio = newBio)
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        _editingProfile.value = _editingProfile.value.copy(phoneNumber = newPhoneNumber)
    }

    fun updateDateOfBirth(newDateOfBirth: Timestamp) {
        _editingProfile.value = _editingProfile.value.copy(dateOfBirth = newDateOfBirth)
    }

    fun updateProfilePicture() {
        _editingProfile.value = _editingProfile.value.copy(
            isProfileImage = "Monogram"
        )
        viewModelScope.launch {
            model.deleteUserProfileImage(_editingProfile.value.uid)
        }
    }

    fun updateBadge(newBadge: Badge?) {
        viewModelScope.launch {
            model.updateUserProfileBadge(loggedUser.value.uid, newBadge)
        }
    }

    fun removeBadge() {
        viewModelScope.launch {
            updateBadge(null)
        }
    }

    fun updateProfilePicture(icon: IconType) {
        _editingProfile.value = _editingProfile.value.copy(
            profilePicture = icon.toString(),
            isProfileImage = "Icon"
        )
        viewModelScope.launch {
            model.deleteUserProfileImage(_editingProfile.value.uid)
        }
    }

    fun updateProfilePicture(imageUri: String, context: Context) {
        viewModelScope.launch {
            model.uploadUserProfileImage(loggedUser.value.uid, imageUri, context)

        }
        _editingProfile.value = _editingProfile.value.copy(
            profilePicture = imageUri,
            isProfileImage = "Uri"
        )
    }

    fun getImageProfile(userUID: String): String {
        return model.getImageUrlFromSupabase(userUID)
    }

    fun getUriImage(imageUri: String): Uri {
        return model.fromStringToUri(imageUri)
    }

    fun updateAddTypeOfExperiences(experience: String) {
        var experienceList = _editingProfile.value.typeOfExperiences.toMutableList()
        experienceList.add(experience)

        _editingProfile.value = _editingProfile.value.copy(typeOfExperiences = experienceList)
    }

    fun updateDeleteTypeOfExperiences(experience: String) {
        var experienceList = _editingProfile.value.typeOfExperiences.toMutableList()
        experienceList.remove(experience)

        _editingProfile.value = _editingProfile.value.copy(typeOfExperiences = experienceList)
    }

    /*
    fun getAllActivityTags(): List<Pair<ActivityTag, Boolean>> {
        return ActivityTag.entries.map {
            it to _editingProfile.value.typeOfExperiences.contains(it)
        }
    }*/

    fun validateFields(): Boolean {
        val errors = UserProfileValidator().validate(_editingProfile.value)
        _validationErrors = errors
        val isValid = errors.asList().all { it.isEmpty() }

        return isValid
    }

    fun getEditFieldDefinitionList(profile: UserProfile): List<EditableFieldDefinition> {

        return listOf(
            EditableFieldDefinition(
                label = "Name",
                value = profile.name,
                errorMessage = validationErrors.name,
                onValueChange = {
                    updateName(it)
                    Log.d("edit name", "name changed to $it")
                }
            ),
            EditableFieldDefinition(
                label = "Surname",
                value = profile.surname,
                errorMessage = validationErrors.surname,
                onValueChange = { updateSurname(it) }
            ),
            EditableFieldDefinition(
                label = "Phone Number",
                value = profile.phoneNumber,
                errorMessage = validationErrors.phoneNumber,
                keyboardType = KeyboardType.Number,
                onValueChange = { updatePhoneNumber(it) }
            ),
            EditableFieldDefinition(
                label = "Desired Destination",
                value = editingProfile.value.mostDesiredDestination,
                errorMessage = validationErrors.mostDesiredDestination,
                onValueChange = { updateMostDesiredDestination(it) }
            ),
            EditableFieldDefinition(
                label = "Bio",
                value = editingProfile.value.bio,
                onValueChange = { updateBio(it) }
            ),
            EditableFieldDefinition(
                label = "Email",
                value = profile.email,
                editable = false,
                keyboardType = KeyboardType.Email,
                onValueChange = { }
            )
        )
    }

    fun getInfoFieldDefinitionList(profile: UserProfile): List<InfoFieldDefinition> {
        return listOf(
            InfoFieldDefinition(
                label = "name",
                value = profile.name,
            ),
            InfoFieldDefinition(
                label = "surname",
                value = profile.surname,
            ),
            InfoFieldDefinition(
                label = "Phone Number",
                value = "+39 ${profile.phoneNumber}",
            ),
            InfoFieldDefinition(
                label = "Desired Destination",
                value = profile.mostDesiredDestination,
            ),
            InfoFieldDefinition(
                label = "Bio",
                value = profile.bio
            ),
            InfoFieldDefinition(
                label = "Email",
                value = profile.email,
            ),
            InfoFieldDefinition(
                label = "Date of Birth",
                value = profile.dateOfBirth.toDateFormat()
            ),
            InfoFieldDefinition(
                label = "Activities Preferences",
                value = profile.typeOfExperiences.joinToString(", ")
            ),
        )
    }

    fun isRegistrationDataCorrect(
        name: String, surname: String,
        email: String, password1: String,
        password2: String
    ): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return (name.isNotEmpty() && surname.isNotEmpty()
                && email.matches(emailRegex.toRegex())
                && password1.isNotEmpty()
                && password1 == password2)
    }
//
//    fun gainExp(value: Int, context: Context) {
//        viewModelScope.launch {
//            model.gainExp(value, context)
//        }
//    }

    fun getLevelRange(): Pair<Float, Float> {
        when (loggedUser.value.currentLevel) {
            1 -> {
                return Pair(loggedUser.value.exp.toFloat(), 20f)
            }

            2 -> {
                return Pair((loggedUser.value.exp.toFloat() - 20), 30f)
            }

            3 -> {
                return Pair(loggedUser.value.exp.toFloat() - 50, 50f)
            }

            4 -> {
                return Pair(loggedUser.value.exp.toFloat() - 100f, 100f)
            }

            5 -> {
                return Pair(loggedUser.value.exp.toFloat() - 200f, 200f)
            }

            else -> {
                return Pair(1f, 1f)
            }
        }
    }

    fun getImageFromUID(user: UserProfile): String {
        return model.getImageFromUID(user.uid)
    }


    //---- Badge Progress Utility Functions ----//
    fun updateBadgeTravelInPackProgress() {
        viewModelScope.launch {
            model.triggerBadgeProgress(
                targetUserUID = loggedUser.value.uid,
                badgeType = BadgeType.TRAVEL_IN_PACK,
                incrementBy = 1
            )
        }
    }


    //-------------🚨EMERGENCY ONLY🚨-------------//
    fun deleteAllBadges() {
        viewModelScope.launch {
            model.deleteAllBadges()
        }
    }

    fun initializeBadgesToAllUsers() {
        viewModelScope.launch {
            model.initializeBadgesToAllUsers()
        }
    }
    //-------------🚨EMERGENCY ONLY🚨-------------//
}