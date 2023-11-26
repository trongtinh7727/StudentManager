package com.app.studentmanagement.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.studentmanagement.R
import com.app.studentmanagement.adapters.CertificateAdapter
import com.app.studentmanagement.adapters.StudentAdapter
import com.app.studentmanagement.data.models.Certificate
import com.app.studentmanagement.databinding.ActivityAddEditAccountBinding
import com.app.studentmanagement.databinding.ActivityDetailStudentInformationBinding

class DetailStudentInformationActivity : AppCompatActivity() {

    private val listCertificate: MutableList<Certificate> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailStudentInformationBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_student_information)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back);
        val adapter = CertificateAdapter();
        adapter.updateList(listCertificate);

        binding.recycleViewListCerfiticate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycleViewListCerfiticate.adapter = adapter


        binding.buttonAdd.setOnClickListener {
            val newCertificate = Certificate("","")
            listCertificate.add(newCertificate)
            adapter.notifyDataSetChanged()        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}