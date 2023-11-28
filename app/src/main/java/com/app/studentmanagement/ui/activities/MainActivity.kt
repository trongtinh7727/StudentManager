package com.app.studentmanagement.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.app.studentmanagement.R
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.databinding.ActivityMainBinding
import com.app.studentmanagement.ui.fragments.AccountManagementFragment
import com.app.studentmanagement.ui.fragments.PersonalFragment
import com.app.studentmanagement.ui.fragments.StudentManagementFragment
import com.app.studentmanagement.viewmodels.AccountViewModel
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


class MainActivity : AppCompatActivity() {
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigationIcon = ContextCompat.getDrawable(this,R.drawable.ic_nav);
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

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


        replaceFragment(StudentManagementFragment())
        buttonAcc.setOnClickListener {
            replaceFragment(AccountManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }
        buttonStudent.setOnClickListener {
            replaceFragment(StudentManagementFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }

        var buttonLogout  = headerView.findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            viewModel.logout {
                var intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }



        // set framelayout account personal
        var layoutPersonal  = headerView.findViewById<LinearLayout>(R.id.layoutAccountPersonal)
        layoutPersonal.setOnClickListener {
            replaceFragment(PersonalFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }
        var avatar = headerView.findViewById<ShapeableImageView>(R.id.imageAvatarHeader)
        avatar.setOnClickListener{
            replaceFragment(PersonalFragment())
            binding.drawableLayout.closeDrawer(GravityCompat.START)
        }

        //binding data
        viewModel.setCurrentUser()
        viewModel.currentUser.observe(this){
            account ->
            val textViewName = headerView.findViewById<TextView>(R.id.textViewName)
            val textViewID = headerView.findViewById<TextView>(R.id.textViewID)
            val textViewEmail = headerView.findViewById<TextView>(R.id.textViewEmail)
            val imageAvatar = headerView.findViewById<ShapeableImageView>(R.id.imageAvatarHeader)

            val cardViewManagerAccount = headerView.findViewById<CardView>(R.id.cardViewManagementAccount)
            if (account.role == Role.Admin){
                cardViewManagerAccount.visibility = View.VISIBLE
            }else{
                cardViewManagerAccount.visibility = View.GONE
            }
            textViewName.setText(account.name)
            textViewID.setText("MS: "+account.id)
            textViewEmail.setText("Email: "+account.email)

            Glide.with(this)
                .load(account.avatarUrl)
                .into(imageAvatar)

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