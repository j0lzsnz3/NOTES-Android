package com.snapnoob.notes.util

import android.widget.EditText

fun isAllEditTextNotEmpty(listEditText: List<EditText>): Boolean {
    var isAllValid = true
    listEditText.forEach {
        if (it.text.isNullOrBlank() || it.text.isNullOrEmpty()) {
            isAllValid = false
            return false
        }
    }
    return isAllValid
}