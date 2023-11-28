package com.app.studentmanagement.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.app.studentmanagement.R
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.databinding.FragmentPersonalBinding
import com.app.studentmanagement.ui.activities.AddEditAccountActivity
import com.app.studentmanagement.viewmodels.AccountViewModel
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PersonalFragment : Fragment() {

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: FragmentPersonalBinding
    private var account: Account? = null
    val CAMERA_REQUEST_CODE = 102
    val GALLERY_REQUEST_CODE = 103

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

        binding.buttonUploadAvt.setOnClickListener(View.OnClickListener {
            showImagePickerOptions()
        })
        return binding.root
    }

    private fun setupData(account: Account) {
        binding.textViewName.setText(account.name)
        binding.editTextID.setText(account.id)
        binding.editTextID.isEnabled = false
        binding.editTextEmail.isEnabled = false
        Glide.with(this)
            .load(account.avatarUrl)
            .into(binding.imageAvatarPersonal)
        binding.editTextEmail.setText(account.email)
    }

    fun showImagePickerOptions() {
        // You can use an AlertDialog to let the user choose between camera and gallery
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    fun takePhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }

    fun choosePhotoFromGallery() {
        // Intent to pick photo from gallery
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    val photoUri = bitmapToUri(requireContext(), photo)
                    photoUri?.let {
                        viewModel.upLoadAvatar(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        viewModel.upLoadAvatar(selectedImageUri)
                    }
                }
            }
        }
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val filename = "temp_image"
        val file = File(context.cacheDir, filename)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()

        try {
            FileOutputStream(file).apply {
                write(bitmapData)
                flush()
                close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // Get the Uri from FileProvider
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

}