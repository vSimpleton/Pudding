package com.miya.magicindicator.abs

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 抽象的指示器标题，r需要新增不同的标题样式时需要实现该接口
 */
interface IPagerTitleView {
    /**
     * 被选中
     */
    fun onSelected(index: Int, totalCount: Int)

    /**
     * 未被选中
     */
    fun onDeselected(index: Int, totalCount: Int)

    /**
     * 离开
     *
     * @param leavePercent 离开的百分比, 0.0f - 1.0f
     * @param leftToRight  从左至右离开
     */
    fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean)

    /**
     * 进入
     *
     * @param enterPercent 进入的百分比, 0.0f - 1.0f
     * @param leftToRight  从左至右离开
     */
    fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean)

    // 用于测量内容区域
    val contentLeft: Int
    val contentTop: Int
    val contentRight: Int
    val contentBottom: Int
}