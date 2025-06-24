package com.example.final_assignment_even_g28.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.Planner
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.NeedToLogin
import com.example.final_assignment_even_g28.ui.components.review.PastTravelReviews
import com.example.final_assignment_even_g28.ui.components.review.ReviewDialog
import com.example.final_assignment_even_g28.ui.components.review.UserReviewDialog
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PastTravelProposalScreen(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    tripId: String,
    userProfileViewModel: UserProfileViewModel = viewModel(factory = AppFactory),
    snackBarHostState: SnackbarHostState,
    initialTabIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope
) {
    tripVm.clickTripInfo(tripId, isPast = true)
    val travelProposal by tripVm.travelProposal.collectAsState()
    val tripPlanner by tripVm.currentTripPlanner.collectAsState()
    val participants by tripVm.currentParticipants.collectAsState()
    val reviews by tripVm.currentReviews.collectAsState()
    val loggedUser by userProfileViewModel.loggedUser.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showReviewDialog by remember { mutableStateOf(false) }
    var showUserReviewDialog by remember { mutableStateOf(false) }

    var tabIndex by remember { mutableIntStateOf(initialTabIndex) }

    LaunchedEffect(initialTabIndex) {
        if (initialTabIndex != 0) {
            Log.d("PastTravelProposalScreen", "Setting initial tab to: $initialTabIndex")
            tabIndex = initialTabIndex
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            CombinedPastBottomBar(
                price = "${travelProposal.price.min} - ${travelProposal.price.max}",
                navActions,
                bottomBarItem,
                onUserReviewButtonClick = { showUserReviewDialog = true },
                onReviewButtonClick = { showReviewDialog = true })
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        if (loggedUser.uid.isEmpty()) {
            //need to Login
            NeedToLogin(navAction = navActions)
        } else {
            Box(Modifier.fillMaxSize()) {
                with(sharedTransitionScope) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .sharedElement(
                                rememberSharedContentState(key = "card_$tripId"),
                                animatedVisibilityScope = animatedContentScope
                            )
                    ) {
                        ImageCarousel(travelProposal.images)
                        Spacer(modifier = Modifier.height(16.dp))
                        HeroSection(
                            title = travelProposal.title,
                            duration = tripVm.showDatesInTripInfo(
                                travelProposal.tripStartDate.toDate(),
                                travelProposal.tripEndDate.toDate()
                            ),
                            tags = travelProposal.activities.map { it.value },
                            travelProposalVM = tripVm,
                            navActions = navActions,
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        InfoReviewTab(tabIndex, reviews.size, onTabSelected = { tabIndex = it })
                        when (tabIndex) {
                            0 -> {
                                TripOverview(tripPlanner, tripVm)
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                ActivitiesPercentages(
                                    travelProposal.experienceComposition,
                                    isLandscape
                                )
                                TripDescription(travelProposal.description)
                                if (isLandscape) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.secondary
                                            )
                                            .padding(
                                                horizontal = 16.dp
                                            ), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        ) {
                                            ItinerarySection(
                                                travelProposal.itinerary
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(
                                                    start = 8.dp
                                                )
                                        ) {
                                            TripMap(
                                                travelProposal.itinerary,
                                                travelProposal.title
                                            )
                                        }
                                    }
                                } else {
                                    ItinerarySection(travelProposal.itinerary)
                                    TripMap(travelProposal.itinerary, travelProposal.title)
                                }
                            }

                            1 -> {
                                PastTravelReviews(reviews)
                            }
                        }
                    }

                }
            }
        }

        if (showReviewDialog) {
            ReviewDialog(
                tripVm, onDismissRequest = {
                    showReviewDialog = false
                    Log.d(
                        "PastTravelProposalScreen",
                        "Trip Info On Dismiss Request: ${tripVm.travelProposal}"
                    )
                })
        }

        if (showUserReviewDialog) {
            UserReviewDialog(
                participants = participants,
                navActions = navActions,
                bottomBarItem = bottomBarItem
            ) { showUserReviewDialog = false }
        }
    }
}

@Composable
fun TripOverview(tripPlanner: Planner, vm: TravelProposalViewModel) {
    val configuration = LocalConfiguration.current
    val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Trip Planner",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            // User Avatar (Placeholder) -> will be updated with the tripPlanner.avatar
            ProfilePicture(tripPlanner, isLandScape = isLandScape, isDashboard = true)

            Spacer(modifier = Modifier.width(12.dp))

            // Name and Rating
            Column {
                Text(text = tripPlanner.name, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = StarColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = tripPlanner.rating.toString(), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!vm.isMyTrip()) {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .width(50.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp),
                    onClick = {}, // will be updated with the tripPlanner.contact
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Message,
                        contentDescription = "Contact",
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(40.dp)
                    )
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InfoReviewTab(
    state: Int,
    numReviews: Int,
    onTabSelected: (Int) -> Unit,
) {
    val iconsWithDescription = listOf<Pair<ImageVector, String>>(
        Icons.Default.Info to "Details", Icons.Default.Reviews to "Reviews (${numReviews})"
    )
    PrimaryTabRow(selectedTabIndex = state) {
        iconsWithDescription.forEachIndexed { index, (icon, title) ->
            Tab(
                selected = state == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = title) },
                icon = { Icon(icon, title) }
            )
        }
    }
}

@Composable
fun CombinedPastBottomBar(
    price: String,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    onUserReviewButtonClick: () -> Unit,
    onReviewButtonClick: () -> Unit = {}
) {
    Column {
        PastTravelActionBar(
            price, onUserReviewButtonClick, onReviewButtonClick
        )
        CustomBottomBar(navActions, selectedItem = bottomBarItem)
    }
}

@Composable
fun PastTravelActionBar(
    price: String,
    onUserReviewButtonClick: () -> Unit,
    onReviewButtonClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$price €",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .height(40.dp)
                        .width(50.dp)
                        .shadow(
                            4.dp, RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = onUserReviewButtonClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PeopleOutline,
                        contentDescription = "participants",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .shadow(
                            4.dp, RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = onReviewButtonClick
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reviews,
                            contentDescription = "review",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Write Review")
                    }
                }

            }
        }
    }
}
