package com.app.studentmanagement.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.CertificateAdapter
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ActivityAddEditStudentBinding
import com.app.studentmanagement.viewmodels.StudentViewModel

class AddEditStudentActivity : AppCompatActivity() {
    private lateinit var viewModel: StudentViewModel
    private lateinit var binding: ActivityAddEditStudentBinding
    private var listCertificate: MutableList<Certificate> = ArrayList()
    val adapter = CertificateAdapter()
    private var existingStudent: Student? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_student)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        existingStudent = intent.getSerializableExtra("student") as Student?
       if(existingStudent!= null) {
            setupEditMode()
        } else {
            setupAddMode()
        }

        adapter.updateList(listCertificate);
        binding.recycleViewListCerfiticate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListCerfiticate.adapter = adapter


        binding.buttonAdd.setOnClickListener {
            val newCertificate = Certificate("","")
            listCertificate.add(newCertificate)
            adapter.notifyDataSetChanged()
        }
        //set progressbar
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }
        binding.buttonSave.setOnClickListener(View.OnClickListener {
            saveStudent()
        })


    }
    fun saveStudent() {
        val id = binding.editTextID.text.toString()
        val email = binding.editTextEmail.text.toString()
        val faculty = binding.editTextFaculty.text.toString()
        val classRoom = binding.editTextClass.text.toString()
        val fullName = binding.textViewName.text.toString()

        // First, validate faculty and class synchronously
        val isFacultyValid = isValidFaculty(faculty)
        val isClassValid = isValidClass(classRoom)

        if (!isFacultyValid || !isClassValid) {
            // Stop further validation if faculty or class is invalid
            return
        }

        // Validate ID asynchronously
        isValidID(id) { isIDValid ->
            if (!isIDValid) {
                return@isValidID
            }

            // If ID is valid, proceed to validate email
            isValidEmail(email) { isEmailValid ->
                if (!isEmailValid) {
                    return@isValidEmail
                }

                val student = Student(
                   id =  id,
                    email =  email,
                   faculty = faculty,
                   fullName =  fullName,
                    classRoom =  classRoom,
                    certificates = listCertificate)
                if (existingStudent != null){
                    viewModel.updateStudent(student){
                        val returnIntent = Intent()
                        returnIntent.putExtra("student", student)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }else{
                    viewModel.createStudent(student){
                        finish()
                    }
                }
            }
        }
    }

// Keep your existing validation functions as they are

    fun isValidFaculty(faculty: String): Boolean{
        if(faculty.isEmpty() || faculty.isBlank()){
            binding.layoutFaculty.error = "Khoa không hợp lệ!"
            return false
        }else{
            binding.layoutFaculty.error = null
            return true
        }
    }
    fun isValidClass(classRom: String): Boolean{
        if(classRom.isEmpty() || classRom.isBlank()){
            binding.layoutClass.error = "Lớp không hợp lệ!"
            return false
        }else{
            binding.layoutClass.error = null
            return true
        }
    }
    fun isValidID(id: String, onComplete: (Boolean) -> Unit) {
        if (id.isEmpty()) {
            binding.layoutID.error = "ID không hợp lệ!"
            onComplete(false)
        } else {
            if (existingStudent != null && id == existingStudent!!.id) {
                binding.layoutID.error = null
                onComplete(true)
            } else {
                viewModel.isCodeUnique(id) { isSuccess ->
                    if (!isSuccess) {
                        binding.layoutID.error = "ID đã tồn tại!"
                        onComplete(false)
                    } else {
                        binding.layoutID.error = null
                        onComplete(true)
                    }
                }
            }
        }
    }
    fun isValidEmail(email: String, onComplete: (Boolean) -> Unit) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"

        if (email.isEmpty() || !email.matches(emailRegex.toRegex())) {
            binding.layoutEmail.error = "Email không hợp lệ!"
            onComplete(false)
        } else if (existingStudent != null && email == existingStudent!!.email) {
            binding.layoutEmail.error = null
            onComplete(true)
        } else {
            viewModel.isEmailUnique(email) { isSuccess ->
                if (!isSuccess) {
                    binding.layoutEmail.error = "Email đã tồn tại!"
                    onComplete(false)
                } else {
                    binding.layoutEmail.error = null
                    onComplete(true)
                }
            }
        }
    }

    private fun setupAddMode() {

    }

    private fun setupEditMode() {
        if (existingStudent!=null){
            binding.editTextID.isEnabled = false
            binding.student = existingStudent
            listCertificate = existingStudent!!.certificates
            adapter.notifyDataSetChanged()
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