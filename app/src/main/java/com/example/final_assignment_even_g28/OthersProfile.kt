package com.example.final_assignment_even_g28

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.screens.Tag
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.UNKNOWN_USER
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OtherProfileScreen(
    userUID: String,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState,
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val travelProposals by tripVm.allTravelProposals.collectAsState(emptyList())
    val tripPlanner by tripVm.currentTripPlanner.collectAsState()
    val travelProposal = travelProposals.firstOrNull() ?: TravelProposal()
    val fetchedOtherUser by userVm.getUserProfileByUID(userUID)
        .collectAsState(initial = UserProfile())
    val otherUser = fetchedOtherUser ?: UNKNOWN_USER

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { CustomBottomBar(navActions = navActions, selectedItem = bottomBarItem) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                ProfileColumn(otherUser)
                PastExperiencesHorizontal(travelProposals, tripPlanner)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                ProfileRow(otherUser)
                PastExperiencesColumn(travelProposal, tripPlanner)
            }
        }
    }
}

@Composable
fun ProfileColumn(user: UserProfile) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ProfilePicture(
                userProfile = user,
                isLandScape = false,
                isDashboard = true,
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "lvl. ${user.currentLevel}",
                modifier = Modifier.padding((4.dp)),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TextReview(user.rating.toString())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            InterestList(user)
        }
    }
}

@Composable
fun PastExperiencesHorizontal(travelProposals: List<TravelProposal>, tripPlanner: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 60.dp, end = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trips Taken", modifier = Modifier.padding(top = 3.dp, bottom = 20.dp),
            // .fillMaxWidth(),
            // .align(Alignment.Center),
            fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
        )
        LazyColumn(
            modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { items(3) { index -> PastExperienceBlock(travelProposals[index], tripPlanner) } }
    }
}

@Composable
fun ProfileRow(user: UserProfile) {
    Row {
        ImageColumn(user)
        NameColumn(user)
    }
}

@Composable
fun ImageColumn(user: UserProfile) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.account_image),
                contentDescription = null,
                modifier = Modifier.size(130.dp)
            )
        }
        Row {
            Text(
                text = "lvl. ${user.currentLevel}",
                modifier = Modifier.padding((4.dp)),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun NameColumn(user: UserProfile) {
    Column(verticalArrangement = Arrangement.Top) {
        Row {
            Text(
                text = "${user.name} ${user.surname}",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
        TextReview(score = user.rating.toString())
        InterestList(user)
    }
}

@Composable
fun TextReview(score: String) {
    Row {
        Text(
            text = score,
            modifier = Modifier.padding(start = 10.dp, top = 0.dp, end = 6.dp, bottom = 6.dp),
            fontSize = 16.sp
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = StarColor,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 0.dp)
        )
    }
}

@Composable
fun InterestList(user: UserProfile) {
    Row { Tag(listOf(ActivityTag.HIKING, ActivityTag.MUSIC, ActivityTag.RELAX)) }
}

@Composable
fun PastExperiencesColumn(trip: TravelProposal, tripPlanner: UserProfile) {
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(1) { index ->
                if (index == 0) {
                    Text(
                        text = "Trips Taken", modifier = Modifier.padding(
                            top = 20.dp, start = 4.dp, bottom = 20.dp, end = 20.dp
                        ), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
                    )
                }
                PastExperienceBlock(trip, tripPlanner)
            }
        }
    }
}

@Composable
fun PastExperienceBlock(tripTaken: TravelProposal, tripPlanner: UserProfile) {
    val image = tripTaken.images.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .shadow(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Image(
                painter = rememberAsyncImagePainter(
                    model = image,
                    error = painterResource(id = R.drawable.error_image)
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 180.dp, width = 0.dp),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tripTaken.title,
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tripPlanner.name} ${tripPlanner.surname}",
                    modifier = Modifier.padding(start = 26.dp),
                    fontWeight = FontWeight.Bold
                )
                // FIXME: is this icon correct for the past experience?
//                Icon(
//                    imageVector = Icons.Default.Groups,
//                    contentDescription = "",
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.padding(start = 4.dp)
//                )
            }
        }

        Row(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "${tripTaken.price.min} - ${tripTaken.price.max}",
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp),
                fontWeight = FontWeight.Bold,
            )
            val formatterProposal = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date =
                formatterProposal.format(tripTaken.tripStartDate.toDate()) + " - " + formatterProposal.format(
                    tripTaken.tripEndDate.toDate()
                )
            Text(
                text = date,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp),
            )
        }
        Row(modifier = Modifier.padding(start = 10.dp)) {
            Tag(tripTaken.activities)
        }
    }
}
