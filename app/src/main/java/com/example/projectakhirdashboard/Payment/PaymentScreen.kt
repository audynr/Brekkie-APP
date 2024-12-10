package com.example.projectakhirdashboard.Payment

import android.content.Context
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.projectakhirdashboard.R
import com.example.projectakhirdashboard.ViewModel.OrderViewModel
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userId: String,
    displayName: String?,
    packageId: String,
    packageName: String,
    packagePrice: String,
    phoneFromDetail: String?,
    addressFromDetail: String?,
    deliveryTimeFromDetail: String?,
    additionalInfo: String?
) {
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    var nama by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(phoneFromDetail ?: "") }
    var alamat by remember { mutableStateOf(addressFromDetail ?: "") }
    var waktu by remember { mutableStateOf(deliveryTimeFromDetail ?: "") }
    var info by remember { mutableStateOf(additionalInfo ?: "") }

    val mContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        BackButton(
            onClick = { navController.popBackStack() }
        )

        Text(
            text = "Pembayaran",
            fontSize = 28.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Konfirmasi pembayaran",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_debit_card),
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = packageName,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = packagePrice,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Pilih metode pembayaran",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(5.dp))
        PaymentOption(
            imageResource = R.drawable.ic_debit_card,
            title = "Debit Card",
            selected = selectedPaymentMethod == "debit",
            onClick = { selectedPaymentMethod = "debit" }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PaymentOption(
            imageResource = R.drawable.ic_cod,
            title = "Cash On Delivery (COD)",
            selected = selectedPaymentMethod == "cod",
            onClick = { selectedPaymentMethod = "cod" }
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Order Confirmation Section
        Text(
            text = "Konfirmasi pemesanan",
            fontSize = 16.sp,
            color = Color(0xFF3C5687),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        // Form Fields
        OutlinedTextField(
            value = nama,
            onValueChange = { newText ->
                nama = newText
            },
            label = { Text("Nama pemesan :") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            )
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { newText ->
                phone = newText
            },
            label = { Text("No. ponsel :") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            )
        )

        OutlinedTextField(
            value = alamat,
            onValueChange = { newText ->
                alamat = newText
            },
            label = { Text("Alamat:") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            )
        )

        OutlinedTextField(
            value = waktu,
            onValueChange = { newText ->
                waktu = newText
            },
            label = { Text("Waktu Pengantaran :") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Gray,
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedTextColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedLabelColor = Color.Gray,
            )
        )

        Spacer(modifier = Modifier.height(1f.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0xFF3C5687),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtotal",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = packagePrice,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val database = FirebaseDatabase.getInstance()
                        val ordersRef = database.getReference("orders")
                        val newOrderRef = ordersRef.push()

                        val orderData = hashMapOf(
                            "packageId" to packageId,
                            "packageName" to packageName,
                            "packagePrice" to packagePrice,
                            "nama" to nama,
                            "phone" to phone,
                            "alamat" to alamat,
                            "waktu" to waktu,
                            "paymentMethod" to selectedPaymentMethod,
                            "info" to info
                        )

                        if (nama.isNotBlank() && phone.isNotBlank() && alamat.isNotBlank() && waktu.isNotBlank() && selectedPaymentMethod != null) {
                            newOrderRef.setValue(orderData).addOnSuccessListener {
                                navController.navigate("payment_success")
                            }.addOnFailureListener { exception ->
                                Toast.makeText(mContext, "Failed to place order. Please try again. ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(
                                mContext,
                                "Please fill in all fields and select a payment method.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "BAYAR",
                        color = Color(0xFF3C5687),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}


