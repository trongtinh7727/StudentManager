package com.app.studentmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.databinding.ItemRowAccountBinding
import com.app.studentmanagement.models.Account

class AccountAdapter : RecyclerView.Adapter<AccountAdapter.ViewHolder>()  {


    private var items: List<Account> = emptyList()

    fun updateList(newItems: List<Account>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemRowAccountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

        override fun getItemCount(): Int {
            return items.size
        }

        class ViewHolder(private val binding: ItemRowAccountBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: Account) {
               binding.account = item
            }
        }

}