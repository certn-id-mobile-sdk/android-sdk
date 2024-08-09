package com.certn.mobile.base.dialog

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DialogOptions(
    val title: String = "",
    val description: String = "",
    val primaryText: String = "",
    @IgnoredOnParcel val primaryAction: (@RawValue () -> Unit)? = null,
    @IgnoredOnParcel var primaryActionSuspend: (@RawValue suspend () -> Unit)? = null,
    @IgnoredOnParcel val dismissAction: (@RawValue () -> Unit)? = null,
    @IgnoredOnParcel var dismissActionSuspend: (@RawValue suspend () -> Unit)? = null,
    val isCancelable: Boolean = false
) : Parcelable
