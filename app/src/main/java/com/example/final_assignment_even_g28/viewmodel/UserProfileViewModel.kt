package com.example.final_assignment_even_g28.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.final_assignment_even_g28.data.Collections
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
import com.google.firebase.Timestamp
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

    val selectedUserProfile: StateFlow<UserProfile?> = model.selectedUserProfile

    val loggedUser: StateFlow<UserProfile> = model.loggedUser

    fun getUserByUID(uid: String) = model.getUserByUid(uid)
    fun getNicknameById(userId: Int): String? = model.getNicknameById(userId.toString())
    fun getNicknameByUID(userUID: String): String? = model.getNicknameByUID(userUID)

    fun updateUserProfile(updatedProfile: UserProfile) = model.updateUserProfile(updatedProfile)

    var editingProfile: MutableState<UserProfile> = mutableStateOf<UserProfile>(UserProfile())

    private var _validationErrors by mutableStateOf(UserProfileError())
    val validationErrors: UserProfileError get() = _validationErrors

    var isEditing by mutableStateOf(false)

    init {
        val currentUser = Collections.auth.currentUser
        if (currentUser != null){
               model.loadUserByUID(currentUser.uid)
        }
        editingProfile.value = loggedUser.value
    }

    fun login(email: String, password: String) {
        model.login(email, password)
    }


    fun logOut() {
        model.logOut()
    }

    fun signUp(userToSign: UserProfile, password: String) {
        model.signUp(userToSign, password)
    }

    fun signUpWithGoogle(context: Context){
        model.signUpWithGoogle(context)
    }

    fun deleteAccount(){
        model.deleteAccount()
    }

    fun startEditing() {
        if (isEditing) {
            return
        }
        editingProfile.value = loggedUser.value
        _validationErrors = UserProfileError()
        isEditing = true
    }

    fun cancelChanges() {
        isEditing = false
        editingProfile.value = loggedUser.value
    }

/*
fun setCurrentUser(userId: String, userName: String, userEmail: String): Boolean {
        getUserByUid(userId)
        if (selectedUserProfile.value != null) {
            _userProfile = selectedUserProfile.value!!
            return true
        } else {
            _userProfile = UserProfile(userId, userName, userEmail)
            return false
        }
    }
 */


     fun saveAndExitEditing(context: Context) {
         viewModelScope.launch {
             editingProfile.value.let { draft ->
                 val errors = UserProfileValidator().validate(draft)
                 if (errors.asList().any { it.isNotEmpty() }) {
                     _validationErrors = errors
                 } else {
                     isEditing = false
                     model.editProfile(editingProfile.value, context)
                 }
             }
         }

    }

     fun handleBackNavigation(context: Context) {
        if (validateFields()) {
            saveAndExitEditing(context)
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
        return loggedUser.value.name[0].toString() + loggedUser.value.surname[0].toString()
    }

    fun getProfilePicture(): ProfilePictureData {
        return loggedUser.value.profilePicture
    }

    fun getEditingProfilePicture(): ProfilePictureData {
        return loggedUser.value.profilePicture
    }

    fun updateName(name: String) {
        editingProfile.value = editingProfile.value.copy(name = name)
    }

    fun updateSurname(surname: String) {
        editingProfile.value = editingProfile.value.copy(surname = surname)
    }

    fun updateTypeOfExperiences(newTypeOfExperiences: String) {
        val newTypeOfExperiences: List<String> = newTypeOfExperiences.split(",").map { it.trim() }
        editingProfile.value = editingProfile.value.copy(typeOfExperiences = newTypeOfExperiences)
    }

    fun updateMostDesiredDestination(newDestination: String) {
        editingProfile.value =
            editingProfile.value.copy(mostDesiredDestination = newDestination)
    }

    fun updateBio(newBio: String) {
        editingProfile.value = editingProfile.value.copy(bio = newBio)
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        editingProfile.value = editingProfile.value.copy(phoneNumber = newPhoneNumber)
    }

    fun updateEmail(newEmail: String) {
        editingProfile.value = editingProfile.value.copy(email = newEmail)
    }

    fun updateDateOfBirth(newDateOfBirth: Timestamp) {
        editingProfile.value = editingProfile.value.copy(dateOfBirth = newDateOfBirth)
    }

    fun updatePastExperiences(newPastExperiences: String) {
        editingProfile.value =
            editingProfile.value.copy(pastExperiences = listOf(newPastExperiences))
    }

    fun updateBadge(newBadge: String) {
        editingProfile.value = editingProfile.value.copy(badge = newBadge)
    }

    fun updateCurrentLevel(newLevel: Int) {
        editingProfile.value = editingProfile.value.copy(currentLevel = newLevel)
    }

    fun updateRating(newRating: Float) {
        editingProfile.value = editingProfile.value.copy(rating = newRating)
    }

    fun updateProfilePicture() {
        editingProfile.value = editingProfile.value.copy(
            profilePicture = ProfilePictureData.Monogram(getInitials()),
            isProfileImage = "Monogram"
        )
    }

    fun updateProfilePicture(icon: IconType) {
        editingProfile.value = editingProfile.value.copy(
            profilePicture = ProfilePictureData.Icon(icon),
            isProfileImage = "Icon"
        )
    }

    fun updateProfilePicture(imageUri: String) {
        editingProfile.value = editingProfile.value.copy(
            profilePicture = ProfilePictureData.UriData(imageUri),
            isProfileImage = "Uri"
        )

    }

    fun validateFields(): Boolean {
        val errors = UserProfileValidator().validate(editingProfile.value)
        _validationErrors = errors

        val isValid = errors.asList().all { it.isEmpty() }


        return isValid
    }

    fun getEditFieldDefinitionList(): List<EditableFieldDefinition> {
        Log.d("Edit Profile", "started")
        var profile = editingProfile.value.copy()
        Log.d("Edit Profile", "finished load")

        return listOf(
            EditableFieldDefinition(
                label = "Name",
                value = profile.name,
                errorMessage = validationErrors.fullName,
                onValueChange = { updateName(it) }
            ),
            EditableFieldDefinition(
                label = "Surname",
                value = profile.surname,
                errorMessage = validationErrors.nickName,
                onValueChange = { updateSurname(it) }
            ),
            EditableFieldDefinition(
                label = "Email",
                value = profile.email,
                editable = false,
                keyboardType = KeyboardType.Email,
                onValueChange = { updateEmail(it) }
            ),
            EditableFieldDefinition(
                label = "Phone Number",
                value = profile.phoneNumber,
                errorMessage = validationErrors.phoneNumber,
                keyboardType = KeyboardType.Number,
                onValueChange = { updatePhoneNumber(it) }
            ),
            /*
            EditableFieldDefinition(
                label = "Type of Experiences Seek",
                value = profile.typeOfExperiences.joinToString(","),
                errorMessage = validationErrors.typeOfExperiences,
                onValueChange = { updateTypeOfExperiences(it) }
            ),*/
            EditableFieldDefinition(
                label = "Desired Destination",
                value = profile.mostDesiredDestination,
                errorMessage = validationErrors.mostDesiredDestination,
                onValueChange = { updateMostDesiredDestination(it) }
            ),
            EditableFieldDefinition(
                label = "Experiences as a Traveler",
                value = profile.pastExperiences.toString(),
                errorMessage = validationErrors.pastExperiences,
                onValueChange = { updatePastExperiences(it) }
            ),
            EditableFieldDefinition(
                label = "Bio",
                value = profile.bio,
                onValueChange = { updateBio(it) }
            )
        )
    }

    fun getInfoFieldDefinitionList(): List<InfoFieldDefinition> {
        val profile = loggedUser.value

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
                label = "Date of Birth",
                value = dateFormat.format(profile.dateOfBirth?.toDate()),
            ),
            InfoFieldDefinition(
                label = "Email",
                value = profile.email,
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
                label = "Experiences as a Traveler",
                value = profile.pastExperiences.toString(),
            ),
            InfoFieldDefinition(
                label = "Bio",
                value = profile.bio
            )
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