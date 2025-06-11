package com.example.final_assignment_even_g28.shared.validation

import com.example.final_assignment_even_g28.data_class.TravelReview

data class ReviewError(
    val title: String = "",
    val images: String = "",
    val rating: String = "",
    val description: String = "",
    val timestamp: String = "",
) {
    val hasError: Boolean
        get() = listOf(title, images, rating, description, timestamp).any { it.isNotEmpty() }

    val toList: List<String>
        get() = asList()
}

fun ReviewError.asList(): List<String> = listOf(
    title,
    rating,
    description,
)

object ReviewValidator {
    fun validate(target: TravelReview): ReviewError {
        return ReviewError(
            title =
                when {
                    target.title.isBlank() -> {
                        "Title cannot be empty"
                    }

                    target.title.length < 3 -> {
                        "Title must be at least 3 characters long"
                    }

                    target.title.length > 30 -> {
                        "Title must be less than 30 characters long"
                    }

                    else -> ""
                },
            rating =
                if (target.rating !in 0f..5f)
                    "Rating must be between 0 and 5"
                else "",
            description = if (target.description.length > 500)
                "Description must be less than 500 characters long"
            else "",
        )
    }
}