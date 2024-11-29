package com.learning.android.chat

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView

class OverlayView : ViewGroup {
    private var isStatusBarBlocked: Boolean =
        false // Flag to determine if the status bar is blocked
    private var overlayStatusTextView: TextView? = null // Reference to the overlay status TextView

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setBackgroundColor(Color.TRANSPARENT) // Set the overlay background color to transparent
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isStatusBarBlocked) {
            // Block touch events to prevent interaction with the status bar
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // Layout the child views within the OverlayView
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(left, top, right, bottom)
        }
    }

    fun setOverlayStatusTextView(textView: TextView?) {
        overlayStatusTextView = textView
    }

    fun updateOverlayStatus(enabled: Boolean) {
        if (overlayStatusTextView != null) {
            if (enabled) {
                overlayStatusTextView!!.text = "Overlay is enabled"
            } else {
                overlayStatusTextView!!.text = "Overlay is disabled"
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isStatusBarBlocked) {
            // Block touch events to prevent interaction with the status bar
            return true
        }
        return super.onTouchEvent(event)
    }
}
