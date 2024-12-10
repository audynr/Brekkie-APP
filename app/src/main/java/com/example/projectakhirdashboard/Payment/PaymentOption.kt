package com.example.projectakhirdashboard.Payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOption(
    imageResource: Int,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        readOnly = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = imageResource),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            RadioButton(
                selected = selected,
                onClick = { onClick() },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF005C92))
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = Color.Gray,
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Blue,
            unfocusedTextColor = Color.Gray,
            focusedTextColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
        )
    )
}