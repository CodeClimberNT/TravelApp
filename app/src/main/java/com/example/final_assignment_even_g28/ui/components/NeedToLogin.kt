package com.example.final_assignment_even_g28.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_assignment_even_g28.navigation.Navigation

@Composable
fun NeedToLogin(navAction: Navigation){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Please Login to access this Area",
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = { navAction.navigateToUserMainPage() },
            modifier = Modifier.size(height = 80.dp, width = 220.dp).padding(6.dp)
        ) {
            Text(
                text = "Go to login"
            )
        }
    }
}