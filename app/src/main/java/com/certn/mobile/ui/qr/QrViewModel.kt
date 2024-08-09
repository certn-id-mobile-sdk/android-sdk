package com.certn.mobile.ui.qr

import androidx.lifecycle.viewModelScope
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.ConfigurationResponse
import com.certn.mobile.domain.repository.CertnIDRepository
import com.certn.mobile.storage.CertnIDEncryptedSharedPreferences
import com.certn.mobile.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class QrViewModel @Inject constructor(
    private val certnIDRepository: CertnIDRepository,
    private val encryptedStorage: CertnIDEncryptedSharedPreferences
) : BaseViewModel() {

    var isCameraCheckReady = true
    val qrScanDisabled = AtomicBoolean(false)

    private val _configuration = MutableStateFlow<ConfigurationResponse?>(null)
    val configuration: StateFlow<ConfigurationResponse?> = _configuration

    fun setQrCode(qrCode: String) {
        val formatted = qrCode.substringAfter("?token=").substringBefore("&")
        getTokenExchange(formatted)
    }

    private fun getTokenExchange(token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading(true)
                certnIDRepository.getTokenExchange(token).let {
                    encryptedStorage.setValue(Constants.ACCESS_TOKEN, it.token)
                }
                _configuration.value = certnIDRepository.getSdkConfiguration()
            } catch (e: Throwable) {
                _uiState.value = UiState.Error(e)
            } finally {
                _uiState.value = UiState.Loading(false)
            }
        }
    }

    fun resetData() {
        _configuration.value = null
        qrScanDisabled.set(false)
    }

}
