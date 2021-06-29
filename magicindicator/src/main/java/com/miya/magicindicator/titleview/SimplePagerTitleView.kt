package com.miya.magicindicator.titleview

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.miya.magicindicator.abs.IPagerTitleView
import com.miya.magicindicator.utils.dpToPx

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 社区页面的viewpager标题样式
 */
class SimplePagerTitleView(context: Context) : AppCompatTextView(context, null), IPagerTitleView {

    var selectedColor = 0
    var normalColor = 0

    init {
        gravity = Gravity.CENTER
        val padding = dpToPx(12f)
        setPadding(padding, 0, padding, 0)
        setSingleLine()
    }

    override fun onSelected(index: Int, totalCount: Int) {
        setTextColor(selectedColor)
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        setTextColor(normalColor)
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {

    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {

    }

    override val contentLeft: Int
        get() {
            val bound = Rect()
            paint.getTextBounds(text.toString(), 0, text.length, bound)
            val contentWidth = bound.width()
            return left + width / 2 - contentWidth / 2
        }

    override val contentTop: Int
        get() {
            val metrics = paint.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 - contentHeight / 2).toInt()
        }

    override val contentRight: Int
        get() {
            val bound = Rect()
            paint.getTextBounds(text.toString(), 0, text.length, bound)
            val contentWidth = bound.width()
            return left + width / 2 + contentWidth / 2
        }

    override val contentBottom: Int
        get() {
            val metrics = paint.fontMetrics
            val contentHeight = metrics.bottom - metrics.top
            return (height / 2 + contentHeight / 2).toInt()
        }

}