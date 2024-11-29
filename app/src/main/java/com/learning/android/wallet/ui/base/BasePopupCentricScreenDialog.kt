package com.learning.android.wallet.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.learning.android.R
import com.learning.android.utils.AppConstants
import com.learning.android.utils.AppUtils

abstract class BasePopupCentricScreenDialog<VB : ViewBinding> : DialogFragment() {
    lateinit var binding: VB
    private lateinit var popupDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Intercept back button press
        AppUtils.logI("", "--------------------------------------1")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            AppUtils.logI("", "--------------------------------------2")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = getBinding(layoutInflater)
        popupDialog = getDialogFromBinding()
        provideBinding(binding)
        initViews()
        setupToolbar()
        onClickListeners()
        liveDataObservers()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            AppUtils.logI("", "--------------------------------------4")
        }
        return popupDialog
    }

    abstract fun getBinding(inflater: LayoutInflater): VB
    abstract fun provideBinding(mBinding: VB)
    abstract fun initViews()
    abstract fun setupToolbar()
    abstract fun onClickListeners()
    abstract fun liveDataObservers()

    fun showMessage(message: String?, messageBackgroundColor: Int) {
        val color = when (messageBackgroundColor) {
            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_ERROR -> {
                R.color.red_400
            }

            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_NOTICE -> {
                R.color.amber_400
            }

            AppConstants.MessageBackgroundColor.MESSAGE_COLOR_SUCCESS -> {
                R.color.light_green_400
            }

            else -> {
                R.color.red_400
            }
        }
        Toast.makeText(popupDialog.context, message, Toast.LENGTH_SHORT).show()
//        Alerter.create(popupDialog).hideIcon().setBackgroundColorRes(color)
//            .setTitle(message?:"").show()
    }

    private fun getDialogFromBinding(): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AppUtils.logI("", "--------------------------------------3")
    }
}