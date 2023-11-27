package com.app.studentmanagement.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.R
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.databinding.FragmentPersonalBinding
import com.app.studentmanagement.ui.activities.AddEditAccountActivity
import com.app.studentmanagement.viewmodels.AccountViewModel

class PersonalFragment : Fragment() {

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: FragmentPersonalBinding
    private var account: Account? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        viewModel.setCurrentUser()
        viewModel.currentUser.observe(this){
            it ->
            account = it
            setupData(it)
        }
        binding.buttonEdit.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), AddEditAccountActivity::class.java)
            intent.putExtra("account",account)
            startActivity(intent)
        })
        return binding.root
    }

    private fun setupData(account: Account) {
        binding.textViewName.setText(account.name)
        binding.editTextID.setText(account.id)
        binding.editTextID.isEnabled = false
        binding.editTextEmail.isEnabled = false

        binding.editTextEmail.setText(account.email)
    }
}