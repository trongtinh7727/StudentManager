package com.app.studentmanagement.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.models.SortCriteria
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.FragmentStudentManagementBinding
import com.app.studentmanagement.ui.activities.AddEditStudentActivity
import com.app.studentmanagement.ui.activities.ImportFileActivity
import com.app.studentmanagement.viewmodels.AccountViewModel
import com.app.studentmanagement.viewmodels.StudentViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StudentManagementFragment : Fragment() {

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: FragmentStudentManagementBinding
    private lateinit var adapter: StudentAdapter
    private lateinit var viewModel: StudentViewModel
    private val REQUEST_PERMISSION_CODE = 101
    private lateinit var csvFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var createFileLauncher: ActivityResultLauncher<Intent>
    private var listStudent: MutableList<Student> = mutableListOf()

    var currentSortCriteria = SortCriteria.ID
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentManagementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        adapter = StudentAdapter(requireContext(),viewModel,null)

        accountViewModel = requireActivity().run {
            ViewModelProvider(this).get(AccountViewModel::class.java)
        }
        accountViewModel.setCurrentUser()
        accountViewModel.currentUser.observe(this){
            if (it.role == Role.Employee){
                setUpEmployee()
            }
        }
        viewModel.getAllStudent()
        viewModel.students.observe(this){
            listStudents->
            listStudent = listStudents
            currentSortCriteria = SortCriteria.ID
            binding.textViewID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            sortData()
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

        createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.exportCSVFile(uri, requireActivity().contentResolver, listStudent)
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

        binding.buttonExport.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                val currentDateTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(
                    LocalDateTime.now())
                putExtra(Intent.EXTRA_TITLE, "sv_$currentDateTime.csv")
            }
            createFileLauncher.launch(intent)
        })

        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }

        binding.textViewID.setOnClickListener {
            if (currentSortCriteria == SortCriteria.ID) {
                currentSortCriteria = SortCriteria.ID_DESCENDING
                binding.textViewID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                currentSortCriteria = SortCriteria.ID
                binding.textViewID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
            binding.textViewClass.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.textViewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            sortData()
        }

        binding.textViewName.setOnClickListener {
            if (currentSortCriteria == SortCriteria.FULL_NAME) {
                currentSortCriteria = SortCriteria.FULL_NAME_DESCENDING
                binding.textViewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                currentSortCriteria = SortCriteria.FULL_NAME
                binding.textViewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
            binding.textViewClass.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.textViewID.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            sortData()
        }
        binding.textViewClass.setOnClickListener {
            if (currentSortCriteria == SortCriteria.CLASS) {
                currentSortCriteria = SortCriteria.CLASS_DESCENDING
                binding.textViewClass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                currentSortCriteria = SortCriteria.CLASS
                binding.textViewClass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
            binding.textViewID.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.textViewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            sortData()
        }

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


    fun setUpEmployee(){
        adapter.setIsEdit(false)
        binding.layoutAction.visibility = View.GONE
        binding.buttonAdd.visibility = View.GONE
        binding.buttonImport.visibility = View.GONE
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

    fun sortData() {
        when (currentSortCriteria) {
            SortCriteria.ID -> {
                listStudent.sortBy { it.id }
            }
            SortCriteria.ID_DESCENDING -> {
                listStudent.sortByDescending { it.id }
            }
            SortCriteria.FULL_NAME -> {
                listStudent = listStudent.sortedWith(compareBy { student ->
                    student.fullName.split(" ").let { parts ->
                        val lastName = parts.lastOrNull() ?: ""
                        val firstName = parts.dropLast(1).joinToString(" ")
                        "$lastName, $firstName"
                    }
                }).map { it } as MutableList<Student>

            }
            SortCriteria.FULL_NAME_DESCENDING -> {
                listStudent = listStudent.sortedWith(compareByDescending { student ->
                    student.fullName.split(" ").let { parts ->
                        val lastName = parts.lastOrNull() ?: ""
                        val firstName = parts.dropLast(1).joinToString(" ")
                        "$lastName, $firstName"
                    }
                }).map { it } as MutableList<Student>
            }
            SortCriteria.CLASS -> {
                listStudent.sortBy { it.classRoom }
            }
            SortCriteria.CLASS_DESCENDING -> {
                listStudent.sortByDescending { it.classRoom }
            }
        }
        adapter.updateList(listStudent)
    }

}