package com.certn.mobile.util.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.isCurrentDestination(): Boolean {
    val currentDestinationClassName =
        when (val currDest = findNavController().currentDestination ?: return false) {
            is FragmentNavigator.Destination -> currDest.className
            is DialogFragmentNavigator.Destination -> currDest.className
            else -> return false
        }

    return currentDestinationClassName == javaClass.name ||
            currentDestinationClassName == parentFragment?.javaClass?.name
}

fun Fragment.collectStateFlow(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}
