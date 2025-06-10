package com.example.final_assignment_even_g28.model

import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserProfileModel() {
    private var _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> get() = _allUsers

    private val _userProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val userProfiles: StateFlow<List<UserProfile>> = _userProfiles

    private val _selectedUserProfile =
        MutableStateFlow<UserProfile?>(UserProfile(uid = "11", name = "Nick"))
    val selectedUserProfile: StateFlow<UserProfile?> = _selectedUserProfile

    init {
        // Initialize with mock data
        _userProfiles.value = listOf(
            UserProfile(1, "Johnny Stecchino", ProfilePictureData.Monogram("JD")),
            UserProfile(2, "JaneS", ProfilePictureData.Monogram("JS")),
            UserProfile(3, "Frank", ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)),
            UserProfile(4, "Ginger", ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)),
            UserProfile(5, "Bobby", ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)),
            UserProfile(6, "Sammy", ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)),
            UserProfile(7, "Donal", ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)),
            UserProfile(
                8,
                "Z38KEZr6UbbGsITGtcAUZY5yUlu2",
                "Test Google",
                ProfilePictureData.Icon(IconType.ACCOUNT_CIRCLE)
            ),
        )
    }

    fun loadUser(user: UserProfile) {
        _selectedUserProfile.value = user
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
      fun getUserById(id: String): UserProfile{
        //get user by id from the list of all users
        return _allUsers.value.find { it.uid == id } ?: UserProfile()
     }

    fun getAvailableIcons(): List<IconType> {
        return IconType.toList()

    }

    fun getUserById(userId: Int) {
        _selectedUserProfile.value = _userProfiles.value.find { it.id == userId }
    }

    fun getUserByUid(uid: String): UserProfile? {
        return _allUsers.value.firstOrNull { it.uid == uid }
    }

    fun getNicknameByUid(userId: String): String? {
        return _userProfiles.value.firstOrNull { it.uid == userId }?.nickName
    }
    fun getNicknameById(userId: Int?): String? {
        return _userProfiles.value.firstOrNull { it.id == userId }?.nickName
    }

    fun updateUserProfile(updatedProfile: UserProfile) {
        _userProfiles.value =
            _userProfiles.value.map { if (it.id == updatedProfile.id) updatedProfile else it }
        _selectedUserProfile.value = updatedProfile
    }
}
