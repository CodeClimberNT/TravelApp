package com.example.final_assignment_even_g28.shared.validation

import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.ItineraryStop
import com.example.final_assignment_even_g28.data_class.Price
import com.google.firebase.Timestamp
import java.util.Date


data class ItineraryStopError(
    val title: String = "",
    val date: String = "",
    val description: String = "",
    val mandatory: String = ""
) {
    val hasError: Boolean
        get() = toList.any { it.isNotEmpty() }

    val toList: List<String>
        get() = listOf(title, date, description, mandatory)
}

data class TravelProposalFirstScreenError(
    val title: String = "",
    val maxParticipant: String = "",
    val description: String = "",
    val activities: String = "",
    val price: String = "",
    val tripStartDate: String = "",
    val tripEndDate: String = "",
    val itinerary: String = "",
    val tripDescription: String = "",
    val tripImages: String = "",
    val itineraryErrors: Map<Int, ItineraryStopError> = emptyMap()
) {
    val hasError: Boolean
        get() = toList.any { it.isNotEmpty() }

    val toList: List<String>
        get() = listOf(
            title,
            maxParticipant,
            description,
            activities,
            price,
            tripStartDate,
            tripEndDate,
            itinerary,
            tripDescription,
            tripImages
        ) +
                itineraryErrors.values.flatMap { it.toList }
}

data class TravelProposalSecondScreenError(
    val title: String = "",
    val activities: String = "",
)

object TravelProposalValidator {

    fun validateFirstScreen(
        title: String,
        maxParticipant: Int,
        price: Price,
        tripStartDate: Timestamp,
        tripEndDate: Timestamp,
        itinerary: List<ItineraryStop>,
        tripDescription: String,
        numTripImages: Int
    ): Pair<TravelProposalFirstScreenError, Boolean> {
        var isValid = true
        val titleValidation = validateTitle(title)

        val (itineraryErrors, itineraryIsValid) = validateItinerary(
            itinerary,
            tripStartDate,
            tripEndDate
        )

        val errors = TravelProposalFirstScreenError(
            title = if (titleValidation.isNotBlank()) {
                isValid = false
                titleValidation
            } else "",
            maxParticipant = if (maxParticipant <= 0) {
                isValid = false
                "The group size cannot be 0"
            } else "",
            price = when {
                price.min <= 0 -> {
                    isValid = false
                    "The price cannot be 0"
                }

                price.min > price.max -> {
                    isValid = false
                    "The min price cannot be greater than the max price"
                }

                else -> ""

            },
            tripStartDate = when {
                tripStartDate == Timestamp(Date(0L)) -> {
                    isValid = false
                    "The start date cannot be empty"
                }

                tripStartDate < Timestamp.now() -> {
                    isValid = false
                    "The start date cannot be in the past"
                }

                else -> ""
            },
            tripEndDate = when {
                tripEndDate == Timestamp(Date(0L)) -> {
                    isValid = false
                    "The end date cannot be empty"
                }

                tripEndDate < tripStartDate -> {
                    isValid = false
                    "The end date cannot be before the start date"
                }

                else -> ""
            },
            tripDescription = if (tripDescription.isBlank()) {
                isValid = false
                "The description cannot be empty"
            } else "",
            itinerary = when {
                itinerary.isEmpty() -> {
                    isValid = false
                    "The itinerary must contain at least one stop"
                }

                !itineraryIsValid -> {
                    isValid = false
                    "The itinerary contains errors"
                }

                else -> ""
            },
            tripImages = if (numTripImages == 0) {
                isValid = false
                "At least one image is required"
            } else "",
            itineraryErrors = itineraryErrors,
        )

        return Pair(errors, isValid)
    }

    private fun validateItinerary(
        itinerary: List<ItineraryStop>,
        tripStartDate: Timestamp,
        tripEndDate: Timestamp
    ): Pair<Map<Int, ItineraryStopError>, Boolean> {
        val errors = mutableMapOf<Int, ItineraryStopError>()
        var isValid = true

        itinerary.forEachIndexed { index, stop ->
            val stopErrors = ItineraryStopError(
                title = when {
                    stop.title.isBlank() -> {
                        isValid = false
                        "Title cannot be empty"
                    }

                    stop.title.length < 3 -> {
                        isValid = false
                        "Title must be at least 3 characters long"
                    }

                    stop.title.length > 35 -> {
                        isValid = false
                        "Title must be less than 35 characters long"
                    }

                    else -> ""
                },
                date = when {
                    stop.date == Timestamp(Date(0L)) -> {
                        isValid = false
                        "Date cannot be empty"
                    }

                    stop.date <= Timestamp.now() -> {
                        isValid = false
                        "Date cannot be in the past"
                    }

                    stop.date < tripStartDate -> {
                        isValid = false
                        "Date cannot be before the trip started"
                    }

                    stop.date > tripEndDate -> {
                        isValid = false
                        "Date cannot be after the trip ended"
                    }

                    index < itinerary.size - 1 && stop.date > itinerary[index + 1].date -> {
                        isValid = false
                        "Itinerary stops must be in chronological order"
                    }

                    else -> ""
                },
                description = if (stop.description.isBlank()) {
                    isValid = false
                    "Description cannot be empty"
                } else "",
            )

            if (stopErrors.date.isNotBlank() && stopErrors.date.contains("chronological")) {
                errors[index + 1] = stopErrors
            } else if (stopErrors.title.isNotBlank() || stopErrors.date.isNotBlank() || stopErrors.description.isNotBlank() || stopErrors.mandatory.isNotBlank()) {
                errors[index] = stopErrors
            }
        }

        return Pair(errors, isValid)
    }

    fun validateSecondScreen(
        title: String, activities: List<ActivityTag>
    ): Pair<TravelProposalSecondScreenError, Boolean> {
        var isValid = true

        val titleValidation = validateTitle(title)

        val errors = TravelProposalSecondScreenError(
            title = if (titleValidation.isNotBlank()) {
                isValid = false
                titleValidation
            } else "",
            activities = when {
                activities.size <= 1 -> {
                    isValid = false
                    "Select at least two activities"
                }

                else -> ""
            },
        )

        return Pair(errors, isValid)
    }

    private fun validateTitle(title: String): String {
        return when {
            title.isBlank() -> "The title cannot be empty"
            title.length > 30 -> "The title cannot be longer than 30 characters"
            title.length < 5 -> "The title cannot be shorter than 5 characters"
            else -> ""
        }
    }
}
