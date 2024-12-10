package com.example.projectakhirdashboard.Dashboard

import android.icu.util.Calendar
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.Data.CheckInData
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LandingPage(navController: NavHostController, userId: String, displayName: String) {
    val database = FirebaseDatabase.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    val checkInRef = database.getReference("checkins").child(userId).child("currentWeek")
    val progress = remember { mutableStateOf(0) }
    val mContext = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    var selectedItem by remember { mutableStateOf("Home") }

    LaunchedEffect(userId) {
        try {
            checkInRef.child("checkedDays").get().addOnSuccessListener { snapshot ->
                val type = object : GenericTypeIndicator<List<Boolean>>() {}
                val checkedDays = snapshot.getValue(type) ?: listOf()
                progress.value = checkedDays.count { it }
                isLoading = false
            }.addOnFailureListener {
                Toast.makeText(mContext, "Gagal memuat data", Toast.LENGTH_LONG)
                isLoading = false
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 56.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 10.dp)
        ) {

            DashboardPage(navController = navController, userId = userId, displayName = auth.currentUser?.displayName.orEmpty())
            CheckInSection(userId = userId)
            InfoBoxSection()
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        BottomNavigationBar(
            navController = navController,
            selectedItem = selectedItem,
            onItemSelected = { item ->
                selectedItem = item
            },
            userId = userId,
            displayName = displayName
        )
    }
}

@Composable
fun DashboardPage(navController: NavHostController, userId: String, displayName: String) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val userName = displayName.ifEmpty { auth.currentUser?.displayName ?: "" }
    var isLoading by remember { mutableStateOf(true) }

    Log.d("Debug", "Current User ID: $userId")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Selamat datang,",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color(0xff5374a1)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$userName",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color(0xffffb16d)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xff5374a1), Color(0xffc5a179))))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = "Sejak kamu menggunakan Brekkie, kamu sudah berhasil meraih langkah pertama untuk memperbaiki pola hidupmu!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            navController.navigate("graph")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(text = "Lacak Progressmu", color = Color.Black, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = "Arrow Right",
                            tint = Color(0xff5374a1),
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_hand),
                    contentDescription = "Illustration",
                    modifier = Modifier
                        .size(100.dp),
                    contentScale = ContentScale.FillHeight
                )
            }
        }

    }
}

@Composable
fun CheckInSection(userId: String) {
    val database = FirebaseDatabase.getInstance("https://project-akhir-2-default-rtdb.firebaseio.com/")
    val checkInRef = database.getReference("checkins").child(userId)

    val showPopup = remember { mutableStateOf(false) }
    val checkedDays = remember { mutableStateOf(listOf(false, false, false, false, false, false, false)) }
    val currentWeekStartDate = remember { mutableStateOf(getCurrentWeekStartDate()) }
    val currentDayIndex = remember { mutableStateOf(-1) }
    val totalCheckedDays = remember { mutableStateOf(0) }
    val checkedInDates = remember { mutableStateOf(listOf<String>()) }
    val currentMonth = remember { mutableStateOf(getCurrentMonth()) }
    val currentQuarter = remember { mutableStateOf(getQuarterOfMonth()) }

    LaunchedEffect(userId) {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        currentDayIndex.value = (today - Calendar.SUNDAY + 7) % 7
        checkInRef.child("currentWeek").get()
            .addOnSuccessListener { snapshot ->
                val currentWeekCheckIn = snapshot.getValue(CheckInData::class.java)
                currentWeekCheckIn?.let {
                    if (isNewWeek(it.checkInDate)) {
                        checkInRef.child("archive").child(it.checkInDate).setValue(it)
                            .addOnSuccessListener {
                                Log.d("Firebase", "Archived data for week")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Error archiving data", exception)
                            }

                        val newWeekCheckIn = CheckInData(
                            checkInDate = getCurrentWeekStartDate(),
                            checkedDays = List(7) { false },
                            checkedInDates = listOf(),
                            totalCheckedDays = 0,
                            checkInMonth = getCurrentMonth(),
                            checkInQuarter = getQuarterOfMonth()

                        )
                        checkInRef.child("currentWeek").setValue(newWeekCheckIn)
                    } else {
                        checkedDays.value = it.checkedDays
                        totalCheckedDays.value = it.totalCheckedDays
                        currentWeekStartDate.value = it.checkInDate
                        checkedInDates.value = it.checkedInDates
                        currentMonth.value = it.checkInMonth
                        currentQuarter.value = it.checkInQuarter
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error loading data", exception)
            }
    }

    Box(
        modifier = Modifier
            .width(350.dp)
            .padding(start = 16.dp, end = 2.dp)
            .height(25.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(totalCheckedDays.value.toFloat() / checkedDays.value.size * 330.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xff5374a1),
                            Color(0xfffc5a179)
                        )
                    )
                )
                .clip(RoundedCornerShape(6.dp))
        )
        Text(
            text = "${totalCheckedDays.value} / ${checkedDays.value.size}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
    Spacer(modifier = Modifier.height(30.dp))
    totalCheckedDays.value = checkedDays.value.count { it }

    val days = listOf("MINGGU", "SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Check in",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 35.sp,
                color = Color(0xff5374a1)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lacak jejak sarapanmu!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEachIndexed { index, day ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(
                            id = if (checkedDays.value[index]) R.drawable.ic_checked_bowl
                            else R.drawable.ic_unchecked_bowl
                        ),
                        contentDescription = day,
                        tint = if (checkedDays.value[index]) Color(0xff003049) else Color.Gray,
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                if (index == currentDayIndex.value && !checkedDays.value[index]) {
                                    showPopup.value = true
                                }
                            }
                    )
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xff5374a1),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (showPopup.value) {
        Dialog(onDismissRequest = { showPopup.value = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kamu sudah Check In Hari ini!",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xff5374a1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showPopup.value = false
                            val updatedCheckedDays = checkedDays.value.toMutableList()
                            updatedCheckedDays[currentDayIndex.value] = true
                            checkedDays.value = updatedCheckedDays

                            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
                            val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Calendar.getInstance().time)
                            val currentQuarter = getQuarterOfMonth()
                            val updatedCheckedInDates = checkedInDates.value.toMutableList()
                            if (!updatedCheckedInDates.contains(currentDate)) {
                                updatedCheckedInDates.add(currentDate)
                            }

                            totalCheckedDays.value = updatedCheckedDays.count { it }

                            val currentWeekCheckIn = CheckInData(
                                checkInDate = currentWeekStartDate.value,
                                checkedDays = updatedCheckedDays,
                                checkedInDates = updatedCheckedInDates,
                                totalCheckedDays = totalCheckedDays.value,
                                checkInMonth = currentMonth,
                                checkInQuarter = currentQuarter
                            )

                            saveCheckInData(userId, currentWeekCheckIn)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5374a1))
                    ) {
                        Text(text = "OK", color = Color.White)
                    }

                }
            }
        }
    }
}

@Composable
fun InfoBoxSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_funfact),
            contentDescription = "Blob Background",
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, start = 40.dp, end = 40.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tahukah Anda?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Divider(
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_lightbulb),
                    contentDescription = "Idea Icon",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 8.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.funfact),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
        }
    }
}

fun getCurrentWeekStartDate(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getCurrentMonth(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getQuarterOfMonth(): String {
    val calendar = Calendar.getInstance()
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val quarterLength = daysInMonth / 4
    val remainderDays = daysInMonth % 4

    return when {
        dayOfMonth <= quarterLength -> "1"
        dayOfMonth <= 2 * quarterLength -> "2"
        dayOfMonth <= 3 * quarterLength + (if (remainderDays > 0) 1 else 0) -> "3"
        else -> "4"
    }
}

fun isNewWeek(previousWeekStartDate: String): Boolean {
    val currentWeekStartDate = getCurrentWeekStartDate()
    return previousWeekStartDate != currentWeekStartDate
}

fun resetCheckInData(userId: String) {
    val database = FirebaseDatabase.getInstance()
    val checkInRef = database.getReference("checkins").child(userId)

    checkInRef.child("currentWeek").removeValue()
    checkInRef.child("archive").removeValue()
}

fun saveCheckInData(userId: String, currentWeekData: CheckInData) {
    val database = FirebaseDatabase.getInstance()
    val checkInRef = database.getReference("checkins").child(userId)

    checkInRef.child("currentWeek").setValue(currentWeekData)
        .addOnSuccessListener {
            Log.d("Firebase", "CheckIn data saved successfully for userId: $userId")
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Error saving CheckIn data for userId: $userId", exception)
        }
}