package com.example.gsadmob.ui.activity.base

import android.Manifest
import android.content.pm.PackageManager
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.extensions.dialogLayout
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.BuildConfig
import com.example.gsadmob.utils.DialogUtils
import com.example.gsadmob.utils.extensions.PERMISSION_WRITE_STORAGE
import com.example.gsadmob.utils.extensions.getPermissionString
import com.example.gsadmob.utils.extensions.isTiramisuPlus
import com.example.gsadmob.utils.extensions.isUpSideDownCakePlus
import com.example.gsadmob.utils.result.RequestPermissionContract
import com.gs.core.utils.result.OpenAppSystemSettingsContract

abstract class BasePermissionActivity<VB : ViewBinding>(inflateBinding: (LayoutInflater) -> VB) : BaseMVVMActivity<VB>(inflateBinding) {
    private var writeStoragePermissionsDialog: AlertDialog? = null
    private var deniedPermissionsDialog: AlertDialog? = null
    private var openAppSystemSettingsContract = registerForActivityResult(OpenAppSystemSettingsContract(BuildConfig.APPLICATION_ID)) { permissionId ->
        if (hasPermission(permissionId, hasFull = false)) {
            when (permissionId) {
                PERMISSION_WRITE_STORAGE -> goToOtherHasWriteStoragePermission()
                else -> goToOtherHasPermission()
            }
        }
    }

    // Register ActivityResult handler
    private val requestWriteStoragePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        // Handle permission requests results
        // See the permission example in the Android platform samples: https://github.com/android/platform-samples
        if (!results.isNullOrEmpty()) {
            val readMediaImages = if (results.containsKey(Manifest.permission.READ_MEDIA_IMAGES)) {
                results[Manifest.permission.READ_MEDIA_IMAGES] ?: false
            } else {
                false
            }
            val readMediaVisualUserSelected = if (results.containsKey(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                results[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] ?: false
            } else {
                false
            }
            val readExternalStorage = if (results.containsKey(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                results[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            } else {
                false
            }
            if (readMediaImages || readMediaVisualUserSelected || readExternalStorage) {
                goToOtherHasWriteStoragePermission()
            } else {
                showDeniedPermissionsDialog(PERMISSION_WRITE_STORAGE)
            }
        }
    }

    private val requestPermissions = registerForActivityResult(RequestPermissionContract()) { data ->
        if (data.second) {
            goToOtherHasPermission()
        } else {
            showDeniedPermissionsDialog(data.first)
        }
    }

    fun checkPermission(permissionId: Int = PERMISSION_WRITE_STORAGE, hasFull: Boolean = true, callback: (granted: Boolean) -> Unit) {
        if (hasPermission(permissionId, hasFull = hasFull)) {
            callback.invoke(true)
        } else {
            when (permissionId) {
                PERMISSION_WRITE_STORAGE -> {
                    // Permission request logic
                    if (isUpSideDownCakePlus()) {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) -> {
                                callback.invoke(true)
                            }

                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) -> {
                                writeStoragePermissionsDialog?.show()
                                dialogLayout(writeStoragePermissionsDialog)
                            }

                            else -> {
                                AdGsManager.instance.isResetPause = true
                                requestWriteStoragePermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED))
                            }
                        }
                    } else if (isTiramisuPlus()) {
                        AdGsManager.instance.isResetPause = true
                        requestWriteStoragePermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                    } else {
                        AdGsManager.instance.isResetPause = true
                        requestWriteStoragePermissions.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    }
                }

                else -> {
                    AdGsManager.instance.isResetPause = true
                    requestPermissions.launch(permissionId)
                }
            }
            //
            callback.invoke(false)
        }
    }

    fun hasPermission(permId: Int = PERMISSION_WRITE_STORAGE, hasFull: Boolean = false): Boolean {
        return when (permId) {
            PERMISSION_WRITE_STORAGE -> {
                if (isTiramisuPlus() && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)) {
                    // Full access on Android 13 (API level 33) or higher
                    true
                } else if (isUpSideDownCakePlus() && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                    // Partial access on Android 14 (API level 34) or higher
                    !hasFull
                } else {
                    // Full access up to Android 12 (API level 32)
                    // Access denied
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                }
            }

            else -> {
                ContextCompat.checkSelfPermission(this, getPermissionString(permId)) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    private fun showDeniedPermissionsDialog(permissionId: Int) {
        deniedPermissionsDialog?.dismiss()
        deniedPermissionsDialog = DialogUtils.initDeniedPermissionsDialog(this) { granted ->
            if (granted) {
                openAppSystemSettingsContract.launch(permissionId)
            }
        }
        deniedPermissionsDialog?.show()
        dialogLayout(deniedPermissionsDialog)
    }

    open fun goToOtherHasWriteStoragePermission() {}

    open fun goToOtherHasPermission() {}
}