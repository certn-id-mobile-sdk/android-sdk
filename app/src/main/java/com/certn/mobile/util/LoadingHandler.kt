package com.certn.mobile.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.certn.mobile.R
import com.certn.mobile.databinding.LayoutLoadingBinding

class LoadingHandler {

    fun getLoadingView(layoutInflater: LayoutInflater, context: Context): View =
        LayoutLoadingBinding.inflate(layoutInflater).root.also {
            it.tag = LayoutLoadingBinding::class.java.name
            val imageView = it.findViewById<ImageView>(R.id.imgLoading)
            imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate))
        }

    fun setViewLoadingForView(
        parentView: ViewGroup,
        visible: Boolean,
        loadingView: View
    ): View? {
        if (loadingView.tag == null || loadingView.tag !is String) {
            throw IllegalStateException("Loading view must have unique 'tag' of type 'String'")
        }

        val tag = getTag(loadingView, parentView)
        loadingView.tag = tag
        val oldLoadingView = parentView.findViewWithTag<View>(tag)

        return if (oldLoadingView == null) {
            if (visible) {
                initLoadingView(loadingView)
                addLoadingViewIntoParentView(loadingView, parentView)
                loadingView
            } else null
        } else {
            if (visible) {
                oldLoadingView
            } else {
                parentView.removeView(oldLoadingView)
                null
            }
        }
    }

    private fun initLoadingView(loadingView: View) {
        loadingView.isClickable = true
        loadingView.elevation = 100F
    }

    private fun addLoadingViewIntoParentView(view: View, parentView: ViewGroup) {
        parentView.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun getTag(loadingView: View, parentView: ViewGroup) =
        "$LOADING_VIEW_TAG-${loadingView.tag}-${parentView.id}"

    companion object {
        private const val LOADING_VIEW_TAG = "loadingViewTag"
    }
}
