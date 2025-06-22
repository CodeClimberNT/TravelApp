package com.example.final_assignment_even_g28.ui.components.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.screens.Tag
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TravelProposalCard(
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    navActions: Navigation,
//TODO: enable to remove notification from explore tab
//    fromExplore: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
) {
    val isPast = tripVm.isTripInPast(travelProposal)
    val numApprovedParticipant = tripVm.getNumApprovedParticipants(travelProposal)
    val image = travelProposal.images.firstOrNull()
    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer
    val notifications by tripVm.notifications.collectAsState()
    val isTripNotified = notifications.any { it.tripId == travelProposal.id }

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = "card_${travelProposal.id}"),
                    animatedVisibilityScope = animatedContentScope
                )
                .fillMaxWidth()
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
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke((0.5).dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row {
                Image(
                    painter =
                        rememberAsyncImagePainter(
                            model = image,
                            error = painterResource(id = R.drawable.error_image)
                        ),
                    contentDescription = "Travel Proposal Image Preview",
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
                    color = textColor,
                    modifier = Modifier.padding(4.dp),
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${numApprovedParticipant}/${travelProposal.maxParticipant}",
                        color = textColor,
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
                    text = "${travelProposal.price.min} - ${travelProposal.price.max}€",
                    color = textColor,
                    modifier = Modifier.padding(
                        top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp
                    ),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = tripVm.showDatesInList(
                        travelProposal.tripStartDate.toDate(), travelProposal.tripEndDate.toDate()
                    ),
                    color = textColor,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 6.dp),
                )
            }
            Row(modifier = Modifier.padding(start = 10.dp)) {
                Tag(travelProposal.activities)
                if (isTripNotified && !fromExplore) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = "Notification Trip",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier
                                .size(34.dp)
                                .padding(end = 10.dp)
                                .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}
