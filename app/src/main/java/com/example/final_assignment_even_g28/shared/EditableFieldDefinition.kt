package com.example.final_assignment_even_g28.shared

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

// Future proof (I hope) for the Trip Planning form
data class EditableFieldDefinition(
    val label: String,
    val value: String,
    val errorMessage: String = "",
    val editable: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val onValueChange: (String) -> Unit,
    val color: Color = Color.Transparent,
)

data class InfoFieldDefinition(
    val label: String,
    val value: String,
)