package com.example.projectakhirdashboard.User

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import java.util.Calendar
import android.Manifest
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.projectakhirdashboard.AlarmReceiver
import com.example.projectakhirdashboard.BottomNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificationPage(modifier: Modifier = Modifier, navController: NavHostController, context: Context, userId: String, displayName: String?) {
    var selectedItem = "Profile"

    var isNotifikasiEnabled by remember { mutableStateOf(false) }
    var isAlarmEnabled by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 6); set(Calendar.MINUTE, 30) }) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var isSoundEnabled by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isEverydayEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAlarmEnabled = getPreferences(context, UserPreferencesKeys.IS_ALARM_ENABLED)
        isSoundEnabled = getPreferences(context, UserPreferencesKeys.IS_SOUND_ENABLED)
        isVibrationEnabled = getPreferences(context, UserPreferencesKeys.IS_VIBRATION_ENABLED)
        isNotifikasiEnabled = getPreferences(context, UserPreferencesKeys.IS_NOTIFICATION_ENABLED)
        isEverydayEnabled = getPreferences(context, UserPreferencesKeys.IS_EVERYDAY_ENABLED)

        val preferences = context.dataStore.data.first()
        val hour = preferences[intPreferencesKey("alarm_hour")] ?: 0
        val minute = preferences[intPreferencesKey("alarm_minute")] ?: 33
        selectedTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if (isNotifikasiEnabled) {
                    scheduleDailyNotification(context)
                }
            } else {
                isNotifikasiEnabled = false
                Toast.makeText(context, "Notification Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    )


    if (!isAlarmEnabled) {
        isVibrationEnabled = false
        isSoundEnabled = false
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { newTime ->
                selectedTime = newTime
                showTimePicker = false
            },
            initialTime = selectedTime
        )
    }

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
                text = "Notifikasi",
                fontWeight = FontWeight.Bold,
                color = Color(0xff002f49),
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .height(3.dp)
                    .width(1000.dp)
                    .background(Color(0xFFffb16d))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SwitchRow(
            label = "Tampilkan notifikasi sebagai Pop Up",
            isChecked = isNotifikasiEnabled,
            onCheckedChange = {
                isNotifikasiEnabled = it
                if (it) {  // Only request if enabling
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        scheduleDailyNotification(context)
                    }
                } else {
                    cancelDailyNotification(context)
                }
            }
        )

        SwitchRow(
            label = "Tampilkan notifikasi sebagai Alarm",
            isChecked = isAlarmEnabled,
            onCheckedChange = {
                isAlarmEnabled = it
                CoroutineScope(Dispatchers.IO).launch {
                    savePreferences(context, UserPreferencesKeys.IS_ALARM_ENABLED, it)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Atur Waktu",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff002f49)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(enabled = isAlarmEnabled) { showTimePicker = true }
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(10.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.5f)
                )
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${selectedTime.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')} : ${
                        selectedTime.get(Calendar.MINUTE).toString().padStart(2, '0')
                    }",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAlarmEnabled) Color(0xff002f49) else Color(0xff002f49).copy(alpha = 0.5f)
                )
                Text(
                    text = "Setiap Hari",
                    fontSize = 16.sp,
                    color = Color(0xff002f49),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            CustomSwitch(
                isChecked = isEverydayEnabled,
                onCheckedChange = { if (isAlarmEnabled) isEverydayEnabled = it },
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                color = Color.LightGray,
                thickness = 1.dp

            )
            Text(
                text = "Pengaturan Alarm",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                color = Color.LightGray,
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SwitchRow(
            label = "Getaran",
            isChecked = isVibrationEnabled,
            onCheckedChange = { isVibrationEnabled = it },
            enabled = isAlarmEnabled
        )

        SwitchRow(
            label = "Bunyi Alarm",
            isChecked = isSoundEnabled,
            onCheckedChange = { isSoundEnabled = it },
            enabled = isAlarmEnabled
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .height(45.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3c5687)),
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    savePreferences(context, UserPreferencesKeys.IS_ALARM_ENABLED, isAlarmEnabled)
                    savePreferences(context, UserPreferencesKeys.IS_SOUND_ENABLED, isSoundEnabled)
                    savePreferences(context, UserPreferencesKeys.IS_VIBRATION_ENABLED, isVibrationEnabled)
                    savePreferences(context, UserPreferencesKeys.IS_NOTIFICATION_ENABLED, isNotifikasiEnabled)
                    savePreferences(context, UserPreferencesKeys.IS_EVERYDAY_ENABLED, isEverydayEnabled)

                    context.dataStore.edit { preferences ->
                        preferences[intPreferencesKey("alarm_hour")] = selectedTime.get(Calendar.HOUR_OF_DAY)
                        preferences[intPreferencesKey("alarm_minute")] = selectedTime.get(Calendar.MINUTE)
                    }
                }

                if (isAlarmEnabled) {
                    setAlarm(context, selectedTime, isSoundEnabled, isVibrationEnabled, isEverydayEnabled)
                }
            }
        ) {
            Text("SIMPAN", color = Color.White)
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
fun TimePickerDialog(onDismissRequest: () -> Unit, onTimeSelected: (Calendar) -> Unit, initialTime: Calendar) {
    var selectedHour by remember { mutableStateOf(initialTime.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(initialTime.get(Calendar.MINUTE)) }


    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White, // Set white background
            elevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Alarm Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xff002f49)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Time Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NumberPicker(
                        value = selectedHour,
                        range = 0..23,
                        onValueChange = { selectedHour = it }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    NumberPicker(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("BATAL", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                            }
                            onTimeSelected(calendar)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                    ) {
                        Text("SIMPAN", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val totalItems = range.count()
    val extendedRange = (range.first - totalItems..range.last + totalItems).toList()
    val initialIndex = extendedRange.indexOf(value)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                val viewportCenter =
                    layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                val centerItem = visibleItems.minByOrNull {
                    val itemCenter = it.offset + it.size / 2
                    kotlin.math.abs(itemCenter - viewportCenter)
                }
                centerItem?.let {
                    val actualIndex = (it.index % totalItems + totalItems) % totalItems
                    val selectedItem = range.elementAtOrNull(actualIndex)
                    selectedItem?.let { onValueChange(it) }
                }
            }

    }

    Box(
        modifier = modifier
            .height(100.dp)
            .width(80.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(extendedRange) { item ->
                val displayValue = (item % totalItems + totalItems) % totalItems
                Text(
                    text = displayValue.toString().padStart(2, '0'),
                    fontSize = if (displayValue == value) 24.sp else 16.sp,
                    color = if (displayValue == value) Color.Black else Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.Transparent)
                .border(2.dp, Color(0xFFffb16d), RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun SwitchRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .background(
                color = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF002F49),
            modifier = Modifier.weight(1f)
        )

        CustomSwitch(
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun CustomSwitch(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier) {
    val gradientColors = listOf(Color(0xFF5374A1), Color(0xFFF7BD73))
    Box(
        modifier = modifier
            .size(50.dp, 30.dp)
            .background(
                brush = if (isChecked) Brush.horizontalGradient(gradientColors) else SolidColor(
                    Color(0xFFD6D6D6)
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable(enabled = enabled) { onCheckedChange(!isChecked) },
        contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = 4.dp, end = 3.dp)
                .size(24.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
        )
    }
}

fun setAlarm(context: Context, time: Calendar, enableSound: Boolean, enableVibration: Boolean, isEverydayEnabled: Boolean) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("SOUND_ENABLED", enableSound)
            putExtra("VIBRATION_ENABLED", enableVibration)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Check for exact alarm permission for Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Redirect the user to the settings to grant permission
                val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(permissionIntent)
                return
            }
        }

        // Schedule the alarm using `setExact` or `setExactAndAllowWhileIdle` based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingIntent)
        }

        // Handle repeating alarms if needed
        if (isEverydayEnabled) {
            val interval = AlarmManager.INTERVAL_DAY
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.timeInMillis, interval, pendingIntent)
        }

        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(time.time)
        Toast.makeText(context, "Alarm set for $formattedTime", Toast.LENGTH_SHORT).show()

        Log.d("AlarmDebug", "Setting alarm for: $formattedTime")

    } catch (e: SecurityException) {
        Log.e("AlarmDebug", "SecurityException: ${e.message}")
        Toast.makeText(context, "Permission denied to schedule alarm. Please enable it in settings.", Toast.LENGTH_SHORT).show()
    }
}





