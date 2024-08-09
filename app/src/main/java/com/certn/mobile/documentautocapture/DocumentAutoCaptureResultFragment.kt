package com.certn.mobile.documentautocapture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.certn.mobile.R
import com.certn.mobile.ResultViewModel
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentAutoCaptureResultBinding
import com.certn.mobile.ui.createGson
import com.certn.mobile.util.extensions.collectStateFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DocumentAutoCaptureResultFragment : BaseFragment<FragmentAutoCaptureResultBinding>() {

    private val resultViewModel: ResultViewModel by activityViewModels()
    private val documentAutoCaptureViewModel: DocumentAutoCaptureViewModel by activityViewModels()
    private val gson = createGson()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoCaptureResultBinding =
        FragmentAutoCaptureResultBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDocumentAutoCaptureViewModel()
        collectStateFlow {
            launch {
                resultViewModel.documentError.collect { documentError ->
                    viewBinding.lblError.text = documentError
                }
            }
        }
    }

    private fun setupDocumentAutoCaptureViewModel() {
        documentAutoCaptureViewModel.state.observe(viewLifecycleOwner) { showResult(it.result!!) }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun showResult(result: BasicDocumentAutoCaptureResult) {
        with(viewBinding) {
            imgResult.setImageBitmap(result.bitmap)
            lblResult.text = gson.toJson(result)
            with(btnBack) {
                text = "Retake"
                setOnClickListener {
                    navigateBack()
                }
            }
        }
    }

    /*
    It needs to be recreated basicDocumentAutoCaptureFragment because Innovatrics fragment
     doesn't show rectangle placeholder
    */
    private fun navigateBack() {
        findNavController().navigate(
            R.id.basicDocumentAutoCaptureFragment,
            null,
            NavOptions.Builder().apply {
                setPopUpTo(R.id.basicDocumentAutoCaptureFragment, true)
            }.build()
        )
    }

}
