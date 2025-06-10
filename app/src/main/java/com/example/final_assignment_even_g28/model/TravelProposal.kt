package com.example.final_assignment_even_g28.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable
import java.util.Date

data class ExperienceComposition(
    val adventure: Int = 0,
    val culture: Int = 0,
    val relax: Int = 0,
    val party: Int = 0
)

data class Price(
    val min: Int = 0, val max: Int = 0
)

// To avoid conflict with names in the Android framework, we used a different name
enum class ActivityTag(val value: String) {
    HIKING("Hiking"), NATURE("Nature"), MUSIC("Music"), PARTY("Party"), RELAX("Relax"), NIGHTLIFE("Nightlife"), TECHNO(
        "Techno"
    ),
}

data class ItineraryStop(
    val date: Timestamp = Timestamp.now(),
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
    val tripStartDate: Timestamp = Timestamp.now(),
    val tripEndDate: Timestamp = Timestamp.now(),
    val tripPlannerId: String = "",
    val activities: List<ActivityTag> = emptyList(),
    val experienceComposition: ExperienceComposition = ExperienceComposition(0, 0, 0, 0),
    val itinerary: List<ItineraryStop> = emptyList(),
    val maxParticipant: Int = 0,
    var participants: MutableList<Participant> = mutableListOf(),
) {
    constructor(
        tripPlannerId: String = ""
    ) : this(
        id = "",
        title = "",
        images = emptyList(),
        price = Price(0, 0),
        description = "",
        tripStartDate = Timestamp(Date(0)),
        tripEndDate = Timestamp(Date(0)),
        tripPlannerId = tripPlannerId,
        activities = emptyList(),
        experienceComposition = ExperienceComposition(0, 0, 0, 0),
        itinerary = emptyList(),
        maxParticipant = 0,
        participants = mutableListOf(),
    )
}

