package com.example.final_assignment_even_g28.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel

@Composable
fun TravelProposalList(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState,
) {
    val filteredTravelProposal by tripVm.allTravelProposals.collectAsState()
    Log.d("TravelProposalList", "Filtered proposals: ${filteredTravelProposal.map { it.id }}")

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { CustomBottomBar(navActions, selectedItem = bottomBarItem) },
        topBar = { },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(start = 15.dp, end = 15.dp)) {
            FilterForm(tripVm)
            Spacer(modifier = Modifier.height(16.dp))
            if (filteredTravelProposal.isNotEmpty()) {
                TravelProposalListColumn(
                    tripVm,
                    filteredTravelProposal,
                    Modifier
                        .fillMaxHeight(),
//                        .padding(innerPadding),
                    navActions
                )
            } else {
                Text(
                    text = "There are no Proposals that match your filters",
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun TravelProposalListColumn(
    tripVm: TravelProposalViewModel,
    travelProposalList: List<TravelProposal>,
    modifier: Modifier = Modifier,
    navActions: Navigation
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 15.dp, end = 15.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        travelProposalList.forEach { travel ->
            TravelProposalBlock(tripVm, travel, navActions)
        }
    }
}

@Composable
fun TravelProposalBlock(
    tripVm: TravelProposalViewModel, travelProposal: TravelProposal, navActions: Navigation
) {
    val numApprovedParticipant = tripVm.getNumApprovedParticipants(travelProposal)
    val image = travelProposal.images.firstOrNull()
    Column(
        modifier = Modifier
            .shadow(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                navActions.navigateToTripInfo(travelProposal.id, false)
            }, verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start
    ) {
        Row {
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
                    text = "${numApprovedParticipant}/${travelProposal.maxParticipant}",
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
                modifier = Modifier.padding(
                    top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp
                ),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = tripVm.showDatesInList(
                    travelProposal.tripStartDate.toDate(), travelProposal.tripEndDate.toDate()
                ),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 6.dp),
            )
        }
        Row(modifier = Modifier.padding(start = 10.dp)) { Tag(travelProposal.activities) }
    }
}

@Composable
fun Tag(tagsList: List<ActivityTag>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
    ) {
        items(tagsList) {
            AssistChip(
                onClick = {},
                label = { Text(it.value) },
                enabled = false,
                shape = RoundedCornerShape(50),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterForm(tripVm: TravelProposalViewModel) {
    val isFilterBarExtended by tripVm.isFilterBarExtended
    val filters = tripVm.filters
    var expanded by remember { mutableStateOf(false) }
    val filterErrors = tripVm.filterErrors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(shape = RoundedCornerShape(16.dp), elevation = 4.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { tripVm.toggleFilterBar() },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFilterBarExtended) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filters.title} | ${tripVm.showFromValue(filters.startDate)} - ${
                        tripVm.showToValue(
                            filters.endDate
                        )
                    } | ${
                        if (filters.activities.isEmpty()) "Activities"
                        else filters.activities.joinToString(", ")
                    }",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.width(300.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.FilterAlt, contentDescription = "Filter"
                )
            }
        } else { //isFilterBarExtended == true
            Row {
                TextField(
                    label = { Text("FROM") },
                    value = tripVm.showFromValue(
                        filters.startDate
                    ),
                    singleLine = true,
                    onValueChange = {},
                    isError = filterErrors.fromDate.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                tripVm.toggleStartDate()
                            }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Date Picker",
                            )
                        }
                    },
                )
                if (tripVm.showStartDate) {
                    DatePickerModal(
                        onDateSelected = {
                            tripVm.updateFrom(it)
                        }, onDismiss = {
                            tripVm.showStartDate = false
                        }, vm = tripVm, typeOfDate = "start"
                    )
                }
                TextField(
                    label = { Text("TO") },
                    value = tripVm.showToValue(filters.endDate),
                    singleLine = true,
                    onValueChange = {},
                    isError = filterErrors.toDate.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                tripVm.toggleEndDate()
                            }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Date Picker",
                            )
                        }
                    },
                )
            }
            if (tripVm.showEndDate) {
                DatePickerModal(
                    onDateSelected = { tripVm.updateTo(it) }, onDismiss = {
                        tripVm.showEndDate = false
                    }, vm = tripVm, typeOfDate = "end"
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 16.dp
                            )
                        )
                        .onFocusChanged {
                            tripVm.resetWhere()
                        },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    label = { Text("WHERE") },
                    value = filters.title,
                    singleLine = true,
                    onValueChange = {
                        tripVm.updateWhere(it)
                    },
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(
                        start = 16.dp, top = 8.dp, end = 4.dp, bottom = 4.dp
                    )
            ) {
                Text(
                    text = "ACTIVITIES/PREFERENCES",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    tripVm.getAllActivityTags().forEach { activity ->
                        FilterChip(
                            selected = tripVm.filters.activities.contains(activity.first) == true,
                            onClick = {
                                tripVm.toggleActivity(
                                    activity
                                )
                            },
                            label = { Text(activity.first.value) })
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        readOnly = true,
                        value = filters.groupSize?.toString() ?: "Any",
                        onValueChange = {},
                        label = { Text("GROUP SIZE") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .width(140.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 16.dp
                                )
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 16.dp
                                )
                            ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        val dropDownList =
                            listOf("Any") + tripVm.groupSizeOptions.map { it.toString() }
                        dropDownList.forEach { size ->
                            DropdownMenuItem(text = { Text(size) }, onClick = {
                                tripVm.updateFilterGroupSize(groupSize = if (size.contains("Any")) null else size.toInt())
                                expanded = false
                            })
                        }
                    }
                }
                TextField(
                    label = { Text("MIN PRICE") },
                    value = filters.minPrice.toString(),
                    singleLine = true,
                    onValueChange = {
                        tripVm.updateMinPrice(it.toInt())
                    },
                    isError = filterErrors.minPrice.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp, MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                TextField(
                    label = { Text("MAX PRICE") },
                    value = filters.maxPrice.toString(),
                    singleLine = true,
                    onValueChange = {
                        tripVm.updateMaxPrice(it.toInt())
                    },
                    isError = filterErrors.maxPrice.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 0.dp
                            )
                        ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormButton(tripVm)
        }
    }
}

@Composable
fun FormButton(tripVm: TravelProposalViewModel) {
    val filterError = tripVm.filterErrors
    val errorToShow = filterError.toList.firstOrNull { it.isNotBlank() }

    if (filterError.hasError && errorToShow != null) {
        Text(
            text = errorToShow,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(6.dp)
        )
    }
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),
        onClick = { tripVm.applyFilters() },
        shape = RoundedCornerShape(10.dp),
        enabled = !filterError.hasError
    ) {
        Text(
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
            text = "Search",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
