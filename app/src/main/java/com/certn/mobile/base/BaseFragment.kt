package com.certn.mobile.base

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.certn.mobile.BuildConfig
import com.certn.mobile.base.dialog.dialog
import com.certn.mobile.util.ILoadingView
import com.certn.mobile.util.LoadingHandler
import com.certn.mobile.util.extensions.hideKeyboard
import com.certn.mobile.util.extensions.isCurrentDestination
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseFragment<VB : ViewBinding> : Fragment(), ILoadingView {

    private var viewBindingInternal: VB? = null

    protected val loadingHandler = LoadingHandler()

    protected val viewBinding: VB
        get() = this.viewBindingInternal!!

    protected val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleActivitySuccessResult(result)
            } else {
                handleActivityFailureResult()
            }
        }

    protected val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantedResults ->
            if (grantedResults.all { it.value }) {
                handleGrantedPermissions()
            } else {
                handleDeniedPermissions()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = createBinding(inflater, container).also {
        viewBindingInternal = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
            if (canNavigateUp()) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed() // call default onBackPressed behavior
            }
        }
    }

    override fun onDestroyView() {
        viewBindingInternal = null
        super.onDestroyView()
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

    fun navigate(navDirections: NavDirections) {
        if (isCurrentDestination()) {
            findNavController().navigate(navDirections)
        }
    }

    fun navigate(navDirections: NavDirections, navOptions: NavOptions) {
        if (isCurrentDestination()) {
            findNavController().navigate(navDirections, navOptions)
        }
    }

    fun navigateUp() {
        findNavController().navigateUp()
    }

    open fun canNavigateUp() = true

    open fun onBackPressed() {}

    protected fun dismissDialogs() {
        childFragmentManager.fragments.takeIf { it.isNotEmpty() }
            ?.map { (it as? BottomSheetDialogFragment)?.dismiss() }
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    open fun handleActivitySuccessResult(activityResult: ActivityResult) {}

    open fun handleActivityFailureResult() {}

    open fun handleGrantedPermissions() {}

    open fun handleDeniedPermissions() {}

    fun showCameraSettingsDialog(onPrimaryClick: () -> Unit) {
        dialog {
            title = "Camera Title"
            description = "Camera Description"
            primaryText = "To settings"
            primaryAction = {
                onPrimaryClick.invoke()
                try {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                    )
                } catch (_: Exception) {
                }
            }
            isCancelable = true
        }
    }

    fun checkCameraPermission() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.CAMERA)
        }
        requestPermissionsLauncher.launch(permissions.toTypedArray())
    }
}
