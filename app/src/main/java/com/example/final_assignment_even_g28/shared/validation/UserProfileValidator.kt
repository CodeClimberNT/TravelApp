package com.example.final_assignment_even_g28.shared.validation

import com.example.final_assignment_even_g28.model.UserProfile

data class UserProfileError(
    val fullName: String = "",
    val nickName: String = "",
    val typeOfExperiences: String = "",
    val mostDesiredDestination: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val pastExperiences: String = "",
)

fun UserProfileError.asList(): List<String> = listOf(
    fullName,
    nickName,
    typeOfExperiences,
    mostDesiredDestination,
    phoneNumber,
    dateOfBirth,
    pastExperiences
)

class UserProfileValidator {
    fun validate(target: UserProfile): UserProfileError {
        return UserProfileError(
            fullName = if (target.name.isBlank()) {
                "Name cannot be empty"
            } else {
                ""
            },
            nickName = if (target.surname.isBlank()) {
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
            /*
            dateOfBirth = if (target.dateOfBirth.isBlank()) {
                "Date of birth cannot be empty"
            } else if (target.dateOfBirth.matches(Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d\\d$"))
                    .not()
            ) {
                "Date of birth must be in the format dd/mm/yyyy"
            } else {
                ""
            },*/
            pastExperiences = if (target.pastExperiences.isEmpty()) {
                "Past experiences cannot be empty"
            } else {
                ""
            },
        )
    }
}