package com.certn.mobile.documentautocapture

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.certn.mobile.CertnIDSdkViewModel
import com.certn.mobile.R
import com.certn.mobile.ResultViewModel
import com.certn.mobile.UiState
import com.certn.mobile.domain.model.RegisterDocumentResult
import com.certn.mobile.util.LoadingHandler
import com.certn.mobile.util.extensions.collectStateFlow
import com.certn.mobile.util.extensions.hideKeyboard
import com.certn.sdk.camera.CertnIDCameraFacing
import com.certn.sdk.camera.CertnIDCameraPreviewScaleType
import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureDetection
import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureFragment
import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureResult
import com.certn.sdk.document.autocapture.CertnIDDocumentConfiguration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasicDocumentAutoCaptureFragment : CertnIDDocumentAutoCaptureFragment() {

    private val documentViewModel: DocumentViewModel by viewModels()
    private val resultViewModel: ResultViewModel by activityViewModels()
    private val certnIDSdkViewModel: CertnIDSdkViewModel by activityViewModels()
    private val documentAutoCaptureViewModel: DocumentAutoCaptureViewModel by activityViewModels()

    private val loadingHandler = LoadingHandler()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            certnIDStopAsync {
                resultViewModel.resetData()
                findNavController().popBackStack(R.id.nav_graph, false)
                findNavController().navigate(R.id.action_global_qrScanFragment)
            }
            isEnabled = false
        }
        setupCertnIDSdkViewModel()
        setupDocumentAutoCaptureViewModel()
        collectStateFlow {
            launch {
                documentViewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            resultViewModel.setDocumentError((uiState.data as RegisterDocumentResult).error)
                            documentViewModel.resetData()
                            certnIDStopAsync {}
                            findNavController().navigate(R.id.action_basicDocumentAutoCaptureFragment_to_documentAutoCaptureResultFragment)
                        }
                        is UiState.Loading -> setFullScreenLoading(uiState.value)
                        else -> Unit
                    }
                }
            }
            launch {
                documentViewModel.documentBackResponse.collect { documentBackResponse ->
                    if (certnIDSdkViewModel.state.value.isInitialized &&
                        documentBackResponse != null &&
                        documentViewModel.documentFrontResponse.value == null
                    ) {
                        certnIDStart()
                    }
                }
            }
            launch {
                documentViewModel.documentFrontResponse.collect { documentFrontResponse ->
                    if (certnIDSdkViewModel.state.value.isInitialized &&
                        documentFrontResponse != null &&
                        documentViewModel.documentBackResponse.value == null
                    ) {
                        if (documentFrontResponse.isTwoSideDocument == true) {
                            certnIDStart()
                        } else {
                            findNavController().navigate(R.id.action_basicDocumentAutoCaptureFragment_to_basicFaceAutoCaptureFragment)
                        }
                    }
                }
            }
            launch {
                documentViewModel.isAllScanned.collect { isAllScanned ->
                    if (isAllScanned) {
                        findNavController().navigate(R.id.action_basicDocumentAutoCaptureFragment_to_basicFaceAutoCaptureFragment)
                    }
                }
            }
        }
    }

    override fun onCertnIDCaptured(certnIDDocumentAutoCaptureResult: CertnIDDocumentAutoCaptureResult) {
        documentAutoCaptureViewModel.process(certnIDDocumentAutoCaptureResult)
    }

    override fun onCertnIDProcessed(certnIDDocumentAutoCaptureDetection: CertnIDDocumentAutoCaptureDetection) {

    }

    override fun provideCertnIDDocumentConfiguration() = CertnIDDocumentConfiguration(
        certnIDCameraFacing = CertnIDCameraFacing.BackType,
        certnIDCameraPreviewScaleType = CertnIDCameraPreviewScaleType.FitType
    )

    override fun onCertnIDNoCameraPermission() {
    }

    private fun setupCertnIDSdkViewModel() {
        collectStateFlow {
            launch {
                certnIDSdkViewModel.state.collect { state ->
                    if (state.isInitialized) {
                        certnIDStart()
                    }
                    state.errorMessage?.let {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                        certnIDSdkViewModel.notifyErrorMessageShown()
                    }
                }
            }
        }
        certnIDSdkViewModel.initializeCertnIDSdkIfNeeded()
    }

    private fun setupDocumentAutoCaptureViewModel() {
        documentAutoCaptureViewModel.initializeState()
        documentAutoCaptureViewModel.state.observe(viewLifecycleOwner) { state ->
            state.result?.let {
                documentViewModel.postEnrolmentDocument(it.bitmap)
            }
        }
    }

    private fun setFullScreenLoading(visible: Boolean): View? {
        if (visible) {
            requireActivity().hideKeyboard()
        }

        return loadingHandler.setViewLoadingForView(
            view as ViewGroup,
            visible,
            loadingHandler.getLoadingView(layoutInflater, requireContext())
        )
    }

}
