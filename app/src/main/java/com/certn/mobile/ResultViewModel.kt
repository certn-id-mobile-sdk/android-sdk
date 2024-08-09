package com.certn.mobile

import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.ConfigurationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
) : BaseViewModel() {

    private val _configuration = MutableStateFlow<ConfigurationResponse?>(null)
    val configuration: StateFlow<ConfigurationResponse?> = _configuration

    private val _documentError = MutableStateFlow<String?>(null)
    val documentError: StateFlow<String?> = _documentError

    private val _faceError = MutableStateFlow<String?>(null)
    val faceError: StateFlow<String?> = _faceError

    fun setConfiguration(configuration: ConfigurationResponse) {
        _configuration.value = configuration
    }

    fun setDocumentError(error: String?) {
        _documentError.value = error
    }

    fun setFaceError(error: String?) {
        _faceError.value = error
    }

    fun resetData() {
        _configuration.value = null
    }

}
