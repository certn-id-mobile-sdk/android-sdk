package com.certn.mobile.faceautocapture

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.NfcKeyResponse
import com.certn.mobile.domain.model.RegisterFaceRequest
import com.certn.mobile.domain.model.SessionCompleteCommandResult
import com.certn.mobile.domain.repository.CertnIDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class FaceViewModel @Inject constructor(
    private val certnIDRepository: CertnIDRepository
) : BaseViewModel() {

    private val _nfcKeyResponse = MutableStateFlow<NfcKeyResponse?>(null)
    val nfcKeyResponse: StateFlow<NfcKeyResponse?> = _nfcKeyResponse

    private val _completeResponse = MutableStateFlow<SessionCompleteCommandResult?>(null)
    val completeResponse: StateFlow<SessionCompleteCommandResult?> = _completeResponse

    fun postEnrolmentFace(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading(true)
                encodeImage(bitmap)?.also {
                    certnIDRepository.postEnrolmentFace(
                        RegisterFaceRequest(imageBase = it)
                    ).apply {
                        when (this.hasError) {
                            true -> _uiState.value = UiState.Success(this)
                            else -> {
                                certnIDRepository.getNfcKeyData().apply {
                                    when (rfidPresence) {
                                        0 -> _completeResponse.value =
                                            certnIDRepository.postEnrolmentComplete()
                                        else -> _nfcKeyResponse.value = this
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                _uiState.value = UiState.Error(e)
            } finally {
                _uiState.value = UiState.Loading(false)
            }
        }
    }

    private fun encodeImage(bm: Bitmap): String? = try {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val b = baos.toByteArray()
        Base64.encodeToString(b, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }

}
