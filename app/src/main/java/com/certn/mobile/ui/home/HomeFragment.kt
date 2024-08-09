package com.certn.mobile.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.certn.mobile.ResultViewModel
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.data.dto.EnrolmentStep
import com.certn.mobile.databinding.FragmentHomeBinding
import com.certn.mobile.util.extensions.collectStateFlow
import com.certn.mobile.util.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val resultViewModel: ResultViewModel by activityViewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            lblHomeTitle.text = "Onboarding trust platform"
            lblDocumentTitle.text = "1. Scan your document"
            lblDocumentDesc.text =
                "Ensure your ID is visible. The app auto-captures when focused on the document. For best results, avoid reflections and bad lighting."
            lblFaceTitle.text = "2. Take a selfie"
            lblFaceDesc.text =
                "Capture your face for validation. App auto-captures when it identifies your face in focus. For best results, avoid reflections and bad lighting."

            with(btnVerify) {
                text = "Verify me"
                setOnClickListener {
                    homeViewModel.postEnrolmentStart()
                }
            }

            collectStateFlow {
                launch {
                    resultViewModel.configuration.collect { configuration ->
                        configuration?.apply {
                            val isDocument =
                                steps?.firstOrNull { step -> step.name == EnrolmentStep.Document } != null
                            val isFace =
                                steps?.firstOrNull { step -> step.name == EnrolmentStep.Face } != null
                            if (isDocument) layDocument.show()
                            if (isFace) layFace.show()
                            if (isDocument && isFace) lnDivider.show()
                        }
                    }
                }
                launch {
                    homeViewModel.uiState.collect { uiState ->
                        when (uiState) {
                            is UiState.Loading -> setFullScreenLoading(uiState.value)
                            else -> Unit
                        }
                    }
                }
                launch {
                    homeViewModel.startResponse.collect { startResponse ->
                        startResponse?.apply {
                            homeViewModel.resetData()
                            navigate(HomeFragmentDirections.actionHomeFragmentToBasicDocumentAutoCaptureFragment())
                        }
                    }
                }
            }
        }
    }

}
