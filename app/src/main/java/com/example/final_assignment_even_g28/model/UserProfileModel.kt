package com.example.final_assignment_even_g28.model

import android.util.Log
import com.example.final_assignment_even_g28.data.Collections
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.UserToSave
import com.example.final_assignment_even_g28.ui.components.user_profile.IconType
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePictureData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    init {
        // Initialize with mock data
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
                                if (userProfile!= null){
                                    loadUser(userProfile)
                                }
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

    //TODO() Remove the user id
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
                                    pastExperiences = userToSign.pastExperiences,
                                    bio = userToSign.bio,
                                    badge = userToSign.badge,
                                    currentLevel = 1,
                                    rating = 0.0f,
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

    fun editProfile(userToEdit: UserToSave){
        Collections.users.document(userToEdit.uid)
            .set(userToEdit).addOnSuccessListener {
                Log.d("Edit User", "Edited User with UID: ${userToEdit.uid}")
                loadUserByUID(userToEdit.uid)
            }.addOnFailureListener { e ->
                Log.e("Edit User", "Error editing user: $e")
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

    fun getUserByUId(userId: String) {
        _selectedUserProfile.value = _userProfiles.value.find { it.uid == userId }
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
}
