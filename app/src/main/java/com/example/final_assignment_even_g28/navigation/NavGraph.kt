package com.example.final_assignment_even_g28.navigation

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.final_assignment_even_g28.NotificationSettingsScreen
import com.example.final_assignment_even_g28.ProfileScreen
import com.example.final_assignment_even_g28.ui.screens.CreateTravelProposalFirstScreen
import com.example.final_assignment_even_g28.ui.screens.CreateTravelProposalSecondScreen
import com.example.final_assignment_even_g28.ui.screens.EditUserProfileInfo
import com.example.final_assignment_even_g28.ui.screens.MyTravelProposalList
import com.example.final_assignment_even_g28.ui.screens.MyUserReviewsList
import com.example.final_assignment_even_g28.ui.screens.PastTravelProposalScreen
import com.example.final_assignment_even_g28.ui.screens.ShowUserProfileInfo
import com.example.final_assignment_even_g28.ui.screens.TravelProposalList
import com.example.final_assignment_even_g28.ui.screens.TravelProposalScreen
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel


@Composable
fun NavGraph(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    reviewVm: UserReviewViewModel = viewModel(factory = AppFactory),
    context: Context,
    navController: NavHostController = rememberNavController(),
    navActions: Navigation = remember(navController) {
        Navigation(navController)
    }
) {


    val bottomBarItemSelected = remember { mutableStateOf(BottomBarItem.Explore) }

    val snackbarHostState = remember { SnackbarHostState() }


    //used for notification
    LaunchedEffect(Unit) {
        tripVm.newTravelProposalNotification.collect { notification ->
            notification?.let {
                snackbarHostState.showSnackbar(
                    message = tripVm.getNotificationMessage(it.type, it.title, true),
                    actionLabel = "View",
                    duration = SnackbarDuration.Short
                ).let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        tripVm.handleNotificationNavigation(it, navActions)
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.TRAVEL_LIST_ROUTE
    ) {

        composable(Destinations.TRAVEL_LIST_ROUTE) {
            bottomBarItemSelected.value = BottomBarItem.Explore
            TravelProposalList(
                tripVm = tripVm,
                navActions = navActions,
                bottomBarItem = BottomBarItem.Explore,
                snackBarHostState = snackbarHostState,
            )
        }
        composable(Destinations.CREATE_NEW_TRAVEL_PROPOSAL_ROUTE) {
            CreateTravelProposalFirstScreen(
                tripVm = tripVm,
                navActions = navActions,

                )
        }
        composable(Destinations.SECOND_SCREEN_ROUTE) {
            CreateTravelProposalSecondScreen(
                tripVm = tripVm,
                navActions = navActions
            )
        }
        composable(Destinations.MY_TRAVEL_PROPOSAL_LIST_ROUTE) {
            bottomBarItemSelected.value = BottomBarItem.MyTrips
            MyTravelProposalList(
                tripVm = tripVm,
                navActions = navActions,
                bottomBarItem = BottomBarItem.MyTrips,
                snackBarHostState = snackbarHostState,
            )
        }

        composable(Destinations.USER_MAIN_PAGE_ROUTE) {
            ProfileScreen(
                viewModel = userVm,
                navActions = navActions,
                bottomBarItem = BottomBarItem.Profile,
                snackBarHostState = snackbarHostState
            )
        }

        composable(Destinations.USER_REVIEW_PAGE_ROUTE) {
            MyUserReviewsList(
                navActions = navActions,
                bottomBarItem = BottomBarItem.Profile,
                snackBarHostState = snackbarHostState,
            )
        }

        composable(Destinations.PROFILE_ROUTE) {
            bottomBarItemSelected.value = BottomBarItem.Profile
            ShowUserProfileInfo(
                viewModel = userVm,
                navActions = navActions,
                onEditClick = {
                    navActions.navigateToEditProfile()
                },
                bottomBarItem = BottomBarItem.Profile,
                snackBarHostState = snackbarHostState
            )
        }
        composable(Destinations.EDIT_PROFILE_ROUTE) {
            EditUserProfileInfo(
                onBackClick = {
                    userVm.handleBackNavigation(context)
                    navActions.navigateToUserMainPage()
                }
            )
        }

        composable(route = Destinations.SETTINGS_ROUTE) {
            NotificationSettingsScreen()
        }

        composable(
            route = Destinations.TRIP_INFO_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.MY_TRIP_TAB) {
                    type = NavType.StringType
                    defaultValue = DestinationsArgs.MY_TRIP_TAB // Valore di default
                },
                navArgument(DestinationsArgs.TRIP_ID_ARG) { type = NavType.StringType },
                navArgument(DestinationsArgs.SHOW_PARTICIPANTS) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),)
             { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(DestinationsArgs.TRIP_ID_ARG)
            val fromMyTripTab = backStackEntry.arguments?.getString(DestinationsArgs.MY_TRIP_TAB)

            val showParticipants = backStackEntry.arguments?.getBoolean(DestinationsArgs.SHOW_PARTICIPANTS) == true

            val selectedItem = if (fromMyTripTab?.contains(DestinationsArgs.MY_TRIP_TAB) == true) {
                BottomBarItem.MyTrips
            } else {
                BottomBarItem.Explore
            }

            Log.d("NavGraph", "TripInfo - tripId: $tripId, showParticipants: $showParticipants")

            TravelProposalScreen(
                tripVm = tripVm,
                navActions = navActions,
                bottomBarItem = selectedItem,
                tripId = tripId ?: "",
                snackBarHostState = snackbarHostState,
                showParticipantsDialog = showParticipants
            )
        }

        composable(
            route = Destinations.PAST_TRIP_INFO_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.MY_TRIP_TAB) {
                    type = NavType.StringType
                    defaultValue = DestinationsArgs.MY_TRIP_TAB // Valore di default
                },
                navArgument(DestinationsArgs.TRIP_ID_ARG) { type = NavType.StringType },
                navArgument(DestinationsArgs.SHOW_REVIEWS_TAB) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),

        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString(DestinationsArgs.TRIP_ID_ARG)
            val fromMyTripTab = backStackEntry.arguments?.getString(DestinationsArgs.MY_TRIP_TAB)

            val showReviewsTab =
                    backStackEntry.arguments?.getBoolean(DestinationsArgs.SHOW_REVIEWS_TAB) == true

            val selectedItem = if (fromMyTripTab?.contains(DestinationsArgs.MY_TRIP_TAB) == true) {
                BottomBarItem.MyTrips
            } else {
                BottomBarItem.Explore
            }

            Log.d("NavGraph", "PastTripInfo - tripId: $tripId, showReviewsTab: $showReviewsTab")

            PastTravelProposalScreen(
                tripVm = tripVm,
                navActions = navActions,
                bottomBarItem = selectedItem,
                tripId = tripId ?: "",
                userReviewVm = reviewVm,
                snackBarHostState = snackbarHostState,
                initialTabIndex = if (showReviewsTab) 1 else 0
            )
        }

    }
}

