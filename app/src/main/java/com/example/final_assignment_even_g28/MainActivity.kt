package com.example.final_assignment_even_g28

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.final_assignment_even_g28.navigation.NavGraph
import com.example.final_assignment_even_g28.ui.theme.MadTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var deleteAllTravels = false
//        var deleteAllBadges = false
//        var initializeAllBadges = false
//        var testNotification = false
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
//                 if (!testNotification) {
//                    val viewModel: TravelProposalViewModel = viewModel(factory = AppFactory)
//                    viewModel.testNotificationApply()
//                     testNotification = true
//                }
                NavGraph()
            }
        }
    }
}
