package com.example.projectakhirdashboard.Payment

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.ViewModel.OrderViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailOrder(
    navController: NavController,
    context: Context = LocalContext.current,
    userId: String,
    displayName: String?,
    packageId: String,
    packageName: String,
    packagePrice: String
) {
    val orderViewModel: OrderViewModel = viewModel()

    val username by orderViewModel.username.collectAsState()
    val phone by orderViewModel.phone.collectAsState()
    val address by orderViewModel.address.collectAsState()
    val deliveryTime by orderViewModel.deliveryTime.collectAsState()

    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var additionalInfo by remember { mutableStateOf("") }

    val mContext = LocalContext.current

    var isPhoneValid by remember { mutableStateOf(false) }
    var isAddressValid by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val navSubtotal = navController.currentBackStackEntry?.arguments?.getDouble("subtotal") ?: 0.0
        val navFoodId = navController.currentBackStackEntry?.arguments?.getInt("foodId") ?: 0
        orderViewModel.updateOrderDetails(navSubtotal, navFoodId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BackButton(
            onClick = { navController.popBackStack() }
        )

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Pengantaran",
                fontWeight = FontWeight.Bold,
                color = Color(0xff3c5687),
                fontSize = 36.sp
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

        UserInfoFields(
            phone = phone,
            onPhoneChange = {
                orderViewModel.updateUserInfo(username, it, address, deliveryTime)
                isPhoneValid = it.isNotBlank()},
            address = address,
            onAddressChange = { orderViewModel.updateUserInfo(username, phone, it, deliveryTime)
                isAddressValid = it.isNotBlank()
            },
            onAdditionalInfoChange = { additionalInfo = it }
        )

        DateSelectionFields(
            startDate = startDate,
            onStartDateChange = { date -> startDate = date },
            showStartDatePicker = showStartDatePicker,
            onShowStartDatePicker = { showStartDatePicker = it },
            endDate = endDate,
            onEndDateChange = { date -> endDate = date },
            showEndDatePicker = showEndDatePicker,
            onShowEndDatePicker = { showEndDatePicker = it }
        )

        DeliveryTimeSelection(
            deliveryTime = deliveryTime,
            onDeliveryTimeChange = { orderViewModel.updateUserInfo(username, phone, address, it) },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )

        SubmitButton(
            onClick = {
                if (isPhoneValid && isAddressValid) {
                    navController.navigate(
                        "payment_screen/${packageId}/${packageName}/${packagePrice}/${Uri.encode(phone)}/${Uri.encode(address)}/${Uri.encode(deliveryTime)}/${Uri.encode(additionalInfo)}"
                    )
                    Log.d("routes", "payment_screen/${packageId}/${packageName}/${packagePrice}/${Uri.encode(phone)}/${Uri.encode(address)}/${Uri.encode(deliveryTime)}/${Uri.encode(additionalInfo)}")
                } else {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}


@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(70.dp)
            .offset(x = (-30).dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF3C5687)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoFields(phone: String, onPhoneChange: (String) -> Unit, address: String, onAddressChange: (String) -> Unit, onAdditionalInfoChange: (String) -> Unit ) {
    var text by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }
    var isAddressError by remember { mutableStateOf(false) }

    val mContext = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Informasi Pengantaran",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onAdditionalInfoChange(newText)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("Masukkan informasi mengenai hal yang kamu tidak suka atau alergi") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Alamat dan No Ponsel",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = {
                onPhoneChange(it)
                isPhoneError = it.isBlank()
            },
            isError = isPhoneError,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("No. ponsel") },
            modifier = Modifier.fillMaxWidth()
        )
        if (isPhoneError) {
            Toast.makeText(mContext, "Nomor telpon tidak boleh kososng", Toast.LENGTH_LONG)
        }
        OutlinedTextField(
            value = address,
            onValueChange = {
                onAddressChange(it)
                isAddressError = it.isBlank()
            },
            isError = isPhoneError,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("Informasi Alamat") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 5
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tanggal dan Waktu Pengantaran",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelectionFields(
    startDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    showStartDatePicker: Boolean,
    onShowStartDatePicker: (Boolean) -> Unit,
    endDate: LocalDate,
    onEndDateChange: (LocalDate) -> Unit,
    showEndDatePicker: Boolean,
    onShowEndDatePicker: (Boolean) -> Unit
) {
    val context = LocalContext.current

    if (showStartDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                onStartDateChange(newDate)
                onShowStartDatePicker(false)
            },
            startDate.year,
            startDate.monthValue - 1,
            startDate.dayOfMonth
        ).apply {
            setOnCancelListener { onShowStartDatePicker(false) }
        }.show()
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = LocalDate.of(year, month + 1, dayOfMonth)
                onEndDateChange(newDate)
                onShowEndDatePicker(false) // Close the dialog
            },
            endDate.year,
            endDate.monthValue - 1,
            endDate.dayOfMonth
        ).apply {
            setOnCancelListener { onShowEndDatePicker(false) } // Handle cancel
        }.show()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = { },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("Tanggal dimulai") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onShowStartDatePicker(true) }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )
        OutlinedTextField(
            value = endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = { },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("Tanggal selesai") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onShowEndDatePicker(true) }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTimeSelection(
    deliveryTime: String,
    onDeliveryTimeChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    val deliveryTimeOptions = listOf("05.00 - 07.00", "07.00 - 09.00", "10.00 - 11.00")

    Box {
        OutlinedTextField(
            value = deliveryTime,
            onValueChange = { },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            ),
            label = { Text("Waktu Pengantaran") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { onExpandedChange(!expanded) }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            deliveryTimeOptions.forEach { timeOption ->
                DropdownMenuItem(
                    onClick = {
                        onDeliveryTimeChange(timeOption)
                        onExpandedChange(false)
                    },
                    text = { Text(timeOption) }
                )
            }
        }
    }
}

@Composable
fun SubmitButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3C5687)),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text = "Lanjutkan", color = Color.White, fontSize = 16.sp)
    }
}