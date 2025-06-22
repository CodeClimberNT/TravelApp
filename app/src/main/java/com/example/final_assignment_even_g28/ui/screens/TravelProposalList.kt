package com.example.final_assignment_even_g28.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.ActivityTag
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.card.TravelProposalCard
import com.example.final_assignment_even_g28.ui.components.modal.DatePickerModal
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.utils.toMillis
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TravelProposalList(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    snackBarHostState: SnackbarHostState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
) {
    val filteredTravelProposal by tripVm.allTravelProposals.collectAsState()
    Log.d("TravelProposalList", "Filtered proposals: ${filteredTravelProposal.map { it.id }}")

    var isFilterExpanded by remember { mutableStateOf(false) }
    val filters = tripVm.filters
    val filterBgColor = MaterialTheme.colorScheme.primaryContainer
    val filterTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    val gradientBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                filterBgColor,
                filterBgColor.copy(alpha = 0.8f)
            )
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { CustomBottomBar(navActions, selectedItem = bottomBarItem) },
        topBar = {
            ExpandableFilterTopBar(
                isExpanded = isFilterExpanded,
                bgColor = filterBgColor,
                textColor = filterTextColor,
                gradient = gradientBrush,
                onToggle = { isFilterExpanded = !isFilterExpanded },
                filterSummary = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildString {
                                append(filters.title)
                                filters.startDate?.let { append(" | ${it.toDateFormat()}") }
                                filters.endDate?.let { append(" - ${it.toDateFormat()}") }
                            },
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))

                        filters.activities.take(2).forEach { act ->
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        act.value,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.height(28.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                        if (filters.activities.size > 2) {
                            Text(
                                "+${filters.activities.size - 2}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                expandedContent = {
                    FilterForm(
                        tripVm = tripVm,
                        bgColor = filterBgColor,
                        textColor = filterTextColor,
                        gradient = gradientBrush,
                        onToggle = { isFilterExpanded = !isFilterExpanded },
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 15.dp, end = 15.dp)
        ) {

            if (filteredTravelProposal.isNotEmpty()) {
                TravelProposalListColumn(
                    tripVm = tripVm,
                    travelProposalList = filteredTravelProposal,
                    modifier = Modifier.fillMaxHeight(),
                    navActions = navActions,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TravelProposalListColumn(
    tripVm: TravelProposalViewModel,
    travelProposalList: List<TravelProposal>,
    modifier: Modifier = Modifier,
    navActions: Navigation,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 15.dp, end = 15.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        travelProposalList.forEach { travel ->
            TravelProposalCard(
                tripVm, travel, navActions,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ExpandableFilterTopBar(
    isExpanded: Boolean,
    bgColor: Color,
    textColor: Color,
    gradient: Brush,
    onToggle: () -> Unit,
    filterSummary: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                gradient,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    ) {
        Column(Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier
                    .height(104.dp)
                    .fillMaxWidth(),
                title = {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Filters",
                                style = MaterialTheme.typography.headlineMedium,
                                color = textColor,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            filterSummary()
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onToggle) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = if (isExpanded) "Hide filters" else "Show filters",
                            modifier = Modifier
                                .rotate(if (isExpanded) 180f else 0f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = textColor,
                    actionIconContentColor = textColor
                ),
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    color = bgColor,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        expandedContent()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterForm(
    tripVm: TravelProposalViewModel,
    bgColor: Color,
    textColor: Color,
    gradient: Brush,
    onToggle: () -> Unit
) {
    var isGroupSizeExtended by remember { mutableStateOf(false) }
    val filterErrors = tripVm.filterErrors
    var showStartDate by remember { mutableStateOf(false) }
    var showEndDate by remember { mutableStateOf(false) }

    val filters = tripVm.filters
    val shape = RoundedCornerShape(20.dp)

    Row(Modifier.fillMaxWidth()) {
        TextField(
            label = { Text("FROM", color = textColor) },
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
                    textColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        showStartDate = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date Picker",
                    )
                }
            },
        )
        if (showStartDate) {
            DatePickerModal(
                initialDate = filters.startDate?.toMillis(),
                onDateSelected = {
                    tripVm.updateFrom(it)
                }, onDismiss = {
                    showStartDate = false
                }
            )
        }
        TextField(
            label = { Text("TO", color = textColor) },
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
                    textColor,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        showEndDate = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Date Picker",
                    )
                }
            },
        )
    }
    if (showEndDate) {
        DatePickerModal(
            initialDate = filters.endDate?.toMillis(),
            onDateSelected = { tripVm.updateTo(it) },
            onDismiss = {
                showEndDate = false
            },
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
                    textColor,
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
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            ),
            label = { Text("WHERE", color = textColor) },
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
                textColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                start = 16.dp, top = 8.dp, end = 4.dp, bottom = 4.dp
            )
    ) {
        Text(
            text = "ACTIVITIES/PREFERENCES",
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = textColor
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
                    label = { Text(activity.first.value, color = textColor) })
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row {
        ExposedDropdownMenuBox(
            expanded = isGroupSizeExtended,
            onExpandedChange = { isGroupSizeExtended = !isGroupSizeExtended }) {
            TextField(
                readOnly = true,
                value = filters.groupSize?.toString() ?: "Any",
                onValueChange = {},
                label = { Text("GROUP SIZE", color = textColor) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isGroupSizeExtended
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
                        textColor,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 16.dp
                        )
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = bgColor,
                    focusedContainerColor = bgColor
                )
            )
            ExposedDropdownMenu(
                expanded = isGroupSizeExtended,
                onDismissRequest = { isGroupSizeExtended = false }) {
                val dropDownList =
                    listOf("Any") + tripVm.groupSizeOptions.map { it.toString() }
                dropDownList.forEach { size ->
                    DropdownMenuItem(
                        text = { Text(size, color = textColor) },
                        onClick = {
                            tripVm.updateFilterGroupSize(groupSize = if (size.contains("Any")) null else size.toInt())
                            isGroupSizeExtended = false
                        })
                }
            }
        }
        TextField(
            label = { Text("MIN PRICE", color = textColor) },
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
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            )
        )
        TextField(
            label = { Text("MAX PRICE", color = textColor) },
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
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            )
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    FormButton(tripVm, onApplyFilters = {
        tripVm.applyFilters()
        onToggle()
    })
}

@Composable
fun FormButton(tripVm: TravelProposalViewModel, onApplyFilters: () -> Unit) {
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
        onClick = onApplyFilters,
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

