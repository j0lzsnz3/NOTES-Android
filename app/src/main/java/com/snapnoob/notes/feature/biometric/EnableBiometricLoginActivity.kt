package com.snapnoob.notes.feature.biometric

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityEnableBiometricLoginBinding
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.util.AppUtil
import com.snapnoob.notes.util.isAllEditTextNotEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnableBiometricLoginActivity : AppCompatActivity() {

    private var progressDialog: AlertDialog? = null
    private lateinit var cryptographyManager: CryptographerManager
    private lateinit var binding: ActivityEnableBiometricLoginBinding
    private lateinit var view: View

    private val viewModel by viewModels<EnableBiometricLoginViewModel>()

    private var login: Login? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableBiometricLoginBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        observeViewModel()
        initView()
    }

    private fun initView() {
        binding.authorize.setOnClickListener { doLogin() }
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when (event) {
                is EnableBiometricLoginEvent.LoginSuccess -> {
                    showBiometricPromptForEncryption()
                }
                is EnableBiometricLoginEvent.ShowError -> {
                    Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
                }
                is EnableBiometricLoginEvent.ShowProgressBar -> showProgressDialog()
                is EnableBiometricLoginEvent.DismissProgressBar -> dismissProgressDialog()
            }
        })
    }

    private fun doLogin() {
        val listEditText = listOf(
            binding.edtEmail,
            binding.edtPassword
        )
        if (isAllEditTextNotEmpty(listEditText)) {
            login = Login(
                email = binding.edtEmail.text.toString(),
                password = binding.edtPassword.text.toString()
            )
            viewModel.login(login!!)
        } else {
            Snackbar.make(view, "All field must be filled!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showBiometricPromptForEncryption() {
        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val secretKeyName = getString(R.string.secret_key_name)
            cryptographyManager = CryptographyManager()
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(this, ::encryptAndStoreServerToken)
            val promptInfo = BiometricPromptUtils.createPromptInfo(this)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        authResult.cryptoObject?.cipher?.apply {
            val credential = Gson().toJson(login)
            Log.d(TAG, "The token from server is $credential")
            val encryptedServerTokenWrapper = cryptographyManager.encryptData(credential, this)
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                encryptedServerTokenWrapper,
                applicationContext,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
            )
        }
        finish()
    }

    private fun showProgressDialog() {
        progressDialog = AppUtil.createProgressDialog(this)
        progressDialog!!.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onPause() {
        dismissProgressDialog()
        super.onPause()
    }

    companion object {
        private const val TAG = "EnableBiometricLogin"
    }
}