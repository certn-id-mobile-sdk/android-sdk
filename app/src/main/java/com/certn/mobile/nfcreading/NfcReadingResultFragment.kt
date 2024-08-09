package com.certn.mobile.nfcreading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentAutoCaptureResultBinding
import com.certn.mobile.ui.createGson

class NfcReadingResultFragment : BaseFragment<FragmentAutoCaptureResultBinding>() {

    private val nfcReadingViewModel: NfcReadingViewModel by activityViewModels()
    private val gson = createGson()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoCaptureResultBinding =
        FragmentAutoCaptureResultBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNfcReadingViewModel()
    }

    private fun setupNfcReadingViewModel() {
        nfcReadingViewModel.state.observe(viewLifecycleOwner) { showResult(it.result!!) }
    }

    private fun showResult(result: NfcReadingResult) {
        with(viewBinding) {
          //  imgResult.setImageBitmap(result.faceBitmap)
            lblResult.text = gson.toJson(result)
        }
    }
}
