package com.example.projectakhirdashboard.Menu

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.Data.Recipe
import com.example.projectakhirdashboard.Data.RecipeData
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.perf.util.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await

@Composable
fun CookPage(navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Menu"
    val auth = FirebaseAuth.getInstance()
    val userName = displayName?.ifEmpty { auth.currentUser?.displayName ?: "" }
    var isCatering by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(0) }

    var searchText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val recipeData = LoadRecipes(context)

    if (recipeData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
        }
    } else {
        val filteredRecipes by remember(searchText, selectedCategory) {
            derivedStateOf {
                val recipesForCategory = when (selectedCategory) {
                    0 -> recipeData.recipes["salad"] ?: emptyList()
                    1 -> recipeData.recipes["drink"] ?: emptyList()
                    2 -> recipeData.recipes["rice"] ?: emptyList()
                    3 -> recipeData.recipes["bread"] ?: emptyList()
                    else -> emptyList()
                }

                if (searchText.isBlank()) {
                    recipesForCategory
                } else {
                    recipesForCategory.filter {
                        it.title.lowercase(Locale.getDefault()).contains(searchText.lowercase(Locale.getDefault()))
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Halo, ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFFEB06D),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(userName)
                        }

                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3C5686)
                    )
                )

                SwitchSlider(isCatering, { isCatering = it }, navController)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ingin makan apa hari ini?",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                onSearch = { query ->
                    searchText = query
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            FoodCategorySection(selectedCategory) { newCategory ->
                selectedCategory = newCategory
            }

            when (selectedCategory) {
                0 -> {
                    FoodCardRow(recipes = filteredRecipes, navController = navController)
                }
                1 -> {
                    FoodCardRow(recipes = filteredRecipes, navController = navController)
                }
                2 -> {
                    FoodCardRow(recipes = filteredRecipes, navController = navController)
                }
                3 -> {
                    FoodCardRow(recipes = filteredRecipes, navController = navController)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { newText ->
            searchText = newText
            onSearch(newText)
        },
        placeholder = {
            Text(
                "Cari resep...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = Color.White,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color(0xFF5374A0),
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White
        ),
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        },

        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(searchText)
            }

        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(24.dp))
    )
}

@Composable
fun SwitchSlider(
    isCatering: Boolean,
    onSwitch: (Boolean) -> Unit,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .width(120.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF5374A0),
                        Color(0xFFFEB06D)
                    )
                )
            )
            .clickable {
                onSwitch(!isCatering)
                if (!isCatering) {
                    navController.navigate("catering")
                }
            }
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "katering",
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = "sendiri",
                fontSize = 12.sp,
                color = if (isCatering) Color(0xFFFEB06D) else Color(0xFF5374A0),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun FoodCategorySection(selectedCategory: Int, onCategorySelected: (Int) -> Unit) {

    val categoryIcons = listOf(
        R.drawable.ic_salad, R.drawable.ic_drink,
        R.drawable.ic_rice, R.drawable.ic_bre
    )


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categoryIcons.forEachIndexed { index, icon ->

            CategoryItem(
                iconRes = icon,
                isSelected = selectedCategory == index,
                onClick = { onCategorySelected(index) }
            )
        }
    }
}

@Composable
fun CategoryItem(iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFECB56E) else Color(0xFFFCE9C9)
    val iconTint = Color(0xFF725B3E)

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Category Icon",
            modifier = Modifier.size(28.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconTint)
        )
    }
}


@Composable
fun FoodCardRow(recipes: List<Recipe>, navController: NavHostController) {
    val chunkedRecipes = recipes.chunked(2)

    Column(modifier = Modifier.fillMaxWidth()) {
        chunkedRecipes.forEach { rowRecipes ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowRecipes.forEach { recipe ->
                    FoodCard(recipe = recipe, navController = navController)
                }
            }
        }
    }
    if (recipes.size < 2) {
        Box(modifier = Modifier
            .aspectRatio(1f)) {

        }
    }
}


@Composable
fun FoodCard(recipe: Recipe, navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser ?: return
    val userId = currentUser.uid
    val database = FirebaseDatabase.getInstance().reference
    val mContext = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageResId = context.resources.getIdentifier(recipe.image, "drawable", context.packageName)

    LaunchedEffect(recipe.title) {
        try {
            val favoriteSnapshot = database.child("users").child(userId).child("favorites").child(recipe.title).get().await()
            isFavorite = favoriteSnapshot.exists()
            isLoading = false
        } catch (e: Exception) {
            Toast.makeText(mContext, "Error : ${e.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Card(
            modifier = Modifier
                .width(160.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .clickable { navController.navigate("recipe_detail/${recipe.id}") }
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_profilepict),
                    error = painterResource(R.drawable.ic_profilepict)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recipe.ingredient,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_schedule),
                            contentDescription = "Time",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = recipe.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconToggleButton(
                        checked = isFavorite,
                        onCheckedChange = { checked ->
                            isFavorite = checked
                            val recipeData = mapOf(
                                "id" to recipe.id,
                                "description" to recipe.description,
                                "ingredients" to recipe.ingredient,
                                "time" to recipe.time,
                                "image" to recipe.image
                            )

                            // Handle Firebase Updates
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
                            tint = Color(0xFFFEB06D)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadRecipes(context: Context = LocalContext.current): RecipeData? {
    var recipeData by remember { mutableStateOf<RecipeData?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val json = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val recipeListType = object : TypeToken<RecipeData>() {}.type
                recipeData = gson.fromJson(json, recipeListType)
            } catch (e: Exception) {
                println("Error loading JSON: ${e.message}")
            }
        }
    }

    return recipeData
}

