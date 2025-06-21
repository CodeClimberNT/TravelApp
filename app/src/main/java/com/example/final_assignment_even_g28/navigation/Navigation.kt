package com.example.final_assignment_even_g28.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.EXPLORE_TAB
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.MY_TRIP_TAB
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.SHOW_PARTICIPANTS
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.SHOW_REVIEWS_TAB
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.TRIP_ID_ARG
import com.example.final_assignment_even_g28.navigation.DestinationsArgs.USER_UID
import com.example.final_assignment_even_g28.navigation.Screens.CREATE_NEW_TRAVEL_PROPOSAL
import com.example.final_assignment_even_g28.navigation.Screens.MY_TRAVEL_PROPOSAL_LIST
import com.example.final_assignment_even_g28.navigation.Screens.PAST_TRIP_INFO
import com.example.final_assignment_even_g28.navigation.Screens.SECOND_SCREEN
import com.example.final_assignment_even_g28.navigation.Screens.TRAVEL_LIST
import com.example.final_assignment_even_g28.navigation.Screens.TRIP_INFO


private object Screens {
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "editProfile"
    const val TRAVEL_LIST = "TravelList"
    const val MY_TRAVEL_PROPOSAL_LIST = "MyTravelProposalList"
    const val CREATE_NEW_TRAVEL_PROPOSAL = "FIRST_SCREEN"

    //const val FIRST_SCREEN = "FirstScreen"
    const val SECOND_SCREEN = "SecondScreen"
    const val TRIP_INFO = "TripInfo"
    const val PAST_TRIP_INFO = "PastTripInfo"

    const val OTHER_PROFILE = "OtherProfile"

    const val USER_MAIN_PAGE = "userMainPage"

    const val USER_REVIEW_PAGE = "userReviewPage"

    const val SIGN_IN = "SignInScreen"
    const val REGISTRATION_FORM = "RegistrationForm"
    const val SETTINGS = "Settings"
}

object DestinationsArgs {
    //const val USER_MESSAGE_ARG = "userMessage"
    const val TRIP_ID_ARG = "taskId"

    //TODO: maybe a better name?
    //OWN_TRAVEL: travel clicked from the explore tab
    //NOT_OWN: travel clicked from the my trips tab
    const val MY_TRIP_TAB = "myTripTab"
    const val EXPLORE_TAB = "exploreTab"

    const val USER_UID = "name"

    const val SHOW_PARTICIPANTS = "showParticipants"
    const val SHOW_REVIEWS_TAB = "showReviewsTab"

}

object Destinations {
    const val PROFILE_ROUTE = Screens.PROFILE
    const val EDIT_PROFILE_ROUTE = Screens.EDIT_PROFILE
    const val TRAVEL_LIST_ROUTE = TRAVEL_LIST

    //const val FIRST_SCREEN_ROUTE = FIRST_SCREEN
    const val SECOND_SCREEN_ROUTE = SECOND_SCREEN
    const val MY_TRAVEL_PROPOSAL_LIST_ROUTE = MY_TRAVEL_PROPOSAL_LIST
    const val CREATE_NEW_TRAVEL_PROPOSAL_ROUTE = CREATE_NEW_TRAVEL_PROPOSAL
    //const val TRIP_INFO_ROUTE = Screens.TRIP_INFO

    const val TRIP_INFO_ROUTE = "$TRIP_INFO/{$MY_TRIP_TAB}/{$TRIP_ID_ARG}?$SHOW_PARTICIPANTS={$SHOW_PARTICIPANTS}"
    const val PAST_TRIP_INFO_ROUTE = "$PAST_TRIP_INFO/{$MY_TRIP_TAB}/{$TRIP_ID_ARG}?$SHOW_REVIEWS_TAB={$SHOW_REVIEWS_TAB}"

    const val OTHER_PROFILE_ROUTE = "${Screens.OTHER_PROFILE}/{$MY_TRIP_TAB}/{$USER_UID}"

    const val USER_MAIN_PAGE_ROUTE = Screens.USER_MAIN_PAGE
    const val USER_REVIEW_PAGE_ROUTE = Screens.USER_REVIEW_PAGE

    const val SIGN_IN_ROUTE = Screens.SIGN_IN
    const val REGISTRATION_FORM_ROUTE = Screens.REGISTRATION_FORM

    const val SETTINGS_ROUTE = Screens.SETTINGS
}

//TODO: check if it is possible to use singleton to not move the instance around
class Navigation(
    private val navController: NavHostController,
) {
    fun navigateToTravelList() {
        navController.navigate(Destinations.TRAVEL_LIST_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToMyTravelProposalList() {
        navController.navigate(Destinations.MY_TRAVEL_PROPOSAL_LIST_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToCreateNewTravelProposal() {
        navController.navigate(Destinations.CREATE_NEW_TRAVEL_PROPOSAL_ROUTE) {
            launchSingleTop = true
        }

    }

    fun navigateToSecondScreen() {
        navController.navigate(Destinations.SECOND_SCREEN_ROUTE) {
            launchSingleTop = true
        }
    }

    fun back() {
        navController.popBackStack()
    }

    fun navigateToSignIn() {
        navController.navigate(Destinations.SIGN_IN_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToRegistrationForm() {
        navController.navigate(Destinations.REGISTRATION_FORM_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToUserMainPage() {
        navController.navigate(Destinations.USER_MAIN_PAGE_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToProfile() {
        navController.navigate(Destinations.PROFILE_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToEditProfile() {
        navController.navigate(Destinations.EDIT_PROFILE_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToUserReview() {
        navController.navigate(Destinations.USER_REVIEW_PAGE_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToTripInfo(tripId: String, fromMyTripTab: Boolean, showParticipants: Boolean = false) {
        Log.d(
            "Navigation",
            "Navigating to trip info with tripId: $tripId, fromMyTripTab: $fromMyTripTab, showParticipants: $showParticipants"
        )

        navController.navigate(
            Destinations.TRIP_INFO_ROUTE
                .replace("{$MY_TRIP_TAB}", if (fromMyTripTab) MY_TRIP_TAB else EXPLORE_TAB)
                .replace("{$TRIP_ID_ARG}", tripId)
                .replace("{$SHOW_PARTICIPANTS}", showParticipants.toString())
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToOtherProfile(userUID: String, fromMyTripTab: Boolean) {
        var temporary = Destinations.OTHER_PROFILE_ROUTE.replace(
            "{$MY_TRIP_TAB}",
            if (fromMyTripTab) MY_TRIP_TAB else EXPLORE_TAB
        ).replace("{$USER_UID}", userUID)
        navController.navigate(temporary) {
            launchSingleTop = true
        }
    }

    fun navigateToPastTravelProposalInfo(tripId: String, fromMyTripTab: Boolean, showReviewsTab: Boolean = false) {
        navController.navigate(
            Destinations.PAST_TRIP_INFO_ROUTE
                .replace("{$MY_TRIP_TAB}", if (fromMyTripTab) MY_TRIP_TAB else EXPLORE_TAB)
                .replace("{$TRIP_ID_ARG}", tripId)
                .replace("{$SHOW_REVIEWS_TAB}", showReviewsTab.toString())
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToSettings() {
        navController.navigate(Destinations.SETTINGS_ROUTE) {
            launchSingleTop = true
        }
    }

}