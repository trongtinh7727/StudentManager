package com.app.studentmanagement.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.app.studentmanagement.R
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.app.studentmanagement.databinding.ActivityMainBinding
import com.app.studentmanagement.ui.fragments.AccountManagementFragment
import com.app.studentmanagement.ui.fragments.StudentManagementFragment


class MainActivity : AppCompatActivity() {
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigationIcon = ContextCompat.getDrawable(this,R.drawable.ic_nav);
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //set tool bar and navigation layout
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


        // set button layout header click
        var headerView: View = binding.headerNav.getHeaderView(0)
        var buttonAcc  = headerView.findViewById<Button>(R.id.buttonManagementAccount)
        var buttonStudent  = headerView.findViewById<Button>(R.id.buttonManagementStudent)

        buttonAcc.setOnClickListener {
            replaceFragment(AccountManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }
        buttonStudent.setOnClickListener {
            replaceFragment(StudentManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }
    }
     fun replaceFragment(fragment : Fragment ): Boolean {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout_main , fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        return true
    }
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}