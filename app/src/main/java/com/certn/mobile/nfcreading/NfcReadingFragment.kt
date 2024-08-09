package com.certn.mobile.nfcreading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.certn.mobile.R
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentNfcReadingBinding
import com.certn.mobile.util.extensions.collectStateFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NfcReadingFragment : BaseFragment<FragmentNfcReadingBinding>() {

    private val nfcViewModel: NfcViewModel by viewModels()
    private val nfcReadingViewModel: NfcReadingViewModel by activityViewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNfcReadingBinding =
        FragmentNfcReadingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding.btnCancel) {
            text = getString(R.string.sample_nfc_reading_cancel)
            setOnClickListener { findNavController().popBackStack() }
        }
        initNfcTravelDocumentReaderFragment(savedInstanceState)
        setupNfcReadingViewModel()
        collectStateFlow {
            launch {
                nfcViewModel.completeResponse.collect { completeResponse ->
                    if (completeResponse != null) {
                        findNavController().navigate(R.id.action_nfcReadingFragment_to_captureCompleteFragment)
                    }
                }
            }
        }
    }

    private fun initNfcTravelDocumentReaderFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(R.id.fcvMain, DefaultNfcTravelDocumentReaderFragment::class.java, null)
            }
        }
    }

    private fun setupNfcReadingViewModel() {
        nfcReadingViewModel.state.observe(viewLifecycleOwner) { state ->
            state.result?.let {
                nfcViewModel.postEnrolmentNfcData(it)
                //  navigate(NfcReadingFragmentDirections.actionNfcReadingFragmentToNfcReadingResultFragment())
            }
            state.errorMessage?.let {

            }
        }
    }
}
