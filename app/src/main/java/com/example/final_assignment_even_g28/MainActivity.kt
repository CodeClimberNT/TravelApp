package com.example.final_assignment_even_g28

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.navigation.NavGraph
import com.example.final_assignment_even_g28.ui.theme.MadTheme
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var deleteAllTravels = false
        var deleteAllBadges = false
        enableEdgeToEdge()
        setContent {
            MadTheme {
//                if (!deleteAllTravels) {
//                    val viewModel: TravelProposalViewModel = viewModel(factory = AppFactory)
//                    viewModel.deleteAllProposals()
//                    deleteAllTravels = true
//                }
                if (!deleteAllBadges) {
                    val viewModel: UserProfileViewModel = viewModel(factory = AppFactory)
                    viewModel.deleteAllBadges()
                    deleteAllBadges = true
                }
                NavGraph(
                    context = LocalContext.current
                )
            }
        }
    }
}