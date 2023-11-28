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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ItemRowAccountBinding
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.ui.activities.AccountInformationActivity
import com.app.studentmanagement.ui.activities.AddEditAccountActivity
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.ui.activities.DetailStudentInformationActivity
import com.app.studentmanagement.viewmodels.AccountViewModel

class AccountAdapter(  private  var context : Context, private val viewModel: AccountViewModel) : RecyclerView.Adapter<AccountAdapter.ViewHolder>()  {


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

      inner  class ViewHolder(private val binding: ItemRowAccountBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: Account) {
                binding.account = item
                binding.buttonEdit.setOnClickListener {
                    val intent = Intent(context, AddEditAccountActivity::class.java)
                    intent.putExtra("account",item)
                    context.startActivity(intent)
                }
                binding.buttonDelete.setOnClickListener {
                    showDeleteConfirm(item)
                }

                itemView.setOnClickListener {
                    val intent = Intent(context, AccountInformationActivity::class.java)
                    intent.putExtra("account",item)
                    context.startActivity(intent)
                }
            }
        }

    private fun showDeleteConfirm(account: Account){
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
            viewModel.deleteAccount(account,context)
            dialog.dismiss()
        }
        buttonBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}