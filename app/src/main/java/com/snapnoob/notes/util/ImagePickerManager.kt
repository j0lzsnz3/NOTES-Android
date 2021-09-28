package com.snapnoob.notes.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.snapnoob.notes.BuildConfig
import com.snapnoob.notes.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val MAX_IMAGE_SIZE_IN_MB = 10
private const val BYTE_LEVEL = 1024

class ImagePickerManager private constructor() {

    private var activity: FragmentActivity? = null
    private var fragment: Fragment? = null

    private var mCurrentPhotoPath: String? = null
    private val JPEG_EXT = ".jpg"
    private val NAME_PHOTO_FORMAT = "yyyyMMdd_HHmmss"
    private lateinit var context: Context

    companion object {
        const val TAG = "ImagePickerManager"

        const val CAMERA = 200
        const val GALLERY = 100

        fun newInstance(activity: FragmentActivity): ImagePickerManager {
            val ipm = ImagePickerManager()
            ipm.activity = activity
            ipm.context = activity.applicationContext
            return ipm
        }

        fun newInstance(fragment: Fragment): ImagePickerManager {
            val ipm = ImagePickerManager()
            ipm.fragment = fragment
            ipm.context = fragment.requireActivity().applicationContext
            return ipm
        }
    }

    fun fromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (null == cameraIntent.resolveActivity(context.packageManager)) {
            Toast.makeText(
                activity, context.getString(R.string.no_camera_detected), Toast.LENGTH_SHORT
            )
                .show()
        } else {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                mCurrentPhotoPath = photoFile.absolutePath
            } catch (ex: IOException) {
                log(ex)
            }

            photoFile?.let {
                val photoURI = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID, photoFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                val resInfoList = context.packageManager.queryIntentActivities(
                    cameraIntent, PackageManager.MATCH_DEFAULT_ONLY
                )
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName, photoURI,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                startActivityForResult(cameraIntent, CAMERA)
            }
        }
    }

    fun fromGallery() {
        val choosePhotoListener = object : AndroidRuntimePermission.OnPermissionGrantedListener {
            override fun onPermissionGranted() {
                try {
                    val openGallery = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(openGallery, GALLERY)
                } catch (e: Exception) {
                    log(e)
                    if(activity != null) {
                        Toast.makeText(
                            activity, "No activity found to open gallery", Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }

        val act = if (activity != null) activity!! else fragment!!.requireActivity()
        AndroidRuntimePermission.checkStoragePermission(act, choosePhotoListener)
    }

    /**
     * one of this will be null, either activity or fragment
     * this is save to execute both at the same time
     */
    fun startActivityForResult(
        intent: Intent,
        requestCode: Int
    ) {
        activity?.startActivityForResult(intent, requestCode)
        fragment?.startActivityForResult(intent, requestCode)
    }

    fun createFileFromBitmap(bitmap: Bitmap): File {
        val f = File(context.cacheDir, defaultFileName() + JPEG_EXT)
        f.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 80, bos)
        val bitmapData = bos.toByteArray()

        val fos = FileOutputStream(f)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return f
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        mCurrentPhotoPath = null //rest image path
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(defaultFileName(), JPEG_EXT, storageDir)
    }

    private fun defaultFileName(): String {
        val timeStamp = SimpleDateFormat(NAME_PHOTO_FORMAT, Locale.getDefault()).format(Date())
        return timeStamp + "_"
    }

    fun generateImageFromLibrary(data: Intent?): File? {

        var result: File? = null
        try {
            if (data?.data != null) {
                val inputStream = context.contentResolver.openInputStream(data.data!!)
                val source = getRealPathFromUri(data.data)
                mCurrentPhotoPath = source?.absolutePath
                result = createImageFile()
                writeInputStreamToFile(inputStream, result)
            }
        } catch (io: IOException) {
            log(io)
        } catch (e: Exception) {
            log(e)
        }
        return result
    }

    @Throws(IOException::class)
    private fun writeInputStreamToFile(
        inputStream: InputStream?,
        file: File
    ) {
        if (inputStream != null) {
            FileOutputStream(file, false).use { outputStream ->
                var read: Int
                val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
                while (inputStream.read(bytes)
                        .also { read = it } != -1
                ) {
                    outputStream.write(bytes, 0, read)
                }
            }
        }
    }


    private fun getRealPathFromUri(contentUri: Uri?): File? {
        var cursor: Cursor? = null
        try {
            val imageData = arrayOf(Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, imageData, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(Media.DATA)
            cursor.moveToFirst()
            return File(cursor.getString(columnIndex))
        } catch (io: IOException) {
            log(io)
        } catch (e: Exception) {
            log(e)
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isFileTooBig(
        source: File,
        maxSize: Int
    ): Boolean {
        val fileSizeInBytes = source.length()
        val fileSizeInKB = fileSizeInBytes / BYTE_LEVEL
        val fileSizeInMB = fileSizeInKB / 1024f
        Formatter.formatShortFileSize(activity, source.length())

        val maxSizeInBytes = (maxSize * BYTE_LEVEL) * BYTE_LEVEL
        Log.d(
            TAG, "[isFileTooBig] file sizes -> " +
                    "bytes: $fileSizeInBytes | KB: $fileSizeInKB |" +
                    " MB: $fileSizeInMB | LIMIT: $maxSizeInBytes"
        )
        return fileSizeInBytes > maxSizeInBytes
    }

    fun isResultFromGallery(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: (Boolean) -> Unit
    ) {
        data?.let {
            val isFromGallery =
                (requestCode == GALLERY && resultCode == Activity.RESULT_OK && null != data.data)
            callback.invoke(isFromGallery)
        } ?: run {
            callback.invoke(false)
        }
    }

    fun isResultFromCamera(
        requestCode: Int,
        resultCode: Int,
        callback: (Boolean, File?) -> Unit
    ) {
        val isFromCamera =
            (requestCode == CAMERA && resultCode == Activity.RESULT_OK && !TextUtils.isEmpty(
                mCurrentPhotoPath
            ))
        if (isFromCamera) {
            val result = File(mCurrentPhotoPath)
            callback.invoke(isFromCamera, result)
        } else {
            callback.invoke(false, null)
        }
    }

    fun isResultFromCropping(
        requestCode: Int,
        resultCode: Int,
        callback: (Boolean) -> Unit
    ) {
//        val code = PhotoChooserActivity.CLOSE_IF_CANCEL
        val code = 300
        callback.invoke(requestCode == code && resultCode == code)
    }

    fun handleResultWithCallback(
        file: File?,
        onSucess: (Bitmap) -> Unit,
        onImageToBig: () -> Unit,
        onError: (String) -> Unit,
        maxSize: Int = MAX_IMAGE_SIZE_IN_MB
    ) {
        file?.let {
            if (isFileTooBig(file, maxSize)) {
                onImageToBig.invoke()
                return
            }
            val options = BitmapFactory.Options()
            options.inSampleSize = 4
            val bitmap = BitmapFactory.decodeFile(file.path, options)
            checkBitmapSize(bitmap)
            onSucess.invoke(bitmap)
        } ?: run {
            onError.invoke("Error: can't open file")
        }
    }

    private fun checkBitmapSize(bitmap: Bitmap) {
        val fileSizeInBytes = bitmap.byteCount
        val fileSizeInKB = fileSizeInBytes / 1024f
        val fileSizeInMB = fileSizeInKB / 1024f
        Log.d(
            TAG,
            "[checkBitmapSize] file sizes -> bytes: $fileSizeInBytes | KB: $fileSizeInKB | MB: $fileSizeInMB"
        )
    }

    private fun log(ex: Exception) {
        if (BuildConfig.DEBUG) {
            ex.printStackTrace()
        }
    }
}