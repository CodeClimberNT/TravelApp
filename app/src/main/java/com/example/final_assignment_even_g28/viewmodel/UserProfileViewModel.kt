package com.example.final_assignment_even_g28.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
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
import java.text.SimpleDateFormat
import java.util.Locale

/*UserProfile(
            id = 1,
            fullName = "John Doe",
            nickname = "Johnny Stecchino",
            typeOfExperiences = listOf("Relax", "Adventure", "Culture"),
            mostDesiredDestination = "London",
            phoneNumber = "1234567890",
            email = "john.doe@example.com",
            dateOfBirth = "01/01/1990",
            pastExperiences = "i went in Dublin last summer",
            bio = "",
            badge = "Explorer",
            currentLevel = 5,
            rating = 3.5F,
            profilePicture = ProfilePictureData.Monogram("JD")
        )*/

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

    fun login(email: String, password: String) {
        model.login(email, password)
    }


    fun logOut() {
        model.logOut()
    }

    fun signUp(userToSign: UserProfile, password: String) {
        model.signUp(userToSign, password)
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


    fun saveAndExitEditing() {
        editingProfile.value.let { draft ->
            val errors = UserProfileValidator().validate(draft)
            if (errors.asList().any { it.isNotEmpty() }) {
                _validationErrors = errors
            } else {
                isEditing = false
                val savingUser = UserToSave(
                    uid = draft.uid,
                    name = draft.name,
                    surname = draft.surname,
                    typeOfExperiences = draft.typeOfExperiences,
                    mostDesiredDestination = draft.mostDesiredDestination,
                    phoneNumber = draft.phoneNumber,
                    email = draft.email,
                    dateOfBirth = draft.dateOfBirth,
                    pastExperiences = draft.pastExperiences,
                    bio = draft.bio,
                    badge = draft.badge,
                    currentLevel = draft.currentLevel,
                    rating = draft.rating,
                )

                model.editProfile(savingUser)
            }
        }
    }

    fun handleBackNavigation() {
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
        val parts: List<String> = (loggedUser.value.name + loggedUser.value.surname).trim().split(" ")

        return when (parts.size) {
            0, 1 -> parts.firstOrNull()?.firstOrNull()?.toString() ?: ""
            else -> "${parts.first().first()}${parts.last().first()}"
        }
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
            profilePicture = ProfilePictureData.Monogram(getInitials())
        )
    }

    fun updateProfilePicture(icon: IconType) {
        editingProfile.value = editingProfile.value.copy(
            profilePicture = ProfilePictureData.Icon(icon)
        )
    }

    fun updateProfilePicture(imageUri: String) {
        editingProfile.value = editingProfile.value.copy(
            profilePicture = ProfilePictureData.UriData(imageUri)
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
            /*TODO() inserire data con calendario*/
            /*
            EditableFieldDefinition(
                label = "Date of Birth (dd/mm/yyyy)",
                value = profile.dateOfBirth.toString(),
                errorMessage = validationErrors.dateOfBirth,
                onValueChange = { updateDateOfBirth(it.toString()) }
            ),*/
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
            EditableFieldDefinition(
                label = "Type of Experiences Seek",
                value = profile.typeOfExperiences.joinToString(","),
                errorMessage = validationErrors.typeOfExperiences,
                onValueChange = { updateTypeOfExperiences(it) }
            ),
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
                value = dateFormat.format(profile?.dateOfBirth?.toDate()),
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
}