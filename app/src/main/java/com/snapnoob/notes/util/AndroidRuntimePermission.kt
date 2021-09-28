package com.snapnoob.notes.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.snapnoob.notes.R

object AndroidRuntimePermission {

    private val MAIN_PERMISSION =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )

    private fun getNotGrantedPermissions(
        context: Context,
        permissions: Array<String>
    ): Array<String> {
        val permissionList = java.util.ArrayList<String>()
        for (requestedPermission in permissions) {
            if (ContextCompat.checkSelfPermission(context, requestedPermission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(requestedPermission)
            }
        }
        return permissionList.toTypedArray()
    }

    private fun getPermissionName(
        context: Context,
        permissions: List<PermissionDeniedResponse>
    ): String {
        return when (permissions.size) {
            1 -> getPermissionName(context, permissions[0])
            2 -> context.getString(
                R.string.runtime_permission_double_name,
                getPermissionName(context, permissions[0]),
                getPermissionName(context, permissions[1])
            )
            else -> {
                val builder = StringBuilder()
                for (i in 0..permissions.size - 3) {
                    builder.append(getPermissionName(context, permissions[i]))
                    builder.append(", ")
                }
                builder.append(
                    context.getString(
                        R.string.runtime_permission_double_name,
                        getPermissionName(context, permissions[permissions.size - 2]),
                        getPermissionName(context, permissions[permissions.size - 1])
                    )
                )
                builder.toString()
            }
        }
    }

    private fun getPermissionName(context: Context, permissions: Array<String>): String {
        return when (permissions.size) {
            1 -> getPermissionName(context, permissions[0])
            2 -> context.getString(
                R.string.runtime_permission_double_name,
                getPermissionName(context, permissions[0]),
                getPermissionName(context, permissions[1])
            )
            else -> {
                val builder = StringBuilder()
                for (i in 0..permissions.size - 3) {
                    builder.append(getPermissionName(context, permissions[i]))
                    builder.append(", ")
                }
                builder.append(
                    context.getString(
                        R.string.runtime_permission_double_name,
                        getPermissionName(context, permissions[permissions.size - 2]),
                        getPermissionName(context, permissions[permissions.size - 1])
                    )
                )
                builder.toString()
            }
        }
    }

    private fun getPermissionName(context: Context, permission: PermissionDeniedResponse): String {
        return getPermissionName(context, permission.permissionName)
    }

    private fun getPermissionName(context: Context, permissionName: String): String {
        return when (permissionName) {
            Manifest.permission.CAMERA -> context.getString(R.string.runtime_permission_camera)
            Manifest.permission.ACCESS_COARSE_LOCATION -> context.getString(R.string.runtime_permission_location)
            Manifest.permission.ACCESS_FINE_LOCATION -> context.getString(R.string.runtime_permission_location)
            Manifest.permission.RECORD_AUDIO -> context.getString(R.string.runtime_permission_microphone)
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> context.getString(R.string.runtime_permission_storage)
            Manifest.permission.READ_EXTERNAL_STORAGE -> context.getString(R.string.runtime_permission_storage)
            else                                       -> context.getString(R.string.runtime_permission_phone)
        }
    }

    fun showRationaleDialog(context: Context, permissions: Array<String>, token: PermissionToken) {
        val permissionName = getPermissionName(context, permissions)
        showRationaleDialog(context, permissionName, token)
    }

    private fun showRationaleDialog(
        context: Context,
        permissionName: String,
        token: PermissionToken
    ) {
        val message = when (permissionName) {
            Manifest.permission.CAMERA -> context.getString(
                R.string.runtime_permission_rationale_message_camera
            )
            Manifest.permission.RECORD_AUDIO -> context.getString(
                R.string.runtime_permission_rationale_message_microphone
            )
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> context.getString(
                R.string.runtime_permission_rationale_message_storage
            )
            else                                       -> context.getString(
                R.string.runtime_permission_rationale_message,
                permissionName
            )
        }

        AlertDialog.Builder(context)
            .setTitle("Permission")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Lanjutkan") { dialog, _ ->
                token.continuePermissionRequest()
            }
            .setNegativeButton("Tidak sekarang") { dialog, _ ->
                token.cancelPermissionRequest()
            }
            .show()
    }

    fun showSettingsDialog(context: Context, response: PermissionDeniedResponse) {
        val permission = getPermissionName(context, response)
        showSettingsDialog(context, permission)
    }

    fun showSettingsDialog(context: Context, response: List<PermissionDeniedResponse>) {
        val permission = getPermissionName(context, response)
        showSettingsDialog(context, permission)
    }

    private fun showSettingsDialog(context: Context, permission: String) {
        val html = context.getString(
            R.string.runtime_permission_permanently_denied_message,
            permission
        )

        val message = fromHtml(html)

        AlertDialog.Builder(context)
            .setTitle("Permission")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Setting") { dialog, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    fun checkCameraPermission(activity: Activity, listener: OnPermissionGrantedListener) {
        checkPermission(activity, Manifest.permission.CAMERA, listener)
    }

    fun checkRecordAudioPermission(activity: Activity, listener: OnPermissionGrantedListener) {
        checkPermission(activity, Manifest.permission.RECORD_AUDIO, listener)
    }

    fun checkStoragePermission(activity: Activity, listener: OnPermissionGrantedListener) {
        checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, listener)
    }

    fun checkMandatoryPermission(activity: Activity, listener: OnPermissionGrantedListener) {
        checkPermission(activity, MAIN_PERMISSION, listener)
    }

    private fun checkPermission(
        activity: Activity,
        requestedPermission: String,
        listener: OnPermissionGrantedListener
    ) {
        Dexter.withActivity(activity)
            .withPermission(requestedPermission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    listener.onPermissionGranted()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialog(activity, response)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    showRationaleDialog(activity, requestedPermission, token)
                }
            })
            .check()
    }

    private fun checkPermission(
        activity: Activity,
        permissions: Array<String>,
        listener: OnPermissionGrantedListener
    ) {
        val requestedPermissions = getNotGrantedPermissions(activity, permissions)

        Dexter.withActivity(activity)
            .withPermissions(*permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        listener.onPermissionGranted()
                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(activity, report.deniedPermissionResponses)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    showRationaleDialog(activity, requestedPermissions, token)
                }
            }).check()
    }

    fun checkPermissionsWithoutMandatory(
        activity: Activity,
        listener: OnPermissionGrantedListener
    ) {
        val requestedPermissions = getNotGrantedPermissions(activity, MAIN_PERMISSION)

        Dexter.withContext(activity)
            .withPermissions(*MAIN_PERMISSION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    listener.onPermissionGranted()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    showRationaleDialog(activity, requestedPermissions, token)
                }
            }).check()
    }

    private fun fromHtml(html: String?): Spanned? {
        return if (VERSION.SDK_INT >= VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    @FunctionalInterface
    interface OnPermissionGrantedListener {
        fun onPermissionGranted()
    }
}