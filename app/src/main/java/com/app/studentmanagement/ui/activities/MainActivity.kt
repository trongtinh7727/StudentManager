package com.app.studentmanagement.ui.activities

import android.R
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.studentmanagement.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigationIcon = ContextCompat.getDrawable(this,com.app.studentmanagement.R.drawable.ic_nav);
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, com.app.studentmanagement.R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawableLayout,
            binding.toolbar,
            com.app.studentmanagement.R.string.navigation_drawer_open,  // Open drawer description
            com.app.studentmanagement.R.string.navigation_drawer_close // Close drawer description
        )
        binding.toolbar.navigationIcon = navigationIcon
        binding.drawableLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

    }
}