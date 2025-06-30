package com.example.gsadmob.utils.extensions

import android.os.Build

fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isLollipopPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun isNPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

fun isOPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isPPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun isBelowS() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

fun isSV2Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2

fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun isUpSideDownCakePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
