package com.example.final_assignment_even_g28.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.Itinerary
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toShortDateFormat
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import kotlinx.coroutines.flow.compose

@Composable
fun ItineraryDialog(
    viewModel: TravelProposalViewModel = viewModel(factory = AppFactory),
    onDismiss: () -> Unit,
    onAccept: (Itinerary?) -> Unit,
    suggestion : List<Itinerary>
) {
    var selectedItinerary by remember { mutableStateOf<Itinerary?>(null) }

    //val tripName = viewModel.tempTravelProposal.title
    val suggestions = suggestion


    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Log.d("Itinerary", "Received suggestions: ${suggestions.size}")
                if (selectedItinerary == null) {

                    Text("Itinerary suggestion", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Choose one of the suggested itineraries:",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (suggestions.isEmpty()) {
                        Text("No suggestions available", color = Color.Red)
                    } else {
                        suggestions.forEach { suggestion ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedItinerary = suggestion
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        suggestion.title,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = onDismiss) {
                            Text("Close")
                        }
                    }

                } else {

                    Text("Itinerary suggestion", style = MaterialTheme.typography.titleMedium)
                    Text("A suggested itinerary for your trip", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))

                    val itemHeight = 92.dp
                    val circleSize = 12.dp
                    val scrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = itemHeight * 5)
                            .verticalScroll(scrollState)
                    ) {
                        Column {
                            selectedItinerary?.stops?.forEachIndexed { index, stop ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(itemHeight)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .fillMaxHeight()
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val centerX = size.width / 2
                                            val centerY = size.height / 2
                                            val circleRadius = circleSize.toPx() / 2

                                            if (index != selectedItinerary!!.stops.lastIndex) {
                                                drawLine(
                                                    color = Color.Gray,
                                                    start = Offset(centerX, centerY + circleRadius),
                                                    end = Offset(centerX, size.height),
                                                    strokeWidth = 2.dp.toPx()
                                                )
                                            }

                                            if (index != 0) {
                                                drawLine(
                                                    color = Color.Gray,
                                                    start = Offset(centerX, 0f),
                                                    end = Offset(centerX, centerY - circleRadius),
                                                    strokeWidth = 2.dp.toPx()
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(circleSize)
                                                .align(Alignment.Center)
                                                .clip(CircleShape)
                                                .background(
                                                    if (stop.mandatory) MaterialTheme.colorScheme.error
                                                    else MaterialTheme.colorScheme.onBackground
                                                ),
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stop.date.toShortDateFormat(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stop.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if(stop.description.length > 20) stop.description.substring(1, 20) + "..." else stop.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { selectedItinerary = null }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            onAccept(selectedItinerary)
                        }) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}
