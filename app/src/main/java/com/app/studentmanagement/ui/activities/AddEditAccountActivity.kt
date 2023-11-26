package com.app.studentmanagement.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
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
    private var existingAccount: Account? = null
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
        existingAccount = intent.getSerializableExtra("account") as Account?
        if (existingAccount != null) {
            setupEditMode()
        } else {
            setupAddMode()
        }

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
    }

    private fun setupAddMode() {
        binding.autoCompleteTextViewOption.setText("Employee", false)
    }

    private fun setupEditMode() {
        existingAccount?.let { account ->
            binding.editTextName.setText(account.name)
            binding.editTextID.setText(account.id)
            binding.editTextEmail.setText(account.email)
            binding.textViewPass.setText("Mật khẩu mới:")
            // Set other fields if necessary
            binding.autoCompleteTextViewOption.setText(account.role.toString(), false)
            role = account.role
        }
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
        val password = binding.editPass.text.toString()
        val confirmPassword = binding.editPassConfirm.text.toString()
        if (password == confirmPassword) {
            if (existingAccount != null){
                account.uid = existingAccount!!.uid
                viewModel.updateAccount(account,password){
                        isSuccess->
                    if (isSuccess){
                        val returnIntent = Intent()
                        returnIntent.putExtra("account", account)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }else{
                viewModel.createAccount(account, password){
                        isSuccess->
                    if (isSuccess){
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show()
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
                    if (existingAccount != null){
                        if (enteredInput.equals(existingAccount!!.id)){
                            binding.layoutID.error = null
                            return@setOnFocusChangeListener
                        }
                    }
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
                    if (existingAccount != null){
                        if (enteredInput.equals(existingAccount!!.email)){
                            binding.layoutEmail.error = null
                            return@setOnFocusChangeListener
                        }
                    }
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

        binding.editPass.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus){
                val enteredInput = binding.editPass.text.toString()
                if (enteredInput.length < 6 || enteredInput.isEmpty()) {
                    binding.layoutPass.error = "Mật khẩu phải từ 6 ký tự trở lên!"
                }else{
                    binding.layoutPass.error =null
                }
            }
        }


        binding.editPassConfirm.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editPassConfirm.text.toString()
                if (!enteredInput.equals(binding.editPass.text.toString())) {
                    binding.layoutPassConfirm.error = "Mật khẩu không khớp!"
                }else{
                    binding.layoutPassConfirm.error =null
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

        val password = binding.editPass.text.toString()
        if (password.length < 6) {
            if (existingAccount == null){
                binding.layoutPass.error = "Mật khẩu phải từ 6 ký tự trở lên!"
                return false
            }
        }

        // Check Password Confirmation
        val passwordConfirm = binding.editPassConfirm.text.toString()
        if (passwordConfirm != password) {
            binding.layoutPassConfirm.error = "Mật khẩu không khớp!"
            return false
        }

        return true
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