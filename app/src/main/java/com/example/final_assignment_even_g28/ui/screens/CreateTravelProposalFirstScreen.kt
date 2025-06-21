package com.example.final_assignment_even_g28.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.Price
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.shared.EditableTextField
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTravelProposalFirstScreen(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
) {
    val travelProposal = tripVm.tempTravelProposal


    val showItineraryCard = remember { mutableStateOf(false) }
    val scrollable = rememberScrollState()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            tripVm.addImageFromGallery(uri)
        }
    }

    val firstScreenError = tripVm.firstScreenValidationError

    BackHandler {
        tripVm.exitEditingTravelProposal()
        navActions.back()
    }

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
                        isError = firstScreenError.title.isNotBlank(),
                        errorMessage = firstScreenError.title,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollable)

        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),

                    contentAlignment = Alignment.Center

                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = travelProposal.maxParticipant.toString(),
                            onValueChange = {},
                            label = { Text("Group Size") },
                            readOnly = true,
                            isError = firstScreenError.maxParticipant.isNotBlank(),
                            trailingIcon = {
                                IconButton(onClick = { tripVm.toggleExpanded() }) {
                                    Icon(
                                        imageVector = if (!tripVm.isExpanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                                        contentDescription = "Drop Down"
                                    )
                                }

                            },
                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 10.dp,
                                bottomEnd = 10.dp,
                                bottomStart = 10.dp,
                            )

                        )
                        if (firstScreenError.maxParticipant.isNotBlank()) {
                            Text(
                                text = firstScreenError.maxParticipant,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 0.dp, top = 4.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = tripVm.isExpanded,
                        onDismissRequest = { tripVm.toggleExpanded() },
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 10.dp,
                                    bottomStart = 10.dp,
                                )
                            ),

                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomEnd = 10.dp,
                            bottomStart = 10.dp,
                        )


                    ) {
                        tripVm.groupSizeOptions.forEach { size ->
                            DropdownMenuItem(text = { Text(size.toString()) }, onClick = {
                                tripVm.updateGroupSize(size)
                                tripVm.toggleExpanded()
                            })
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Column {
                        Text("Select Price Range", style = MaterialTheme.typography.titleMedium)
                        RangeSlider(
                            value = (travelProposal.price.min.toFloat())..(travelProposal.price.max.toFloat()),
                            onValueChange = { values ->
                                tripVm.updatePriceRange(Price(values.start.toInt(), values.endInclusive.toInt()))
                            },
                            valueRange = 0f..1000f,

                            )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Min: ${travelProposal.price.min} €",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Max: ${travelProposal.price.max.toInt()} €",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (firstScreenError.price.isNotBlank()) {
                            Text(
                                text = firstScreenError.price,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 0.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = travelProposal.tripStartDate.toDateFormat(),
                    onValueChange = { },
                    label = { Text("From") },
                    isError = firstScreenError.tripStartDate.isNotBlank(),


                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 10.dp,
                    ),
                    trailingIcon = {

                        IconButton(onClick = { tripVm.toggleStartDate() }) {
                            Icon(

                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Date Picker",
                            )

                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 0.dp)

                )
                if (tripVm.showStartDate) {
                    DatePickerModal(
                        onDateSelected = { tripVm.selectedStartDate = it },
                        onDismiss = { tripVm.showStartDate = false },
                        vm = tripVm,
                        typeOfDate = "start"

                    )
                }

                OutlinedTextField(
                    value = travelProposal.tripEndDate.toDateFormat(),
                    onValueChange = { },
                    label = { Text("To") },
                    isError = firstScreenError.tripEndDate.isNotBlank(),

                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 10.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 0.dp,
                    ),
                    trailingIcon = {
                        IconButton(onClick = { tripVm.toggleEndDate() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Date Picker",
                            )

                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 0.dp)

                )
                if (tripVm.showEndDate) {
                    DatePickerModal(
                        onDateSelected = {
                            tripVm.selectedEndDate = it
                        },
                        onDismiss = { tripVm.showEndDate = false },
                        vm = tripVm,
                        typeOfDate = "end"

                    )
                }

            }

            Row(
                modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (firstScreenError.tripStartDate.isNotBlank()) {
                    Text(
                        text = firstScreenError.tripStartDate,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .weight(1f)
                    )
                }
                if (firstScreenError.tripEndDate.isNotBlank()) {
                    Text(
                        text = firstScreenError.tripEndDate,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(start = 0.dp, top = 4.dp)
                            .weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = travelProposal.description,
                    onValueChange = { tripVm.updateDescription(it) },
                    label = { Text("Trip Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.30f)
                        .height(150.dp),
                    isError = firstScreenError.tripDescription.isNotBlank(),
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomEnd = 10.dp,
                        bottomStart = 10.dp,
                    )
                )
            }
            if (firstScreenError.tripDescription.isNotBlank()) {
                Text(
                    text = firstScreenError.tripDescription,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color.Gray)
                )
            }
            Column(
                modifier = Modifier.padding(
                    top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp
                )
            ) {
                Text(
                    "IMAGES",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp
                    )
                )
                Row(
                    modifier = Modifier
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    DisplayImagesWithAddButton(
                        tripVm = tripVm,
                        oldImages = travelProposal.images,
                        newImages = travelProposal.tempImages,
                        galleryLauncher = galleryLauncher,
                    )
                }
                if (firstScreenError.tripImages.isNotBlank()) {
                    Text(
                        text = firstScreenError.tripImages,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color.Gray)
                )
            }

            Column(
                modifier = Modifier.padding(
                    top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ITINERARY",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showItineraryCard.value = true },
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Help",
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (showItineraryCard.value) {
                    ItineraryDialog(
                        onDismiss = { showItineraryCard.value = false },
                        onAccept = {
                            showItineraryCard.value = false
                        }
                    )
                }

                ItineraryWithStops(tripVm = tripVm)
                Button(
                    onClick = {
                        if (tripVm.validateFirstScreenFields()) {
                            //vm.saveTravelProposal(vm.travelProposal)

                            navActions.navigateToSecondScreen()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        "Next ",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    vm: TravelProposalViewModel,
    typeOfDate: String = "start",
    index: Int = -1,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            if (typeOfDate == "start") {
                vm.startDateSelected(datePickerState.selectedDateMillis)
            } else if (typeOfDate == "end") {
                vm.endDateSelected(datePickerState.selectedDateMillis)
            } else {
                vm.updateStopDate(datePickerState.selectedDateMillis, index)
            }
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayImagesWithAddButton(
    tripVm: TravelProposalViewModel,
    oldImages: List<String>,
    newImages: List<String>,
    galleryLauncher: ActivityResultLauncher<String>,
) {
    val images = oldImages + newImages.toList()

    val rowNumber = (images.size / 3 + if (images.size % 3 == 0) 0 else 1)
    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until rowNumber) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {
                for (j in 0..2) {
                    val index = i * 3 + j
                    if (index < images.size) {
                        val image = if (index < oldImages.size) {
                            images[index]
                        } else { //newImages are local, and need to be converted to Uri for coil
                            images[index].toUri()
                        }

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        model = image,
                                        error = painterResource(id = R.drawable.error_image)
                                    ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                IconButton(
                                    onClick = { tripVm.removeImageFromGallery(index) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Image",
                                        tint = Color.Red,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                    } else if (index == images.size) {
                        IconButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Image",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }
        if (images.size % 3 == 0) IconButton(
            onClick = {
                galleryLauncher.launch("image/*")
            },
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}


@Composable
fun ItineraryWithStops(
    tripVm: TravelProposalViewModel
) {
    val travelProposal = tripVm.tempTravelProposal

    val mandatoryColor = MaterialTheme.colorScheme.error
    val optionalColor = MaterialTheme.colorScheme.onSecondaryContainer
    val itinerarySize = travelProposal.itinerary.size
    val stopDateIndex = remember { mutableIntStateOf(-1) }

    val firstScreenError = tripVm.firstScreenValidationError

    Column {
        travelProposal.itinerary.forEachIndexed { index, stop ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .drawBehind {
                        if (index != itinerarySize) {
                            val strokeWidth = 4.dp.toPx()
                            drawLine(
                                color = optionalColor,
                                start = Offset(8.dp.toPx(), 50.dp.toPx()),
                                end = Offset(8.dp.toPx(), size.height + 35.dp.toPx()),
                                strokeWidth = strokeWidth
                            )
                        }
                    }, verticalAlignment = Alignment.Top
            ) {
                Canvas(
                    modifier = Modifier
                        .padding(top = 28.dp)
                        .size(16.dp)
                        .background(Color.Transparent, shape = CircleShape)
                ) {
                    drawCircle(
                        color = if (stop.mandatory) mandatoryColor else optionalColor,
                        radius = size.minDimension / 2
                    )
                }

                Column {
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ) {


                        OutlinedTextField(
                            value = stop.title,
                            onValueChange = { tripVm.updateStopTitle(it, index) },
                            //isError = firstScreenError.itinerary.isNotBlank(),
                            label = { Text("Stop Name") },


                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 10.dp,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 0.dp)

                        )

                        OutlinedTextField(
                            value = stop.date.toDateFormat(),
                            onValueChange = { },
                            label = { Text("Day") },


                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 10.dp,
                                bottomEnd = 10.dp,
                                bottomStart = 0.dp,
                            ),
                            trailingIcon = {

                                IconButton(onClick = {
                                    tripVm.toggleStopDate(); stopDateIndex.intValue = index
                                }) {
                                    Icon(

                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Date Picker",
                                    )

                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 0.dp)

                        )


                        if (tripVm.showStopDate && stopDateIndex.intValue == index) {
                            DatePickerModal(
                                onDateSelected = {
                                    tripVm.selectedStopDate = it
                                },
                                onDismiss = { tripVm.showStopDate = false },
                                vm = tripVm,
                                typeOfDate = "stop",
                                index = index

                            )

                        }

                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (firstScreenError.itineraryErrors[index]?.title?.isNotBlank() == true) {
                            Text(
                                text = firstScreenError.itineraryErrors[index]?.title ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 4.dp)
                                    .weight(1f)
                            )
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .height(20.dp)
                                    .weight(1f)
                            )
                        }

                        if (firstScreenError.itineraryErrors[index]?.date?.isNotBlank() == true) {
                            Text(
                                text = firstScreenError.itineraryErrors[index]?.date ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 0.dp, top = 4.dp)
                                    .weight(1f)
                            )
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .height(20.dp)
                                    .weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(
                                top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp
                            )
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = stop.description,
                            onValueChange = { tripVm.updateStopDescription(index, it) },
                            label = { Text("Stop Description") },


                            shape = RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 10.dp,
                                bottomEnd = 10.dp,
                                bottomStart = 10.dp,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 0.dp)
                                .fillMaxHeight()

                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()

                            ) {
                                Text("Mandatory Stop")

                                Checkbox(
                                    checked = stop.mandatory,
                                    onCheckedChange = { tripVm.updateStopMandatory(index, it) },

                                    )
                            }


                            IconButton(
                                onClick = { tripVm.deleteStop(index) },
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.error, shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete Stop",
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(40.dp)


                                )
                            }
                        }


                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (firstScreenError.itineraryErrors[index]?.description?.isNotBlank() == true) {
                            Text(
                                text = firstScreenError.itineraryErrors[index]?.description ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 0.dp)
                                    .weight(1.2f)
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .height(20.dp)
                                .weight(1f)
                        )

                    }
//                    if (index == itinerarySize - 1)
//
//                        Button(onClick = { vm.addStop() }, modifier = Modifier.padding(16.dp)) {
//                            Text("Add Stop")
//
//                        }
                }

            }

        }
        if (firstScreenError.itinerary.contains("empty")) {
            Text(
                text = firstScreenError.itinerary,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (itinerarySize != 0) Canvas(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .size(16.dp)
                    .background(Color.Transparent, shape = CircleShape)
            ) {
                drawCircle(
                    color = optionalColor, radius = size.minDimension / 2
                )
            }
            Button(onClick = { tripVm.addStop() }, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Stop",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Add Stop",
                    style = MaterialTheme.typography.titleMedium,
                )

            }

        }
    }
}

@Preview
@Composable
fun PreviewCreateTravelProposalFirstScreen() {

    val navController = rememberNavController()
    val navActions = Navigation(navController)

    CreateTravelProposalFirstScreen(navActions = navActions)
}