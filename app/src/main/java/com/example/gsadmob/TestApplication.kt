package com.example.gsadmob

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.example.gsadmob.ui.activity.splash.SplashActivity
import com.example.gsadmob.ui.fragment.ResumeDialogFragment
import com.gs.core.GsApplication
import kotlinx.coroutines.MainScope

class TestApplication : GsApplication() {
    var canShowAppOpenResume: Boolean = true

    init {
        instance = this
    }

    override fun fixWebView(packageName: String) {
        super.fixWebView(getPackageName())
    }

    override fun setupAdMob(isDebug: Boolean) {
        super.setupAdMob(BuildConfig.DEBUG)
    }

    override fun initConfig() {
        val adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_APP_OPEN_RESUME

        AdGsManager.instance.registerCoroutineScope(
            application = this,
            coroutineScope = MainScope(),
            callbackStartLifecycle = { activity ->
                if (canShowAppOpenResume && activity !is SplashActivity) {
                    if (adPlaceName.fragmentTagAppOpenResumeResId == 0) return@registerCoroutineScope
                    AdGsManager.instance.showAd(adPlaceName = adPlaceName, callbackCanShow = { canShow, hasAdsError ->
                        activity.supportFragmentManager.let { fragmentManager ->
                            val tag = activity.getString(adPlaceName.fragmentTagAppOpenResumeResId)
                            val bottomDialogFragment = fragmentManager.findFragmentByTag(tag) as? ResumeDialogFragment
                            if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                                // BottomDialogFragment đang hiển thị
                                bottomDialogFragment.onShowAds("onResume")
                            } else {
                                // BottomDialogFragment không hiển thị
                                val fragment = (activity.window.decorView.rootView as? ViewGroup)?.let { ResumeDialogFragment.newInstance(it) }
                                fragment?.show(fragmentManager, tag)
                            }
                        }
                    })
                }
            },
            callbackPauseLifecycle = { activity ->
                if (adPlaceName.fragmentTagAppOpenResumeResId == 0) return@registerCoroutineScope
                val bottomDialogFragment = activity.supportFragmentManager.findFragmentByTag(activity.getString(adPlaceName.fragmentTagAppOpenResumeResId)) as? ResumeDialogFragment
                if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                    // BottomDialogFragment đang hiển thị
                    activity.runOnUiThread {
                        bottomDialogFragment.dismissAllowingStateLoss()
                    }
                } else {
                    // BottomDialogFragment không hiển thị
                }
            }, callbackNothingLifecycle = {
                // 1 số logic cần thiết khác (ví dụ retry vip hoặc Lingver)
            }
        )
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TestApplication? = null

        fun applicationContext(): TestApplication {
            return instance as TestApplication
        }
    }
}