package com.app.studentmanagement.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.CertificateAdapter
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ActivityAddEditStudentBinding
import com.app.studentmanagement.viewmodels.StudentViewModel

class AddEditStudentActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 101

    private lateinit var viewModel: StudentViewModel
    private lateinit var binding: ActivityAddEditStudentBinding
    private lateinit var csvFileLauncher: ActivityResultLauncher<Intent>

    private var listCertificate: MutableList<Certificate> = ArrayList()
    val adapter = CertificateAdapter()
    private var existingStudent: Student? = null
    private var importMode: String? = null
    var faculty : String = ""
    var facultyCode : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_student)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        existingStudent = intent.getSerializableExtra("student") as Student?
        importMode = intent.getSerializableExtra("importMode") as String?
        if(existingStudent!= null) {
            setupEditMode()
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

        val faculties = resources.getStringArray(R.array.faculties)
        val facultiesCode = resources.getStringArray(R.array.facultiesCode)

        val adapterInputRole = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, faculties)
        val autoCompleteTextView = binding.autoCompleteTextViewOption
        autoCompleteTextView.setAdapter(adapterInputRole)
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            faculty = faculties[position]
            facultyCode = facultiesCode[position]
        }

        if(existingStudent== null) {
            faculty = faculties[0]
            facultyCode = facultiesCode[0]
            binding.autoCompleteTextViewOption.setText(faculty,false)
        }
        csvFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    listCertificate =   viewModel.readCertificatesCSVFile(uri, contentResolver)
                    adapter.updateList(listCertificate)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.buttonImport.setOnClickListener(View.OnClickListener {
            if (checkPermissions()) {
                openCsvFilePicker()
            } else {
                requestPermissions()
            }
        })
    }
    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above, check for MANAGE_EXTERNAL_STORAGE permission
            return Environment.isExternalStorageManager()
        } else {
            // For Android 10 and below, check for read and write permissions
            val readPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission = ContextCompat.checkSelfPermission(
                this,
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
                this,
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
    fun saveStudent() {
        val email = binding.editTextEmail.text.toString()
        val faculty = faculty
        val classRoom = binding.editTextClass.text.toString()
        val fullName = binding.textViewName.text.toString()

        // First, validate faculty and class synchronously
        val isClassValid = isValidClass(classRoom)
        val isValidName = isValidName(fullName)
        if (!isClassValid || !isValidName) {
            return
        }

        // Validate ID asynchronously
        isValidEmail(email) { isEmailValid ->
            if (!isEmailValid) {
                return@isValidEmail
            }

            val student = Student(
                email =  email,
                faculty = faculty,
                fullName =  fullName,
                facultyCode = facultyCode,
                classRoom =  classRoom,
                certificates = listCertificate)
            if (importMode != null){

                if (student.facultyCode.isBlank()){
                    student.facultyCode = existingStudent?.facultyCode ?: ""
                }
                student.id = existingStudent!!.id
                val returnIntent = Intent()
                returnIntent.putExtra("student", student)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }else if (existingStudent != null){
                if (student.facultyCode.isBlank()){
                    student.facultyCode = existingStudent?.facultyCode ?: ""
                }
                student.id = existingStudent!!.id
                viewModel.updateStudent(student){
                    val returnIntent = Intent()
                    returnIntent.putExtra("student", student)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }else{
                student.facultyCode = facultyCode
                viewModel.createStudent(student){
                    finish()
                }
            }
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
    fun isValidName(name: String): Boolean{
        if(name.isEmpty() || name.isBlank()){
            binding.layoutName.setText("Tên không hợp lệ!")
            return false
        }else{
            binding.layoutName.setText(null)
            return true
        }
    }

    fun isValidEmail(email: String, onComplete: (Boolean) -> Unit) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"

        if (email.isEmpty() || !email.matches(emailRegex.toRegex())) {
            binding.layoutEmail.error = "Email không hợp lệ!"
            onComplete(false)
            return
        } else if (existingStudent != null && email == existingStudent!!.email && importMode == null) {
            binding.layoutEmail.error = null
            onComplete(true)
        }else {
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
            binding.autoCompleteTextViewOption.setText(existingStudent!!.faculty, false)
            faculty = existingStudent!!.faculty
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