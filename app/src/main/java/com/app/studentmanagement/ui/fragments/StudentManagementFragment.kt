package com.app.studentmanagement.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.StudentAdapter
import com.app.studentmanagement.databinding.FragmentStudentManagementBinding
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.ui.activities.ImportFileActivity
import com.app.studentmanagement.viewmodels.StudentViewModel


class StudentManagementFragment : Fragment() {


    private lateinit var binding: FragmentStudentManagementBinding
    private lateinit var viewModel: StudentViewModel
    private val REQUEST_PERMISSION_CODE = 101
    private lateinit var csvFileLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentManagementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        val adapter = StudentAdapter(requireContext(),viewModel,null)

        viewModel.getAllStudent()
        viewModel.students.observe(this){
            listStudents->
            adapter.updateList(listStudents)
        }

        val facultyCodeArray = resources.getStringArray(R.array.facultiesCode)
        val facultyNameArray = resources.getStringArray(R.array.faculties)

        val facultyMap = mutableMapOf<String, String>()

        for (i in facultyCodeArray.indices) {
            val code = facultyCodeArray[i]
            val name = facultyNameArray.getOrNull(i) ?: ""
            facultyMap[code] = name
        }
        csvFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    val listStudent =   viewModel.readCSVFile(uri, requireActivity().contentResolver,facultyMap)
                    val intent = Intent(requireContext(), ImportFileActivity::class.java)
                    intent.putExtra("listStudent",ArrayList(listStudent))
                    startActivity(intent)
                }
            }
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

        binding.buttonImport.setOnClickListener(View.OnClickListener {
            if (checkPermissions()) {
                openCsvFilePicker()
            } else {
                requestPermissions()
            }
        })

        return binding.root
    }
    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above, check for MANAGE_EXTERNAL_STORAGE permission
            return Environment.isExternalStorageManager()
        } else {
            // For Android 10 and below, check for read and write permissions
            val readPermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCsvFilePicker()
            } else {
            }
        }
    }
    private fun openCsvFilePicker() {
        val intent = Intent().apply {
            type = "*/*"
            putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true)
            action = Intent.ACTION_GET_CONTENT
        }

        csvFileLauncher.launch(intent)
    }



}