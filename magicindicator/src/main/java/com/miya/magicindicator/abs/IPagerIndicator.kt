package com.miya.magicindicator.abs

import co.runner.app.widget.magicindicator.PositionData

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 抽象的viewpager指示器，需要新增不同的指示器样式时需要实现该接口
 */
interface IPagerIndicator {

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)

    fun onPageSelected(position: Int)

    fun onPageScrollStateChanged(state: Int)

    fun onPositionDataProvide(dataList: List<PositionData>)

}