package com.app.studentmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ItemRowCertificateBinding

class CertificateAdapter :
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowCertificateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    private var items: List<Certificate> = emptyList()
    fun updateList(newItems: List<Certificate>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size



    class ViewHolder(private val binding: ItemRowCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Certificate) {
            binding.certificate = item
            // You can set click listeners for your buttons here if needed.
        }
    }
}
