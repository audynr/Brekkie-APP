package com.example.projectakhirdashboard.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class OrderViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note

    private val _deliveryTime = MutableStateFlow("05.00 - 07.00")
    val deliveryTime: StateFlow<String> = _deliveryTime

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    private val _foodId = MutableStateFlow(0)
    val foodId: StateFlow<Int> = _foodId


    fun updateUserInfo(username: String, phone: String, address: String, deliveryTime: String) {
        _username.value = username
        _phone.value = phone
        _address.value = address
        _deliveryTime.value = deliveryTime
    }

    fun updateOrderDetails(subtotal: Double, foodId: Int) {
        _subtotal.value = subtotal
        _foodId.value = foodId
    }

}