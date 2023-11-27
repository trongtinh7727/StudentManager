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
            viewModel.login(email,password){
                isSuccess ->
                if (isSuccess){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}