package com.vsimpleton.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * NAME: vSimpleton
 * DATE: 2022-03-01
 * DESC: ViewPager嵌套ViewPager滑动冲突解决
 */

class ChildViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var curPosition = 0
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE -> {
                curPosition = currentItem
                val count = adapter?.count ?: 0
                // 当当前页面在最后一页和第0页的时候，由父亲拦截触摸事件
                if (curPosition == count - 1 || curPosition == 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    // 其他情况，由孩子拦截触摸事件
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}