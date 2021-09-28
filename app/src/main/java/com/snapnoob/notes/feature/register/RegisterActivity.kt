package com.snapnoob.notes.feature.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityRegisterBinding
import com.snapnoob.notes.databinding.ViewChoosePhotoBinding
import com.snapnoob.notes.feature.login.LoginActivity
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.util.AndroidRuntimePermission
import com.snapnoob.notes.util.AppUtil
import com.snapnoob.notes.util.ImagePickerManager
import com.snapnoob.notes.util.isAllEditTextNotEmpty
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var view: View

    private lateinit var viewModel: RegisterViewModel

    private var alertDialog: AlertDialog? = null
    private var progressDialog: AlertDialog? = null

    private var photoFile: File? = null

    private val imagePicker by lazy {
        ImagePickerManager.newInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        observeViewModel()

        initView()
    }

    private fun initView() {
        binding.toolBar.title = getString(R.string.register)
        binding.toolBar.setNavigationOnClickListener { finish() }
        binding.imgProfile.setOnClickListener { choosePhoto() }
        binding.btnChoosePhoto.setOnClickListener { choosePhoto() }
        binding.btnRegister.setOnClickListener { doRegister() }
        binding.edtBirthDay.apply {
            setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.str_choose_birth_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener {
                    setText(AppUtil.millisToDate(datePicker.selection ?: 0L))
                }

                if (!datePicker.isVisible) {
                    datePicker.show(supportFragmentManager, "Date Picker")
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when (event) {
                is RegisterEvent.ShowProgressBar -> showProgressDialog()
                is RegisterEvent.HideProgressBar -> dismissProgressDialog()
                is RegisterEvent.RegisterSuccess -> {
                    dismissProgressDialog()
                    Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                is RegisterEvent.UploadProfilePicture -> uploadProfilePicture(event.userId)
                is RegisterEvent.ShowError -> Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun doRegister() {
        val listEditText = listOf(
            binding.edtName,
            binding.edtEmail,
            binding.edtBirthDay,
            binding.edtPassword,
            binding.edtPasswordConfirm
        )
        if (isAllEditTextNotEmpty(listEditText)) {
            if (binding.edtPassword.text.toString() == binding.edtPasswordConfirm.text.toString()) {
                val user = User(
                    name = binding.edtName.text.toString(),
                    birthDay = binding.edtBirthDay.text.toString(),
                    email = binding.edtEmail.text.toString(),
                    password = binding.edtPassword.text.toString()
                )
                val isPhotoAvailable = photoFile != null
                viewModel.register(user, isPhotoAvailable)
            } else {
                Snackbar.make(view, "Password with Password Confirm not match!", Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(view, "All field must be filled!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun uploadProfilePicture(userId: Long) {
        photoFile?.let { file ->
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("profile_picture", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("user_id", userId.toString())
                .build()

            viewModel.uploadProfilePicture(requestBody)
        }
    }

    private fun choosePhoto() {
        val bindingChoosePhoto = ViewChoosePhotoBinding.inflate(LayoutInflater.from(this))

        bindingChoosePhoto.chooseFromCamera.setOnClickListener {
            checkCameraPermission()
        }

        bindingChoosePhoto.chooseFromGallery.setOnClickListener {
            imagePicker.fromGallery()
        }

        alertDialog = AlertDialog.Builder(this)
            .setView(bindingChoosePhoto.root)
            .create()

        alertDialog!!.show()
    }

    private fun checkCameraPermission() {
        val cameraPermissionListener = object :
            AndroidRuntimePermission.OnPermissionGrantedListener {
            override fun onPermissionGranted() {
                imagePicker.fromCamera()
            }
        }

        AndroidRuntimePermission.checkCameraPermission(this, cameraPermissionListener)
    }

    private fun onCameraResult(file: File?) {
        if (file != null && file.exists()) {
            displayImage(file)
        }
    }

    private fun onGalleryResult(file: File?) {
        if (file != null && file.exists()) {
            displayImage(file)
        }
    }

    private fun displayImage(file: File) {
        Glide.with(view)
            .load(file)
            .centerCrop()
            .into(binding.imgProfile)

        photoFile = file

        binding.btnChoosePhoto.visibility = View.INVISIBLE

        alertDialog?.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagePicker.isResultFromGallery(requestCode, resultCode, data) { isOk ->
            if (isOk) {
                onGalleryResult(imagePicker.generateImageFromLibrary(data))
            }
        }

        imagePicker.isResultFromCamera(requestCode, resultCode, callback = { isOk, file ->
            if (isOk) {
                onCameraResult(file)
            }
        })
    }

    private fun showProgressDialog() {
        progressDialog = AppUtil.createProgressDialog(this)
        progressDialog!!.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onPause() {
        super.onPause()
        dismissProgressDialog()
    }
}