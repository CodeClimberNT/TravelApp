package com.example.final_assignment_even_g28.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EditableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    suffix: String? = null,
    isSingleLine: Boolean = true,
    color: Color
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = color) },
            isError = isError,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = color
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = color
                    )
                }
            } else {
                null
            },
            suffix = if (suffix != null) {
                {
                    Text(
                        text = suffix,
                        color = color
                    )

                }
            } else {
                null
            },
            trailingIcon = if (isError) {
                {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Invalid input",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = color
                    )
                }
            } else {
                null
            },
            singleLine = isSingleLine,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EditableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {},
    suffix: String? = null,
    isSingleLine: Boolean = true,
    textFieldModifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
            suffix = if (suffix != null) {
                {
                    Text(text = suffix)
                }
            } else {
                null
            },
            trailingIcon = if (isError) {
                {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Invalid input",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else if (trailingIcon != null) {
                {
                    IconButton(
                        onClick = onTrailingIconClick,
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = "Delete Title",
                        )
                    }
                }
            } else {
                null
            },
            singleLine = isSingleLine,
            modifier = textFieldModifier.then(Modifier.fillMaxWidth())
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}