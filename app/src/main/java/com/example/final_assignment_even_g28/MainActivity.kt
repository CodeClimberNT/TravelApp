package com.example.final_assignment_even_g28

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.final_assignment_even_g28.navigation.NavGraph
import com.example.final_assignment_even_g28.ui.theme.MadTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var deleteAllTravels = false
//        var deleteAllBadges = false
//        var initializeAllBadges = false
        enableEdgeToEdge()
        setContent {
            MadTheme {
//                if (!deleteAllTravels) {
//                    val viewModel: TravelProposalViewModel = viewModel(factory = AppFactory)
//                    viewModel.deleteAllProposals()
//                    deleteAllTravels = true
//                }
//                if (!deleteAllBadges) {
//                    val viewModel: UserProfileViewModel = viewModel(factory = AppFactory)
//                    viewModel.deleteAllBadges()
//                    deleteAllBadges = true
//                }
//                if (!initializeAllBadges) {
//                    val viewModel: UserProfileViewModel = viewModel(factory = AppFactory)
//                    viewModel.initializeBadgesToAllUsers()
//                    initializeAllBadges = true
//                }
                NavGraph(
                    context = LocalContext.current
                )
            }
        }
    }
}
//41f59ea7-9b5a-4636-8c64-db5caf77530d
/*
* id
"4"
(stringa)



invitedGuests
(array)


0
"Franco"
(stringa)


status
"APPROVED"
* */