package com.snapnoob.notes.feature.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityNotesListBinding
import com.snapnoob.notes.feature.biometric.CIPHERTEXT_WRAPPER
import com.snapnoob.notes.feature.biometric.CryptographyManager
import com.snapnoob.notes.feature.biometric.EnableBiometricLoginActivity
import com.snapnoob.notes.feature.biometric.SHARED_PREFS_FILENAME
import com.snapnoob.notes.feature.edit.EditNotesActivity
import com.snapnoob.notes.feature.edit.EditNotesEvent
import com.snapnoob.notes.feature.notification.NotificationActivity
import com.snapnoob.notes.feature.profile.ProfileActivity
import com.snapnoob.notes.network.RetrofitService
import com.snapnoob.notes.network.model.Notes
import com.snapnoob.notes.network.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    private lateinit var binding: ActivityNotesListBinding
    private lateinit var view: View

    private lateinit var viewModel: NotesListViewModel

    private lateinit var user: User

    private val notesListAdapter by lazy {
        NotesListAdapter {
            openEditNoteActivity(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[NotesListViewModel::class.java]
        observeViewModel()
        initView()

        viewModel.getAllNotes()

        val canAuthenticate = BiometricManager.from(this).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (ciphertextWrapper == null) {
                startActivity(Intent(this, EnableBiometricLoginActivity::class.java))
            }
        }
    }

    private fun initView() {
        viewModel.getProfile()

        binding.swipeRefresh.setOnRefreshListener { viewModel.getAllNotes() }
        binding.rvNotesList.apply {
            layoutManager = LinearLayoutManager(this@NotesListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = notesListAdapter
        }
        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuCreate -> openEditNoteActivity(null)
                R.id.createNotification -> openCreateNotificationActivity()
            }
            true
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllNotes()
        }
        binding.imgSetting.setOnClickListener { openProfileActivity() }
    }

    private fun observeViewModel() {
        viewModel.notesListLiveData.observe(this, { listNotes ->
            if (listNotes.isNotEmpty()) {
                binding.rvNotesList.visibility = View.VISIBLE
                notesListAdapter.setData(listNotes)
                binding.viewNoData.root.visibility = View.GONE
            } else {
                binding.rvNotesList.visibility = View.GONE
                binding.viewNoData.root.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.eventLiveData.observe(this, this::handleEvent)
    }

    private fun handleEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.ShowError -> {
                Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
            }
            is NotesListEvent.DisplayProfile -> displayUserData(event.user)
        }
    }

    private fun displayUserData(user: User) {
        this.user = user
        if (user.profilePicture != null && user.profilePicture.isNotEmpty()) {
            Glide.with(view)
                .load(RetrofitService.BASE_URL_V1.plus(user.profilePicture))
                .into(binding.imgProfile)
        }
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
    }

    private fun openEditNoteActivity(notes: Notes?) {
        val intent = Intent(this, EditNotesActivity::class.java)
        if (notes != null) {
            intent.putExtra(EditNotesActivity.NOTES_OBJECT, notes)
        }
        startActivity(intent)
    }

    private fun openProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(ProfileActivity.USER_PARCELABLE, user)
        startActivity(intent)
    }

    private fun openCreateNotificationActivity() {
        startActivity(Intent(this, NotificationActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefresh.isRefreshing = true
        viewModel.getAllNotes()
    }
}