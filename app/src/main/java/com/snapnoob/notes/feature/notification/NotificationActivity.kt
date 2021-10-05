package com.snapnoob.notes.feature.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityNotificationBinding
import com.snapnoob.notes.feature.notification.NotificationType.DATA
import com.snapnoob.notes.feature.notification.NotificationType.TOKEN
import com.snapnoob.notes.feature.notification.NotificationType.TOPIC
import com.snapnoob.notes.network.model.PushNotificationRequest
import com.snapnoob.notes.util.isAllEditTextNotEmpty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var view: View

    private lateinit var viewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        initView()

        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        observeViewModel()
        viewModel.getToken()
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, { event ->
            when (event) {
                is NotificationEvent.PushNotificationSuccess -> {
                    Toast.makeText(this, "Notification created", Toast.LENGTH_LONG).show()
                    clearAllField()
                }
                is NotificationEvent.ShowError -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_LONG).show()
                }
                is NotificationEvent.SetToken -> {
                    binding.edtToken.setText(event.token)
                }
            }
        })
    }

    private fun initView() {
        binding.toolBar.title = "Push Notification"
        binding.toolBar.setNavigationOnClickListener { finish() }

        var notificationType: NotificationType? = null
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            notificationType = when (checkedId) {
                R.id.rbTopic -> TOPIC
                R.id.rbToken -> TOKEN
                R.id.rbData -> DATA
                else -> null
            }
        }

        binding.btnPushNotification.setOnClickListener {
            checkUserInput(notificationType)
        }
    }

    private fun checkUserInput(notificationType: NotificationType?) {
        val listEditText = listOf(
            binding.edtTitle,
            binding.edtMessage,
            binding.edtToken,
            binding.edtTopic
        )
        if (isAllEditTextNotEmpty(listEditText) && notificationType != null) {
            pushNotification(notificationType)
        } else {
            Toast.makeText(this, "All field must be filled", Toast.LENGTH_LONG).show()
        }
    }

    private fun pushNotification(notificationType: NotificationType) {
        val title = binding.edtTitle.text.toString()
        val message = binding.edtMessage.text.toString()
        val topic = binding.edtTopic.text.toString()
        val token = binding.edtToken.text.toString()
        val request = PushNotificationRequest(
            title = title,
            message = message,
            topic = topic,
            token = token
        )
        viewModel.pushNotification(notificationType, request)
    }

    private fun clearAllField() {
        binding.edtTitle.setText("")
        binding.edtMessage.setText("")
        binding.edtTopic.setText("")
        binding.edtToken.setText("")
        binding.radioGroup.clearCheck()
        viewModel.getToken()
    }
}