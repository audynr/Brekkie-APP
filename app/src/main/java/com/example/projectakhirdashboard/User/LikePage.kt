package com.example.projectakhirdashboard.User

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.Data.FavoriteRecipe
import com.example.projectakhirdashboard.Data.Recipe
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

@Composable
fun LikePage(modifier: Modifier = Modifier, navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Profile"
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference
    var isLoading by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    val favoriteRecipesState = remember { mutableStateOf(mutableListOf<Recipe>()) }
    val favoriteRecipes by remember { derivedStateOf { favoriteRecipesState.value } }

    LaunchedEffect(userId) {
        try {
            val userFavoritesRef = database.child("users").child(userId).child("favorites")
            val favoritesSnapshot = userFavoritesRef.get().await()

            Log.d("Favorites", "UserId: $userId")

            if (favoritesSnapshot.exists()) {
                favoriteRecipes.clear()
                favoritesSnapshot.children.forEach { favorite ->
                    val title = favorite.key ?: "" // Title of the recipe
                    val recipeData = favorite.getValue(FavoriteRecipe::class.java) // Fetch details

                    Log.d("Favorites", "Favorite title: $title")
                    recipeData?.let {
                        it.title = title
                        it.isFavorite = true
                        val recipe = Recipe(
                            id = it.id,
                            image = it.image,
                            title = title,
                            ingredient = it.ingredients,
                            description = it.description,
                            time = it.time,
                            steps = listOf(),
                            isFavorite = true
                        )
                        favoriteRecipes.add(recipe)
                        Log.d("Favorites", "Recipe added: $it")
                    }
                }
            } else {
                Log.d("Favorites", "No favorites found for user $userId")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading favorites: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "List Sarapan",
                fontWeight = FontWeight.Bold,
                color = Color(0xff002f49),
                fontSize = 36.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ingin Dicoba",
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

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(favoriteRecipes.size) { index ->
                    val recipe = favoriteRecipes[index]
                    BreakfastCardItem(
                        title = recipe.title,
                        description = recipe.description,
                        ingredients = recipe.ingredient,
                        timeEstimate = recipe.time,
                        imageRes = getImageResource(recipe.image, context),
                        cardColor = Color.White,
                        isFavorite = recipe.isFavorite,
                        onFavoriteToggle = { newValue ->
                            if (!newValue) {
                                val newRecipes = favoriteRecipes.toMutableList()
                                newRecipes.removeAt(index)
                                favoriteRecipesState.value = newRecipes
                            }
                            val recipeRef = database.child("users").child(userId).child("favorites").child(recipe.title)
                            if (newValue) {
                                recipeRef.setValue(recipe)
                            } else {
                                recipeRef.removeValue()
                            }
                        },
                        onRemove = {},
                        navController = navController,
                        recipe = recipe
                    )
                }
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
}

fun getImageResource(imageName: String, context: Context): Int {
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    if (resId == 0) {
        Log.e("Image Loading", "Image resource not found: $imageName")
    }
    return if (resId != 0) resId else R.drawable.ic_profilepict // Return a default image if not found
}

@Composable
fun BreakfastCardItem(
    title: String,
    description: String,
    ingredients: String,
    timeEstimate: String,
    imageRes: Int,
    cardColor: Color,
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    onRemove: () -> Unit,
    navController: NavHostController,
    recipe: Recipe,
    modifier: Modifier = Modifier
) {
    val cardHeight = 290.dp

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                navController.navigate("recipe_detail/${recipe.id}")
                Log.d("Routes", "recipe_detail/${recipe.id}")
            }
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(10.dp))
            .height(cardHeight),
        backgroundColor = cardColor,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (image, titleRef, timeRef, favoriteIcon, descriptionRef) = createRefs()

            AsyncImage(
                model = recipe.image,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Title Section
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1, // Restrict title to a single line
                overflow = TextOverflow.Ellipsis, // Ellipsis for overflow
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(image.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Description Section
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                maxLines = 2, // Restrict description to two lines
                overflow = TextOverflow.Ellipsis, // Ellipsis for overflow
                modifier = Modifier.constrainAs(descriptionRef) {
                    top.linkTo(titleRef.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Time Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.constrainAs(timeRef) {
                    top.linkTo(descriptionRef.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_schedule),
                    contentDescription = "Time Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = timeEstimate,
                    style = MaterialTheme.typography.caption,
                    color = Color.Black
                )
            }

            // Favorite Icon Section
            IconButton(
                onClick = {
                    onFavoriteToggle(!recipe.isFavorite)
                    if (!recipe.isFavorite) onRemove()
                },
                modifier = Modifier.constrainAs(favoriteIcon) {
                    top.linkTo(timeRef.bottom, margin = 8.dp)
                    end.linkTo(parent.end)
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                    tint = if (isFavorite) Color(0xFFFEB06D) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}







