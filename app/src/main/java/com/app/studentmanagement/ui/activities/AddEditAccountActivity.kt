package com.app.studentmanagement.ui.activities

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnDetach
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.R
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.viewmodels.AccountViewModel

class AddEditAccountActivity : AppCompatActivity() {
    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: ActivityAddEditAccountBinding
    private var role: Role = Role.Employee
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_account)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //view model
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]
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
        binding.autoCompleteTextViewOption.setText("Employee", false)

        //set progressbar
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }

        validateInput()
        binding.buttonSave.setOnClickListener(View.OnClickListener {
            if (isFormValid()){
                createAccount()
            }
        })
        // set activity layout
        binding.editTextPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.editTextPassword.hint = "" else binding.editTextPassword.hint = "Your password" }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun createAccount() {
        val account = Account(
            name = binding.editTextName.text.toString(),
            id = binding.editTextID.text.toString(),
            email = binding.editTextEmail.text.toString(),
            role = role
        )
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextPasswordConfirm.text.toString()
        if (password == confirmPassword) {
            viewModel.createAccount(account, password)
        } else {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
        }
    }
    fun validateInput(){
        binding.editTextName.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editTextName.text.toString()
                if (enteredInput.isEmpty()){
                    binding.layoutName.error = "Họ tên không hợp lệ!"
                }else{
                    binding.layoutName.error = null
                }
            }
        }
        binding.editTextID.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editTextID.text.toString()
                if (enteredInput.isEmpty()){
                    binding.layoutID.error = "ID không hợp lệ!"
                }else{
                    viewModel.isCodeUnique(enteredInput) { isSuccess ->
                        if (!isSuccess) {
                            binding.layoutID.error = "ID đã tồn tại!"
                        } else {
                            binding.layoutID.error = null
                        }
                    }
                }
            }
        }

        binding.editTextEmail.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editTextEmail.text.toString()
                if (!isValidEmail(enteredInput)) {
                    binding.layoutEmail.error = "Email không hợp lệ!"
                }else {
                    viewModel.isEmailUnique(enteredInput) { isSuccess ->
                        if (!isSuccess) {
                            binding.layoutEmail.error = "Email đã tồn tại!"
                        } else {
                            binding.layoutEmail.error = null
                        }
                    }
                }
            }
        }

        binding.editTextPassword.setOnFocusChangeListener { view, hasFocus ->
            val enteredInput = binding.editTextPassword.text.toString()
            if (enteredInput.length < 6 || enteredInput.isEmpty()) {
                binding.layoutPassword.error = "Mật khẩu phải từ 6 ký tự trở lên!"
            }else{
                binding.layoutPassword.error =null
            }
        }
        binding.editTextPasswordConfirm.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editTextPasswordConfirm.text.toString()
                if (!enteredInput.equals(binding.editTextPassword.text.toString())) {
                    binding.layoutpasswordConfirm.error = "Mật khẩu không khớp!"
                }else{
                    binding.layoutpasswordConfirm.error =null
                }
            }
        }
    }

    fun isFormValid(): Boolean {
        // Check Name
        val name = binding.editTextName.text.toString()
        if (name.isEmpty()) {
            binding.layoutName.error = "Họ tên không hợp lệ!"
            return false
        }

        if (binding.layoutID.error != null) {
            return false
        }
        if (binding.layoutEmail.error != null) {
            return false
        }

        val password = binding.editTextPassword.text.toString()
        if (password.length < 6) {
            binding.layoutPassword.error = "Mật khẩu phải từ 6 ký tự trở lên!"
            return false
        }

        // Check Password Confirmation
        val passwordConfirm = binding.editTextPasswordConfirm.text.toString()
        if (passwordConfirm != password) {
            binding.layoutpasswordConfirm.error = "Mật khẩu không khớp!"
            return false
        }

        return true // No errors, form is valid
    }


    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()){
            return false
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    private fun createProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return  dialog
    }
}