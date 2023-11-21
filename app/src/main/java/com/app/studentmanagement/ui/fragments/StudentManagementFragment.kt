package com.app.studentmanagement.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.adapters.StudentAdapter
import com.app.studentmanagement.databinding.FragmentStudentManagementBinding
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.viewmodels.StudentViewModel


class StudentManagementFragment : Fragment() {


    private lateinit var binding: FragmentStudentManagementBinding
    private lateinit var viewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentManagementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]


        // set viewModel,Adapter and fill recycleView
        val accounts = viewModel.generateStudents(30) // You should implement your data generation logic
        val adapter = StudentAdapter(requireContext())
        adapter.updateList(accounts)

        binding.recycleViewListStudent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListStudent.adapter = adapter

        binding.buttonAdd.setOnClickListener {
            var intent = Intent(requireContext(), AddEditStudentActivity::class.java)
            startActivity(intent)
        }


        // set button add new Student

        return binding.root
    }

}