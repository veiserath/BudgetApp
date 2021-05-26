package com.example.budgetapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object Shared {
    @RequiresApi(Build.VERSION_CODES.O)
    val financialOperations = fillSpendingsWithInitialValues()


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillSpendingsWithInitialValues(): ArrayList<FinancialOperation> {
        val list = ArrayList<FinancialOperation>()

        val income = FinancialOperation(0, R.drawable.income, "Dubaj", Random.nextDouble(10.0,200.0).toInt().toDouble(), LocalDate.parse(
            "2020-05-02", DateTimeFormatter.ISO_DATE))

        list.add(income)

        for (i in 1 until 20) {
            var id = i
            if (i % 3 == 0) {
                id = R.drawable.diningout_expenses
            }
            if (i % 3 == 1) {
                id = R.drawable.leisure_expenses
            }
            if (i % 3 == 2) {
                id = R.drawable.transportation_expenses
            }
            val day = Random.nextInt(10,31)
            val financialOperation = FinancialOperation(i, id, "Warszawa", Random.nextDouble(-100.0,-50.0).toInt().toDouble(), LocalDate.parse(
                "2020-05-$day", DateTimeFormatter.ISO_DATE))
            list.add(financialOperation)
        }
        return list
    }
}