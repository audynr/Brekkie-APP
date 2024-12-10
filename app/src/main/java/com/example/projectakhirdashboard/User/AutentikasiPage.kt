package com.example.projectakhirdashboard.User

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.R
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth

@Composable
fun AutentikasiPage(modifier: Modifier = Modifier, navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Profile"
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf<String?>(null) }

    val mContext = LocalContext.current

    auth = Firebase.auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Password &",
            fontWeight = FontWeight.Bold,
            color = Color(0xff002f49),
            fontSize = 36.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Autentikasi",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDB875),
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .height(3.dp)
                    .width(1000.dp)
                    .background(Color(0xFFffb16d))
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Column {
            Text(text = "Password Lama", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
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

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(text = "Password Baru", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
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

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .height(45.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3c5687)),
            onClick = {
                if (newPassword != confirmPassword) {
                    Toast.makeText(mContext, "Password baru dan konfirmasi tidak cocok.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (newPassword.length < 8) {
                    Toast.makeText(mContext, "Password baru harus memiliki minimal 8 karakter.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                changePassword(mContext, oldPassword, newPassword) { success ->
                    if (success) {
                        oldPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                    }
                }
            }
        ) {
            Text("UBAH PASSWORD", color = Color.White)
        }
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

fun changePassword(context: Context, oldPassword: String, newPassword: String, callback: (Boolean) -> Unit) {
    val user = auth.currentUser
    if (user != null) {
        val email = user.email
        if (email != null) {
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(context, "Password berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                            callback(true)
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal memperbarui password: ${updateTask.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            callback(false)
                        }
                    }
                } else {
                    Toast.makeText(context, "Password lama salah.", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
        } else {
            Toast.makeText(context, "Email tidak ditemukan. Coba lagi.", Toast.LENGTH_SHORT).show()
            callback(false)
        }
    } else {
        Toast.makeText(context, "Pengguna tidak masuk.", Toast.LENGTH_SHORT).show()
        callback(false)
    }
}

