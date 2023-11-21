package com.app.studentmanagement.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.R
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ItemRowStudentBinding
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.ui.activities.DetailStudentInformationActivity

class StudentAdapter(    private  var context : Context
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>()  {


    private var items: List<Student> = emptyList()
    fun updateList(newItems: List<Student>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStudentBinding.inflate(
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

   inner class ViewHolder(private val binding: ItemRowStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Student) {
            binding.student = item


            binding.buttonEdit.setOnClickListener {
                val intent = Intent(context, AddEditStudentActivity::class.java)
                context.startActivity(intent)
            }
            binding.buttonDelete.setOnClickListener {
                showDeleteConfirm()
            }

            itemView.setOnClickListener {
                val intent = Intent(context, DetailStudentInformationActivity::class.java)
                context.startActivity(intent)
            }

        }
    }

    private fun showDeleteConfirm(){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
            dialog.window!!.attributes = wlp
        }


        val buttonConfirmDelete = dialog.findViewById<TextView>(R.id.buttonConfirmDelete)
        val buttonBack = dialog.findViewById<TextView>(R.id.buttonBack)

        buttonConfirmDelete.setOnClickListener {

        }
        buttonBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }


}