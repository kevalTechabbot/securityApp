package com.learning.android.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.UnderlineSpan
import android.view.View

class SpannableText private constructor(private val builder: Builder) {

    fun build(): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(builder.text)

        for (spanData in builder.spans) {
            val start = spanData.start
            val end = spanData.end

            // Set color span
            if (spanData.textColor != null) {
                val colorSpan = ForegroundColorSpan(spanData.textColor)
                spannableStringBuilder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Set underline span
            if (spanData.underline) {
                val underlineSpan = UnderlineSpan()
                spannableStringBuilder.setSpan(underlineSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Set bold span
            if (spanData.bold) {
                val boldSpan = TypefaceSpanCompat(Typeface.BOLD)
                spannableStringBuilder.setSpan(boldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Set click listener span
            if (spanData.clickListener != null) {
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        spanData.clickListener.onClick(view)
                    }
                }
                spannableStringBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        return spannableStringBuilder
    }

    data class SpanData(
        val start: Int,
        val end: Int,
        val textColor: Int? = null,
        val underline: Boolean = false,
        val bold: Boolean = false,
        val clickListener: View.OnClickListener? = null
    )

    class Builder(val text: CharSequence) {
        val spans: MutableList<SpanData> = mutableListOf()

        fun color(color: Int, start: Int, end: Int): Builder {
            spans.add(SpanData(start, end, textColor = color))
            return this
        }

        fun underline(start: Int, end: Int): Builder {
            spans.add(SpanData(start, end, underline = true))
            return this
        }

        fun bold(start: Int, end: Int): Builder {
            spans.add(SpanData(start, end, bold = true))
            return this
        }

        fun clickListener(listener: View.OnClickListener, start: Int, end: Int): Builder {
            spans.add(SpanData(start, end, clickListener = listener))
            return this
        }

        fun build(): SpannableText {
            return SpannableText(this)
        }
    }

    class TypefaceSpanCompat(private val style: Int) : MetricAffectingSpan() {

        override fun updateDrawState(ds: TextPaint) {
            applyStyle(ds, style)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyStyle(paint, style)
        }

        private fun applyStyle(paint: TextPaint, style: Int) {
            val oldTypeface = paint.typeface
            val newTypeface = Typeface.create(oldTypeface, style)
            val fake = newTypeface.style and style.inv()

            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }

            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = newTypeface
        }
    }
}
