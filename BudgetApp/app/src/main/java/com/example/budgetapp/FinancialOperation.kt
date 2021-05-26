package com.example.budgetapp

import java.io.Serializable
import java.time.LocalDate

data class FinancialOperation(
    val index: Int,
    val category: Int,
    val location: String,
    val amount: Double,
    val date: LocalDate
) : Serializable