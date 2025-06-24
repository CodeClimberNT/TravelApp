package com.example.final_assignment_even_g28.ui.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Weekend
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.ExperienceComposition
import com.example.final_assignment_even_g28.data_class.ItineraryStop
import com.example.final_assignment_even_g28.data_class.ParticipantStatus
import com.example.final_assignment_even_g28.data_class.Planner
import com.example.final_assignment_even_g28.data_class.TravelProposal
import com.example.final_assignment_even_g28.data_class.UserProfile
import com.example.final_assignment_even_g28.navigation.BottomBarItem
import com.example.final_assignment_even_g28.navigation.CustomBottomBar
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.ui.components.user_profile.ProfilePicture
import com.example.final_assignment_even_g28.ui.theme.AdventureColor
import com.example.final_assignment_even_g28.ui.theme.CultureColor
import com.example.final_assignment_even_g28.ui.theme.DimColor
import com.example.final_assignment_even_g28.ui.theme.PartyColor
import com.example.final_assignment_even_g28.ui.theme.RelaxColor
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.net.URLEncoder
import java.util.Locale
import kotlin.coroutines.resume


//TODO: refactor this file with common component
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TravelProposalScreen(
    tripVm: TravelProposalViewModel = viewModel(factory = AppFactory),
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    tripId: String,
    snackBarHostState: SnackbarHostState,
    showParticipantsDialog: Boolean = false,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope
) {
    tripVm.clickTripInfo(tripId)
    val travelProposal by tripVm.travelProposal.collectAsState()
    val tripPlanner by tripVm.currentTripPlanner.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var openCandidatesDialog = remember { mutableStateOf(showParticipantsDialog) }


    LaunchedEffect(showParticipantsDialog) {
        if (showParticipantsDialog) {
            openCandidatesDialog.value = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            CombinedBottomBar(
                tripVm = tripVm,
                proposal = travelProposal,
                price = "${travelProposal.price.min} - ${travelProposal.price.max}",
                navActions = navActions,
                bottomBarItem = bottomBarItem
            )
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            with(sharedTransitionScope) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .sharedElement(
                            rememberSharedContentState(key = "card_$tripId"),
                            animatedVisibilityScope = animatedContentScope
                        )
                ) {
                    ImageCarousel(travelProposal.images)
                    Spacer(modifier = Modifier.height(16.dp))
                    HeroSection(
                        title = travelProposal.title,
                        duration = tripVm.showDatesInTripInfo(
                            travelProposal.tripStartDate.toDate(),
                            travelProposal.tripEndDate.toDate()
                        ),
                        tags = travelProposal.activities.map { it.value },
                        travelProposalVM = tripVm,
                        navActions = navActions,
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    TripOverview(
                        tripVm = tripVm,
                        proposal = travelProposal,
                        tripPlanner = tripPlanner,
                        navActions = navActions,
                        bottomBarItem = bottomBarItem,
                        openCandidatesDialog
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    ActivitiesPercentages(travelProposal.experienceComposition, isLandscape)
                    TripDescription(travelProposal.description)
                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                ItinerarySection(travelProposal.itinerary)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                                TripMap(travelProposal.itinerary, travelProposal.title)
                            }
                        }
                    } else {
                        ItinerarySection(travelProposal.itinerary)
                        TripMap(travelProposal.itinerary, travelProposal.title)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                8.dp, RoundedCornerShape(
                    topStart = 0.dp, topEnd = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp, topEnd = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp
                )
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) { page ->
            Image(
                painter =
                    rememberAsyncImagePainter(
                        model = images[page],
                        error = painterResource(id = R.drawable.error_image)
                    ),
                contentDescription = "Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }
        // Indicatori personalizzati
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(width = 6.dp, height = 6.dp)
                        .background(color = color, shape = CircleShape)
                )
            }
        }
    }
}

@Composable
fun HeroSection(
    title: String,
    duration: String,
    tags: List<String>,
    travelProposalVM: TravelProposalViewModel,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    navActions: Navigation
) {
    val currentUser by userVm.loggedUser.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (!travelProposalVM.isMyTrip()) {
                //is not your trip
                TextButton(onClick = {
                    travelProposalVM.clickCloneTrip(currentUser.uid)
                    navActions.navigateToCreateNewTravelProposal()
                }) {
                    Text(
                        "Clone Proposal", textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
        Text(duration, color = DimColor)
        TripActivityTags(tags)
    }
}

@Composable
fun TripActivityTags(tagsList: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tagsList) {
            AssistChip(
                onClick = {},
                label = { Text(it) },
                enabled = false,
                shape = RoundedCornerShape(50),
            )
        }
    }
}

@Composable
fun TripOverview(
    tripVm: TravelProposalViewModel,
    proposal: TravelProposal,
    tripPlanner: Planner,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    openCandidatesDialog: MutableState<Boolean>

) {
    //var openCandidatesDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Trip Planner",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            ProfilePicture(tripPlanner, isLandscape, isDashboard = true)

            Spacer(modifier = Modifier.width(12.dp))

            // Name and Rating
            Column {
                Text(text = tripPlanner.name, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = StarColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "%.2f".format(tripPlanner.rating), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!tripVm.isMyTrip()) {
                Button(
                    modifier = Modifier
                        .height(45.dp)
                        .width(50.dp),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp),
                    onClick = {
                        val message =
                            "Hi ${tripPlanner.name}, I'm interested in this trip itinerary."
                        openWhatsAppChat(context, tripPlanner.phoneNumber, message)
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Message,
                        contentDescription = "Contact",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }
            } else {
                Button(
                    modifier = Modifier
                        .height(40.dp)
                        .width(130.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(4.dp),
                    onClick = {
                        openCandidatesDialog.value = true
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Contact",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Candidates")

                    }
                }
            }
            if (openCandidatesDialog.value) {
                ParticipantList(
                    tripVm = tripVm,
                    travelProposal = proposal,
                    onDismissRequest = { openCandidatesDialog.value = false },
                    navActions = navActions,
                    bottomBarItem = bottomBarItem,
                )
            }
        }

    }
}

fun openWhatsAppChat(context: Context, phoneNumber: String, message: String? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        putExtra("jid", "39$phoneNumber@s.whatsapp.net")
        `package` = "com.whatsapp"
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val url = "https://api.whatsapp.com/send?phone=39$phoneNumber&text=" +
                URLEncoder.encode(message, "UTF-8")
        Log.d("URL", url)
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}

@Composable
fun ActivitiesPercentages(
    experienceComposition: ExperienceComposition,
    isLandscape: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                start = if (isLandscape) 100.dp else 0.dp,
                end = if (isLandscape) 100.dp else 0.dp
            )
    ) {
        CategoryStat(
            "Adventure", experienceComposition.adventure, AdventureColor, Icons.Outlined.Forest
        )
        CategoryStat(
            "Culture", experienceComposition.culture, CultureColor, Icons.Outlined.AccountBalance
        )
        CategoryStat(
            "Relax", experienceComposition.relax, RelaxColor, Icons.Outlined.Weekend
        )
        CategoryStat(
            "Party", experienceComposition.party, PartyColor, Icons.Outlined.MusicNote
        )
    }
}

@Composable
fun CategoryStat(label: String, value: Int, color: Color, icon: ImageVector) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .padding(8.dp)
            .size(85.dp)
    ) {
        CircularProgressIndicator(
            progress = { value.toFloat() / 100 },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 6.dp,
            color = color,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${value}%",
                style = MaterialTheme.typography.labelMedium,
                color = color,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TripDescription(tripDescription: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Trip Description", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = tripDescription, fontSize = 14.sp
        )
    }
}

@Composable
fun ItinerarySection(itinerary: List<ItineraryStop>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(
            "Itinerary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(8.dp))

        itinerary.forEachIndexed { index, stop ->
            ItineraryItem(
                stop = stop, isLastItem = index == itinerary.lastIndex
            )
        }
    }
}

@Composable
fun ItineraryItem(stop: ItineraryStop, isLastItem: Boolean) {
    val mandatoryColor = MaterialTheme.colorScheme.error
    val optionalColor = MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .drawBehind {
                if (!isLastItem) {
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
        // Ellipse Indicator
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
        Spacer(modifier = Modifier.width(8.dp))
        // Stop Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stop.date.toDateFormat(), fontSize = 12.sp, color = optionalColor
            )
            Text(
                text = stop.title, fontWeight = FontWeight.SemiBold, color = optionalColor
            )
            Text(
                text = stop.description, fontSize = 12.sp, color = optionalColor
            )
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun TripMap(itinerary: List<ItineraryStop>, tripName: String) {
    val defaultLocation = LatLng(45.438, 10.992)
    val context = LocalContext.current
    var locations by remember { mutableStateOf<List<LatLng?>>(emptyList()) }
    var location_title by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    var isMapInteracting by remember { mutableStateOf(false) }

    LaunchedEffect(tripName) {
        location_title = resolveLocation(tripName, context)
        location_title?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 8f), 1000)
        }
    }

    LaunchedEffect(itinerary) {
        locations = itinerary.map { stop ->
            resolveLocation(stop.title, context)
        }
        locations.filterNotNull().let { pts ->
            if (pts.isNotEmpty()) {
                val bounds = LatLngBounds.builder().apply {
                    pts.forEach(::include)
                }.build()
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 60),
                    1000
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleMap(
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isMapInteracting = true },
                        onDragEnd = { isMapInteracting = false }
                    ) { _, _ -> }
                },
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.NORMAL),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true
            ),
            onMapClick = { isMapInteracting = !isMapInteracting }
        ) {
            locations.forEachIndexed { i, latLng ->
                latLng?.let {
                    Marker(
                        state = rememberUpdatedMarkerState(it),
                        title = itinerary[i].title
                    )
                }
                // Draw route line
                val pts = locations.filterNotNull()
                if (pts.size >= 2) {
                    Polyline(
                        points = pts,
                        width = 6f,
                        color = Color.Blue,
                        jointType = JointType.ROUND,
                        geodesic = true
                    )

                    /*val arrowCap = remember {
                        CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 30f)
                    }*/
                    pts.windowed(2).forEach { (from, to) ->
                        Polyline(
                            points = listOf(from, to),
                            width = 6f,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            //endCap = arrowCap,
                            geodesic = true
                        )
                    }
                }
            }
        }
    }
}

fun titleFragments(raw: String): List<String> {
    return listOf(raw) + raw
        .split(" by ", " to ", " at ", " of ", " near ", ",")
        .map { it.trim() }
        .filter { it.length >= 3 }
}


suspend fun resolveLocation(
    title: String,
    context: Context
): LatLng? {
    for (fragment in titleFragments(title)) {
        geocodeWithSdk(context, fragment)?.let { return it }
    }
    return null
}

suspend fun geocodeWithSdk(context: Context, text: String): LatLng? =
    withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null
        return@withContext try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine<LatLng?> { cont ->
                    Geocoder(context, Locale.getDefault())
                        .getFromLocationName(text, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: List<Address>) {
                                cont.resume(
                                    addresses.firstOrNull()
                                        ?.let { LatLng(it.latitude, it.longitude) })
                            }

                            override fun onError(errorMessage: String?) {
                                cont.resume(null)
                            }
                        })
                }
            } else {
                Geocoder(context, Locale.getDefault())
                    .getFromLocationName(text, 1)
                    ?.firstOrNull()
                    ?.let { LatLng(it.latitude, it.longitude) }
            }
        } catch (e: IOException) {
            null
        }
    }


fun geocodeAddresses(context: Context, name: String, onResult: (LatLng?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(name, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: List<Address>) {
                    onResult(addresses.firstOrNull()?.let { LatLng(it.latitude, it.longitude) })
                }

                override fun onError(errorMessage: String?) {
                    onResult(null)
                }
            })
        } else {
            // Deprecated sync call
            val addresses = geocoder.getFromLocationName(name, 1)
            onResult(addresses?.firstOrNull()?.let { LatLng(it.latitude, it.longitude) })
        }
    } catch (e: IOException) {
        onResult(null)
    }
}


@Composable
fun CombinedBottomBar(
    tripVm: TravelProposalViewModel,
    proposal: TravelProposal,
    price: String,
    navActions: Navigation,
    bottomBarItem: BottomBarItem
) {
    Column {
        TravelActionBar(
            tripVm = tripVm, proposal = proposal, price = price, navActions = navActions
        )
        CustomBottomBar(navActions, selectedItem = bottomBarItem)

    }
}


@Composable
fun TravelActionBar(
    tripVm: TravelProposalViewModel,
    proposal: TravelProposal,
    price: String,
    navActions: Navigation,
    userProfileViewModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    var showApplyDialog by remember { mutableStateOf(false) }
    var showPendingDialog by remember { mutableStateOf(false) }
    var showAcceptedDialog by remember { mutableStateOf(false) }

    val userParticipationStatus = tripVm.getUserParticipantStatus()
    val tripPlanner by tripVm.currentTripPlanner.collectAsState()

    val ctx = LocalContext.current
    val numApprovedParticipant = tripVm.getNumApprovedParticipants(proposal)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$price €",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (!tripVm.isMyTrip()) {
                    Button(
                        modifier = Modifier
                            .height(40.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp),
                        shape = RoundedCornerShape(10.dp),
                        onClick = when (userParticipationStatus) {
                            ParticipantStatus.PENDING -> {
                                { showPendingDialog = true }
                            }

                            ParticipantStatus.APPROVED -> {
                                { showAcceptedDialog = true }
                            }

                            ParticipantStatus.REJECTED -> {
                                {}
                            }

                            null -> {
                                { showApplyDialog = true }
                            }
                        },
                        enabled = true,
                        colors = when (userParticipationStatus) {
                            ParticipantStatus.APPROVED -> {
                                ButtonDefaults.buttonColors(
                                    disabledContainerColor = AdventureColor
                                )
                            }

                            ParticipantStatus.PENDING -> {
                                ButtonDefaults.buttonColors()
                            }

                            ParticipantStatus.REJECTED -> {
                                ButtonDefaults.buttonColors(
                                    disabledContainerColor = MaterialTheme.colorScheme.error
                                )
                            }

                            else -> {
                                ButtonDefaults.buttonColors()
                            }
                        }
                    ) {
                        when (userParticipationStatus) {
                            ParticipantStatus.APPROVED -> {
                                Text(
                                    text = "Approved!",
                                )
                            }

                            ParticipantStatus.PENDING -> {
                                Text(
                                    text = "Pending",
                                )
                            }

                            ParticipantStatus.REJECTED -> {
                                Text(
                                    text = "Rejected",
                                )
                            }

                            else -> {
                                Text(
                                    text = "Apply",
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .height(40.dp)
                            .width(50.dp)
                            .shadow(4.dp, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            tripVm.clickEditOwnTrip()
                            navActions.navigateToCreateNewTravelProposal()
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Travel Proposal",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Button(
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .height(40.dp)
                            .width(50.dp)
                            .shadow(4.dp, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        onClick = {
                            tripVm.deleteTravelProposal()
                            tripVm.deleteNotification()
                            navActions.back()
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }
    if (showApplyDialog) {
        ApplyToTripDialog(
            proposal = proposal,
            tripPlanner = tripPlanner,
            onDismiss = { showApplyDialog = false },
            onApply = {
                tripVm.applyToTrip(guests = it)
                if (numApprovedParticipant >= 3) {
                    userProfileViewModel.updateBadgeTravelInPackProgress()
                }
                showApplyDialog = false
                userProfileViewModel.gainExp(5, ctx)
            })
    }
    if (showAcceptedDialog) {
        AcceptedDialog(
            onDismissRequest = { showAcceptedDialog = false },
            travelProposalVM = tripVm,
            travelProposal = proposal
        )
    }
    if (showPendingDialog) {
        PendingDialog(
            onDismissRequest = { showPendingDialog = false },
            travelProposalVM = tripVm,
            travelProposal = proposal
        )
    }
}

@Composable
fun AcceptedDialog(
    onDismissRequest: () -> Unit,
    travelProposalVM: TravelProposalViewModel,
    travelProposal: TravelProposal,
    userProfileViewModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),

            ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Changed your Mind?",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    onClick = {
                        travelProposalVM.removePendingParticipation(
                            travelProposal,
                            userProfileViewModel.loggedUser.value
                        )
                        onDismissRequest()
                    },
                    modifier = Modifier.size(height = 50.dp, width = 200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove Participation")
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(height = 50.dp, width = 200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cancel")
                }

            }
        }
    }
}

@Composable
fun PendingDialog(
    onDismissRequest: () -> Unit,
    travelProposalVM: TravelProposalViewModel,
    travelProposal: TravelProposal,
    userProfileViewModel: UserProfileViewModel = viewModel(factory = AppFactory)
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),

            ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Changed your Mind?",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    onClick = {
                        travelProposalVM.removePendingParticipation(
                            travelProposal,
                            userProfileViewModel.loggedUser.value
                        )
                        onDismissRequest()
                    },
                    modifier = Modifier.size(height = 50.dp, width = 200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove your Request")
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(height = 50.dp, width = 200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Cancel")
                }

            }
        }
    }
}

@Composable
fun ParticipantList(
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    onDismissRequest: () -> Unit,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
) {
    Log.d("ParticipantList", "Opening participant list dialog")
    val scrollState = rememberScrollState()
    val numApprovedParticipant = tripVm.getNumApprovedParticipants(travelProposal)
    var isLoading by remember { mutableStateOf(false) }
    val participants by tripVm.currentParticipants.collectAsState()
    val sortedParticipant = participants.sortedByDescending {
        when (it.status) {
            ParticipantStatus.APPROVED -> 1
            ParticipantStatus.PENDING -> 0
            ParticipantStatus.REJECTED -> -1
        }
    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {

                    Text("Manage Candidates", style = MaterialTheme.typography.titleMedium)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People, contentDescription = "People count"
                        )

                        Text("${numApprovedParticipant}/${travelProposal.maxParticipant}")
                    }
                }
                Text(
                    "Accept or Decline users that applied for your trip",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    if (!isLoading) {
                        sortedParticipant.forEach { participant ->
                            CandidateProfile(
                                candidate = participant.user,
                                guests = participant.invitedGuests,
                                isChecked = when {
                                    participant.status == ParticipantStatus.APPROVED -> true
                                    participant.status == ParticipantStatus.REJECTED -> null
                                    // participant.status == ParticipantStatus.PENDING -> false
                                    else -> false
                                },
                                tripVm = tripVm,
                                travelProposal = travelProposal,
                                isLandscape = isLandscape,
                                navActions = navActions,
                                bottomBarItem = bottomBarItem,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        modifier = Modifier.padding(16.dp),
                        onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

}

@Composable
fun CandidateProfile(
    candidate: UserProfile,
    guests: List<String>,
    isChecked: Boolean?,
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    isLandscape: Boolean,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
) {
    var showNullDialog by remember { mutableStateOf(false) }
    var acceptedDialog by remember { mutableStateOf(false) }
    var rejectedDialog by remember { mutableStateOf(false) }
    var showMiniProfile by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.clickable {
                showMiniProfile = true
            }
        ) {
            ProfilePicture(candidate, isLandScape = isLandscape, isCandidate = true)
        }
        Column(
            modifier = Modifier
                .weight(2f)
                .clickable {
                    showMiniProfile = true
                }
        ) {
            Text(
                text = candidate.name + if (guests.isNotEmpty()) " + ${guests.size} guests" else "",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            Text(
                text = candidate.bio,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, end = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            TriStateCheckbox(
                modifier = Modifier.padding(0.dp), state = when (isChecked) {
                    true -> ToggleableState.On
                    false -> ToggleableState.Off
                    null -> ToggleableState.Indeterminate
                }, colors = if (isChecked != null) {
                    CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.error,
                        uncheckedColor = MaterialTheme.colorScheme.onError
                    )
                }, onClick = {
                    if (isChecked == false) {
                        showNullDialog = true
                    }
                    if (isChecked == true) {
                        acceptedDialog = true
                    }
                    if (isChecked == null) {
                        rejectedDialog = true
                    }
                })
            Text(
                text = " % .2f".format(candidate.rating),
                modifier = Modifier.padding(0.dp),
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall
            )
        }

    }
    HorizontalDivider(Modifier.padding(horizontal = 16.dp))

    if (showNullDialog) {
        NullDialog(candidate, guests, tripVm, travelProposal) { newState ->
            showNullDialog = newState
        }
    }
    if (acceptedDialog) {
        AcceptedDialog(candidate, guests, tripVm, travelProposal) { newState ->
            acceptedDialog = newState
        }
    }
    if (rejectedDialog) {
        RejectedDialog(candidate, guests, tripVm, travelProposal) { newState ->
            rejectedDialog = newState
        }
    }
    if (showMiniProfile) {
        MiniProfileDialog(
            candidate, navActions = navActions, bottomBarItem = bottomBarItem
        ) { newState ->
            showMiniProfile = newState
        }
    }

}

@Composable
fun NullDialog(
    candidate: UserProfile,
    guests: List<String>,
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    onShow: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onShow(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.fillMaxSize()) {

                Text(
                    text = "Accept ${candidate.name} ?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The participant have ${guests.size} guests:" +
                            "\n${guests.joinToString(separator = ", ")}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = {
                            tripVm.approveParticipant(candidate, travelProposal)
                            onShow(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add to this trip")
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = {
                            tripVm.rejectParticipant(candidate, travelProposal)
                            onShow(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reject from this trip")
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = { onShow(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun AcceptedDialog(
    candidate: UserProfile,
    guests: List<String>,
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    onShow: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onShow(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.fillMaxSize()) {

                Text(
                    text = "Reject ${candidate.name} ?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The participant have ${guests.size} guests:" +
                            "\n${guests.joinToString(separator = ", ")}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = {
                            tripVm.rejectParticipant(candidate, travelProposal)
                            onShow(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Remove from this trip")
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = { onShow(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun RejectedDialog(
    candidate: UserProfile,
    guests: List<String>,
    tripVm: TravelProposalViewModel,
    travelProposal: TravelProposal,
    onShow: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onShow(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.fillMaxSize()) {

                Text(
                    text = "Accept ${candidate.name} ?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "The participant have ${guests.size} guests:" +
                            "\n${guests.joinToString(separator = ", ")}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = {
                            tripVm.approveParticipant(candidate, travelProposal)
                            onShow(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Accept from this trip")
                    }

                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(height = 60.dp, width = 200.dp),
                        onClick = { onShow(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun MiniProfileDialog(
    candidate: UserProfile,
    navActions: Navigation,
    bottomBarItem: BottomBarItem,
    onShow: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onShow(false) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfilePicture(
                            candidate,
                            isLandScape = false,
                            isDashboard = true
                        )
                        Text(
                            text = "Lvl. 3",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = candidate.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            Text(
                                text = candidate.rating.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 16.dp, end = 6.dp),
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "",
                                tint = StarColor
                            )
                        }

                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = candidate.bio,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 16.dp, end = 6.dp, top = 16.dp),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { onShow(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier.padding(8.dp), onClick = {
                            onShow(false)
                            navActions.navigateToOtherProfile(
                                candidate.uid,
                                bottomBarItem == BottomBarItem.MyTrips
                            )
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("View profile")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyToTripDialog(
    proposal: TravelProposal,
    tripPlanner: UserProfile,
    onDismiss: () -> Unit,
    onApply: (guests: List<String>) -> Unit
) {
    val guests = remember { mutableStateListOf<String>() }
    val scrollState = rememberScrollState()
    val maxNumOfGuests = 3
    val isMaxGuestsReached = guests.size >= maxNumOfGuests ||
            // minus one to consider also the user applying to the trip
            (proposal.maxParticipant - 1) <= guests.size

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {

                Text(
                    text = "Apply to this trip", style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(12.dp))

                /* trip summary */
                Text(
                    text = proposal.title, fontWeight = FontWeight.SemiBold
                )
                Text("Planned by ${tripPlanner.name}")

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { guests.add("Guest") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isMaxGuestsReached,
                    ) {
                        Icon(Icons.Default.People, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (!isMaxGuestsReached) "Add a guest" else "Max guests reached!")
                    }
                }

                guests.forEachIndexed { index, name ->
                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (name.isNotBlank()) name.first().uppercase() else "G",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(Modifier.width(12.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { guests[index] = it },
                            label = { Text("Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { guests.removeAt(index) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Remove")
                                }
                            })
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { onApply(guests.toList()) }) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMiniProfile() {
    val navController = rememberNavController()
    val navActions = Navigation(navController)

    MiniProfileDialog(
        UserProfile(
            name = "Giovanna",
            surname = "Azzurri",
            rating = 4.0f,
            bio = "This is my description"
        ),
        navActions,
        BottomBarItem.MyTrips
    ) { }
}