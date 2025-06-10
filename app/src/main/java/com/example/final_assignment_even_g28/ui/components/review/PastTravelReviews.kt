package com.example.final_assignment_even_g28.ui.components.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.final_assignment_even_g28.model.TravelReview
import com.example.final_assignment_even_g28.ui.components.RatingStar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PastTravelReviews(reviews: List<TravelReview>) {
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
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
                    text = "${" % .2f".format(reviews.map { it.rating }.average())} / 5",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
        reviews.forEach { review ->
            ReviewCard(review)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewCard(
    review: TravelReview,
) {
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
                    text = if (review.reviewerName.isNotBlank()) review.reviewerName else "Unknown User",
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