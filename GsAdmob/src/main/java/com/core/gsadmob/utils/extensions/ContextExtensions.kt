package com.core.gsadmob.utils.extensions

import android.content.Context
import com.core.gsadmob.utils.preferences.CMPUtils

val Context.cmpUtils: CMPUtils get() = CMPUtils.newInstance(applicationContext)