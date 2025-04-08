package com.core.gsadmob.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.R
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.model.AdPlaceName
import com.core.gsadmob.utils.extensions.log
import com.core.gscore.hourglass.Hourglass

class AdGsDelayManager(
    private val activity: AppCompatActivity,
    fragment: Fragment?,
    private val adPlaceName: AdPlaceName? = null,
    private val callbackFinished: () -> Unit
) {

    private var timerLoading: Hourglass? = null
    private var timerFakeDelay: Hourglass? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            if (timerLoading?.isPaused == true) timerLoading?.resumeTimer()
            if (timerFakeDelay?.isPaused == true) timerFakeDelay?.resumeTimer()
        }

        override fun onPause(owner: LifecycleOwner) {
            if (timerLoading?.isRunning == true) timerLoading?.pauseTimer()
            if (timerFakeDelay?.isRunning == true) timerFakeDelay?.pauseTimer()
        }
    }

    init {
        fragment?.lifecycle?.addObserver(lifecycleObserver) ?: activity.lifecycle.addObserver(lifecycleObserver)
        registerAndShowAds()
    }

    private fun registerAndShowAds() {
        val timeDelayLoading = activity.resources.getInteger(R.integer.time_delay_loading).toLong().coerceAtLeast(1000)

        timerLoading = object : Hourglass(timeDelayLoading, 500) {
            override fun onTimerTick(timeRemaining: Long) {
                // Do nothing
            }

            override fun onTimerFinish() {
                adPlaceName?.let {
                    AdGsManager.instance.clearWithAdPlaceName(adPlaceName = it)
                }
                callbackFinished.invoke()
            }
        }
        timerLoading?.startTimer()

        val timeFakeDelay = activity.resources.getInteger(R.integer.time_fake_delay).toLong().coerceAtLeast(500)

        timerFakeDelay = object : Hourglass(timeFakeDelay, 500) {
            override fun onTimerTick(timeRemaining: Long) {
                // nothing
            }

            override fun onTimerFinish() {
                adPlaceName?.let {
                    AdGsManager.instance.registerAndShowAds(adPlaceName = it, adGsListener = object : AdGsListener {
                        override fun onAdClose(isFailed: Boolean) {
                            if (!isFailed) {
                                timerLoading?.onTimerFinish()
                                callbackFinished.invoke()
                            }
                        }
                    }, callbackShow = { adShowStatus ->
                        log("AdGsDelayManager_adShowStatus", adShowStatus)
                    })
                } ?: {
                    callbackFinished.invoke()
                }
            }
        }
        timerFakeDelay?.startTimer()
    }
}