package com.example.gsadmob.utils.extensions

import android.content.Context
import com.example.gsadmob.utils.preferences.Config

val Context.config: Config get() = Config.newInstance(applicationContext)