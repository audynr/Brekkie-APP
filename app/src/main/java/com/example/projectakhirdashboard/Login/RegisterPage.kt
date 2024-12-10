package com.example.projectakhirdashboard.Login

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth

@Composable
fun RegisterScreen(navController: NavController, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val mContext = LocalContext.current

    auth = Firebase.auth

    fun registerWithEmail(email: String, password: String) {
        if (password != confirmPassword) {
            Toast.makeText(mContext, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Toast.makeText(
                                    mContext,
                                    "Registrasi Berhasil: ${user.email}, Username: $username",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Navigate to the dashboard, passing userId and username:
                                navController.navigate("home/${user!!.uid}/$username")
                            } else {
                                Toast.makeText(mContext, "Registrasi Gagal: ${profileTask.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        mContext,
                        "Registrasi Gagal: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun googleSignIn() {
        val googleSignInClient = GoogleSignIn.getClient(
            mContext,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
        val signInIntent = googleSignInClient.signInIntent
        (mContext as? Activity)?.startActivityForResult(signInIntent, 100)
    }



    Box(modifier = Modifier
        .fillMaxSize())
    {
        Image(painter = painterResource(id = R.drawable.blue_bg), contentDescription = null)

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "REGISTER",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            ),
            modifier = Modifier
                .padding(top = 40.dp, bottom = 8.dp)
        )
        Text(
            text = "Daftar sekarang untuk menikmati fitur lengkap Brekkie",
            fontSize = 12.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))

        Column {
            Text(
                text = "Username",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Masukan username Anda di sini", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFF384662), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF384662),
                    unfocusedTextColor = Color(0xFF384662),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column {
            Text(
                text = "E-mail",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                label = { Text(text = "Masukan email Anda di sini", color = Color.Gray) },
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

        Column {
            Text(text = "Password", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Minimal 8 karakter", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFF384662), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF384662),
                    unfocusedTextColor = Color(0xFF384662),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.lock), contentDescription = null, tint = Color.Gray)
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(
                            painter = painterResource(id = if (passwordVisibility) R.drawable.seen else R.drawable.invisible),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp),
                            tint = Color.Gray
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Column {
            Text(text = "Konfirmasi Password", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(text = "Minimal 8 karakter", color = Color.Gray) },
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
                    Icon(painter = painterResource(id = R.drawable.lock), contentDescription = null, tint = Color.Gray)
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(
                            painter = painterResource(id = if (passwordVisibility) R.drawable.seen else R.drawable.invisible),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp),
                            tint = Color.Gray
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                registerWithEmail(email, password)
                navController.navigate("login")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF3C5687)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "REGISTER", fontSize = 18.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Sudah punya akun? ", color = Color.Black, fontWeight = FontWeight.Normal)
            Text(
                modifier = Modifier
                    .clickable { navController.navigate("login") },
                text = "Log in!",
                style = TextStyle(
                    textDecoration = TextDecoration.Underline
                ),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
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
                text = "Atau masuk dengan",
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
        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(iconId = R.drawable.google, text = "Masuk dengan Google") {
            googleSignIn()
        }
        Spacer(modifier = Modifier.height(40.dp))

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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}