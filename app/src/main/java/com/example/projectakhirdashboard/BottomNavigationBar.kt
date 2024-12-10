package com.example.projectakhirdashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    userId: String,
    displayName: String?
) {
    val items = listOf("Home", "Menu", "Stats", "Profile")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(20.dp), clip = false)
    ) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color.DarkGray,
            modifier = Modifier
                .height(80.dp)
                .background(Color.White)
                .clip(RoundedCornerShape(20.dp))
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        BottomNavItem(item = item, isSelected = item == selectedItem)
                    },
                    selected = selectedItem == item,
                    onClick = {
                        onItemSelected(item)
                        navigateTo(navController, item, userId, displayName)
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(item: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 80.dp else 45.dp)
            .offset(y = 11.dp)
            .background(
                color = if (isSelected) Color(0xff003049) else Color.Transparent,
                shape = RoundedCornerShape(15.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .width(50.dp)
                    .height(3.dp)
                    .background(Color.White, RoundedCornerShape(1.5.dp))
            )
        }
        Icon(
            painter = painterResource(id = when (item) {
                "Home" -> R.drawable.ic_home
                "Menu" -> R.drawable.ic_edit
                "Stats" -> R.drawable.ic_graph
                "Profile" -> R.drawable.ic_user
                else -> R.drawable.ic_home
            }),
            contentDescription = item,
            modifier = Modifier.size(28.dp),
            tint = if (isSelected) Color.White else Color.DarkGray
        )
    }
}

private fun navigateTo(navController: NavHostController, item: String, userId: String, displayName: String? = null) {
    when (item) {
        "Home" -> navController.navigate("home/$userId/${displayName ?: ""}") {
            launchSingleTop = true
            popUpTo("home/$userId/${displayName ?: ""}") { inclusive = true } // Update popUpTo as well
        }
        "Stats" -> navController.navigate("graph") {
            launchSingleTop = true
        }
        "Profile" -> navController.navigate("profile") { //  You might need arguments for profile too, depending on your setup
            launchSingleTop = true
        }
        "Menu" -> navController.navigate("menu") { //  You might need arguments for profile too, depending on your setup
            launchSingleTop = true
        }
    }
}