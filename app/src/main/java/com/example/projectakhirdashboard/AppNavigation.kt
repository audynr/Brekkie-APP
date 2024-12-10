package com.example.projectakhirdashboard

import RecipeViewModel
import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier = Modifier, navController: NavController) {
    var auth: FirebaseAuth

    val navController = rememberNavController()
    auth = FirebaseAuth.getInstance()

    val startDestination = if (auth.currentUser != null) {
        "home/${auth.currentUser!!.uid}/${auth.currentUser!!.displayName ?: ""}"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginPage(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("forgotpass") {
            ForgotPassPage(navController = navController, auth = auth)
        }
        composable("home/{userId}/{displayName}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            LandingPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("graph") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            GraphPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("profile") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            ProfilePage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("policy") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            PolicyPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("like/{userId}/{displayName}", arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
            navArgument("displayName") { type = NavType.StringType; nullable = true } // displayName can be null
        )) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName")
            LikePage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("notif") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            val context = LocalContext.current
            NotificationPage(navController = navController, context = context, userId = userId, displayName = displayName)
        }
        composable("passauth") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            AutentikasiPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable("menu") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            CookPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable(route = "recipe_detail/{recipeId}", arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""

            val viewModel: RecipeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application)
            )
            val recipeData by viewModel.recipeData.collectAsState()
            val recipe = recipeData.recipes.values.flatten().find { it.id == recipeId }

            RecipeDetail(recipeId = recipeId, navController = navController, recipe = recipe, userId = userId, displayName = displayName
            )
        }
        composable("catering") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            CateringPage(navController = navController, userId = userId, displayName = displayName)
        }
        composable(route = "detail_order/{packageId}/{packageName}/{packagePrice}", arguments = listOf(
            navArgument("packageId") { type = NavType.StringType },
            navArgument("packageName") { type = NavType.StringType },
            navArgument("packagePrice") { type = NavType.StringType }
        )
        ) { backStackEntry ->
            val packageId = backStackEntry.arguments?.getString("packageId") ?: ""
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            val packagePrice = backStackEntry.arguments?.getString("packagePrice") ?: ""
            val context = LocalContext.current
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            DetailOrder(
                navController = navController,
                context = context,
                userId = userId,
                displayName = displayName,
                packageId = packageId.toString(),
                packageName = packageName.toString(),
                packagePrice = packagePrice.toString()
            )
        }
        composable(
            route = "payment_screen/{packageId}/{packageName}/{packagePrice}/{phone}/{address}/{deliveryTime}/{additionalInfo}",
            arguments = listOf(
                navArgument("packageId") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType },
                navArgument("packagePrice") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("address") { type = NavType.StringType },
                navArgument("deliveryTime") { type = NavType.StringType },
                navArgument("additionalInfo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            val packageId = backStackEntry.arguments?.getString("packageId") ?: ""
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            val packagePrice = backStackEntry.arguments?.getString("packagePrice") ?: ""
            val phoneFromDetail = backStackEntry.arguments?.getString("phone")?.let { Uri.decode(it) }
            val addressFromDetail = backStackEntry.arguments?.getString("address")?.let { Uri.decode(it) }
            val deliveryTimeFromDetail = backStackEntry.arguments?.getString("deliveryTime")?.let { Uri.decode(it) }
            val additionalInfo = backStackEntry.arguments?.getString("additionalInfo")?.let { Uri.decode(it) }
            PaymentScreen(navController = navController, packageId = packageId, packageName = packageName,
                packagePrice = packagePrice, userId = userId, displayName = displayName, phoneFromDetail = phoneFromDetail,  addressFromDetail = addressFromDetail,
                deliveryTimeFromDetail = deliveryTimeFromDetail, additionalInfo = additionalInfo)
        }
        composable("payment_success") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val displayName = backStackEntry.arguments?.getString("displayName") ?: ""
            PaymentSuccess(navController = navController, userId = userId, displayName = displayName)
        }
    }
}