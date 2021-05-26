package com.example.budgetapp

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetapp.databinding.ActivityMainBinding
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity(), Serializable {

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val exampleAdapter by lazy {
        Adapter(this)
    }
    private lateinit var intentDetailView: Intent
    private lateinit var intentSpendingGraph: Intent


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(activityMainBinding.root)

        intentDetailView = Intent(applicationContext, DetailViewActivity::class.java)
        intentSpendingGraph = Intent(applicationContext, SpendingGraphActivity::class.java)

        activityMainBinding.recyclerView.adapter = exampleAdapter
        activityMainBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        calculateMonthlySum()
        setupAddingNewFinancialOperation()
        setupEditingFinancialOperation()


        activityMainBinding.newFinancialOperation.setOnClickListener {
            val size = Shared.financialOperations.size
            intentDetailView.putExtra("index", size)
            startActivity(intentDetailView)
        }

        activityMainBinding.monthlySumGraph.setOnClickListener {
            startActivity(intentSpendingGraph)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAddingNewFinancialOperation() {
        val financialOperation = intent.getSerializableExtra("FinancialOperationObject")
        if (financialOperation != null) {
            Shared.financialOperations.add(financialOperation as FinancialOperation)
            refresh()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupEditingFinancialOperation() {
        val financialOperationEdit =
            intent.getSerializableExtra("FinancialOperationObjectEdit") as FinancialOperation?
        if (financialOperationEdit != null) {
            Shared.financialOperations[financialOperationEdit.index] = financialOperationEdit
            refresh()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh() {
        exampleAdapter.notifyDataSetChanged()
        calculateMonthlySum()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateMonthlySum() {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        var sumOfSpending = 0.0
        for (operation in Shared.financialOperations) {
            if (operation.date.monthValue == currentMonth)
                sumOfSpending += operation.amount
        }
        activityMainBinding.monthlySum.text = sumOfSpending.toString()

        if (sumOfSpending < 0)
            activityMainBinding.monthlySum.setTextColor(Color.RED)
        else
            activityMainBinding.monthlySum.setTextColor(Color.GREEN)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}