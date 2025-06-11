package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.NeedToLogin
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTravelProposalList(
    tripVm: TravelProposalViewModel,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState
) {
    val loggedUser by userVm.loggedUser.collectAsState()
    /* ------- tab state ------- */
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Current Proposal", "Past Proposal")

    /* ------- data streams ------- */
    val myProposals by tripVm.myTravelProposals.collectAsState()
    val pastProposals by tripVm.pastTravelProposals.collectAsState()

    Scaffold(
        bottomBar = { CustomBottomBar(navActions, selectedItem = bottomBarItem) },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        if (loggedUser.uid.isEmpty()) {
            NeedToLogin(navAction = navActions)
        }else{
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                /* ----- headline & “plan trip” button ----- */
                HeaderRow(
                    onPlanNew = {
                        tripVm.clickPlanNewOwnTrip(loggedUser.uid)
                        navActions.navigateToCreateNewTravelProposal()
                    }
                )
                /* ----- tabs ----- */
                PrimaryTabRow(selectedTabIndex = tabIndex) {
                    tabTitles.forEachIndexed { i, title ->
                        Tab(
                            selected = tabIndex == i,
                            onClick = { tabIndex = i },
                            text = { Text(title) },
                            icon = {
                                val icon =
                                    when (i) {
                                        0 -> Icons.Default.Bookmark
                                        else ->
                                            Icons.Default
                                                .Reviews
                                    }
                                Icon(icon, contentDescription = null)
                            }
                        )
                    }
                }
                /* ----- list content ----- */
                when (tabIndex) {
                    0 -> {
                        OwnedTravelProposalListColumn(
                            tripVm,
                            myProposals,
                            Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 15.dp),
                            navActions,
                            isPast = false
                        )
                    }
                    1 -> {
                        OwnedTravelProposalListColumn(
                            tripVm,
                            pastProposals,
                            Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 15.dp),
                            navActions,
                            isPast = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OwnedTravelProposalListColumn(
    travelProposalVM: TravelProposalViewModel,
    travelProposalList: List<TravelProposal>,
    modifier: Modifier = Modifier,
    navActions: Navigation,
    isPast: Boolean
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(start = 15.dp, end = 15.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        if (travelProposalList.isEmpty()) {
            Text(
                text = if (isPast) {
                    "You didn't participate in any trips yet!"
                } else {
                    "You have no trip planned, try creating a new one!"
                },
                fontSize = 25.sp,
                lineHeight = 30.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
        travelProposalList.forEach { travel ->
            OwnedTravelProposalBlock(travelProposalVM, travel, navActions, isPast)
        }
    }
}

@Composable
fun OwnedTravelProposalBlock(
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    navActions: Navigation,
    isPast: Boolean
) {
    val numApprovedParticipant = tripVm.getNumApprovedParticipants(travelProposal)
    val notifications by tripVm.notifications.collectAsState()
//    val currentUserId = tripVm.getCurrentUserUId()
    val isTripNotified =
        notifications.any {
            it.tripId == travelProposal.id
//                    && it["read"].any {
//                it.contains(
//                    currentUserId
//                )
//            }
        }

    Column(
        modifier =
            Modifier
                .shadow(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    if (!isPast)
                        navActions.navigateToTripInfo(
                            travelProposal.id,
                            true
                        )
                    else
                        navActions.navigateToPastTravelProposalInfo(
                            travelProposal.id,
                            true
                        )
                },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            val image = travelProposal.images.firstOrNull()
            Image(
                painter =
                    rememberAsyncImagePainter(
                        model = image,
                        error = painterResource(id = R.drawable.error_image)
                    ),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .size(
                            height = 180.dp,
                            width = 0.dp
                        ),
                contentScale = ContentScale.Crop
            )


        }

        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = travelProposal.title,
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        "${numApprovedParticipant}/${travelProposal.maxParticipant}",
                    modifier = Modifier.padding(end = 4.dp),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }

        Row(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text =
                    "${travelProposal.price.min} - ${travelProposal.price.max}€",
                modifier =
                    Modifier.padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 6.dp,
                        end = 6.dp
                    ),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                    tripVm.showDatesInList(
                        travelProposal.tripStartDate.toDate(),
                        travelProposal.tripEndDate.toDate()
                    ),
                color = MaterialTheme.colorScheme.secondary,
                modifier =
                    Modifier.padding(top = 4.dp, bottom = 4.dp, start = 6.dp),
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Tag(travelProposal.activities)

            if (isTripNotified) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(34.dp)
                            .padding(end = 10.dp)
                            .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun HeaderRow(onPlanNew: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Your Trips", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onPlanNew, shape = RoundedCornerShape(10.dp)) {
            Text("Plan a new trip")
        }
    }
}
