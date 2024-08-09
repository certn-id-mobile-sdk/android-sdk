package com.certn.mobile.faceautocapture

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
import com.certn.mobile.domain.model.RegisterFaceResult
import com.certn.mobile.nfcreading.NfcReadingViewModel
import com.certn.mobile.util.LoadingHandler
import com.certn.mobile.util.extensions.collectStateFlow
import com.certn.mobile.util.extensions.hideKeyboard
import com.certn.mobile.util.extensions.isCurrentDestination
import com.certn.sdk.face.autocapture.*
import com.certn.sdk.nfc.CertnIDNfcKey
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BasicFaceAutoCaptureFragment : CertnIDFaceAutoCaptureFragment() {

    private val faceViewModel: FaceViewModel by viewModels()
    private val resultViewModel: ResultViewModel by activityViewModels()
    private val certnIDSdkViewModel: CertnIDSdkViewModel by activityViewModels()
    private val faceAutoCaptureViewModel: FaceAutoCaptureViewModel by activityViewModels()
    private val nfcReadingViewModel: NfcReadingViewModel by activityViewModels()

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
        setupFaceAutoCaptureViewModel()
        setupNfcReadingViewModel()
        collectStateFlow {
            launch {
                faceViewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            resultViewModel.setFaceError((uiState.data as RegisterFaceResult).error)
                            certnIDStopAsync {}
                            findNavController().navigate(R.id.action_basicFaceAutoCaptureFragment_to_faceAutoCaptureResultFragment)
                        }
                        is UiState.Loading -> setFullScreenLoading(uiState.value)
                        else -> Unit
                    }
                }
            }
            launch {
                faceViewModel.nfcKeyResponse.collect { nfcKeyResponse ->
                    nfcKeyResponse?.data?.apply {
                        if (documentNumber != null && dateOfExpiry != null && dateOfBirth != null) {
                            nfcReadingViewModel.setupConfiguration(
                                CertnIDNfcKey(documentNumber, dateOfExpiry, dateOfBirth)
                            )
                        } else throw IllegalStateException("Machine Readable Zone must be present.")
                    }
                }
            }
            launch {
                faceViewModel.completeResponse.collect { completeResponse ->
                    if (completeResponse != null) {
                        findNavController().navigate(R.id.action_basicFaceAutoCaptureFragment_to_captureCompleteFragment)
                    }
                }
            }
        }
    }

    override fun onCertnIDCaptured(certnIDFaceAutoCaptureResult: CertnIDFaceAutoCaptureResult) {
        faceAutoCaptureViewModel.process(certnIDFaceAutoCaptureResult)
    }

    override fun onCertnIDProcessed(certnIDFaceAutoCaptureDetection: CertnIDFaceAutoCaptureDetection) {
    }

    override fun provideCertnIDFaceConfiguration() = CertnIDFaceConfiguration(
        certnIDFaceDetectionQuery = CertnIDFaceDetectionQuery(
            certnIDFaceQualityQuery = CertnIDFaceQualityQuery(
                certnIDFaceImageQualityQuery = CertnIDFaceImageQualityQuery(
                    evaluateSharpness = true,
                    evaluateBrightness = true,
                    evaluateContrast = true,
                    evaluateUniqueIntensityLevels = true,
                    evaluateShadow = true,
                    evaluateSpecularity = true,
                ),
                certnIDHeadPoseQuery = CertnIDHeadPoseQuery(
                    evaluateRoll = true,
                    evaluateYaw = true,
                    evaluatePitch = true
                ),
                certnIDWearablesQuery = CertnIDWearablesQuery(
                    evaluateGlasses = true
                ),
                certnIDExpressionQuery = CertnIDExpressionQuery(
                    certnIDEyesExpressionQuery = CertnIDEyesExpressionQuery(
                        evaluateRightEye = true,
                        evaluateLeftEye = true
                    ),
                    evaluateMouth = true
                )
            )
        )
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

    private fun setupFaceAutoCaptureViewModel() {
        faceAutoCaptureViewModel.initializeState()
        faceAutoCaptureViewModel.state.observe(viewLifecycleOwner) { state ->
            state.result?.let {
                faceViewModel.postEnrolmentFace(it.bitmap)
            }
            state.errorMessage?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                faceAutoCaptureViewModel.notifyErrorMessageShown()
            }
        }
    }

    private fun setupNfcReadingViewModel() {
        nfcReadingViewModel.initializeState()
        nfcReadingViewModel.state.observe(viewLifecycleOwner) { state ->
            state.certnIDTravelConfiguration?.let {
                if (isCurrentDestination()) {
                    findNavController().navigate(BasicFaceAutoCaptureFragmentDirections.actionBasicFaceAutoCaptureFragmentToNfcReadingFragment())
                }
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
