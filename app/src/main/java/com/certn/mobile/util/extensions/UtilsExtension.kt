package com.certn.mobile.util.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.showOrHide(condition: Boolean) {
    if (condition) this.show() else this.hide()
}

fun View.showOrInvisible(condition: Boolean) {
    if (condition) this.show() else this.invisible()
}

fun TextView.showOrHideText(text: String?) {
    if (text.isNullOrEmpty()) {
        this.hide()
    } else {
        this.show()
        this.text = text
    }
}

fun View.setBgColor(context: Context, color: Int) =
    this.setBackgroundColor(ContextCompat.getColor(context, color))

fun View.setBgDrawable(context: Context, drawable: Int) {
    this.background = ContextCompat.getDrawable(context, drawable)
}

fun TextView.setLblColor(context: Context, color: Int) =
    this.setTextColor(ContextCompat.getColor(context, color))

fun ImageView.setImgDrawable(context: Context, drawable: Int) =
    this.setImageDrawable(ContextCompat.getDrawable(context, drawable))

fun ImageView.setImgTint(context: Context, color: Int) {
    this.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
}

fun Editable?.trimmed(): String = this.toString().trim()
