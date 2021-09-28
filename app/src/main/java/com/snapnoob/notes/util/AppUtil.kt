package com.snapnoob.notes.util

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.snapnoob.notes.R
import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    fun createProgressDialog(activity: Activity): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.view_progress_dialog, null)
        return AlertDialog.Builder(activity)
            .setView(view)
            .create()
    }

    fun millisToDate(millis: Long): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(millis)
    }
}