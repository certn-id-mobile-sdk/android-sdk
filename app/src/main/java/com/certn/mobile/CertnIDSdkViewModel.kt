package com.certn.mobile

import androidx.lifecycle.viewModelScope
import com.certn.mobile.base.BaseViewModel
import com.certn.sdk.CertnIDMobileSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertnIDSdkViewModel @Inject constructor(
    private val initializeCertnIDSdkUseCase: InitializeCertnIDSdkUseCase
) : BaseViewModel() {

    private val mutableState = MutableStateFlow(CertnIDSdkState())
    val state = mutableState.asStateFlow()

    fun initializeCertnIDSdkIfNeeded() {
        viewModelScope.launch {
            initializeCertnIDSdkIfNeededInternal()
        }
    }

    private suspend fun initializeCertnIDSdkIfNeededInternal() {
        when (CertnIDMobileSdk.isInitialized()) {
            true -> mutableState.update { it.copy(isInitialized = true) }
            false -> initializeCertnIDSdkSafely()
        }
    }

    private suspend fun initializeCertnIDSdkSafely() = try {
        initializeCertnIDSdkUseCase()
        mutableState.update { it.copy(isInitialized = true) }
    } catch (e: Exception) {
        mutableState.update { it.copy(errorMessage = e.message) }
    }

    fun notifyErrorMessageShown() {
        mutableState.update { it.copy(errorMessage = null) }
    }

}
