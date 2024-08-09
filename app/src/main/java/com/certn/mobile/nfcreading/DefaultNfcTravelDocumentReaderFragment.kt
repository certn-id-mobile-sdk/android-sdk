package com.certn.mobile.nfcreading

import androidx.fragment.app.activityViewModels
import com.certn.sdk.nfc.CertnIDTravelConfiguration
import com.certn.sdk.nfc.CertnIDTravelDocument
import com.certn.sdk.nfc.reader.ui.CertnIDNfcTravelDocumentReaderFragment

class DefaultNfcTravelDocumentReaderFragment : CertnIDNfcTravelDocumentReaderFragment() {

    private val nfcReadingViewModel: NfcReadingViewModel by activityViewModels()

    override fun onCertnIDSucceeded(certnIDTravelDocument: CertnIDTravelDocument) {
        nfcReadingViewModel.setTravelDocument(certnIDTravelDocument)
    }

    override fun onCertnIDFailed(exception: Exception) {
        nfcReadingViewModel.setError(exception)
    }

    override fun provideCertnIDTravelConfiguration(): CertnIDTravelConfiguration {
        return nfcReadingViewModel.state.value?.certnIDTravelConfiguration!!
    }
}
