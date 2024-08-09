package com.certn.mobile.base.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.certn.mobile.databinding.DialogErrorFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DialogErrorFragment :
    BaseBottomSheetDialogFragment<DialogErrorFragmentBinding>() {

    protected lateinit var config: DialogOptions

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogErrorFragmentBinding =
        DialogErrorFragmentBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        config =
            BundleCompat.getParcelable(requireArguments(), CONFIG_KEY, DialogOptions::class.java)
                ?: throw IllegalStateException("ErrorDialogFragment config is missing.")

        isCancelable = config.isCancelable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            lblTitle.text = config.title
            lblDescription.text = config.description
            btnPrimary.apply {
                text = config.primaryText
                contentDescription = "dialog_primary_button"
                isVisible = config.primaryText.isNotEmpty()
                setOnClickListener {
                    dismiss()
                    config.primaryActionSuspend?.run {
                        lifecycleScope.launch {
                            this@run.invoke()
                        }
                    } ?: config.primaryAction?.invoke()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config.dismissActionSuspend?.run {
            lifecycleScope.launch {
                this@run.invoke()
            }
        } ?: config.dismissAction?.invoke()
    }

    companion object {
        private const val CONFIG_KEY = "CONFIG_KEY"

        fun newInstance(config: DialogOptions) = DialogErrorFragment().also {
            it.arguments = bundleOf(CONFIG_KEY to config)
        }
    }

}
