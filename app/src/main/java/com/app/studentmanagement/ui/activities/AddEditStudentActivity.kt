package com.app.studentmanagement.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.studentmanagement.R
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.databinding.ActivityAddEditStudentBinding

class AddEditStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddEditStudentBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_student)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}