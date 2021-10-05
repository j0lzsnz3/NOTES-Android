package com.snapnoob.notes

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

object FirebaseSetup {

    fun retrieveRegistrationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseSetup", "Failed to retrieve Firebase Registration Token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FirebaseSetup", "Registration Token $token")
        }
    }

    fun subscribeToChannel(context: Context, topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var result = ""
                if (!task.isSuccessful) {
                    result = "Failed to subscribe."
                } else {
                    result = "Success to subscribe. channel name -> $topic"
                }
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
    }

    fun unsubscribeToChannel(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic)
    }
}