package co.runner.app.widget.magicindicator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.miya.magicindicator.CommonNavigator

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 可扩展的ViewPager指示器
 *
 * 原作者github: https://github.com/hackware1993/MagicIndicator
 */
class MagicIndicator @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mNavigator: CommonNavigator? = null

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mNavigator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    fun onPageSelected(position: Int) {
        mNavigator?.onPageSelected(position)
    }

    fun onPageScrollStateChanged(state: Int) {
        mNavigator?.onPageScrollStateChanged(state)
    }

    fun setNavigator(navigator: CommonNavigator) {
        if (mNavigator === navigator) {
            return
        }
        mNavigator?.onDetachFromMagicIndicator()
        mNavigator = navigator
        removeAllViews()
        if (mNavigator is View) {
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(mNavigator, lp)
            navigator.onAttachToMagicIndicator()
        }
    }
}