package com.app.studentmanagement.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.AccountAdapter
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.databinding.FragmentAccountManagementBinding
import com.app.studentmanagement.ui.activities.AddEditAccountActivity
import com.app.studentmanagement.viewmodels.AccountViewModel


class AccountManagementFragment : Fragment() {


    private lateinit var binding: FragmentAccountManagementBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountManagementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]


        viewModel.getAccounts(Role.All)
        // set viewModel,Adapter and fill recycleView
        val adapter = AccountAdapter(requireContext(),viewModel)
        viewModel.accounts.observe(this){
            accounts ->
            adapter.updateList(accounts)
        }
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }

        binding.recycleViewListAccount.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListAccount.adapter = adapter




        // set array InputTextDrop
        val options = arrayOf("Tất cả", "Admin", "Manager", "Employee") // Replace with your data
        val adapterInputRole = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
        val autoCompleteTextView = binding.autoCompleteTextViewOption
        autoCompleteTextView.setAdapter(adapterInputRole)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = options[position]
            when (selectedItem) {
                "Tất cả" -> {
                    viewModel.getAccounts(Role.All)
                }
                "Admin" ->{
                    viewModel.getAccounts(Role.Admin)
                }
                "Manager" ->{
                    viewModel.getAccounts(Role.Manager)
                }
                "Employee" ->{
                    viewModel.getAccounts(Role.Employee)
                }
            }

            }
        binding.autoCompleteTextViewOption.setText("Tất cả", false)

        //  set button add new Student

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(requireActivity(), AddEditAccountActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun createProgressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return  dialog
    }

}