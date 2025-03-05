package com.example.gsadmob.utils.extensions

import android.content.Context
import com.example.gsadmob.utils.preferences.CMPUtils

val Context.cmpUtils: CMPUtils get() = CMPUtils.newInstance(applicationContext)