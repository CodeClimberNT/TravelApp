package com.example.final_assignment_even_g28.ui.components.review

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.final_assignment_even_g28.shared.EditableTextField
import com.example.final_assignment_even_g28.ui.components.RatingStar
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel


@Composable
fun ReviewDialog(
    vm: TravelProposalViewModel,
    onDismissRequest: () -> Unit
) {
    val review = vm.tempReview
    val reviewErrors = vm.reviewErrors
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            vm.addReviewImageFromGallery(uri)
        }
    }

    val ctx = LocalContext.current


    Dialog(onDismissRequest = {
        // Don't exit the dialog when clicking outside
//        onDismissRequest()
    }) {
        Card(
            modifier = Modifier
                .height(550.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Review your Trip!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                EditableTextField(
                    review.title,
                    onValueChange = { vm.updateReviewTitle(it) },
                    label = "Brief Review",
                    isError = !reviewErrors.title.isBlank(),
                    errorMessage = reviewErrors.title,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingStar(
                        review.rating,
                        maxRating = 5,
                        onStarClick = { vm.updateReviewRating(it.toFloat()) },
                        isIndicator = false
                    )
                    Text(review.rating.toString())
                }
                HorizontalDivider(modifier = Modifier.padding(8.dp))
                PreviewReviewImages(
                    images = review.tempImages,
                    galleryLauncher = galleryLauncher,
                    vm = vm
                )
                HorizontalDivider(modifier = Modifier.padding(8.dp))

                EditableTextField(
                    review.description,
                    onValueChange = { vm.updateReviewDescription(it) },
                    label = "Write about your experience and suggestions",
                    isError = !reviewErrors.description.isBlank(),
                    errorMessage = reviewErrors.description,
                    isSingleLine = false,
                    textFieldModifier = Modifier.height(130.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            vm.clearReview()
                            onDismissRequest()
                        }
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp),
                        onClick = {
                            if (vm.submitReview(ctx)) {
                                onDismissRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Submit Review")
                    }
                }
            }
        }
    }
}
