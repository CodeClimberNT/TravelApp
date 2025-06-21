package com.example.final_assignment_even_g28.data_class

import com.example.final_assignment_even_g28.utils.tomorrow
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

data class ExperienceComposition(
    val adventure: Int = 0,
    val culture: Int = 0,
    val relax: Int = 0,
    val party: Int = 0
)

data class Price(
    val min: Int = 0, val max: Int = 0
)

data class ItineraryStop(
    val date: Timestamp = Timestamp.tomorrow(),
    val title: String = "",
    val description: String = "",
    val mandatory: Boolean = false,
)


@Serializable
enum class ParticipantStatus(val value: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected")
}

@Serializable
data class Participant(
    val id: String = "",
    val status: ParticipantStatus = ParticipantStatus.PENDING,
    val invitedGuests: List<String> = emptyList(),
)

data class ParticipantDetailed(
    val user: UserProfile = UserProfile(),
    val invitedGuests: List<String> = emptyList(),
    val status: ParticipantStatus = ParticipantStatus.PENDING,
)

data class Filters(
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val title: String = "Anywhere",
    val activities: List<ActivityTag> = emptyList(),
    val groupSize: Int? = null, //"Any"
    val minPrice: Int = 0,
    val maxPrice: Int = 900
)

data class TravelProposal(
    // Firebase id -> local id
    @get:Exclude var id: String = "",
    val title: String = "",
    val images: List<String> = emptyList(),
    // List of Uri
    @get:Exclude var tempImages: List<String> = listOf(),
    val price: Price = Price(0, 0),
    val description: String = "",
    val tripStartDate: Timestamp = Timestamp.tomorrow(),
    val tripEndDate: Timestamp = Timestamp.tomorrow(),
    val tripPlannerId: String = "",
    val activities: List<ActivityTag> = emptyList(),
    val experienceComposition: ExperienceComposition = ExperienceComposition(0, 0, 0, 0),
    val itinerary: List<ItineraryStop> = emptyList(),
    val maxParticipant: Int = 0,
    var participants: MutableList<Participant> = mutableListOf(),
)

