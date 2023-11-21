package com.app.studentmanagement.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding

class AddEditAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddEditAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_account)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set activity layout
        binding.editTextPassword.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.editTextPassword.hint = "" else binding.editTextPassword.hint = "Your password" }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}