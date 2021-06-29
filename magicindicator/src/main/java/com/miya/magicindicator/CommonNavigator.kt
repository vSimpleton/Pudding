package com.miya.magicindicator

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import co.runner.app.widget.magicindicator.NavigatorHelper
import co.runner.app.widget.magicindicator.NavigatorHelper.OnNavigatorScrollListener
import co.runner.app.widget.magicindicator.PositionData
import co.runner.app.widget.magicindicator.SCROLL_STATE_IDLE
import com.miya.magicindicator.abs.CommonNavigatorAdapter
import com.miya.magicindicator.abs.IPagerIndicator
import com.miya.magicindicator.abs.IPagerTitleView
import java.util.*
import kotlin.math.min

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 通用的ViewPager指示器，包含PagerTitle和PagerIndicator
 */
class CommonNavigator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnNavigatorScrollListener {

    private val mNavigatorHelper by lazy { NavigatorHelper() }

    init {
        mNavigatorHelper.setNavigatorScrollListener(this)
    }

    private lateinit var mScrollView: HorizontalScrollView
    private lateinit var mTitleContainer: LinearLayout
    private lateinit var mIndicatorContainer: LinearLayout

    private var pagerIndicator: IPagerIndicator? = null
    private var mAdapter: CommonNavigatorAdapter? = null

    // ===================提供给外部的参数配置=====================
    // 自适应模式，适用于数目固定的、少量的title
    var mAdjustMode = false

    // 启动中心点滚动
    var isEnablePivotScroll = false

    // 滚动中心点 0.0f - 1.0f
    var mScrollPivotX = 0.5f

    // 是否平滑滚动，适用于 !mAdjustMode && !mFollowTouch
    var mSmoothScroll = true

    // 是否手指跟随滚动
    var mFollowTouch = true

    var mRightPadding = 0
    var mLeftPadding = 0

    // 指示器是否在title上层，默认为下层
    var mIndicatorOnTop = false

    // 跨多页切换时，中间页是否显示 "掠过" 效果
    var mSkimOver = false

    // PositionData准备好时，是否重新选中当前页，为true可保证在极端情况下指示器状态正确
    var mReselectWhenLayout = true
    // ===================提供给外部的参数配置=====================

    // 保存每个title的位置信息，为扩展indicator提供保障
    private val mPositionDataList: MutableList<PositionData> = ArrayList()
    private val mObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            mAdapter?.let {
                mNavigatorHelper.totalCount = it.count // 如果使用helper，应始终保证helper中的totalCount为最新
                initView()
            }
        }

        override fun onInvalidated() {
            // 暂不做处理
        }
    }

    fun notifyDataSetChanged() {
        mAdapter?.notifyDataSetChanged()
    }

    fun setAdapter(adapter: CommonNavigatorAdapter) {
        if (mAdapter === adapter) {
            return
        }
        mAdapter?.unregisterDataSetObserver(mObserver)
        mAdapter = adapter
        mAdapter?.let {
            it.registerDataSetObserver(mObserver)
            mNavigatorHelper.totalCount = it.count
            it.notifyDataSetChanged()
        }
    }

    private fun initView() {
        removeAllViews()
        val root: View = if (mAdjustMode) {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout_no_scroll, this)
        } else {
            LayoutInflater.from(context).inflate(R.layout.pager_navigator_layout, this)
        }
        mScrollView = root.findViewById(R.id.scroll_view)
        mTitleContainer = root.findViewById(R.id.title_container)
        mTitleContainer.setPadding(mLeftPadding, 0, mRightPadding, 0)
        mIndicatorContainer = root.findViewById(R.id.indicator_container)
        if (mIndicatorOnTop) {
            mIndicatorContainer.parent.bringChildToFront(mIndicatorContainer)
        }
        initTitlesAndIndicator()
    }

    /**
     * 初始化title和indicator
     */
    private fun initTitlesAndIndicator() {
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val titleView = mAdapter?.getTitleView(context, i)
            if (titleView is View) {
                val view = titleView as View
                var lp: LinearLayout.LayoutParams
                if (mAdjustMode) {
                    lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.weight = mAdapter?.getTitleWeight(context, i) ?: 0f
                } else {
                    lp = LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT
                    )
                }
                mTitleContainer.addView(view, lp)
            }
            i++
        }
        mAdapter?.let {
            pagerIndicator = it.getIndicator(context)
            if (pagerIndicator is View) {
                val lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                mIndicatorContainer.addView(pagerIndicator as View, lp)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mAdapter?.let {
            preparePositionData()
            pagerIndicator?.onPositionDataProvide(mPositionDataList)
            if (mReselectWhenLayout && mNavigatorHelper.scrollState == SCROLL_STATE_IDLE) {
                onPageSelected(mNavigatorHelper.currentIndex)
                onPageScrolled(mNavigatorHelper.currentIndex, 0.0f, 0)
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private fun preparePositionData() {
        mPositionDataList.clear()
        var i = 0
        val j = mNavigatorHelper.totalCount
        while (i < j) {
            val data = PositionData()
            val title = mTitleContainer.getChildAt(i)
            title?.let {
                data.apply {
                    mLeft = title.left
                    mTop = title.top
                    mRight = title.right
                    mBottom = title.bottom
                }
                if (title is IPagerTitleView) {
                    val view = title as IPagerTitleView
                    data.apply {
                        mContentLeft = view.contentLeft
                        mContentTop = view.contentTop
                        mContentRight = view.contentRight
                        mContentBottom = view.contentBottom
                    }
                } else {
                    data.apply {
                        mContentLeft = data.mLeft
                        mContentTop = data.mTop
                        mContentRight = data.mRight
                        mContentBottom = data.mBottom
                    }
                }
            }
            mPositionDataList.add(data)
            i++
        }
    }

    fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mAdapter?.let {
            mNavigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels)
            pagerIndicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)

            // 手指跟随滚动
            if (mPositionDataList.size > 0 && position >= 0 && position < mPositionDataList.size) {
                if (mFollowTouch) {
                    val currentPosition = min(mPositionDataList.size - 1, position)
                    val nextPosition = min(mPositionDataList.size - 1, position + 1)
                    val current = mPositionDataList[currentPosition]
                    val next = mPositionDataList[nextPosition]
                    val scrollTo = current.horizontalCenter() - mScrollView.width * mScrollPivotX
                    val nextScrollTo = next.horizontalCenter() - mScrollView.width * mScrollPivotX
                    mScrollView.scrollTo(
                        (scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(),
                        0
                    )
                } else if (!isEnablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    fun onPageSelected(position: Int) {
        mAdapter?.let {
            mNavigatorHelper.onPageSelected(position)
            pagerIndicator?.onPageSelected(position)
        }
    }

    fun onPageScrollStateChanged(state: Int) {
        mAdapter?.let {
            mNavigatorHelper.onPageScrollStateChanged(state)
            pagerIndicator?.onPageScrollStateChanged(state)
        }
    }

    fun onAttachToMagicIndicator() {
        // 将初始化延迟到这里
        initView()
    }

    fun onDetachFromMagicIndicator() {

    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val title = mTitleContainer.getChildAt(index)
        if (title is IPagerTitleView) {
            (title as IPagerTitleView).onEnter(index, totalCount, enterPercent, leftToRight)
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val title = mTitleContainer.getChildAt(index)
        if (title is IPagerTitleView) {
            (title as IPagerTitleView).onLeave(index, totalCount, leavePercent, leftToRight)
        }
    }

    override fun onSelected(index: Int, totalCount: Int) {
        val title = mTitleContainer.getChildAt(index)
        if (title is IPagerTitleView) {
            (title as IPagerTitleView).onSelected(index, totalCount)
        }
        if (!mAdjustMode && !mFollowTouch && mPositionDataList.size > 0) {
            val currentIndex = min(mPositionDataList.size - 1, index)
            val current = mPositionDataList[currentIndex]
            if (isEnablePivotScroll) {
                val scrollTo = current.horizontalCenter() - mScrollView.width * mScrollPivotX
                if (mSmoothScroll) {
                    mScrollView.smoothScrollTo(scrollTo.toInt(), 0)
                } else {
                    mScrollView.scrollTo(scrollTo.toInt(), 0)
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (mScrollView.scrollX > current.mLeft) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mLeft, 0)
                    } else {
                        mScrollView.scrollTo(current.mLeft, 0)
                    }
                } else if (mScrollView.scrollX + width < current.mRight) {
                    if (mSmoothScroll) {
                        mScrollView.smoothScrollTo(current.mRight - width, 0)
                    } else {
                        mScrollView.scrollTo(current.mRight - width, 0)
                    }
                }
            }
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        val v = mTitleContainer.getChildAt(index)
        if (v is IPagerTitleView) {
            (v as IPagerTitleView).onDeselected(index, totalCount)
        }
    }

}