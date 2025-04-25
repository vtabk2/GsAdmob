package com.example.gsadmob.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.core.gsadmob.utils.AdGsDelayManager
import com.example.gsadmob.utils.remoteconfig.AdGsRemoteExtraConfig
import com.example.gsadmob.databinding.FragmentResumeBinding
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResumeDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(viewRoot: ViewGroup) = ResumeDialogFragment().apply {
            this.viewRoot = viewRoot
        }
    }

    lateinit var binding: FragmentResumeBinding
    private var heightScreen: Int = 0
    private var viewRoot: ViewGroup? = null

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

            blurView()

            view.isFocusableInTouchMode = true
            view.requestFocus()

            view.setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
        }
    }

    fun onShowAds(from: String) {
        (activity as? AppCompatActivity)?.let {
            AdGsDelayManager(
                activity = it,
                fragment = this,
                adPlaceName = AdGsRemoteExtraConfig.instance.adPlaceNameAppOpenResume,
                callbackFinished = {
                    dismissAllowingStateLoss()
                })
        }
    }

    private fun blurView() {
        viewRoot?.let { root ->
            val decorView = activity?.window?.decorView ?: return@let
            val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
            val windowBackground = decorView.background
            binding.blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(5f)
        }
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
}
