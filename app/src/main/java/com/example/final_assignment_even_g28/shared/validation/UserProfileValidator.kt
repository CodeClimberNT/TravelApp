package com.example.final_assignment_even_g28.shared.validation

import com.example.final_assignment_even_g28.data_class.UserProfile

data class UserProfileError(
    val name: String = "",
    val surname: String = "",
    val typeOfExperiences: String = "",
    val mostDesiredDestination: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val pastExperiences: String = "",
)

fun UserProfileError.asList(): List<String> = listOf(
    name,
    surname,
    typeOfExperiences,
    mostDesiredDestination,
    phoneNumber,
    dateOfBirth,
    pastExperiences
)

class UserProfileValidator {
    fun validate(target: UserProfile): UserProfileError {
        return UserProfileError(
            name = if (target.name.isBlank()) {
                "Name cannot be empty"
            } else {
                ""
            },
            surname = if (target.surname.isBlank()) {
                "Surname cannot be empty"
            } else {
                ""
            },
            typeOfExperiences = if (target.typeOfExperiences.isEmpty() ||
                target.typeOfExperiences.filter {
                    it.isBlank()
                }.size == target.typeOfExperiences.size
            ) {
                "Type of experiences cannot be empty"
            } else {
                ""
            },
            mostDesiredDestination = if (target.mostDesiredDestination.isEmpty()) {
                "Most desired destination cannot be empty"
            } else {
                ""
            },
            phoneNumber = if (target.phoneNumber.isBlank()) {
                "Phone number cannot be empty"
            } else if (target.phoneNumber.matches(Regex("^[0-9]+$")).not()) {
                "Phone number must contain only digits"
            } else if (target.phoneNumber.length != 10) {
                "Phone number must be 10 digits long"
            } else {
                ""
            },
            pastExperiences = if (target.pastExperiences.isEmpty()) {
                "Past experiences cannot be empty"
            } else {
                ""
            },
        )
    }
}