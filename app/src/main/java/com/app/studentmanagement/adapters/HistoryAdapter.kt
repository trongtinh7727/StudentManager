package com.app.studentmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.LoginHistory
import com.app.studentmanagement.databinding.ItemRowHistoryLoginBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class HistoryAdapter:RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowHistoryLoginBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    private var items: List<LoginHistory> = mutableListOf()
    fun updateList(newItems: List<LoginHistory>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

    }

    override fun getItemCount() = items.size

    class ViewHolder(private val binding: ItemRowHistoryLoginBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var buttonDelete: ImageButton
        fun bind(item: LoginHistory) {
            val dateTime = LocalDateTime.ofEpochSecond(item.time, 0, ZoneOffset.of("+7"))
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
            val formattedString = dateTime.format(formatter)
            binding.textViewTime.setText(formattedString)
            binding.textViewName.setText(item.device)
        }
    }
}