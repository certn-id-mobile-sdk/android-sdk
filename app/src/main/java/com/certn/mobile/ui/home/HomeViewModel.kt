package com.certn.mobile.ui.home

import androidx.lifecycle.viewModelScope
import com.certn.mobile.UiState
import com.certn.mobile.base.BaseViewModel
import com.certn.mobile.domain.model.SessionStartResult
import com.certn.mobile.domain.repository.CertnIDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val certnIDRepository: CertnIDRepository
) : BaseViewModel() {

    private val _startResponse = MutableStateFlow<SessionStartResult?>(null)
    val startResponse: StateFlow<SessionStartResult?> = _startResponse

    fun postEnrolmentStart() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading(true)
                _startResponse.value = certnIDRepository.postEnrolmentStart()
            } catch (e: Throwable) {
                _uiState.value = UiState.Error(e)
            } finally {
                _uiState.value = UiState.Loading(false)
            }
        }
    }

    fun resetData() {
        _startResponse.value = null
    }

}
