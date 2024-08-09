package com.certn.mobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.certn.mobile.R
import com.certn.mobile.ResultViewModel
import com.certn.mobile.base.BaseFragment
import com.certn.mobile.databinding.FragmentAutoCaptureResultBinding

class CaptureCompleteFragment : BaseFragment<FragmentAutoCaptureResultBinding>() {

    private val resultViewModel: ResultViewModel by activityViewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutoCaptureResultBinding =
        FragmentAutoCaptureResultBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewBinding) {
            lblError.text = "Enrolment completed"
            lblError.text = "Everything is OK"
            with(btnBack) {
                text = "Continue"
                setOnClickListener {
                    navigateToQrScanFragment()
                }
            }
        }
    }

    override fun canNavigateUp(): Boolean = false

    override fun onBackPressed() {
        navigateToQrScanFragment()
    }

    private fun navigateToQrScanFragment() {
        resultViewModel.resetData()
        findNavController().popBackStack(R.id.nav_graph, false)
        findNavController().navigate(R.id.action_global_qrScanFragment)
    }

}
