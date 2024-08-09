package com.certn.mobile.faceautocapture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.certn.mobile.ResultViewModel
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentAutoCaptureResultBinding
import com.certn.mobile.ui.createGson
import com.certn.mobile.util.extensions.collectStateFlow
import kotlinx.coroutines.launch

class FaceAutoCaptureResultFragment : BaseFragment<FragmentAutoCaptureResultBinding>() {

    private val resultViewModel: ResultViewModel by activityViewModels()
    private val faceAutoCaptureViewModel: FaceAutoCaptureViewModel by activityViewModels()
    private val gson = createGson()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoCaptureResultBinding =
        FragmentAutoCaptureResultBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFaceAutoCaptureViewModel()
        collectStateFlow {
            launch {
                resultViewModel.faceError.collect { faceError ->
                    viewBinding.lblError.text = faceError
                }
            }
        }
    }

    private fun setupFaceAutoCaptureViewModel() {
        faceAutoCaptureViewModel.state.observe(viewLifecycleOwner) { showResult(it.result!!) }
    }

    private fun showResult(result: BasicFaceAutoCaptureResult) {
        with(viewBinding) {
            imgResult.setImageBitmap(result.bitmap)
            lblResult.text = gson.toJson(result)
            with(btnBack) {
                text = "Retake"
                setOnClickListener {
                    navigateUp()
                }
            }
        }
    }
}
