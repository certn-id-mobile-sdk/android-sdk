package com.certn.mobile.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.certn.mobile.R
import com.certn.mobile.util.ILoadingView
import com.certn.mobile.util.LoadingHandler
import com.certn.mobile.util.extensions.hideKeyboard
import com.certn.mobile.util.extensions.isCurrentDestination
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment(),
    ILoadingView {

    private var viewBindingInternal: VB? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog

    protected val loadingHandler = LoadingHandler()

    protected val viewBinding: VB
        get() = this.viewBindingInternal!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogStyle)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = createBinding(inflater, container).also {
        viewBindingInternal = it
    }.root

    override fun onDestroyView() {
        viewBindingInternal = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.isDraggable = false
    }

    override fun setFullScreenLoading(visible: Boolean): View? {
        if (visible) {
            requireActivity().hideKeyboard()
        }

        return loadingHandler.setViewLoadingForView(
            viewBinding.root as ViewGroup,
            visible,
            loadingHandler.getLoadingView(layoutInflater, requireContext())
        )
    }

    fun dismissDialog() {
        findNavController().navigateUp()
    }

    fun navigate(navDirections: NavDirections) {
        if (isCurrentDestination()) {
            findNavController().navigate(navDirections)
        }
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

}
