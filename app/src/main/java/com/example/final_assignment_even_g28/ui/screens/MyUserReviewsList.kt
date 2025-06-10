package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.final_assignment_even_g28.model.UserReview
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.RatingStar
import com.example.final_assignment_even_g28.ui.components.review.DisplayReviewImages
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

//OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUserReviewsList(
    viewModel: UserReviewViewModel,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState
) {

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Made to you", "Made by you")

    viewModel.getReviews()

    var reviewsMadeToMe = viewModel.othersReview
    var reviewsMadeByMe = viewModel.myReviews

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { CustomBottomBar(navActions, selectedItem = bottomBarItem) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Reviews",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            PrimaryTabRow(selectedTabIndex = tabIndex) {
                tabTitles.forEachIndexed { i, title ->
                    Tab(
                        selected = tabIndex == i,
                        onClick = { tabIndex = i },
                        text = { Text(title) },
                        icon = {
                            val icon = when (i) {
                                0 -> Icons.Outlined.Reviews
                                else -> Icons.Outlined.RateReview
                            }
                            Icon(icon, contentDescription = null)
                        }
                    )
                }
            }

            when (tabIndex) {
                0 -> ReviewsList(reviewsMadeToMe.value!!)
                else -> ReviewsList(reviewsMadeByMe.value!!, displayReviewed = true)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewsList(reviews: List<UserReview>, displayReviewed: Boolean = false) {
if (reviews.isNotEmpty()){
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (reviews.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Average Rating: ", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.weight(1f))
                RatingStar(
                    reviews.map { it.rating }.average().toFloat(),
                    maxRating = 5,
                    onStarClick = {},
                    isIndicator = false
                )
                Text(
                    text = "${" % .2f".format(reviews.map({ it.rating }).average())} / 5",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
        reviews.forEach { review ->
            UserReviewCard(review, displayReviewed)
        }
    }
    }
    else{
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "There are no Reviews to see",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserReviewCard(
    review: UserReview,
    displayReviewed: Boolean = false,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    var nickname = ""
    if (!displayReviewed) {
        if (review.reviewerName.isNotEmpty())
            nickname = review.reviewerName
        else
            nickname = userVm.getNicknameById(review.reviewerId) ?: "Unknown User"
    } else {
        if (review.reviewedName.isNotEmpty())
            nickname = review.reviewedName
        else
            nickname = userVm.getNicknameById(review.reviewedUserId) ?: "Unknown User"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            FlowRow {
                Text(
                    text = review.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .width(8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStar(review.rating, maxRating = 5, onStarClick = {}, isIndicator = false)
                    Text(
                        text = "${review.rating} / 5",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            DisplayReviewImages(review.images)
            Text(
                text = review.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}