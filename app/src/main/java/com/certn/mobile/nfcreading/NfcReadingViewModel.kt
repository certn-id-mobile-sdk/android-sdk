package com.certn.mobile.nfcreading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certn.sdk.nfc.CertnIDNfcKey
import com.certn.sdk.nfc.CertnIDTravelConfiguration
import com.certn.sdk.nfc.CertnIDTravelDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NfcReadingViewModel @Inject constructor(
    private val resolveAuthorityCertificatesFileUseCase: ResolveAuthorityCertificatesFileUseCase,
    private val createNfcReadingResultUseCase: CreateNfcReadingResultUseCase
) : ViewModel() {

    private val mutableState: MutableLiveData<NfcReadingState> = MutableLiveData()
    val state: LiveData<NfcReadingState> = mutableState

    fun initializeState() {
        mutableState.value = NfcReadingState()
    }

    fun setupConfiguration(certnIDNfcKey: CertnIDNfcKey) {
        viewModelScope.launch {
            val certnIDTravelConfiguration = CertnIDTravelConfiguration(
                certnIDNfcKey = certnIDNfcKey,
                authorityCertificatesFilePath = resolveAuthorityCertificatesFileUseCase().path,
            )
            mutableState.value =
                state.value!!.copy(certnIDTravelConfiguration = certnIDTravelConfiguration)
        }
    }

    fun setTravelDocument(certnIDTravelDocument: CertnIDTravelDocument) {
        viewModelScope.launch {
            val result = createNfcReadingResultUseCase(certnIDTravelDocument)
            mutableState.value = state.value!!.copy(result = result)
        }
    }

    fun setError(exception: Exception) {
        viewModelScope.launch {
            mutableState.value =
                state.value!!.copy(errorMessage = exception.message ?: exception::class.java.name)
        }
    }

    fun notifyErrorMessageShown() {
        mutableState.value = state.value!!.copy(errorMessage = null)
    }
}
