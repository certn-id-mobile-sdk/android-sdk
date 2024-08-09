package com.certn.mobile.base.dialog

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

@DslMarker
annotation class DslDialog

@DslDialog
class DialogBuilder(
    val context: Context,
    val setup: DialogBuilder.() -> Unit = {}
) {

    var title: String = ""
    var description: String = ""
    var primaryText: String = ""
    var primaryAction: (() -> Unit)? = null
    var primaryActionSuspend: (suspend () -> Unit)? = null
    var dismissAction: (() -> Unit)? = null
    var dismissActionSuspend: (suspend () -> Unit)? = null
    var isCancelable: Boolean = false

    fun build(): DialogErrorFragment {
        setup()
        val options = DialogOptions(
            title = title,
            description = description,
            primaryText = primaryText,
            primaryAction = primaryAction,
            primaryActionSuspend = primaryActionSuspend,
            dismissAction = dismissAction,
            dismissActionSuspend = dismissActionSuspend,
            isCancelable = isCancelable
        )

        return DialogErrorFragment.newInstance(options)
    }
}

fun Fragment.dialog(setup: DialogBuilder.() -> Unit) {
    val builder = DialogBuilder(requireContext(), setup = setup)
    builder.build().show(childFragmentManager, null)
}

fun FragmentActivity.dialog(setup: DialogBuilder.() -> Unit) {
    val builder = DialogBuilder(this, setup = setup)
    builder.build().show(supportFragmentManager, null)
}
