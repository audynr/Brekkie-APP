package com.example.projectakhirdashboard.Menu

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.Data.PackageItem
import com.example.projectakhirdashboard.Data.PromoItem
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun CateringPage(navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Menu"
    val auth = FirebaseAuth.getInstance()
    val userName = displayName?.ifEmpty { auth.currentUser?.displayName ?: "" }
    var isCook by remember { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }

    val populerPackages = remember {
        listOf(
            PackageItem("populer_001","Paket Tinggi Protein", "Rp 170.000", R.drawable.ic_protein),
            PackageItem("populer_002","Paket Ekonomis", "Rp 120.000", R.drawable.ic_ekonomis),
            PackageItem("populer_003","Paket Lokal", "Rp 135.000", R.drawable.ic_lokal),
            PackageItem("populer_004","Paket Vegetarian", "Rp 140.000", R.drawable.ic_veg)
        )
    }

    val ekonomisPackages = remember {
        listOf(
            PackageItem("ekonomis_001", "Paket Hemat 1", "Rp 80.000", R.drawable.ic_protein),
            PackageItem("ekonomis_002", "Paket Hemat 2", "Rp 95.000", R.drawable.ic_ekonomis),
            PackageItem("ekonomis_003", "Paket Hemat 3", "Rp 75.000", R.drawable.ic_veg)
        )
    }

    val filteredPopulerPackages by remember(searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                populerPackages
            } else {
                populerPackages.filter { packageItem ->
                    packageItem.name.lowercase(Locale.getDefault()).contains(searchText.lowercase(Locale.getDefault()))
                }
            }
        }
    }

    val filteredEkonomisPackages by remember(searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                ekonomisPackages
            } else {
                ekonomisPackages.filter { packageItem ->
                    packageItem.name.lowercase(Locale.getDefault()).contains(searchText.lowercase(Locale.getDefault()))
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

            SwitchSliderCatering(isCook, { isCook = it }, navController)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingin makan apa hari ini?",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBarCatering(
            onSearch = { query ->
                searchText = query
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PromoSection(navController = navController)
        Spacer(modifier = Modifier.height(20.dp))

        BreakfastPackageSection(filteredPopulerPackages, filteredEkonomisPackages, navController)
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

@Composable
fun SwitchSliderCatering(isCatering: Boolean, onSwitch: (Boolean) -> Unit, navController: NavHostController) {
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
                    navController.navigate("menu")
                }
            }
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 2.dp, vertical = 4.dp)
        ) {
            Text(
                text = "katering",
                fontSize = 12.sp,
                color = if (isCatering) Color(0xFFFEB06D) else Color(0xFF5374A0),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Text(
            text = "sendiri",
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )



    }
}

@Composable
fun PromoSection(navController: NavController) {
    val promoItem = PromoItem("promo_001", "Paket Rendah Kalori", "Rp 150.000", R.drawable.greek)

    val promoImageResId = painterResource(promoItem.imageResId)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    navController.navigate("detail_order/${promoItem.id}/${promoItem.name}/${promoItem.price}")
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = promoImageResId,
                contentDescription = "Promo Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Promo title
                Text(
                    text = promoItem.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3C5686)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Promo price
                Text(
                    text = promoItem.price,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFFFEB06D)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFF3461AD) else Color(0xFF738DB1),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = if (selected) Color.White else Color.White,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun BreakfastPackageSection(populerPackages: List<PackageItem>, ekonomisPackages: List<PackageItem>, navController: NavHostController) {
    val (selectedTab, setSelectedTab) = remember { mutableStateOf("Populer") }

    val packagesToDisplay = when (selectedTab) {
        "Populer" -> populerPackages
        "Ekonomis" -> ekonomisPackages
        else -> emptyList()
    }

    Column {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Row {
                Text(
                    text = "Paket ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFEB06D)
                    )
                )
                Text(
                    text = "Sarapan",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3C5686)
                    )
                )
            }
            Text(
                text = "Pilih paket sarapan praktis untuk pagi yang penuh energi!",
                style = MaterialTheme.typography.labelMedium.copy(
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TabButton(
                text = "Populer",
                selected = selectedTab == "Populer",
                onClick = { setSelectedTab("Populer") }
            )
            Spacer(modifier = Modifier.width(50.dp))
            TabButton(
                text = "Ekonomis",
                selected = selectedTab == "Ekonomis",
                onClick = { setSelectedTab("Ekonomis") }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            packagesToDisplay.chunked(2).forEach { packageRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    packageRow.forEach { packageItem ->

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.dp)
                                .padding(start = 10.dp)
                        ) {
                            PackageCard(
                                name = packageItem.name,
                                price = packageItem.price,
                                imageResId = packageItem.imageResId,
                                onItemClick = {
                                    navController.navigate("detail_order/${packageItem.id}/${packageItem.name}/${packageItem.price}")
                                    Log.d("Routes", "detail_order/${packageItem.id}/${packageItem.name}/${packageItem.price}")
                                }
                            )
                        }
                    }
                    if (packageRow.size < 2) {
                        Box(modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)) {

                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCatering(
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
                "Cari catering...",
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
fun PackageCard(name: String, price: String, imageResId: Int, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
            .aspectRatio(0.6f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onItemClick() }
                .padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3461AD)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Best Price",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    maxLines = 1
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_shop),
                    contentDescription = "Shop Icon",
                    tint = Color(0xFFFEB06D),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


