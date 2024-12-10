package com.example.projectakhirdashboard.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DatePicker(
//    currentDate: LocalDate = LocalDate.now(),
//    onDateSelected: (LocalDate) -> Unit
//) {
//    val year = currentDate.year
//    val month = currentDate.monthValue
//    val day = currentDate.dayOfMonth
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .background(Color.White),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Title
//        Text(
//            text = "Agustus 2024",
//            fontSize = 24.sp,
//            color = Color(0xFF3C5687),
//            fontWeight = FontWeight.ExtraBold,
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Start
//        )
//
//        // Calendar Grid
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(7),
//            modifier = Modifier.fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(31) { index ->
//                val date = LocalDate.of(year, month, index + 1)
//                DateItem(
//                    date = date,
//                    isSelected = date.dayOfMonth == day,
//                    onClick = { onDateSelected(date) }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Done Button
//        Button(
//            onClick = { /* Handle form submission */ },
//            shape = RoundedCornerShape(8.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//        ) {
//            Text(text = "Done", fontSize = 16.sp)
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DateItem(
//    date: LocalDate,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .clickable { onClick() }
//            .size(40.dp)
//            .background(
//                color = if (isSelected) Color(0xFF3C5687) else Color.Transparent,
//                shape = CircleShape
//            ),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = date.dayOfMonth.toString(),
//            color = if (isSelected) Color.White else Color(0xFF3C5687),
//            fontSize = 14.sp
//        )
//    }
//}

