package com.example.final_assignment_even_g28.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_assignment_even_g28.model.User


class UserViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user


    fun login(user: User) {
        _user.value = user
    }

    fun logout() {
        _user.value = null
    }


}