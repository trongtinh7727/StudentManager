package com.app.studentmanagement.ui.fragments

import android.content.Intent
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


        // set viewModel,Adapter and fill recycleView
        val accounts = viewModel.generateAccounts() // You should implement your data generation logic
        val adapter = AccountAdapter(requireContext())
        adapter.updateList(accounts)

        binding.recycleViewListAccount.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListAccount.adapter = adapter



        // set array InputTextDrop
        val options = arrayOf("User", "Admin") // Replace with your data
        val adapterInputRole = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
        val autoCompleteTextView = binding.autoCompleteTextViewOption
        autoCompleteTextView.setAdapter(adapterInputRole)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedOption = parent.getItemAtPosition(position) as String
        }


        //  set button add new Student

        binding.buttonAdd.setOnClickListener{
            val intent = Intent(requireActivity(), AddEditAccountActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}