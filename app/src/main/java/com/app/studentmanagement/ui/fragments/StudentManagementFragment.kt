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
        val adapter = StudentAdapter(requireContext(),viewModel)

        viewModel.getAllStudent()
        viewModel.students.observe(this){
            listStudents->
            adapter.updateList(listStudents)
        }

        binding.recycleViewListStudent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListStudent.adapter = adapter

        binding.buttonAdd.setOnClickListener {
            var intent = Intent(requireContext(), AddEditStudentActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSearch.setOnClickListener(View.OnClickListener {
            val name = binding.editTextName.text.toString()
            val id = binding.editTextID.text.toString()
            val classroom = binding.editTextClass.text.toString()
            viewModel.search(name,id,classroom)
        })

        return binding.root
    }

}