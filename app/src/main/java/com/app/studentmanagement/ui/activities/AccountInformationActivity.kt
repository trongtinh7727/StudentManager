package com.app.studentmanagement.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ActivityAccountInformationBinding

class AccountInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAccountInformationBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_information)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set array InputTextDrop
        val options = arrayOf("User", "Admin") // Replace with your data
        val adapterInputRole = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        val autoCompleteTextView = binding.autoCompleteTextViewOption
        autoCompleteTextView.setAdapter(adapterInputRole)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedOption = parent.getItemAtPosition(position) as String
        }

        // set hint new password
        setHintPasswordText(binding.editTextNewPassword)
        setHintPasswordText(binding.editTextOldPasword)
        setHintPasswordText(binding.editTextPasswordConfirm)

        binding.buttonShowHistory.setOnClickListener{
            val intent = Intent(this, LoginHistoryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setHintPasswordText(editText: EditText){
        editText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> if (hasFocus) editText.hint = "" else editText.hint = "Your password" }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}