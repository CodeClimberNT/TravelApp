package com.example.final_assignment_even_g28.shared.validation

import com.google.firebase.Timestamp

data class FilterError(
    val minPrice: String = "",
    val maxPrice: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val dateRange: String = "",
) {
    val toList: List<String>
        get() = listOf(minPrice, maxPrice, fromDate, toDate, dateRange)

    val hasError: Boolean
        get() = this.toList.any { it.isNotEmpty() }

}

object FilterValidator {

    fun validateFilter(
        minPrice: Int,
        maxPrice: Int,
        tripStartDate: Timestamp?,
        tripEndDate: Timestamp?,
    ): FilterError {

        val errors = FilterError(
            minPrice = if (minPrice < 0) {
                "Min Price must be >= 0"
            } else "",
            maxPrice = if (maxPrice <= 0 || maxPrice < minPrice) {
                "Max Price must be > Min Price"
            } else "",
            fromDate = when {
                tripStartDate != null && tripStartDate <= Timestamp.now() -> {
                    "Select a valid FROM Date"
                }

                else -> ""

            },
            toDate = when {
                tripEndDate != null && tripEndDate < (tripStartDate ?: Timestamp.now()) -> {
                    "Select a valid TO Date"
                }

                else -> ""

            },
            dateRange = when {
                (tripEndDate != null && tripStartDate != null && tripEndDate < tripStartDate) -> {
                    "Select a valid range of dates"
                }

                else -> ""
            },
        )

        return errors
    }
}
