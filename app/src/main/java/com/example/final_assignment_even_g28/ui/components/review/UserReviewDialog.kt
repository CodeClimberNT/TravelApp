package com.example.final_assignment_even_g28.ui.components.review

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.ParticipantDetailed
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.data_class.UserReview
import com.example.final_assignment_even_g28.ui.components.RatingStar
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.getNameFromFullName
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.example.final_assignment_even_g28.viewmodel.UserReviewViewModel
import com.google.firebase.Timestamp


@Composable
fun UserReviewDialog(userReviewVm: UserReviewViewModel, participants: List<ParticipantDetailed>, travelProposalViewModel: TravelProposalViewModel = viewModel(factory = AppFactory), onDismiss: () -> Unit) {

    Log.d("REVIEW DIALOG","PARTICIPANTS: $participants")
    Dialog(onDismissRequest = { }) {

        val scrollState = rememberScrollState()

        Card(
            modifier = Modifier
                .height(450.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Participants",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, bottom = 6.dp)
                    )
                    Text(
                        text = "List of yout trip companions",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                ) {
                    for (participant in participants){
                        ProfileRow(
                          participant.user,
                            userReviewVm
                        )
                    }


                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileRow(user: UserProfile, userReviewVm: UserReviewViewModel) {

    var participant by remember { mutableStateOf(UserProfile("", 0.0f, "")) }
    var showReview by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }

        Column {
            Text(
                text = getNameFromFullName(user.name),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { }
            )
            Text(
                text = user.bio,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                modifier = Modifier
                    .size(height = 40.dp, width = 130.dp)
                    .padding(start = 16.dp),
                onClick = {
                    participant = user
                    showReview = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Review")
            }
        }
    }
    HorizontalDivider(Modifier.padding(horizontal = 16.dp))

    if (showReview) {
        SingleUserReviewCard(participant, userReviewVm) { newState -> showReview = newState }
    }
}

@Composable
fun SingleUserReviewCard(user: UserProfile, userReviewVm: UserReviewViewModel, userVM: UserProfileViewModel = viewModel(factory = AppFactory), onDismiss: (Boolean) -> Unit) {
    val textState = remember { mutableStateOf("") }
    val textTitle = remember { mutableStateOf("") }
    var reviewValue by remember { mutableFloatStateOf(0.0f) }

    val ctx = LocalContext.current

    Dialog(onDismissRequest = { onDismiss(false) }) {
        Card(
            modifier = Modifier
                .height(500.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row {
                    Text(
                        text = "Review of the traveler",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                OutlinedTextField(
                    value = textTitle.value,
                    onValueChange = { newText -> textTitle.value = newText },
                    label = { Text("Title Review") },
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier.padding(16.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                ) {
                    RatingStar(reviewValue, 5, { reviewValue = it.toFloat() })
                }

                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { newText -> textState.value = newText },
                    label = { Text("Insert your Review") },
                    modifier = Modifier
                        .padding(16.dp)
                        .height(150.dp),
                    maxLines = 5,
                    singleLine = false
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = getNameFromFullName(user.fullName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = { onDismiss(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = {
                                    userReviewVm.writeReview(
                                        review = UserReview(
                                        reviewedUserUID = user.uid,
                                        reviewerUID = userVM.loggedUser.value.uid,
                                        title = textTitle.value,
                                        rating = reviewValue,
                                        description = textState.value,
                                        timestamp = Timestamp.now(),
                                        )
                                    )
                                    userVM.gainExp(5, ctx)
                                    onDismiss(false)
                                  },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }

}