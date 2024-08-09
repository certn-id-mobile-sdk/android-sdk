package com.certn.mobile.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.certn.mobile.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel : ViewModel(), LifecycleObserver {

    protected val _uiState = MutableStateFlow<UiState<Any>>(UiState.Empty)
    val uiState: StateFlow<UiState<Any>> = _uiState

}
