package com.app.studentmanagement.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.app.studentmanagement.R
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.studentmanagement.databinding.ActivityMainBinding
import com.app.studentmanagement.ui.fragments.AccountManagementFragment
import com.app.studentmanagement.ui.fragments.PersonalFragment
import com.app.studentmanagement.ui.fragments.StudentManagementFragment


class MainActivity : AppCompatActivity() {
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
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


        replaceFragment(AccountManagementFragment())
        buttonAcc.setOnClickListener {
            replaceFragment(AccountManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }
        buttonStudent.setOnClickListener {
            replaceFragment(StudentManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }

        var buttonLogin  = headerView.findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }



        // set framelayout account personal
        var layoutPersonal  = headerView.findViewById<LinearLayout>(R.id.layoutAccountPersonal)
        layoutPersonal.setOnClickListener {
            replaceFragment(PersonalFragment())
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