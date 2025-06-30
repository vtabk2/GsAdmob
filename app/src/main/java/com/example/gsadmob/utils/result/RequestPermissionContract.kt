package com.example.gsadmob.utils.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gsadmob.utils.extensions.PERMISSION_CAMERA
import com.example.gsadmob.utils.extensions.getPermissionString

class RequestPermissionContract : ActivityResultContract<Int, Pair<Int, Boolean>>() {
    private var input: Int = PERMISSION_CAMERA

    override fun createIntent(context: Context, input: Int): Intent {
        this.input = input

        return Intent(ActivityResultContracts.RequestMultiplePermissions.ACTION_REQUEST_PERMISSIONS)
            .putExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS, arrayOf(getPermissionString(input)))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int, Boolean> {
        if (intent == null || resultCode != Activity.RESULT_OK) return Pair(input, false)
        val grantResults =
            intent.getIntArrayExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS)
        return Pair(input, grantResults?.any { result ->
            result == PackageManager.PERMISSION_GRANTED
        } == true)
    }
}