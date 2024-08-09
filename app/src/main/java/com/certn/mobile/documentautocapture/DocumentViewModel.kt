package com.certn.mobile.documentautocapture

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.RegisterDocumentRequest
import com.certn.mobile.domain.model.RegisterDocumentResult
import com.certn.mobile.domain.repository.CertnIDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val certnIDRepository: CertnIDRepository
) : BaseViewModel() {

    private val _documentFrontResponse = MutableStateFlow<RegisterDocumentResult?>(null)
    val documentFrontResponse: StateFlow<RegisterDocumentResult?> = _documentFrontResponse

    private val _documentBackResponse = MutableStateFlow<RegisterDocumentResult?>(null)
    val documentBackResponse: StateFlow<RegisterDocumentResult?> = _documentBackResponse

    val isAllScanned: Flow<Boolean> = combine(
        _documentFrontResponse,
        _documentBackResponse
    ) { documentFronResponse, documentBackResponse ->
        return@combine (documentFronResponse != null) and (documentBackResponse != null)
    }

    fun postEnrolmentDocument(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading(true)
                encodeImage(bitmap)?.also {
                    certnIDRepository.postEnrolmentDocument(
                        RegisterDocumentRequest(imageBase = it)
                    ).apply {
                        when (this.hasError) {
                            true -> _uiState.value = UiState.Success(this)
                            else -> when (this.pageSide) {
                                "FRONT" -> _documentFrontResponse.value = this
                                else -> _documentBackResponse.value = this
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

    fun resetData() {
        _documentBackResponse.value = null
        _documentFrontResponse.value = null
    }

}
