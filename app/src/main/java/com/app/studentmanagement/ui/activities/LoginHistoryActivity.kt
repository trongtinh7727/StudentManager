package com.app.studentmanagement.ui.activities

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.HistoryAdapter
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.databinding.ActivityLoginHistoryBinding
import com.app.studentmanagement.viewmodels.AccountViewModel

class LoginHistoryActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityLoginHistoryBinding
    private lateinit var viewModel: AccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_history)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        val account = intent.getSerializableExtra("account") as Account?
        if (account!= null){
            viewModel.getHistoryList(account.uid)
        }
        val adapter = HistoryAdapter()
        viewModel.loginHistories.observe(this){
            adapter.updateList(it)
            adapter.notifyDataSetChanged()
        }
        binding.recycleViewListHistoryLogin.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListHistoryLogin.adapter = adapter

        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
}