package com.snapnoob.notes

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

object FirebaseSetup {
    fun subscribeToChannel(context: Context, topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                val result: String = if (!task.isSuccessful) {
                    "Failed to subscribe."
                } else {
                    "Success to subscribe. channel name -> $topic"
                }
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
    }

    fun unsubscribeToChannel(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic)
    }
}