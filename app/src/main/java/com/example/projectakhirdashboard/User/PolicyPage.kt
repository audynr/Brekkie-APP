package com.example.projectakhirdashboard.User

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.R

@Composable
fun PolicyPage(modifier: Modifier = Modifier, navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Profile"
    val gradientColors = listOf(Color(0xFF5374A1), Color(0xffcbb7b0), Color(0xFFF7BD73))

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(bottom = 100.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            brush = Brush.horizontalGradient(gradientColors),
                            fontWeight = FontWeight.Bold,
                            fontSize = 50.sp
                        )
                    ) {
                        append("Brekkie:")
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Kebijakan Privasi",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 28.sp,
                        color = Color(0xff5374a1)
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .height(3.dp)
                        .width(150.dp)
                        .background(Color(0xFFffb16d))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Section(
                title = "Lorem Ipsum",
                content = stringResource(id = R.string.paragraf)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Section(
                title = "1. Lorem Ipsum",
                content = stringResource(id = R.string.paragraf2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Section(
                title = "2. Lorem Ipsum",
                content = stringResource(id = R.string.paragraf3)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavigationBar(
                navController = navController,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                userId = userId,
                displayName = displayName
            )
        }
    }
}

@Composable
fun Section(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xff5374a1)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Section Content
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                textAlign = TextAlign.Justify,
                color = Color.Gray
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}




