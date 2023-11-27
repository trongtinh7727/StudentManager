package com.app.studentmanagement.ui.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.CertificateAdapter
import com.app.studentmanagement.adapters.StudentAdapter
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.databinding.ActivityAddEditStudentBinding
import com.app.studentmanagement.databinding.ActivityDetailStudentInformationBinding
import com.app.studentmanagement.viewmodels.StudentViewModel

class DetailStudentInformationActivity : AppCompatActivity() {

    private lateinit var viewModel: StudentViewModel
    private lateinit var binding: ActivityDetailStudentInformationBinding
    private var listCertificate: MutableList<Certificate> = ArrayList()
    val adapter = CertificateAdapter()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var existingStudent: Student? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_student_information)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back);

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        existingStudent = intent.getSerializableExtra("student") as Student?
        if (existingStudent!=null){
            binding.student = existingStudent
            binding.autoCompleteTextViewOption.setText(existingStudent!!.faculty)
            listCertificate = existingStudent!!.certificates
            adapter.notifyDataSetChanged()
        }
        adapter.setIsEdit(false)
        adapter.updateList(listCertificate)

        binding.recycleViewListCerfiticate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListCerfiticate.adapter = adapter

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null){
                    existingStudent = data.getSerializableExtra("student") as Student?
                    if (existingStudent!=null){
                        binding.student = existingStudent
                        listCertificate = existingStudent!!.certificates
                        binding.autoCompleteTextViewOption.setText(existingStudent!!.faculty)
                        adapter.updateList(listCertificate)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        binding.buttonEdit.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddEditStudentActivity::class.java)
            intent.putExtra("student",existingStudent)
            resultLauncher.launch(intent)
        })
        binding.buttonDelete.setOnClickListener(View.OnClickListener {
            existingStudent?.let { it1 -> showDeleteConfirm(it1) }
        })
        val progressDialog = createProgressDialog()
        viewModel.loadingIndicator.observe(this){
            if (it){
                progressDialog.show()
            }else{
                progressDialog.dismiss()
            }
        }
    }
    private fun showDeleteConfirm(student: Student){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = dialog.window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
            dialog.window!!.attributes = wlp
        }


        val buttonConfirmDelete = dialog.findViewById<TextView>(R.id.buttonConfirmDelete)
        val buttonBack = dialog.findViewById<TextView>(R.id.buttonBack)

        buttonConfirmDelete.setOnClickListener {
            viewModel.deleteStudent(student.id){
                finish()
            }
            dialog.dismiss()
        }
        buttonBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

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
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}