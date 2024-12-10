package com.example.projectakhirdashboard.Menu

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.projectakhirdashboard.Data.Recipe
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@Composable
fun RecipeDetail(recipeId: String?, navController: NavHostController, recipe: Recipe?, userId: String, displayName: String?
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser ?: return
    val userId = currentUser.uid
    val database = FirebaseDatabase.getInstance().reference
    var isFavorite by remember { mutableStateOf(false) }

    if (recipe == null) {
        Text("Recipe not found", style = MaterialTheme.typography.bodyLarge, color = Color.Red)
        return
    }

    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(recipe.image, "drawable", context.packageName)

    LaunchedEffect(recipe.title) {
        val favoriteSnapshot = database.child("users").child(userId).child("favorites").child(recipe.title).get().await()
        isFavorite = favoriteSnapshot.exists()
    }

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 170.dp, bottomEnd = 170.dp))
                    .background(Color(0xFF3C5686))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) { // Make it clickable
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )

                    }
                    IconToggleButton(
                        checked = isFavorite,
                        onCheckedChange = { checked ->
                            isFavorite = checked
                            val recipeData = mapOf(
                                "description" to recipe.description,
                                "ingredients" to recipe.ingredient,
                                "time" to recipe.time,
                                "image" to recipe.image
                            )

                            if (checked) {
                                database.child("users").child(userId).child("favorites").child(recipe.title).setValue(recipeData)
                                    .addOnCompleteListener {
                                        val message = if (it.isSuccessful) "Added to favorites" else "Failed to add to favorites"
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                database.child("users").child(userId).child("favorites").child(recipe.title).removeValue()
                                    .addOnCompleteListener {
                                        val message = if (it.isSuccessful) "Removed from favorites" else "Failed to remove from favorites"
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "Time Icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recipe.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Box(
                modifier = Modifier
                    .offset(y = (-70).dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_profilepict),
                    error = painterResource(R.drawable.ic_profilepict),
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Section(
                title = "Deskripsi",
                content = recipe.description
            )
            Spacer(modifier = Modifier.height(16.dp))

            Section(
                title = "Bahan-bahan",
                content = recipe.ingredient
            )
            Spacer(modifier = Modifier.height(16.dp))

            Section(
                title = "Langkah memasak",
                content = recipe.steps.joinToString(separator = "\n") { step -> "- ${step.instruction}" }
            )
        }
    }
}

@Composable
fun Section(title: String, content: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3C5686),
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(
                    color = Color(0xFFFED9AE),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        )
    }
}