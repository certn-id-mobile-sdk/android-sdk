package com.certn.mobile.nfcreading

import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.RegisterNfcDataCommand
import com.certn.mobile.domain.model.SessionCompleteCommandResult
import com.certn.mobile.domain.repository.CertnIDRepository
import com.certn.mobile.ui.createGson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NfcViewModel @Inject constructor(
    private val certnIDRepository: CertnIDRepository
) : BaseViewModel() {

    private val _completeResponse = MutableStateFlow<SessionCompleteCommandResult?>(null)
    val completeResponse: StateFlow<SessionCompleteCommandResult?> = _completeResponse

    private val gson = createGson()

    fun postEnrolmentNfcData(result: NfcReadingResult) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading(true)
                gson.toJson(result).apply {
                    val byteArray = this.toByteArray(Charsets.UTF_8)
                    val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
                    certnIDRepository.postEnrolmentNfcData(
                        RegisterNfcDataCommand(encoded)
                    ).apply {
                        when (this.hasError) {
                            true -> _uiState.value = UiState.Success(this)
                            else -> {
                                _completeResponse.value =
                                    certnIDRepository.postEnrolmentComplete()
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

}
