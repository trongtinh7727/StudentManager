package com.app.studentmanagement.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.R
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.databinding.ActivityAccountInformationBinding
import com.app.studentmanagement.viewmodels.AccountViewModel
import com.bumptech.glide.Glide

class AccountInformationActivity : AppCompatActivity() {
     private  lateinit var binding: ActivityAccountInformationBinding
    private lateinit var viewModel: AccountViewModel

    private var account: Account? = null
    private var role: Role = Role.Employee

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_information)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        account = intent.getSerializableExtra("account") as Account?
        // set array InputTextDrop
        val options = arrayOf("Admin", "Manager", "Employee") // Replace with your data
        val adapterInputRole = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        val autoCompleteTextView = binding.autoCompleteTextViewOption
        autoCompleteTextView.setAdapter(adapterInputRole)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = options[position]
            when (selectedItem) {
                "Admin" ->{
                    role = Role.Admin
                }
                "Manager" ->{
                    role = Role.Manager
                }
                "Employee" ->{
                    role = Role.Employee
                }
            }
        }

        // set hint new password
        setupData()

        binding.buttonShowHistory.setOnClickListener{
            val intent = Intent(this, LoginHistoryActivity::class.java)
            startActivity(intent)
        }

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null){
                    account = data.getSerializableExtra("account") as Account?
                    setupData()
                }
            }
        }

        binding.buttonEdit.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddEditAccountActivity::class.java)
            intent.putExtra("account",account)
            resultLauncher.launch(intent)
        })

    }
    private fun setupData() {
        account?.let { account ->
            Glide.with(this)
                .load(account.avatarUrl)
                .into(binding.imageAvatar)
            binding.textViewName.setText(account.name)
            binding.editTextID.setText(account.id)
            binding.editTextID.isEnabled = false
            binding.editTextEmail.isEnabled = false
            binding.autoCompleteTextViewOption.isEnabled = false
            binding.autoCompleteTextViewOption.isClickable = false
            binding.editTextEmail.setText(account.email)
            role = account.role
            // Set other fields if necessary
            binding.autoCompleteTextViewOption.setText(account.role.toString(), false)
        }
    }

    private fun showDeleteConfirm(account: Account){
        val dialog = Dialog(this)
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
            viewModel.deleteAccount(account)
            dialog.dismiss()
        }
        buttonBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}