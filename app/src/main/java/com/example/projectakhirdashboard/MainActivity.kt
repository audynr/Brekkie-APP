package com.example.projectakhirdashboard

import RecipeViewModel
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projectakhirdashboard.Dashboard.CheckInSection
import com.example.projectakhirdashboard.Dashboard.DashboardPage
import com.example.projectakhirdashboard.Dashboard.InfoBoxSection
import com.example.projectakhirdashboard.Dashboard.LandingPage
import com.example.projectakhirdashboard.Login.ForgotPassPage
import com.example.projectakhirdashboard.Login.LoginPage
import com.example.projectakhirdashboard.Login.RegisterScreen
import com.example.projectakhirdashboard.Menu.CateringPage
import com.example.projectakhirdashboard.Menu.CookPage
import com.example.projectakhirdashboard.Menu.RecipeDetail
import com.example.projectakhirdashboard.Payment.DetailOrder
import com.example.projectakhirdashboard.Payment.PaymentScreen
import com.example.projectakhirdashboard.Payment.PaymentSuccess
import com.example.projectakhirdashboard.Tracking.GraphPage
import com.example.projectakhirdashboard.User.AutentikasiPage
import com.example.projectakhirdashboard.User.LikePage
import com.example.projectakhirdashboard.User.NotificationPage
import com.example.projectakhirdashboard.User.PolicyPage
import com.example.projectakhirdashboard.User.ProfilePage
import com.example.projectakhirdashboard.ui.theme.ProjectAkhirDashboardTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setContent {
            ProjectAkhirDashboardTheme {
                navController = rememberNavController()
                
                AppNavigation(navController = navController)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    val account = task.result
                    signInWithGoogle(account.idToken!!)
                } catch (e: Exception) {
                    Log.e("SignInError", "Google Sign-In failed", e)
                }
            }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        navController.navigate("home/${it.uid}/${it.displayName ?: ""}")
                        Toast.makeText(this, "Login Berhasil : ${it.displayName}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("AuthError", "Authentication failed: ${task.exception?.message}")
                }
            }
    }
}



@Composable
fun SocialLoginButton(iconId: Int, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .height(48.dp)
            .border(2.dp, Color(0xFF384662), RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(1.dp))
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = text,
                color = Color(0xFF384662),
                fontSize =16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
