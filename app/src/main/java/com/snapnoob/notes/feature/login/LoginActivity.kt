package com.snapnoob.notes.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.databinding.ActivityLoginBinding
import com.snapnoob.notes.feature.main.NotesListActivity
import com.snapnoob.notes.feature.register.RegisterActivity
import com.snapnoob.notes.network.model.Login
import com.snapnoob.notes.network.model.User
import com.snapnoob.notes.util.AppUtil
import com.snapnoob.notes.util.isAllEditTextNotEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

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
}