package com.app.studentmanagement.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.R
import com.app.studentmanagement.data.StudentAdapterListener
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ItemRowStudentBinding
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.ui.activities.DetailStudentInformationActivity
import com.app.studentmanagement.viewmodels.StudentViewModel

class StudentAdapter(private  var context : Context, private val viewModel: StudentViewModel, private val listener: StudentAdapterListener?
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>()  {

    private var isEdit = true
    private var emailUniquenessMap: MutableList<Student> = mutableListOf()

    private var items: MutableList<Student> = mutableListOf()
    fun updateList(newItems: MutableList<Student>) {
        items = newItems
        notifyDataSetChanged()
    }


    fun setEmailUniquenessMap(emailUniquenessMap: MutableList<Student>){
        this.emailUniquenessMap = emailUniquenessMap
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

    fun getListItems():MutableList<Student>{
        return items
    }
    fun setIsEdit(isEdit: Boolean){
        this.isEdit = isEdit
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        if (listener != null) {
            if (hasDuplicate(item) || nonUniqueEmai(item)) {
                holder.setError()
            }else{
                holder.setValid()
            }
        }
        if (!isEdit){
            holder.setViewMode()
        }else{
            holder.buttonDelete.setOnClickListener(View.OnClickListener {
                if (listener!= null){
                    items.removeAt(position)
                    notifyDataSetChanged()
                }else{
                    showDeleteConfirm(item)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

   inner class ViewHolder(private val binding: ItemRowStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val  buttonDelete = binding.buttonDelete
        fun bind(item: Student) {
            binding.student = item
            binding.buttonEdit.setOnClickListener {
                if (listener!= null){
                    listener.onEditStudent(item,adapterPosition)
                }else{
                    val intent = Intent(context, AddEditStudentActivity::class.java)
                    intent.putExtra("student",item)
                    context.startActivity(intent)
                }
            }

            itemView.setOnClickListener {
                if (listener== null){
                    val intent = Intent(context, DetailStudentInformationActivity::class.java)
                    intent.putExtra("student",item)
                    context.startActivity(intent)
                }
            }
        }
       fun setViewMode(){
           binding.layoutAction.visibility = View.GONE
       }

       fun setError(){
       binding.textViewName.setTextColor(Color.RED)
       binding.textViewClass.setTextColor(Color.RED)
       }

       fun setValid(){
           binding.textViewName.setTextColor(Color.BLACK)
           binding.textViewClass.setTextColor(Color.BLACK)
       }
   }
    private fun showDeleteConfirm(student: Student){
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
            viewModel.deleteStudent(student.id){
            }
            dialog.dismiss()
        }
        buttonBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun hasDuplicate(student: Student): Boolean {
        return items.any { it != student && it.email == student.email}
    }

    private fun nonUniqueEmai(student: Student): Boolean{
        return emailUniquenessMap.any { it == student || it.email == student.email }
    }


}