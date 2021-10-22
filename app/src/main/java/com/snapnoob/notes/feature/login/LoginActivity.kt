package com.snapnoob.notes.feature.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityLoginBinding
import com.snapnoob.notes.feature.biometric.BiometricPromptUtils
import com.snapnoob.notes.feature.biometric.CIPHERTEXT_WRAPPER
import com.snapnoob.notes.feature.biometric.CryptographyManager
import com.snapnoob.notes.feature.biometric.EnableBiometricLoginActivity
import com.snapnoob.notes.feature.biometric.SHARED_PREFS_FILENAME
import com.snapnoob.notes.feature.main.NotesListActivity
import com.snapnoob.notes.feature.register.RegisterActivity
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.util.AppUtil
import com.snapnoob.notes.util.isAllEditTextNotEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    private lateinit var binding: ActivityLoginBinding
    private lateinit var view: View

    private lateinit var viewModel: LoginViewModel

    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        observeViewModel()

        initView()

        val canAuthenticate = BiometricManager.from(this).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (ciphertextWrapper != null) {
                showBiometricPromptForDecryption()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when (event) {
                is LoginEvent.ShowProgressBar -> showProgressDialog()
                is LoginEvent.LoginSuccess -> {
                    dismissProgressDialog()
                    Toast.makeText(this, "Login success", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, NotesListActivity::class.java))
                    finish()
                }
                is LoginEvent.ShowError -> Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener { doLogin() }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        val listEditText = listOf(
            binding.edtEmail,
            binding.edtPassword
        )
        if (isAllEditTextNotEmpty(listEditText)) {
            val login = Login(
                email = binding.edtEmail.text.toString(),
                password = binding.edtPassword.text.toString()
            )
            viewModel.login(login)
        } else {
            Snackbar.make(view, "All field must be filled!", Snackbar.LENGTH_LONG).show()
        }
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

    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val secretKeyName = getString(R.string.secret_key_name)
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                secretKeyName, textWrapper.initializationVector
            )
            biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    this,
                    ::decryptCredentialFromStorage
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun decryptCredentialFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                val login = Gson().fromJson(plaintext, Login::class.java)
                viewModel.login(login)
            }
        }
    }
}