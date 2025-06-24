package com.example.final_assignment_even_g28.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.data_class.Notification
import com.example.final_assignment_even_g28.navigation.Navigation
import com.example.final_assignment_even_g28.navigation.handleNotificationNavigation
import com.example.final_assignment_even_g28.ui.theme.LocalNotificationBellColors
import com.example.final_assignment_even_g28.ui.theme.MadTheme
import com.example.final_assignment_even_g28.ui.theme.NewNotificationBackgroundReadColor
import com.example.final_assignment_even_g28.ui.theme.NewNotificationBackgroundUnReadColor
import com.example.final_assignment_even_g28.ui.theme.NewNotificationBorderReadColor
import com.example.final_assignment_even_g28.ui.theme.NewNotificationBorderUnReadColor
import com.example.final_assignment_even_g28.ui.theme.OldNotificationBackgroundColor
import com.example.final_assignment_even_g28.ui.theme.OldNotificationBorderColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.utils.toDateFormat
import com.example.final_assignment_even_g28.viewmodel.TravelProposalViewModel



@Composable
fun NotificationBell(navActions: Navigation, tripVm : TravelProposalViewModel = viewModel(factory = AppFactory)) {
    var expanded by remember { mutableStateOf(false) }

    val notifications by tripVm.notifications.collectAsState(initial = emptyList())

    val unreadCount by tripVm.unreadNotificationCount.collectAsState()

    Box(contentAlignment = Alignment.TopEnd) {
        Box {
            IconButton(onClick = { expanded = !expanded
               }) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 20.dp, y = 4.dp)
                        .size(18.dp)
                        .background(Color.Red, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopEnd,
                offset = IntOffset(0, 120),
                properties = PopupProperties(focusable = true),
                onDismissRequest = { expanded = false }
            ) {
                NotificationDropdown(
                    notifications = notifications,
                    hasUnread = unreadCount,
                    modifier = Modifier
                        .width(280.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(0.dp),
                    navActions = navActions,
                    tripVm,
                    onDismiss = { expanded = false }
                )
            }
        }
    }
}

@Composable
fun NotificationDropdown(
    notifications: List<Notification>,
    hasUnread: Int,
    modifier: Modifier = Modifier,
    navActions: Navigation,
    tripVm: TravelProposalViewModel,
    onDismiss: () -> Unit
) {
    val sortedNotifications = tripVm.getSortedNotifications()
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notifications", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                if (hasUnread > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, shape = CircleShape)
                    )
                }
            }
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 12.dp, top = 12.dp), modifier = Modifier.height(300.dp)) {
            items(sortedNotifications) { notification ->
                NotificationItem(notification, navActions,tripVm, onDismiss)
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    navActions: Navigation,
    tripVm: TravelProposalViewModel,
    onDismiss: () -> Unit
) {
    val currentUserId = tripVm.getCurrentUserUId()
    val isRecent = notification.isRecent()
    val isReadFromDB = notification.isRead(currentUserId)

    val notificationColors = LocalNotificationBellColors.current

    val backgroundColor = when {
        isRecent && !isReadFromDB -> notificationColors.backgroundColor.unRead
        !isRecent && !isReadFromDB -> notificationColors.backgroundColor.read
        else -> notificationColors.backgroundColor.old
    }

    val borderColor = when {
        isRecent && !isReadFromDB -> notificationColors.borderColor.unRead
        !isRecent && !isReadFromDB -> notificationColors.borderColor.read
        else -> notificationColors.borderColor.old
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable(onClick = {
                onDismiss()
                //Log.d("NotificationItem", "Notification clicked: $id, isReadFromDB: $isReadFromDB, isRecent: $isRecent , readList: $readList, currentUserId: $currentUserId")
                if (!isReadFromDB) {
                    tripVm.markNotificationAsRead(notification.id)
                }
                handleNotificationNavigation(notification, navActions)
            })
            .background(color = backgroundColor, shape = MaterialTheme.shapes.small)
            .border(2.dp, borderColor, MaterialTheme.shapes.small)
            .padding(12.dp)
    ) {
        Text(
            text = tripVm.getNotificationMessage(notification.type,notification.title, false),
            fontWeight = if (!isReadFromDB) FontWeight.Bold else FontWeight.Normal
        )
        Text(if( isRecent ) "Now" else notification.timestamp.toDateFormat(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}