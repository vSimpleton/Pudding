package com.miya.magicindicator.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import co.runner.app.widget.magicindicator.PositionData
import com.miya.magicindicator.abs.IPagerIndicator
import com.miya.magicindicator.utils.dpToPx


/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 社区页面的viewpager指示器样式
 */
class LinePagerIndicator(context: Context) : View(context), IPagerIndicator {

    @IntDef(MODE_EXACTLY_CENTER, MODE_EXACTLY_START)
    annotation class IndicatorMode

    @IndicatorMode
    private var mMode = MODE_EXACTLY_CENTER // 直线宽度模式，默认为MODE_EXACTLY_CENTER

    // 控制动画
    private var mStartInterpolator: Interpolator = LinearInterpolator()
    private var mEndInterpolator: Interpolator = LinearInterpolator()

    // 指示器相对于底部的偏移量，如果你想让直线位于title上方，设置它即可
    private var mYOffset = 0f
    private var mXOffset = 0f

    // 指示器宽度/高度
    private var mIndicatorWidth = 0f
    private var mIndicatorHeight = 0f
    private var mMidMargin = DEFAULT_MID_MARGIN.toFloat()

    // 左侧部分圆角矩形的圆角尺寸
    private var mRoundRadius = 0f

    // 右侧部分圆形半径(= mIndicatorHeight / 2)
    private var mCircleRadius = 0f

    @ColorInt
    private var mLeftColor = 0

    @ColorInt
    private var mRightColor = 0
    private var mPositionDataList: List<PositionData> = mutableListOf()

    private val mPaint by lazy { Paint() }
    private val mLineRect by lazy { RectF() }
    private val mCenterOfCircle by lazy { PointF() }

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        setIndicatorWidth(DEFAULT_INDICATOR_WIDTH.toFloat())
        setIndicatorHeight(DEFAULT_INDICATOR_HEIGHT.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mRightColor
        canvas.drawCircle(mCenterOfCircle.x, mCenterOfCircle.y, mCircleRadius, mPaint)
        mPaint.color = mLeftColor
        canvas.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, mPaint)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mPositionDataList.isEmpty()) {
            return
        }
        mPaint.color = mLeftColor

        // 计算锚点位置
        val current = getImitativePositionData(mPositionDataList, position)
        val next = getImitativePositionData(mPositionDataList, position + 1)
        val leftX: Float
        val nextLeftX: Float
        val rightX: Float
        val nextRightX: Float

        if (mMode == MODE_EXACTLY_CENTER) {
            leftX = current.mLeft + (current.width() - mIndicatorWidth) / 2
            nextLeftX = next.mLeft + (next.width() - mIndicatorWidth) / 2
            rightX = current.mLeft + (current.width() + mIndicatorWidth) / 2
            nextRightX = next.mLeft + (next.width() + mIndicatorWidth) / 2
        } else {
            leftX = current.mContentLeft.toFloat()
            nextLeftX = next.mContentLeft.toFloat()
            rightX = leftX + mIndicatorWidth
            nextRightX = nextLeftX + mIndicatorWidth
        }

        mCenterOfCircle.x = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset) - mCircleRadius
        mCenterOfCircle.y = height - mIndicatorHeight - mYOffset + mCircleRadius
        mLineRect.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset)
        mLineRect.right = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset) - mCircleRadius * 2 - mMidMargin
        mLineRect.top = height - mIndicatorHeight - mYOffset
        mLineRect.bottom = height - mYOffset

        invalidate()
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPositionDataProvide(dataList: List<PositionData>) {
        mPositionDataList = dataList
    }

    fun setYOffset(yOffsetDp: Float) {
        mYOffset = dpToPx(yOffsetDp).toFloat()
    }

    fun setXOffset(xOffsetDp: Float) {
        mXOffset = dpToPx(xOffsetDp).toFloat()
    }

    fun setIndicatorHeight(indicatorHeightDp: Float) {
        mIndicatorHeight = dpToPx(indicatorHeightDp).toFloat()
        mCircleRadius = mIndicatorHeight / 2
    }

    fun setIndicatorWidth(indicatorWidthDp: Float) {
        mIndicatorWidth = dpToPx(indicatorWidthDp).toFloat()
    }

    fun setMidMargin(marginDp: Float) {
        mMidMargin = dpToPx(marginDp).toFloat()
    }

    fun setRoundRadius(roundRadiusDp: Float) {
        mRoundRadius = dpToPx(roundRadiusDp).toFloat()
    }

    fun setMode(@IndicatorMode mode: Int) {
        mMode = mode
    }

    fun setColors(@ColorInt leftColor: Int, @ColorInt rightColor: Int) {
        mLeftColor = leftColor
        mRightColor = rightColor
    }

    fun setStartInterpolator(startInterpolator: Interpolator) {
        mStartInterpolator = startInterpolator
    }

    fun setEndInterpolator(endInterpolator: Interpolator) {
        mEndInterpolator = endInterpolator
    }

    companion object {
        const val MODE_EXACTLY_CENTER = 0 // 直线宽度 == mLineWidth 居中
        const val MODE_EXACTLY_START = 1 // 直线宽度 == mLineWidth 居左
        private val DEFAULT_INDICATOR_WIDTH = dpToPx(16f)
        private val DEFAULT_INDICATOR_HEIGHT = dpToPx(4f)
        private val DEFAULT_MID_MARGIN = dpToPx(2f)

        /**
         * IPagerIndicator支持弹性效果的辅助方法
         */
        fun getImitativePositionData(positionDataList: List<PositionData>, index: Int): PositionData {
            return if (index >= 0 && index <= positionDataList.size - 1) { // 越界后，返回假的PositionData
                positionDataList[index]
            } else {
                val result = PositionData()
                val referenceData: PositionData
                val offset: Int
                if (index < 0) {
                    offset = index
                    referenceData = positionDataList[0]
                } else {
                    offset = index - positionDataList.size + 1
                    referenceData = positionDataList[positionDataList.size - 1]
                }
                result.apply {
                    mLeft = referenceData.mLeft + offset * referenceData.width()
                    mTop = referenceData.mTop
                    mRight = referenceData.mRight + offset * referenceData.width()
                    mBottom = referenceData.mBottom
                    mContentLeft = referenceData.mContentLeft + offset * referenceData.width()
                    mContentTop = referenceData.mContentTop
                    mContentRight = referenceData.mContentRight + offset * referenceData.width()
                    mContentBottom = referenceData.mContentBottom
                }
            }
        }
    }
}