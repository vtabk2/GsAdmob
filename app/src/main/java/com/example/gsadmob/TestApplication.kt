package com.example.gsadmob

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.core.gsadmob.model.AdShowStatus
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gsadmob.utils.preferences.VipPreferences
import com.example.gsadmob.ui.activity.base.BaseAdsActivity
import com.example.gsadmob.ui.activity.splash.SplashActivity
import com.example.gsadmob.ui.fragment.ResumeDialogFragment
import com.gs.core.GsApplication
import kotlinx.coroutines.MainScope

class TestApplication : GsApplication() {
    var canShowAppOpenResume: Boolean = true

    private val mainScope = MainScope()

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
        val adPlaceName = AdPlaceNameConfig.instance.AD_PLACE_NAME_APP_OPEN_RESUME
        val tag = ResumeDialogFragment.javaClass.simpleName

        AdGsManager.instance.registerCoroutineScope(
            application = this,
            coroutineScope = mainScope,
            applicationId = BuildConfig.APPLICATION_ID,
            keyVipList = VipPreferences.defaultKeyVipList,
            callbackStartLifecycle = { activity ->
                if (canShowAppOpenResume && activity !is SplashActivity) {
                    AdGsManager.instance.showAd(adPlaceName = adPlaceName, onlyCheckNotShow = true, callbackShow = { adShowStatus ->
                        when (adShowStatus) {
                            AdShowStatus.CAN_SHOW, AdShowStatus.REQUIRE_LOAD -> {
                                activity.supportFragmentManager.let { fragmentManager ->
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
                            }

                            else -> {

                            }
                        }
                    })
                }
            },
            callbackPauseLifecycle = { activity ->
                val bottomDialogFragment = activity.supportFragmentManager.findFragmentByTag(tag) as? ResumeDialogFragment
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
            }, callbackChangeVip = { currentActivity, isVip ->
                if (currentActivity is BaseAdsActivity<*>) {
                    currentActivity.updateUiWithVip(isVip = isVip)
                }
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