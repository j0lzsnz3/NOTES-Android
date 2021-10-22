package com.snapnoob.notes.feature.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.snapnoob.notes.databinding.ActivitySplashBinding
import com.snapnoob.notes.feature.login.LoginActivity
import com.snapnoob.notes.feature.main.NotesListActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var view: View

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        observeViewModel()
        checkIsUserLoggedIn()
        viewModel.setFcmToken()
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when (event) {
                is SplashEvent.OpenLoginPage -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                is SplashEvent.OpenHomePage -> {
                    startActivity(Intent(this, NotesListActivity::class.java))
                }
            }
        })
    }

    private fun checkIsUserLoggedIn() {
        Timer("Launch next activity", false).schedule(4000) {
            viewModel.checkIsUserLoggedIn()
        }
    }
}