package co.runner.app.widget.magicindicator

import androidx.viewpager.widget.ViewPager

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 简化和ViewPager绑定
 */
object ViewPagerHelper {

    @JvmStatic
    fun bind(magicIndicator: MagicIndicator, viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magicIndicator.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }
}