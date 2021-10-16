package com.snapnoob.notes.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

interface SharedPreference {
    fun setIsLogin(isLogin: Boolean)
    fun getIsLogin(): Boolean
    fun setUserEmail(email: String)
    fun getUserEmail(): String
    fun setUserId(userId: Long)
    fun getUserId(): Long
    fun setFcmToken(token: String)
    fun getFcmToken(): String?
    fun clearAllData()
}

class SharedPreferenceImpl @Inject constructor(
    private val context: Context
): SharedPreference {

    private var sharedPreference: SharedPreferences? = null

    private fun getSharedPreference(): SharedPreferences {
        if (sharedPreference == null) {
            sharedPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }
        return sharedPreference!!
    }

    override fun setIsLogin(isLogin: Boolean) {
        getSharedPreference().edit().putBoolean(IS_LOGIN, isLogin).apply()
    }

    override fun getIsLogin(): Boolean = getSharedPreference().getBoolean(IS_LOGIN, false)

    override fun setUserEmail(email: String) {
        getSharedPreference().edit().putString(USER_EMAIL, email).apply()
    }

    override fun getUserEmail(): String = getSharedPreference().getString(USER_EMAIL, "")!!

    override fun setUserId(userId: Long) {
        getSharedPreference().edit().putLong(USER_ID, userId).apply()
    }

    override fun getUserId(): Long = getSharedPreference().getLong(USER_ID, 0L)

    override fun setFcmToken(token: String) {
        getSharedPreference().edit().putString(FCM_TOKEN, token).apply()
    }

    override fun getFcmToken(): String? =
        getSharedPreference().getString(FCM_TOKEN, "")

    override fun clearAllData() {
        getSharedPreference().edit {
            putBoolean(IS_LOGIN, false)
            putString(USER_EMAIL, "")
            putLong(USER_ID, 0L)
        }
    }

    companion object {
        private const val SHARED_PREF_NAME = "notes_pref"

        private const val IS_LOGIN = "is_login"
        private const val USER_EMAIL = "user_email"
        private const val USER_ID = "user_id"
        private const val FCM_TOKEN = "fcm_token"
    }
}