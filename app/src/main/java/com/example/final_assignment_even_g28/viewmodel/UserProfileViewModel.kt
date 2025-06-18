package com.example.final_assignment_even_g28.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.model.UserProfileModel
import com.example.final_assignment_even_g28.data_class.UserToSave
import com.example.final_assignment_even_g28.shared.EditableFieldDefinition
import com.example.final_assignment_even_g28.shared.InfoFieldDefinition
import com.example.final_assignment_even_g28.shared.validation.UserProfileError
import com.example.final_assignment_even_g28.shared.validation.UserProfileValidator
import com.example.final_assignment_even_g28.shared.validation.asList
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/*
* FOR LOGIN:
* mail: account@yahoo.com
* password: accountpassword
* */


class UserProfileViewModel(private val model: UserProfileModel) : ViewModel() {
    private var _userProfile by mutableStateOf(UserProfile())
    val userProfile: UserProfile get() = _userProfile

    val isLoading: StateFlow<Boolean> = model.isSigningIn

    val selectedUserProfile: StateFlow<UserProfile?> = model.selectedUserProfile

    val loggedUser: StateFlow<UserProfile> = model.loggedUser

    fun getUserByUID(uid: String) = model.getUserByUid(uid)
    fun getNicknameById(userId: Int): String? = model.getNicknameById(userId.toString())
    fun getNicknameByUID(userUID: String): String? = model.getNicknameByUID(userUID)

    fun updateUserProfile(updatedProfile: UserProfile) = model.updateUserProfile(updatedProfile)

    private var _editingProfile = MutableStateFlow<UserProfile>(UserProfile())
    val editingProfile: StateFlow<UserProfile> get() = _editingProfile

    private var _validationErrors by mutableStateOf(UserProfileError())
    val validationErrors: UserProfileError get() = _validationErrors

    var isEditing by mutableStateOf(false)

    init {
        val currentUser = Collections.auth.currentUser
        if (currentUser != null){
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
        Log.d("INIT","logged User: ${loggedUser.value}, editUser: ${editingProfile.value}")

    }


    fun logOut() {
        model.logOut()
        _editingProfile.value = model.loggedUser.value
    }

    fun signUp(userToSign: UserProfile, password: String) {
        model.signUp(userToSign, password)
    }

    fun signUpWithGoogle(context: Context){
        viewModelScope.launch {
            model.signUpWithGoogle(context)
        }
    }

    fun deleteAccount(){
        model.deleteAccount()
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


     fun saveAndExitEditing() {
         isEditing = false
         model.editProfile(editingProfile.value)
         Log.d("Edit User","Saving Profile: ${editingProfile.value}")
    }

     fun handleBackNavigation(context: Context) {
        if (validateFields()) {
            saveAndExitEditing()
        } else {
            cancelChanges()
        }
    }

    fun getIconsList(): List<Any> {
        // Temporary add the initials to the list
        val initials = getInitials()
        val iconsList = model.getAvailableIcons() + initials
        val profilePicture = _userProfile.profilePicture

        return when (profilePicture) {
            is ProfilePictureData.Monogram -> {
                iconsList.toMutableList().apply {
                    // I hope the compiler optimize this
                    val target = initials
                    remove(target)
                    add(0, target)
                }
            }

            is ProfilePictureData.Icon -> {
                iconsList.toMutableList().apply {
                    val target = profilePicture.icon
                    remove(target)
                    add(0, target)
                }
            }

            is ProfilePictureData.UriData -> {
                iconsList
            }
        }
    }


    private fun getInitials(): String {
        return loggedUser.value.name[0].toString() + " " + { if (loggedUser.value.surname[0].toString().isNotEmpty())loggedUser.value.surname[0].toString() }
    }

    fun getProfilePicture(): ProfilePictureData {
        return loggedUser.value.profilePicture
    }

    fun getEditingProfilePicture(): ProfilePictureData {
        return editingProfile.value.profilePicture
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

    fun updateEmail(newEmail: String) {

    }

    fun updateDateOfBirth(newDateOfBirth: Timestamp) {
        _editingProfile.value = _editingProfile.value.copy(dateOfBirth = newDateOfBirth)
    }

    fun updatePastExperiences(newPastExperiences: String) {
    }

    fun updateBadge(newBadge: String) {
    }

    fun updateCurrentLevel(newLevel: Int) {
    }

    fun updateRating(newRating: Float) {
    }

    fun updateProfilePicture() {
        _editingProfile.value = _editingProfile.value.copy(
            profilePicture = ProfilePictureData.Monogram(getInitials()),
            isProfileImage = "Monogram"
        )
    }

    fun updateProfilePicture(icon: IconType) {
        _editingProfile.value = _editingProfile.value.copy(
            profilePicture = ProfilePictureData.Icon(icon),
            isProfileImage = "Icon"
        )
    }

    fun updateProfilePicture(imageUri: String) {
        _editingProfile.value = _editingProfile.value.copy(
            profilePicture = ProfilePictureData.UriData(imageUri),
            isProfileImage = "Uri"
        )

    }

    fun updateAddTypeOfExperiences(experience: String){
        var experienceList = _editingProfile.value.typeOfExperiences.toMutableList()
        experienceList.add(experience)

        _editingProfile.value = _editingProfile.value.copy(typeOfExperiences = experienceList)
    }

    fun updateDeleteTypeOfExperiences(experience: String){
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
                onValueChange = { updateName(it)
                    Log.d("edit name","name changed to $it")
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
                onValueChange = { updateEmail(it) }
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

    fun isRegistrationDataCorrect(name: String, surname: String,
                                  email: String, password1: String,
                                    password2: String) : Boolean{
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return (name.isNotEmpty() && surname.isNotEmpty()
                && email.matches(emailRegex.toRegex())
                && password1.isNotEmpty()
                && password1 == password2)
    }


}