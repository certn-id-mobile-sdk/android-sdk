package com.certn.mobile.faceautocapture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.certn.sdk.face.autocapture.CertnIDFaceAutoCaptureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceAutoCaptureViewModel @Inject constructor(
    private val createFaceUiResultUseCase: CreateFaceUiResultUseCase,
) : ViewModel() {

    private val mutableState: MutableLiveData<FaceAutoCaptureState> = MutableLiveData()
    val state: LiveData<FaceAutoCaptureState> = mutableState

    fun initializeState() {
        mutableState.value = FaceAutoCaptureState()
    }

    fun process(certnIDFaceAutoCaptureResult: CertnIDFaceAutoCaptureResult) {
        viewModelScope.launch {
            mutableState.value = state.value!!.copy(isProcessing = true)
            mutableState.value = processSafely(certnIDFaceAutoCaptureResult)
        }
    }

    private suspend fun processSafely(certnIDFaceAutoCaptureResult: CertnIDFaceAutoCaptureResult): FaceAutoCaptureState {
        return try {
            val result = createFaceUiResultUseCase(certnIDFaceAutoCaptureResult)
            state.value!!.copy(isProcessing = false, result = result)
        } catch (e: Exception) {
            state.value!!.copy(isProcessing = false, errorMessage = e.message)
        }
    }

    fun notifyErrorMessageShown() {
        mutableState.value = state.value!!.copy(errorMessage = null)
    }
}
