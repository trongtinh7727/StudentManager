package com.app.studentmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ItemRowCertificateBinding

class CertificateAdapter :
    RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {
    private var isEdit = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowCertificateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    private var items: MutableList<Certificate> = mutableListOf()
    fun updateList(newItems: MutableList<Certificate>) {
        items = newItems
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if (!isEdit){
            holder.buttonDelete.visibility = View.GONE
        }
        holder.buttonDelete.setOnClickListener(View.OnClickListener {
            if (isEdit){
                items.removeAt(position)
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount() = items.size
    fun setIsEdit(isEdit: Boolean){
        this.isEdit = isEdit
    }


    class ViewHolder(private val binding: ItemRowCertificateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var buttonDelete: ImageButton
        fun bind(item: Certificate) {
            binding.certificate = item
            buttonDelete =binding.buttonEditDelete
        }
    }
}
