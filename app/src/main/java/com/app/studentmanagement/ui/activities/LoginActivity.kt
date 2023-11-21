package com.app.studentmanagement.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}