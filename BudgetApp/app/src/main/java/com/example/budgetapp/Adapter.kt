package com.example.budgetapp

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Adapter(private val mainActivity: MainActivity) :
    RecyclerView.Adapter<Adapter.FinancialOperationViewHolder>() {

    lateinit var parent: ViewGroup

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FinancialOperationViewHolder {
        this.parent = parent
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.spending_item, parent, false)
        return FinancialOperationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinancialOperationViewHolder, position: Int) {
        val currentItem = Shared.financialOperations[position]

        holder.category.setImageResource(currentItem.category)
        holder.amount.text = currentItem.amount.toString()
        holder.location.text = currentItem.location
        holder.date.text = currentItem.date.toString()

        holder.itemView.setOnClickListener {
            editPosition(position)
        }
        holder.itemView.setOnLongClickListener {
            deletePosition(position)
        }
    }

    private fun deletePosition(position: Int): Boolean {
        AlertDialog.Builder(parent.context)
            .setTitle("Delete operation")
            .setMessage("Are you sure you want to delete this position?")
            .setPositiveButton(
                "Delete"
            ) { _, _ ->
                Shared.financialOperations.removeAt(position)
                mainActivity.refresh()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(android.R.drawable.ic_delete)
            .show()
        return true
    }

    private fun editPosition(position: Int) {
        val intent = Intent(Intent(parent.context, DetailViewActivity::class.java))
        intent.putExtra("FinancialOperationObjectEdit", Shared.financialOperations[position])
        mainActivity.startActivity(intent)
    }


    override fun getItemCount() = Shared.financialOperations.size

    class FinancialOperationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val category: ImageView = itemView.findViewById(R.id.image_view)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val date: TextView = itemView.findViewById(R.id.date)
        val location: TextView = itemView.findViewById(R.id.location)

    }
}