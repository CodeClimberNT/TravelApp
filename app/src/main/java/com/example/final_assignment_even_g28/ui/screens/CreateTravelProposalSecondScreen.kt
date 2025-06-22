package com.example.final_assignment_even_g28.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.EditableTextField
import com.example.final_assignment_even_g28.ui.theme.AdventureColor
import com.example.final_assignment_even_g28.ui.theme.CultureColor
import com.example.final_assignment_even_g28.ui.theme.DimColor
import com.example.final_assignment_even_g28.ui.theme.PartyColor
import com.example.final_assignment_even_g28.ui.theme.RelaxColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel

@Composable
fun CreateTravelProposalSecondScreen(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    userVM: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    val travelProposal = tripVm.tempTravelProposal
    val experienceComposition = travelProposal.experienceComposition
    val scrollState = rememberScrollState()

    val secondScreenError = tripVm.secondScreenValidationError
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp,
                        )
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    EditableTextField(
                        value = travelProposal.title,
                        onValueChange = {
                            tripVm.updateTitle(
                                it,
                            )
                        },
                        label = "TRIP NAME",
                        isError = !secondScreenError.title.isBlank(),
                        errorMessage = secondScreenError.title,
                        trailingIcon = Icons.Outlined.Delete,
                        onTrailingIconClick = { tripVm.updateTitle("") },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },

        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
            ) {
                Text("Experience Composition", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Define the mood of your trip by assigning percentages to different experience types.\nThis helps other travelers understand what to expect, whether it’s full-on adventure, relaxing days, or cultural explorations.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    EditableTextField(
                        value = "${experienceComposition.adventure}",
                        onValueChange = {
                            tripVm.updateAdventureComposition(
                                it,
                            )
                        },
                        label = "ADVENTURE",
                        suffix = "%",
                        leadingIcon = Icons.Outlined.Forest,
                        color = AdventureColor,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                    EditableTextField(
                        value = "${experienceComposition.relax}",
                        onValueChange = {
                            tripVm.updateRelaxComposition(
                                it,
                            )
                        },
                        label = "RELAX",
                        suffix = "%",
                        leadingIcon = Icons.Outlined.Weekend,
                        color = RelaxColor,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    EditableTextField(
                        value = "${experienceComposition.culture}",
                        onValueChange = {
                            tripVm.updateCultureComposition(
                                it,
                            )
                        },
                        label = "CULTURE",
                        suffix = "%",
                        leadingIcon = Icons.Outlined.AccountBalance,
                        color = CultureColor,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )

                    EditableTextField(
                        value = "${experienceComposition.party}",
                        onValueChange = {
                            tripVm.updatePartyComposition(
                                it,
                            )
                        },
                        label = "PARTY",
                        suffix = "%",
                        leadingIcon = Icons.Outlined.MusicNote,
                        color = PartyColor,
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            OutlinedCard(
                border = if (secondScreenError.activities.isNotEmpty())
                    BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                else
                    BorderStroke(1.dp, DimColor),
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "ACTIVITIES / PREFERENCES",
                            style = MaterialTheme.typography.labelSmall
                        )

                        if (secondScreenError.activities.isNotEmpty()) {
                            Text(
                                secondScreenError.activities,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ActivitiesChips(
                        chips = tripVm.getAllActivityTags(),
                        updateActivity = { tripVm.updateActivityTags(it) },
                        isError = secondScreenError.activities.isNotEmpty(),
                    )
                }
            }

            // Send the Navigation button to the bottom of the screen
            Spacer(Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    navActions.back()

                }, shape = RoundedCornerShape(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon",
                        )
                        Text("Back")
                    }
                }
                Button(onClick = {
                    if (tripVm.validateSecondScreenFields()) {
                        if (tripVm.isEditing) {
                            tripVm.updateTravelProposal(ctx)
                        } else {
                            tripVm.addTravelProposal(ctx)
                            userVM.gainExp(10, ctx)
                        }
                        tripVm.exitEditingTravelProposal()

                        navActions.navigateToTravelList()
                    }
                }, shape = RoundedCornerShape(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(if (tripVm.isEditing) "Save" else "Create")
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Create Icon",
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivitiesChips(
    chips: List<Pair<ActivityTag, Boolean>>,
    updateActivity: (List<Pair<ActivityTag, Boolean>>) -> Unit,
    isError: Boolean
) {
    val updatedChips = chips.toMutableList()
    FlowRow {
        chips.forEachIndexed { index, activity ->
            val isSelected = activity.second
            InputChip(
                onClick = {
                    updatedChips[index] = activity.copy(second = !isSelected)
                    updateActivity(updatedChips)
                },
                label = { Text(activity.first.value) },
                selected = isSelected,
                trailingIcon = {
                    if (!isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Enable activity",
                            Modifier.size(InputChipDefaults.AvatarSize)
                        )
                    } else {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Disable Activity",
                            Modifier.size(InputChipDefaults.AvatarSize)
                        )

                    }
                },
                border = if (isError && !isSelected) BorderStroke(
                    (0.5).dp,
                    MaterialTheme.colorScheme.error
                ) else InputChipDefaults.inputChipBorder(true, isSelected),
                modifier = Modifier.padding(horizontal = 9.dp)
            )
        }
    }

}

@Preview
@Composable
fun PreviewCreateTravelProposalSecondScreen() {
    val navController = rememberNavController()
    val navActions = Navigation(navController)

    CreateTravelProposalSecondScreen(navActions = navActions)
}

