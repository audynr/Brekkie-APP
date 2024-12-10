package com.example.projectakhirdashboard.Payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectakhirdashboard.R

@Composable
fun PaymentSuccess(navController: NavController, userId: String, displayName: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ellipse_1),
                contentDescription = null
            )
            Image(
                painter = painterResource(id = R.drawable.ellipse_2),
                contentDescription = null
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Pembayaran Berhasil!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3C5687),
                                Color(0xFFF7BD73)
                            )
                        )
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Sarapanmu sebentar lagi akan diantar, silakan ditunggu ..",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Button(
                    onClick = { navController.navigate("home/$userId/${displayName ?: ""}") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3C5687),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 11.dp,
                            end = 11.dp
                        )
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text(text = "Kembali ke halaman utama", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

