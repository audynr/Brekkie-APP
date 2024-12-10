package com.example.projectakhirdashboard.Login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectakhirdashboard.R
import com.example.projectakhirdashboard.SocialLoginButton
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPassPage(navController: NavController, auth: FirebaseAuth, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val mContext = LocalContext.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 50.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 160.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOGIN",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF384662)
                    ),
                    modifier = Modifier
                        .padding(top = 32.dp, bottom = 8.dp)
                )
                Text(
                    text = "Sebelum mulai, ayo Sign In terlebih dahulu!",
                    fontSize = 12.sp,
                    color = Color(0xFF384662),
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Atur Ulang Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF384662)
                )
                Spacer(modifier = Modifier.height(30.dp))

                Column {
                    Text(
                        text = "E-mail",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Alamat E-mail", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF384662),
                            unfocusedTextColor = Color(0xFF384662),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color(0xFF384662), RoundedCornerShape(8.dp)),
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.envelope), contentDescription = null, tint = Color.Gray)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isLoading = true
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(mContext, "Email reset link sent to $email", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorMessage = task.exception?.message ?: "Unknown error"
                                    Toast.makeText(mContext, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                                    Log.e("ForgotPass", "Error sending reset email:", task.exception)
                                }
                            }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF3C5687)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text(text = "KIRIM", fontSize = 18.sp, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))

                Button(
                    onClick = {
                        navController.navigate("login")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(start = 16.dp, end = 16.dp)
                        .border(2.dp, Color(0xFF384662), RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "LOGIN", fontSize = 18.sp, color = Color(0xFF384662))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                    Text(
                        text = "Atau lanjutkan dengan",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                SocialLoginButton(iconId = R.drawable.google, text = "Masuk dengan Google") {
                }
                Spacer(modifier = Modifier.height(72.dp))

                Text(text = "Dengan masuk atau mendaftar, Anda menyetujui", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        modifier = Modifier
                            .clickable {  },
                        text = "Ketentuan Layanan",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(text = " dan ", fontSize = 12.sp, )
                    Text(
                        modifier = Modifier
                            .clickable {  },
                        text = "Kebijakan Privasi",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

        }
    }

}