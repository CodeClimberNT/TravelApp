package com.example.final_assignment_even_g28.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_assignment_even_g28.R
import com.example.final_assignment_even_g28.data_class.Badge
import com.example.final_assignment_even_g28.data_class.isCompleted
import com.example.final_assignment_even_g28.data_class.toImageVector
import com.example.final_assignment_even_g28.ui.theme.StarColor
import com.example.final_assignment_even_g28.utils.AppFactory
import com.example.final_assignment_even_g28.viewmodel.UserProfileViewModel


@Composable
fun BadgeIcon(badge: Badge, isMiniBadge: Boolean, modifier: Modifier = Modifier) {
    val isCompleted = badge.isCompleted()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                if (isCompleted) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isCompleted) StarColor else MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape
            )
            .size(
                if (isMiniBadge) 36.dp else 80.dp
            )


    ) {
        Icon(
            imageVector = badge.icon.toImageVector(),
            contentDescription = badge.title,
            tint = if (isCompleted) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp)
        )
    }
}

@Composable
fun BadgeIconWithInfo(
    badge: Badge, modifier: Modifier = Modifier,
    isMiniBadge: Boolean = true
) {
    var showInfoBadge by remember { mutableStateOf(false) }
    if (isMiniBadge) {
        IconButton(
            onClick = { showInfoBadge = true }, modifier = modifier.size(
                36.dp
            )
        ) {
            BadgeIcon(badge = badge, isMiniBadge = isMiniBadge)
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable(onClick = { showInfoBadge = true }),
        ) {
            BadgeIcon(badge = badge, isMiniBadge = false, modifier)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = badge.title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    "Info",
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

        }
    }

    if (showInfoBadge) {
        InfoBadge(
            badge = badge,
            isMiniBadge = isMiniBadge,
            onDismiss = { showInfoBadge = false })
    }
}

@Composable
fun InfoBadge(
    badge: Badge,
    isMiniBadge: Boolean = true,
    userVm: UserProfileViewModel = viewModel(factory = AppFactory),
    onDismiss: () -> Unit
) {
    val isCompleted = badge.isCompleted()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeIcon(badge, isMiniBadge = false)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(R.string.close), color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (!isMiniBadge) {
                        TextButton(
                            onClick = {
                                userVm.updateBadge(badge)
                                onDismiss()
                            },
                            enabled = isCompleted
                        ) {
                            if (isCompleted) {
                                Text(stringResource(R.string.equip_this_badge))
                            } else {
                                Text(
                                    "${badge.progress.current}/${badge.progress.total} ${
                                        stringResource(
                                            R.string.to_unlock
                                        )
                                    }", color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                }
                Spacer(Modifier.height(24.dp))

            }
        }
    }
}