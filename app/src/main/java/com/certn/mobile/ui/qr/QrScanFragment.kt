package com.certn.mobile.ui.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.certn.mobile.ResultViewModel
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentQrScanBinding
import com.certn.mobile.util.extensions.collectStateFlow
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class QrScanFragment : BaseFragment<FragmentQrScanBinding>() {

    private val qrViewModel: QrViewModel by viewModels()
    private val resultViewModel: ResultViewModel by activityViewModels()
    private var cameraController: LifecycleCameraController? = null
    private var cameraExecutor: ExecutorService? = null
    private var barcodeScanner: BarcodeScanner? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentQrScanBinding =
        FragmentQrScanBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            lblQrTitle.text = "Use your smartphone"
            lblQrDescription.text =
                "For efficient identity verification, scan this QR code with your phone's native camera app"
            with(btnQrContinue) {
                text = "Continue on this device"
                setOnClickListener {

                }
            }
            collectStateFlow {
                launch {
                    qrViewModel.uiState.collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> setFullScreenLoading(uiState.value)
                            else -> Unit
                        }
                    }
                }
                launch {
                    qrViewModel.configuration.collect { configuration ->
                        configuration?.apply {
                            resultViewModel.setConfiguration(this)
                            qrViewModel.resetData()
                            navigateToHomeFragment()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (qrViewModel.isCameraCheckReady) {
            checkCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        closeScanner()
    }

    override fun handleGrantedPermissions() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    override fun handleDeniedPermissions() {
        qrViewModel.isCameraCheckReady = false
        showCameraSettingsDialog { qrViewModel.isCameraCheckReady = true }
    }

    private fun startCamera() {
        cameraController = LifecycleCameraController(requireContext())
        val previewView: PreviewView = viewBinding.pvCamera

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController?.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(requireContext())
            ) { result: MlKitAnalyzer.Result? ->
                var barcodeResults: List<Barcode>? = null
                try {
                    barcodeScanner?.apply {
                        barcodeResults = result?.getValue(this)
                    }
                } catch (_: Throwable) {
                }
                barcodeResults?.firstOrNull()?.rawValue?.apply qr@{
                    if (qrViewModel.qrScanDisabled.compareAndSet(false, true)) {
                        lifecycleScope.launch {
                            delay(100L)
                            qrViewModel.setQrCode(this@qr)
                        }
                    }
                }
            }
        )
        cameraController?.bindToLifecycle(this)
        previewView.controller = cameraController
    }

    private fun closeScanner() {
        cameraController?.unbind()
        cameraExecutor?.shutdown()
        barcodeScanner?.close()
    }

    private fun navigateToHomeFragment() {
        navigate(QrScanFragmentDirections.actionQrScanFragmentToHomeFragment())
    }
}
