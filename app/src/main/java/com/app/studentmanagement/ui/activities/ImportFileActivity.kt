package com.app.studentmanagement.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.StudentAdapter
import com.app.studentmanagement.data.StudentAdapterListener
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ActivityImportFileBinding
import com.app.studentmanagement.viewmodels.StudentViewModel
import kotlinx.coroutines.*

class ImportFileActivity : AppCompatActivity(), StudentAdapterListener {
    private lateinit var viewModel: StudentViewModel
    var listStudents: MutableList<Student>? = mutableListOf()
    var duplicateEmai: MutableList<Student> = mutableListOf()
    private lateinit var adapter: StudentAdapter
    private lateinit var binding: ActivityImportFileBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_import_file)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back);

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        listStudents = intent.getSerializableExtra("listStudent") as? ArrayList<Student>

        adapter = StudentAdapter(this, viewModel, this)

        listStudents?.let { adapter.updateList(it) }
        adapter.notifyDataSetChanged()

        binding.recycleViewListStudent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListStudent.adapter = adapter

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val existingStudent = data.getSerializableExtra("student") as Student?
                    if (listStudents != null) {
                        if (existingStudent != null) {
                            (listStudents as ArrayList<Student>)[index] = existingStudent
                            adapter.updateList(listStudents as ArrayList<Student>)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }

        binding.buttonSave.setOnClickListener(View.OnClickListener {
            checkAllEmailsAndSave()
        })
    }
    fun checkAllEmail(){
        listStudents?.let { students ->
            val duplicateEmails = mutableListOf<Student>()
            var remainingEmailChecks = students.size

            for (student in students) {
                viewModel.isEmailUnique(student.email) { isUnique ->
                    remainingEmailChecks--
                    if (!isUnique) {
                        duplicateEmails.add(student)
                    }
                    if (remainingEmailChecks == 0) {
                        if (!duplicateEmails.isEmpty()) {
                            adapter.setEmailUniquenessMap(duplicateEmails)
                        }
                    }
                }
            }
        }
    }
    private fun checkAllEmailsAndSave() {
        listStudents?.let { students ->
            val duplicateEmails = mutableListOf<Student>()
            var remainingEmailChecks = students.size

            for (student in students) {
                viewModel.isEmailUnique(student.email) { isUnique ->
                    remainingEmailChecks--
                    if (!isUnique) {
                        duplicateEmails.add(student)
                    }
                    if (remainingEmailChecks == 0) {
                        if (duplicateEmails.isEmpty()) {
                            Toast.makeText(this@ImportFileActivity, "Đang lưu", Toast.LENGTH_SHORT).show()
                            viewModel.addListStudent(students) {
                                finish()
                            }
                        } else {
                            adapter.setEmailUniquenessMap(duplicateEmails)
                            Toast.makeText(this@ImportFileActivity, "Có một hoặc nhiều email đã tồn tại hoặc trùng vui lòng sửa lại!.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onEditStudent(student: Student, index: Int) {
        val intent = Intent(this, AddEditStudentActivity::class.java)
        intent.putExtra("student", student)
        intent.putExtra("importMode", "true")
        this.index = index
        resultLauncher.launch(intent)
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