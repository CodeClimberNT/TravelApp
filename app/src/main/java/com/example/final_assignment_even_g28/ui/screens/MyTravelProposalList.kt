package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Reviews
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.NeedToLogin
import com.example.final_assignment_even_g28.ui.components.card.TravelProposalCard
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MyTravelProposalList(
    tripVm: TravelProposalViewModel,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
) {
    val loggedUser by userVm.loggedUser.collectAsState()
    /* ------- tab state ------- */
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Current Proposal", "Past Proposal")

    /* ------- data streams ------- */
    val myProposals by tripVm.myTravelProposals.collectAsState(initial = emptyList())
    val pastProposals by tripVm.pastTravelProposals.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { CustomBottomBar(navActions, selectedItem = bottomBarItem) },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        if (loggedUser.uid.isEmpty()) {
            NeedToLogin(navAction = navActions)
        } else {
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
                            isPast = false,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
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
                            isPast = true,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun OwnedTravelProposalListColumn(
    travelProposalVM: TravelProposalViewModel,
    travelProposalList: List<TravelProposal>,
    modifier: Modifier = Modifier,
    navActions: Navigation,
    isPast: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(start = 15.dp, end = 15.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
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
            TravelProposalCard(
                travelProposalVM,
                travel,
                navActions,
                fromMyTrip = true,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
            )
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
