package com.example.final_assignment_even_g28.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun CustomBottomBar(navActions: Navigation, selectedItem: BottomBarItem) {
    NavigationBar(
        tonalElevation = 16.dp,
        //containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationBarItem(
            selected = selectedItem == BottomBarItem.Explore,
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Explore",
                    tint =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    modifier = Modifier.size(38.dp)
                )
            },
            label = {
                Text(
                    "Explore",
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                )
            },
            onClick = {
                navActions.navigateToTravelList()
                //selectedItem.value = BottomBarItem.Explore
            },
        )
        NavigationBarItem(
            selected = selectedItem == BottomBarItem.MyTrips,
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "My Trips",
                    tint =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    modifier = Modifier.size(38.dp)
                )
            },
            label = {
                Text(
                    "My Trips",
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                )
            },
            onClick = {
                navActions.navigateToMyTravelProposalList()
                //selectedItem = BottomBarItem.MyTrips
            }
        )
        NavigationBarItem(
            selected = selectedItem == BottomBarItem.Profile,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    modifier = Modifier.size(38.dp)
                )
            },
            label = {
                Text(
                    "Profile",
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                )
            },
            onClick = {
                navActions.navigateToUserMainPage()
                //selectedItem = BottomBarItem.Profile
            }
        )
    }
}

enum class BottomBarItem {
    Explore,
    MyTrips,
    Profile
}
