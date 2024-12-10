package com.example.projectakhirdashboard.Data

data class CheckInData(
    val checkInDate: String = "",
    val checkedDays: List<Boolean> = listOf(),
    val checkedInDates: List<String> = listOf(),
    val totalCheckedDays: Int = 0,
    val checkInMonth: String = "",
    val checkInQuarter: String = ""
) {
    constructor() : this("", listOf(), listOf(), 0, "", "")
}