package com.example.projectakhirdashboard.Tracking

import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.BottomNavigationBar
import com.example.projectakhirdashboard.Data.CheckInData
import com.example.projectakhirdashboard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

@Composable
fun GraphPage(navController: NavHostController, userId: String, displayName: String?) {
    var selectedItem = "Stats"
    var checkedDates by remember { mutableStateOf(emptyList<Int>()) }
    var progress by remember { mutableStateOf(0) }
    var checkins by remember { mutableStateOf<Map<String, Any>?>(null) }
    val allCheckedDates = remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedMonth by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isLoading = true
                loadCheckinsData { data ->
                    checkins = data
                    isLoading = false
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LoadCheckedDates { dates ->
        checkedDates = dates
        progress = dates.size
    }

    LaunchedEffect(Unit) {
        loadAllCheckedDates(getMonthName(Calendar.getInstance().get(Calendar.MONTH) + 1)) { dates ->
            allCheckedDates.value = dates
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Check in",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFffb16d),
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.width(30.dp))
                Box(modifier = Modifier
                    .height(3.dp)
                    .width(1000.dp)
                    .background(Color(0xFFffb16d))
                )
            }
            Text(
                text = "Record",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5374a1),
                fontSize = 25.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                CustomCalendar(
                    checkedDates = allCheckedDates.value,
                    onDateChecked = { date ->
                        if (!checkedDates.contains(date)) {
                            checkedDates = checkedDates + date
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            CheckInStatusCard(progress=progress)

            LongestCheckInCard(progress=progress)

            Spacer(modifier = Modifier.height(40.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Grafik Progress",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5374a1),
                    fontSize = 30.sp
                )
                Spacer(
                    modifier = Modifier.width(20.dp))
                Box(modifier = Modifier
                    .height(3.dp)
                    .width(1000.dp)
                    .background(Color(0xFFffb16d))
                )
            }
            Text(
                text = "*grafik diakumulasi per minggu",
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CustomDropdownButton(selectedMonth) { newSelectedMonth ->
                selectedMonth = newSelectedMonth
            }

            Spacer(modifier = Modifier.height(8.dp))

            DynamicGraphAndProgress(selectedMonth = selectedMonth)

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

@Composable
fun CustomCalendar(
    checkedDates: List<Int>,
    onDateChecked: (Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (calendar.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 16.dp)
    ) {
        Text(
            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(20.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            daysOfWeek.forEach { Text(it, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }

        Spacer(modifier = Modifier.height(10.dp))

        var currentDay = 1
        for (row in 0 until (daysInMonth + firstDayOfWeek + 6) / 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (column in 0..6) {
                    if (row == 0 && column < firstDayOfWeek || currentDay > daysInMonth) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(
                                    if (checkedDates.contains(currentDay)) Color(0xFFffb16d)
                                    else Color.Transparent
                                )
                                .clickable {
                                    // Trigger the callback when a date is clicked
                                    onDateChecked(currentDay)
                                }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "$currentDay",
                                color = if (checkedDates.contains(currentDay)) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        currentDay++
                    }
                }
            }
        }
    }
}

fun loadAllCheckedDates(month: String, onDataLoaded: (List<Int>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val path = "checkins/$userId"
    val reference = database.getReference(path)
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val dateList = mutableListOf<Int>()

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            dateList.clear() // Clear the list to avoid duplicates

            // Load from archive
            snapshot.child("archive").children.forEach { archiveSnapshot ->
                val checkInData = archiveSnapshot.getValue(CheckInData::class.java)
                if (checkInData != null && getMonthName(checkInData.checkInMonth.toIntOrNull() ?: 0) == month) {
                    checkInData.checkedInDates.forEach { dateStr ->
                        val day = formatter.parse(dateStr)?.date
                        day?.let { dateList.add(it) }
                    }
                }
            }

            // Load from current week (if it's in the same month)
            val currentWeekSnapshot = snapshot.child("currentWeek")
            val currentWeekData = currentWeekSnapshot.getValue(CheckInData::class.java)

            if (currentWeekData != null && getMonthName(currentWeekData.checkInMonth.toIntOrNull() ?: 0) == month) {
                currentWeekData.checkedInDates.forEach { dateStr ->
                    val day = formatter.parse(dateStr)?.date
                    day?.let { dateList.add(it) }
                }
            }

            onDataLoaded(dateList)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error loading checked dates: ${error.message}")
            onDataLoaded(emptyList())
        }
    })
}

@Composable
fun CheckInStatusCard(progress: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(
                width = 2.dp,
                color = Color(0xff003049),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_checked_bowl),
                    contentDescription = "Bowl Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xff003049)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Minggu ini kamu sudah ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFffb16d))) {
                            append("$progress")
                        }
                        append(" kali Check In berturut-turut!")
                    },
                    color = Color(0xff002f49),
                    fontSize = 16.sp
                )
                Text(
                    text = "Keep up the spirit!",
                    color = Color(0xff002f49),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun LongestCheckInCard(modifier: Modifier = Modifier, progress: Int) {
    val longestStreak = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { // Calculate longest streak on composition
        calculateLongestStreak { streak ->
            longestStreak.value = streak
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xff5374a1), Color(0xffc5a179))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = "Rekor terpanjang Check In mu adalah",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Text(
                text = "${longestStreak.value} x",
                color = Color(0xFF2B3A55),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_fire),
            contentDescription = "Flame Icon",
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(30.dp)
        )

    }
}

fun calculateLongestStreak(onStreakCalculated: (Int) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val path = "checkins/$userId"
    val reference = database.getReference(path)

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val allCheckedDates = mutableListOf<Date>()
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            // Collect dates from archive
            snapshot.child("archive").children.forEach { archiveSnapshot ->
                val checkInData = archiveSnapshot.getValue(CheckInData::class.java)
                checkInData?.checkedInDates?.forEach { dateStr ->
                    try{
                        formatter.parse(dateStr)?.let { allCheckedDates.add(it) }
                    }
                    catch(e:java.text.ParseException){
                        Log.e("DateError","Invalid Date Format")
                    }
                }
            }

            // Collect dates from current week
            val currentWeekData = snapshot.child("currentWeek").getValue(CheckInData::class.java)
            currentWeekData?.checkedInDates?.forEach { dateStr ->
                try {
                    formatter.parse(dateStr)?.let { allCheckedDates.add(it) }
                }catch(e:java.text.ParseException){
                    Log.e("DateError","Invalid Date Format")
                }
            }

            // Sort dates chronologically
            allCheckedDates.sort()

            var longestStreak = 0
            var currentStreak = 0

            for (i in 0 until allCheckedDates.size) {
                if (i > 0 && (allCheckedDates[i].time - allCheckedDates[i - 1].time).toInt() == 24 * 60 * 60 * 1000) { // Check if consecutive days
                    currentStreak++
                } else {
                    currentStreak = 1 // Reset streak if not consecutive
                }
                longestStreak = max(longestStreak, currentStreak)
            }

            onStreakCalculated(longestStreak)
        }


        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error calculating longest streak: ${error.message}")
            onStreakCalculated(0)
        }
    })
}

@Composable
fun CustomDropdownButton(selectedMonth: String, onMonthSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val availableMonths = remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        loadAvailableMonths { months ->
            availableMonths.value = months.sortedByDescending { getMonthOrder(it) }

            if (selectedMonth.isEmpty() && availableMonths.value.isNotEmpty()) {
                onMonthSelected(availableMonths.value.first())
            }
        }
    }

    val options = availableMonths.value.map { month -> "$month Q1 - $month Q4" }
    var selectedRange by remember(selectedMonth) { // Recompose on selectedMonth change
        mutableStateOf(if (selectedMonth.isEmpty() && availableMonths.value.isNotEmpty()) {
            "${availableMonths.value.first()} Q1 - ${availableMonths.value.first()} Q4"
        } else {
            "$selectedMonth Q1 - $selectedMonth Q4"
        })
    }

    Box {
        Button(
            modifier = Modifier
                .height(36.dp),
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
        ) {
            Text(
                text = selectedRange,
                color = Color.Black,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "Dropdown arrow",
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedRange = option
                        onMonthSelected(option.substringBefore(" Q1"))
                        expanded = false
                    }
                ) {
                    Text(text = option, color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

fun loadAvailableMonths(onMonthsLoaded: (List<String>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val basePath = "checkins/$userId"
    val reference = database.getReference(basePath)

    val months = mutableSetOf<String>()

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            // Handle archive data FIRST (more reliable for past months)
            snapshot.child("archive").children.forEach { archiveSnapshot ->
                val checkInData = archiveSnapshot.getValue(CheckInData::class.java)
                val monthNumber = checkInData?.checkInMonth?.toIntOrNull()
                monthNumber?.let {
                    val monthName = getMonthName(it)
                    if (monthName.isNotEmpty()) {
                        months.add(monthName) // Add month if found in archive
                    }
                }
            }
            snapshot.child("currentWeek").apply {
                val checkInData = getValue(CheckInData::class.java)
                if (!checkInData?.checkedInDates.isNullOrEmpty()) { // Check for checked-in dates
                    val monthNumber = checkInData?.checkInMonth?.toIntOrNull()
                    monthNumber?.let {
                        val monthName = getMonthName(it)
                        if (monthName.isNotEmpty()) {
                            months.add(monthName)
                        }
                    }
                }
            }

            val sortedMonths = months.sortedByDescending { getMonthOrder(it) }
            onMonthsLoaded(sortedMonths)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error loading available months: ${error.message}")
            onMonthsLoaded(emptyList())
        }
    })
}

@Composable
fun LineGraph(dataPoints: List<Float>, xLabels: List<String>, modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            val maxY = dataPoints.maxOrNull() ?: 0f
            val minY = dataPoints.minOrNull() ?: 0f

            val graphWidth = size.width
            val graphHeight = size.height

            val stepX = graphWidth / (dataPoints.size - 1)
            val stepY = if (maxY - minY != 0f) graphHeight / (maxY - minY) else 0f

            val points = dataPoints.mapIndexed { index, value ->
                Offset(index * stepX, graphHeight - (value - minY) * stepY)
            }

            for (i in 0 until points.size - 1) {
                drawLine(
                    color = Color(0xFF5374a1),
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 8f
                )
            }

            points.forEach { point ->
                drawCircle(
                    color = Color(0xFF5374a1),
                    radius = 8f,
                    center = point
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        xLabels.forEach { label ->
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProgressItem(title: String, progress: Float, percentage: Int) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF5374a1),
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE8E8E8))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((progress * 260).dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF5374a1),
                                    Color(0xFFc5a179)
                                )
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3D3D3D)
            )
        }
    }
}

@Composable
fun DynamicGraphAndProgress(selectedMonth: String) {
    val quarterlyData = remember { mutableStateOf(listOf(0f, 0f, 0f, 0f)) }
    val checkInMonth = remember { mutableStateOf("") }
    val availableMonths = remember { mutableStateOf(emptyList<String>()) }
    var selectedRange by remember { mutableStateOf("") }
    var initialLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loadAvailableMonths { months ->
            val sortedMonths = months.sortedByDescending { getMonthOrder(it) }
            availableMonths.value = sortedMonths

            if (sortedMonths.isNotEmpty()) {
                val latestMonth = sortedMonths.first()
                selectedRange = "$latestMonth Q1 - $latestMonth Q4"

                if (initialLoad) {
                    loadQuarterlyData(latestMonth) { data, month ->
                        quarterlyData.value = data
                        checkInMonth.value = month
                        initialLoad = false
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedMonth) {
        if (selectedMonth.isNotBlank()) {
            loadQuarterlyData(selectedMonth) { data, month ->
                quarterlyData.value = data
                checkInMonth.value = month
            }
        }
    }

    val range = listOf("Q1", "Q2", "Q3", "Q4").map {
        "${checkInMonth.value} - $it"
    }
    val progressData = quarterlyData.value.mapIndexed { index, value ->
        range[index] to value / 7f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LineGraph(
            dataPoints = quarterlyData.value,
            xLabels = listOf("Q1", "Q2", "Q3", "Q4"),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .background(Color(0xFFF8F8F8), shape = RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))

        progressData.forEach { (title, progress) ->
            ProgressItem(
                title = title,
                progress = progress,
                percentage = (progress * 100).toInt(),
            )
        }
    }
}

fun loadQuarterlyData(selectedMonth: String, onDataLoaded: (List<Float>, String) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val path = "checkins/$userId"
    val reference = database.getReference(path)

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val quarterlyData = mutableListOf(0f, 0f, 0f, 0f)

            // Process Archive Data
            snapshot.child("archive").children.forEach { archiveSnapshot ->
                val checkInData = archiveSnapshot.getValue(CheckInData::class.java)
                if (checkInData != null && getMonthName(checkInData.checkInMonth.toIntOrNull() ?: 0) == selectedMonth) {
                    val quarter = checkInData.checkInQuarter.toIntOrNull() ?: 0
                    if (quarter in 1..4) {
                        quarterlyData[quarter - 1] += checkInData.totalCheckedDays.toFloat()
                    }
                }
            }

            // Process Current Week Data (if it's in the selected month)
            val currentWeekSnapshot = snapshot.child("currentWeek")
            val currentWeekData = currentWeekSnapshot.getValue(CheckInData::class.java)
            if (currentWeekData != null && getMonthName(currentWeekData.checkInMonth.toIntOrNull() ?: 0) == selectedMonth) {
                val quarter = currentWeekData.checkInQuarter.toIntOrNull() ?: 0
                if (quarter in 1..4) {
                    quarterlyData[quarter - 1] += currentWeekData.totalCheckedDays.toFloat()
                }
            }

            onDataLoaded(quarterlyData, selectedMonth)
        }

        override fun onCancelled(error: DatabaseError) {
            onDataLoaded(listOf(0f, 0f, 0f, 0f), "")
            Log.e("FirebaseError", "Error loading quarterly data: ${error.message}")
        }
    })
}

fun getMonthName(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }
}

fun getMonthOrder(month: String): Int {
    return when (month) {
        "January" -> 1
        "February" -> 2
        "March" -> 3
        "April" -> 4
        "May" -> 5
        "June" -> 6
        "July" -> 7
        "August" -> 8
        "September" -> 9
        "October" -> 10
        "November" -> 11
        "December" -> 12
        else -> 0
    }
}

fun LoadCheckedDates(onDataLoaded: (List<Int>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val path = "checkins/$userId/currentWeek/checkedInDates"
    val reference = database.getReference(path)
    reference.addValueEventListener(object : ValueEventListener { // Use addValueEventListener for updates
        override fun onDataChange(snapshot: DataSnapshot) {
            val dateList = mutableListOf<Int>()
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            for(dateSnapshot in snapshot.children){
                val dateStr= dateSnapshot.getValue(String::class.java)
                dateStr?.let{
                    val day= formatter.parse(it)?.date
                    day?.let{dateList.add(day)}
                }

            }

            onDataLoaded(dateList)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error loading checked dates: ${error.message}")
            onDataLoaded(emptyList())
        }
    })
}

fun loadCheckinsData(onDataLoaded: (Map<String, Any>?) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val path = "checkins/$userId" // Path to your checkins data
    val reference = database.getReference(path)

    reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val data = snapshot.value as? Map<String, Any>
            onDataLoaded(data)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseError", "Error loading checkins data: ${error.message}")
            onDataLoaded(null) // Handle error appropriately
        }
    })
}