package com.example.budgetapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.databinding.ActivityDetailViewBinding
import com.example.budgetapp.databinding.ActivityDetailViewBinding.inflate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class DetailViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailViewBinding
    private var expenseCategory: Int = 0
    private lateinit var spinner: Spinner
    var isFinancialOperationBeingEdited = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        setupDatePickerDialog()
        setupExpenseCategories()
        setupEditCapability()
        setupAddNewExpense()
    }
    private fun setupDatePickerDialog() {
        var cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.dateLabelDetailView.text = sdf.format(cal.time)
            }

        binding.setDateButton.setOnClickListener {
            DatePickerDialog(
                this@DetailViewActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
    private fun setupExpenseCategories() {
        val expenseCategories = resources.getStringArray(R.array.expense_categories)
        spinner = findViewById(R.id.editType)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, expenseCategories)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                expenseCategory = position

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setupEditCapability() {
        if (intent.getSerializableExtra("FinancialOperationObjectEdit") != null) {
            val financialOperation =
                intent.getSerializableExtra("FinancialOperationObjectEdit") as FinancialOperation
            binding.editAmount.setText(financialOperation.amount.toString())
            binding.editLocation.setText(financialOperation.location)
            binding.dateLabelDetailView.text = financialOperation.date.toString()
            expenseCategory = financialOperation.category
            spinner.setSelection(translateResourceIDIntoCategoryID(expenseCategory))
            isFinancialOperationBeingEdited = true
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAddNewExpense() {
        binding.addIncomeButton.setOnClickListener {
            createOrEditFinancialOperation(false, isFinancialOperationBeingEdited)
        }
        binding.addExpenseButton.setOnClickListener {
            createOrEditFinancialOperation(true, isFinancialOperationBeingEdited)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOrEditFinancialOperation(isExpense: Boolean, isEdit: Boolean) {

        val index = intent.getIntExtra("index", 0)
        val location: String = binding.editLocation.text.toString()
        var amount: Double = binding.editAmount.text.toString().toDouble()
        val date: String = binding.dateLabelDetailView.text.toString()
        val type: Int = translateCategoryIDIntoResourceID(expenseCategory)

        if (isExpense && amount > 0)
            amount = -amount
        if (!isExpense && amount < 0)
            amount = -amount

        val financialOperation = FinancialOperation(
            index,
            type,
            location,
            amount,
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        )
        val intent = Intent(applicationContext, MainActivity::class.java)

        if (isEdit) {
            intent.putExtra("FinancialOperationObjectEdit", financialOperation)
        } else {
            intent.putExtra("FinancialOperationObject", financialOperation)
        }
        startActivity(intent)
    }

    fun translateResourceIDIntoCategoryID(resourceID: Int): Int {
        return when (resourceID) {
            R.drawable.diningout_expenses -> 0
            R.drawable.leisure_expenses -> 1
            R.drawable.transportation_expenses -> 2
            R.drawable.income -> 3
            else -> -1
        }
    }

    fun translateCategoryIDIntoResourceID(categoryID: Int): Int {
        return when (categoryID) {
            0 -> R.drawable.diningout_expenses
            1 -> R.drawable.leisure_expenses
            2 -> R.drawable.transportation_expenses
            3 -> R.drawable.income
            else -> -1
        }
    }


}
