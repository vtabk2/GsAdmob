package com.example.gsadmob.ui.activity.language

import android.os.Bundle
import android.util.Log
import com.core.gsadmob.natives.GsNativeAd
import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityLanguageBinding

class LanguageActivity : BaseMVVMActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        Log.d("LanguageActivity_datnd", "setupView: ")
        GsNativeAd.withLifecycleOwner(this@LanguageActivity).load(getString(com.core.gsadmob.R.string.native_id)).into(bindingView.nativeLanguage)
    }
}