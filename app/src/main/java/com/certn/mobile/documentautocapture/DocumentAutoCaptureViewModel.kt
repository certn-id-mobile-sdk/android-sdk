package com.certn.mobile.documentautocapture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentAutoCaptureViewModel @Inject constructor(
    private val createDocumentUiResultUseCase: CreateDocumentUiResultUseCase,
) : ViewModel() {

    private val mutableState: MutableLiveData<DocumentAutoCaptureState> = MutableLiveData()
    val state: LiveData<DocumentAutoCaptureState> = mutableState

    fun initializeState() {
        mutableState.value = DocumentAutoCaptureState()
    }

    fun process(certnIDDocumentAutoCaptureResult: CertnIDDocumentAutoCaptureResult) {
        viewModelScope.launch {
            val result = createDocumentUiResultUseCase(certnIDDocumentAutoCaptureResult)
            mutableState.value = state.value!!.copy(result = result)
        }
    }
}
