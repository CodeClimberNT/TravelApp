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
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.BadgeType
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(private val model: UserProfileModel) : ViewModel() {
    private var _userProfile by mutableStateOf(UserProfile())
    val userProfile: UserProfile get() = _userProfile

    val isLoading: StateFlow<Boolean> = model.isSigningIn

    val loggedUser: StateFlow<UserProfile> = model.loggedUser

    val leveledUp: StateFlow<Boolean> = model.leveledUp

    val isPasswordError: StateFlow<Boolean> = model.isPasswordError

    val isUserWrong: StateFlow<Boolean> = model.isAccountWrong

    val userBadges: StateFlow<List<Badge>> = model.userBadges

    private var _editingProfile = MutableStateFlow<UserProfile>(UserProfile())
    val editingProfile: StateFlow<UserProfile> get() = _editingProfile

    private var _validationErrors by mutableStateOf(UserProfileError())
    val validationErrors: UserProfileError get() = _validationErrors

    var isEditing by mutableStateOf(false)

    init {
        val currentUser = Collections.auth.currentUser
        if (currentUser != null) {
            model.loadUserByUID(currentUser.uid)
            Log.d("INIT", "User charged: $")
        }
        viewModelScope.launch {
            loggedUser.collect { loggedUserValue ->
                _editingProfile.value = loggedUserValue
                Log.d("INIT", "loggedUser updated: $loggedUserValue")
            }
        }
    }

    fun login(email: String, password: String) {
        model.login(email, password)
        _editingProfile.value = model.loggedUser.value
        Log.d("INIT", "logged User: ${loggedUser.value}, editUser: ${editingProfile.value}")

    }


    fun logOut() {
        model.logOut()
        _editingProfile.value = model.loggedUser.value
    }

    fun signUp(userToSign: UserProfile, password: String) {
        model.signUp(userToSign, password)
    }

    fun signUpWithGoogle(context: Context) {
        viewModelScope.launch {
            model.signUpWithGoogle(context)
        }
    }

    fun deleteAccount() {
        model.deleteAccount()
    }

    fun setAccountWrong(){
        model.setAccountWrong()
    }

    fun setIsPasswordWrong(){
        model.setPasswordError()
    }


//    fun updateNotificationSettings(settings: List<NotificationPreference>) {
//        viewModelScope.launch {
//            val currentUserUid = Collections.auth.currentUser?.uid ?: ""
//            model.updateNotificationSettings(currentUserUid, settings)
//            val updatedUser = loggedUser.value.copy(notificationSettings = settings)
//            model.loadUser(updatedUser)
//        }
//    }

//    fun updateSingleNotificationSetting(key: String, isEnabled: Boolean) {
//        viewModelScope.launch {
//            val currentUserUid = Collections.auth.currentUser?.uid ?: ""
//            val updatedSettings = loggedUser.value.notificationSettings.map { pref ->
//                if (pref.type == key) pref.copy(enabled = isEnabled) else pref
//            }
//            Log.d("UpdateNotification", "Updated settings: $updatedSettings")
//            model.updateNotificationSettings(currentUserUid, updatedSettings)
//        }
//    }

    fun updateNotificationSetting(
        type: NotificationPreferenceType,
        enabled: Boolean,
        ctx: Context
    ) {
        viewModelScope.launch {
            val currentUserUid = loggedUser.value.uid
            val updatedSettings = loggedUser.value.notificationSettings.map { pref ->
                if (pref.type == type) pref.copy(enabled = enabled) else pref
            }
//            model.updateNotificationSettings(currentUserUid, updatedSettings)
            val newProfile = loggedUser.value.copy(notificationSettings = updatedSettings)
            model.editProfile(newProfile, ctx)
        }
    }

    fun getNotificationSetting(type: NotificationPreferenceType): Boolean {
        return loggedUser.value.notificationSettings.find { it.type == type }?.enabled == true
    }

    fun startEditing() {
        if (isEditing) {
            return
        }
        _editingProfile.value = model.loggedUser.value
        _validationErrors = UserProfileError()
        isEditing = true
    }

    fun cancelChanges() {
        isEditing = false
        _editingProfile.value = model.loggedUser.value
    }


    fun saveAndExitEditing(context: Context) {
        viewModelScope.launch {
            isEditing = false
            model.editProfile(editingProfile.value, context)
            Log.d("Edit User", "Saving Profile: ${editingProfile.value}")
        }
    }

    fun getUserProfileByUID(userUID: String): UserProfile {
        return model.getUserByUid(userUID) ?: UNKNOWN_USER
    }

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

    fun getIconIndex(iconName: String): Int{
        when (iconName){
            "DIRECTIONS_WALK" -> {
                return 3
            }
            "HOUSE" -> {
                return 1
            }
            "ACCOUNT_CIRCLE" -> {
                return 2
            }
            "TRAIN" -> {
                return 4
            }
            "TRAM" -> {
                return 5
            }
            "AIRPLANE" -> {
                return 6
            }
            else -> {
                return 0
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
        val profilePicture = _userProfile.profilePicture

        return when (_userProfile.isProfileImage) {
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
                    val target = getIconNameFromString(_userProfile.profilePicture)
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
        if (user.name.isEmpty() && user.surname.isEmpty()) {
            return "?"
        }
        if (user.surname.isEmpty())
            return (user.name[0]).toString().uppercase()
        return (user.name[0].toString().uppercase() + user.surname[0].toString().uppercase())
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

    fun updatePastExperiences(newExperiences: String) {
        val experienceList = newExperiences.split(",").map{it.trim()}
        _editingProfile.value = _editingProfile.value.copy(pastExperiences = experienceList)
    }

    fun getPastExperiences(): String{
        if(_editingProfile.value.pastExperiences.isEmpty()){
            return ""
        }else{
            return _editingProfile.value.pastExperiences.joinToString(", ")
        }
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
            model.deleteUserProfileImage()
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
            model.deleteUserProfileImage()
        }
    }

     fun updateProfilePicture(imageUri: String, context: Context) {
         model.makeImageUri()
        viewModelScope.launch {
            model.uploadUserProfileImage(loggedUser.value.uid, imageUri, context)
        }

    }

    fun getImageProfile(user: UserProfile): String {
        return model.getImageUrlFromSupabase(user)
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
        val errors = UserProfileValidator.validate(_editingProfile.value)
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
                label = "Past Experiences Destinations",
                value = getPastExperiences(),
                errorMessage = validationErrors.pastExperiences,
                onValueChange = { updatePastExperiences(it) }
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
                label = "Name",
                value = profile.name,
            ),
            InfoFieldDefinition(
                label = "Surname",
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
                label = "Past Experiences Destination",
                value = getPastExperiences(),
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

    fun gainExp(value: Int, context: Context) {
        viewModelScope.launch {
            model.gainExp(value, context)
        }
    }

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

    fun getImageFromUser(user: UserProfile): String{
        return model.getImageFromUser(user)
    }

    fun editLevelUp() {
        model.editLevelUp()
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