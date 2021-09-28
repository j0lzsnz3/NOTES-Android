package com.snapnoob.notes.feature.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityProfileBinding
import com.snapnoob.notes.databinding.ViewChoosePhotoBinding
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.util.AndroidRuntimePermission
import com.snapnoob.notes.util.AppUtil
import com.snapnoob.notes.util.ImagePickerManager
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var view: View

    private lateinit var viewModel: ProfileViewModel

    private lateinit var user: User

    private var alertDialog: AlertDialog? = null

    private val imagePicker by lazy {
        ImagePickerManager.newInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        observeViewModel()

        if (intent.hasExtra(USER_PARCELABLE)) {
            user = intent.getParcelableExtra(USER_PARCELABLE)!!
            initView()
            displayProfile()
        } else {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when(event) {
                is ProfileEvent.UpdateProfilePictureSuccess -> {
                    Snackbar.make(view, "Update Profile Picture success", Snackbar.LENGTH_LONG).show()
                }
                is ProfileEvent.ShowError -> Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun initView() {
        binding.toolBar.title = "Profile"
        binding.toolBar.setNavigationOnClickListener { finish() }
        binding.btnChoosePhoto.setOnClickListener { choosePhoto() }
        binding.imgProfile.setOnClickListener { choosePhoto() }
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

    private fun displayProfile() {
        binding.edtName.setText(user.name)
        binding.edtEmail.setText(user.email)
        binding.edtBirthDay.setText(user.birthDay)
    }

    private fun uploadProfilePicture(photoFile: File, userId: Long) {
        photoFile.let { file ->
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

        binding.btnChoosePhoto.visibility = View.INVISIBLE
        uploadProfilePicture(file, user.id!!)
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

    companion object {
        const val USER_PARCELABLE = "user_parcelable"
    }
}