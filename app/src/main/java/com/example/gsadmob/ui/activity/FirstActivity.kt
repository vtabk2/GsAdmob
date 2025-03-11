package com.example.gsadmob.ui.activity

import com.core.gsmvvm.ui.activity.BaseMVVMActivity
import com.example.gsadmob.databinding.ActivityFirstBinding

class FirstActivity : BaseMVVMActivity<ActivityFirstBinding>() {
    override fun getViewBinding(): ActivityFirstBinding {
        return ActivityFirstBinding.inflate(layoutInflater)
    }
}