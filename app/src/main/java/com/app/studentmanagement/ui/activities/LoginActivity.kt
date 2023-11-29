package com.app.studentmanagement.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ActivityLoginBinding
import com.app.studentmanagement.viewmodels.AccountViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        binding.buttonLogin.setOnClickListener(View.OnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editPass.text.toString()
            if (!isValidEmail(email)) {
                binding.layoutEmail.error = "Email không hợp lệ!"
            }else {
                binding.layoutEmail.error = null
                viewModel.login(email,password){
                        isSuccess ->
                    if (isSuccess){
                        binding.layoutPass.error = null
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        binding.layoutPass.error = "Tài Khoản hoặc mật khẩu không chính xác!"
                    }
                }
            }
        })
        //set progressbar
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }

        binding.editTextEmail.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val enteredInput = binding.editTextEmail.text.toString()
                if (!isValidEmail(enteredInput)) {
                    binding.layoutEmail.error = "Email không hợp lệ!"
                }else {
                    binding.layoutEmail.error = null
                }
            }
        }
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
    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()){
            return false
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }
    override fun onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed() // Gọi phương thức cha trước khi thoát ứng dụng
                finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}