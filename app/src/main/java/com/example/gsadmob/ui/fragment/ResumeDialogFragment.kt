package com.example.gsadmob.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.core.gsadmob.callback.AdGsListener
import com.core.gsadmob.utils.AdGsManager
import com.core.gsadmob.utils.AdPlaceNameConfig
import com.core.gscore.hourglass.Hourglass
import com.example.gsadmob.databinding.FragmentResumeBinding
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class ResumeDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(viewRoot: ViewGroup) = ResumeDialogFragment().apply {
            this.viewRoot = viewRoot
        }
    }

    lateinit var binding: FragmentResumeBinding
    private var heightScreen: Int = 0
    private var viewRoot: ViewGroup? = null
    private var timerLoading: Hourglass? = null
    private var timerDelay: Hourglass? = null

    private val adPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_APP_OPEN_RESUME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            heightScreen = it.resources.displayMetrics.heightPixels
        }
    }

    private fun updateHeightScreen() {
        dialog?.let {
            val bottomSheet = it.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = heightScreen
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResumeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            onShowAds("onViewCreated")

            viewRoot?.let { root ->
                context?.let { blurView(it, binding.blurView, root) }
            }

            view.isFocusableInTouchMode = true
            view.requestFocus()

            view.setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
        }
    }

    fun onShowAds(from: String) {
        timerLoading = object : Hourglass(3500, 500) {
            override fun onTimerTick(timeRemaining: Long) {
                // Do nothing
            }

            override fun onTimerFinish() {
                dismissAllowingStateLoss()
            }
        }
        timerLoading?.startTimer()

        timerDelay = object : Hourglass(1000, 500) {
            override fun onTimerTick(timeRemaining: Long) {
                // nothing
            }

            override fun onTimerFinish() {
                activity?.let {
                    AdGsManager.instance.registerAdsListener(adPlaceName = adPlaceName, adGsListener = object : AdGsListener {
                        override fun onAdClose(isFailed: Boolean) {
                            if (!isFailed) {
                                timerLoading?.onTimerFinish()
                                dismissAllowingStateLoss()
                            }
                        }
                    })
                    AdGsManager.instance.showAd(adPlaceName = adPlaceName, callbackShow = { adShowStatus ->
                    })
                }
            }
        }
        timerDelay?.startTimer()
    }

    private fun blurView(context: Context, blurView: BlurView, viewRoot: ViewGroup) {
        blurView.setupWith(
            viewRoot, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                RenderEffectBlur()
            } else {
                RenderScriptBlur(context)
            }
        ).setBlurRadius(5f)
    }

    override fun onStart() {
        super.onStart()
        updateHeightScreen()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
            bottomSheet.requestDisallowInterceptTouchEvent(true)
        }
        return dialog
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {
            ignored.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        if (timerLoading?.isRunning == true) timerLoading?.pauseTimer()
        if (timerDelay?.isRunning == true) timerDelay?.pauseTimer()
    }

    override fun onResume() {
        super.onResume()
        if (timerLoading?.isPaused == true) timerLoading?.resumeTimer()
        if (timerDelay?.isPaused == true) timerDelay?.resumeTimer()
    }
}
