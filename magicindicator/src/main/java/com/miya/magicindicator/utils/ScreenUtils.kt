package com.miya.magicindicator.utils

import android.content.res.Resources

/**
 * NAME: vSimpleton
 * DATE: 2021/6/18
 * DESC:
 */

fun dpToPx(dpValue: Float): Int {
    return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(pxValue: Float): Int {
    return (pxValue / Resources.getSystem().displayMetrics.density).toInt()
}
