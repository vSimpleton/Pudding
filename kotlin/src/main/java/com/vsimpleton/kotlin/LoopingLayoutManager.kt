package com.vsimpleton.kotlin

import android.content.Context
import android.graphics.PointF
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import java.lang.Math.min
import kotlin.math.abs
import kotlin.math.max

/**
 * NAME: vSimpleton
 * DATE: 2021-06-14
 * DESC: 使RecyclerView无限轮播的LayoutManager
 */
class LoopingLayoutManager : LayoutManager, RecyclerView.SmoothScroller.ScrollVectorProvider {

    /**
     * 用于触发滚动到适配器更改后的更新和方向更改。
     */
    private var layoutRequest = LayoutRequest(anchorIndex = 0)

    /**
     * 需要增加（即不可见）空间来填充视图可见空间
     */
    private var extraLayoutSpace = 0

    /**
     * 获取每个视图的非滚动边缘.
     */
    private lateinit var orientationHelper: OrientationHelper

    val layoutWidth: Int
        get() = width - paddingLeft - paddingRight

    val layoutHeight: Int
        get() = height - paddingTop - paddingBottom

    var topLeftIndex = 0
        private set

    var bottomRightIndex = 0
        private set

    /**
     * 第 0 项布局边缘视图的适配器索引
     */
    val anchorIndex
        get() = getInitialIndex(getMovementDirectionFromAdapterDirection(TOWARDS_LOWER_INDICES))

    /**
     * 布局边缘的视图适配器索引
     */
    val optAnchorIndex
        get() = getInitialIndex(getMovementDirectionFromAdapterDirection(TOWARDS_HIGHER_INDICES))

    /**
     * 当布局管理器需要滚动到位置（通过平滑滚动），它需要一些方法来决定滚动哪个运动方向
     * 此变量存储该方法。
     */
    var smoothScrollDirectionDecider: (Int, LoopingLayoutManager, Int) -> Int = ::defaultDecider

    /**
     * 设置是否可以水平滑动（当且仅当设置为false的时候才会执行）
     */
    var canScrollHorizontally = true

    @JvmOverloads
    constructor(context: Context, orientation: Int = VERTICAL, reverseLayout: Boolean = false) {
        this.orientation = orientation
        this.reverseLayout = reverseLayout
    }

    var orientation: Int = 0
        set(orientation) {
            require(orientation == HORIZONTAL || orientation == VERTICAL) {
                "invalid orientation:$orientation"
            }
            if (orientation != this.orientation) {
                orientationHelper = OrientationHelper.createOrientationHelper(this, orientation)
                assertNotInLayoutOrScroll(null)
                field = orientation
                requestLayout()
                return
            }
            if (!::orientationHelper.isInitialized) {
                orientationHelper = OrientationHelper.createOrientationHelper(this, orientation)
            }
        }

    var reverseLayout = false
        set(reverseLayout) {
            if (reverseLayout == this.reverseLayout) {
                return
            }
            assertNotInLayoutOrScroll(null)
            field = reverseLayout
            requestLayout()
        }

    val isLayoutRTL: Boolean
        get() = layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val direction = getMovementDirectionFromAdapterDirection(TOWARDS_LOWER_INDICES)
        return if (childCount == 0) {
            null
        } else {
            LayoutRequest(getInitialIndex(direction), getInitialItem(direction).hiddenSize)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is LayoutRequest) {
            layoutRequest = state
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        layoutRequest.initialize(this, state)

        detachAndScrapAttachedViews(recycler)

        val movementDir = getMovementDirectionFromAdapterDirection(-layoutRequest.adapterDirection)
        var prevItem: ListItem? = null
        val size = if (orientation == HORIZONTAL) layoutWidth else layoutHeight
        var sizeFilled = 0
        var index = min(layoutRequest.anchorIndex, state.itemCount - 1)
        while (sizeFilled < size) {
            val view = createViewForIndex(index, movementDir, recycler)
            val item = getItemForView(movementDir, view)
            var layoutRect = getNonScrollingEdges(view)
            layoutRect = prevItem?.getPositionOfItemFollowingSelf(item, layoutRect)
                    ?: item.getPositionOfSelfAsFirst(layoutRect, layoutRequest.scrollOffset)
            layoutDecorated(view, layoutRect.left, layoutRect.top,
                    layoutRect.right, layoutRect.bottom)

            index = stepIndex(index, movementDir, state, false)
            sizeFilled += item.size
            prevItem = item
        }

        if (movementDir == TOWARDS_TOP_LEFT) {
            bottomRightIndex = layoutRequest.anchorIndex
            topLeftIndex = stepIndex(index, -movementDir, state, false)
        } else {
            topLeftIndex = layoutRequest.anchorIndex
            bottomRightIndex = stepIndex(index, -movementDir, state, false)
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        layoutRequest.finishProcessing();
    }

    override fun canScrollVertically(): Boolean {
        return orientation == VERTICAL
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollBy(dy, recycler, state)
    }

    override fun canScrollHorizontally(): Boolean {
        return if (canScrollHorizontally) {
            orientation == HORIZONTAL
        } else {
            canScrollHorizontally
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return scrollBy(dx, recycler, state)
    }

    private fun scrollBy(delta: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || delta == 0) {
            return 0
        }

        val movementDir = Integer.signum(delta)
        scrapNonVisibleViews(recycler)
        val absDelta = abs(delta)
        var amountScrolled = 0
        var index = getInitialIndex(movementDir)
        var selectedItem = getInitialItem(movementDir)

        while (amountScrolled < absDelta) {
            val hiddenSize = selectedItem.hiddenSize
            // Scroll just enough to complete the scroll, or bring the view fully into view.
            val amountToScroll = hiddenSize.coerceAtMost(absDelta - amountScrolled)
            amountScrolled += amountToScroll
            offsetChildren(amountToScroll * -movementDir)

            if (amountScrolled < absDelta) {
                index = stepIndex(index, movementDir, state)
                val newView = createViewForIndex(index, movementDir, recycler)
                val newItem = getItemForView(movementDir, newView)
                var layoutRect = getNonScrollingEdges(newView)
                layoutRect = selectedItem.getPositionOfItemFollowingSelf(newItem, layoutRect)
                layoutDecorated(newView, layoutRect.left, layoutRect.top,
                        layoutRect.right, layoutRect.bottom)
                selectedItem = newItem
            }

        }

        // The amount of extra (i.e not visible) space currently covered by views.
        var viewSpace = selectedItem.hiddenSize
        while (viewSpace < extraLayoutSpace) {
            // We don't want the topLeftIndex or bottomRightIndex to reflect non-visible views.
            index = stepIndex(index, movementDir, state, updateIndex = false)
            val newView = createViewForIndex(index, movementDir, recycler)
            val newItem = getItemForView(movementDir, newView)
            var layoutRect = getNonScrollingEdges(newView)
            layoutRect = selectedItem.getPositionOfItemFollowingSelf(newItem, layoutRect)
            layoutDecorated(newView, layoutRect.left, layoutRect.top,
                    layoutRect.right, layoutRect.bottom)
            selectedItem = newItem
            viewSpace += selectedItem.size
        }

        recycleViews(movementDir, recycler, state)
        return amountScrolled * movementDir
    }

    /**
     * 返回填充视图静态边缘位置的条件。
     * 即水平模式下左右，垂直模式上下。
     */
    private fun getNonScrollingEdges(view: View): Rect {
        val layoutRect = Rect()
        val isVertical = orientation == VERTICAL
        when {
            isVertical && isLayoutRTL -> {
                layoutRect.right = width - paddingRight
                layoutRect.left = layoutRect.right -
                        orientationHelper.getDecoratedMeasurementInOther(view)
            }
            isVertical && !isLayoutRTL -> {
                layoutRect.left = paddingLeft
                layoutRect.right = layoutRect.left +
                        orientationHelper.getDecoratedMeasurementInOther(view)
            }
            else -> {  // Horizontal
                layoutRect.top = paddingTop
                layoutRect.bottom = layoutRect.top +
                        orientationHelper.getDecoratedMeasurementInOther(view)
            }
        }
        return layoutRect
    }

    /**
     * 创建、测量并插入给定 adapterIndex 的回收器视图。然后返回视图，以便正确定位视图。
     * @param movementDir 当前视图正在滚动的方向
     */
    private fun createViewForIndex(adapterIndex: Int, movementDir: Int, recycler: RecyclerView.Recycler): View {
        val newView = recycler.getViewForPosition(adapterIndex)
        if (movementDir == TOWARDS_LOWER_INDICES) {
            addView(newView, 0)
        } else {
            addView(newView)
        }
        measureChildWithMargins(newView, 0, 0)
        return newView
    }

    /**
     * 按给定 amount 移动所有子视图
     * 根据方向确定它们是水平移动还是垂直移动。
     */
    private fun offsetChildren(amount: Int) {
        if (orientation == HORIZONTAL) {
            offsetChildrenHorizontal(amount)
        } else {
            offsetChildrenVertical(amount)
        }
    }

    /**
     * 返回最接近新视图显示位置的视图的适配器索引
     */
    private fun getInitialIndex(movementDir: Int): Int {
        return if (movementDir == TOWARDS_TOP_LEFT) {
            topLeftIndex
        } else {
            bottomRightIndex
        }
    }

    /**
     * 返回最接近新视图显示位置的视图
     */
    private fun getInitialItem(movementDir: Int): ListItem {
        val initialView = if (movementDir == TOWARDS_LOWER_INDICES) {
            getChildAt(0)
        } else {
            getChildAt(childCount - 1)
        }
        return getItemForView(movementDir, initialView!!)
    }

    /**
     * 根据列表传入的 index 增量/减量并返回所提供的索引
     */
    private fun stepIndex(
            index: Int,
            movementDir: Int,
            state: RecyclerView.State,
            updateIndex: Boolean = true
    ): Int {
        val adapterDirection = getAdapterDirectionFromMovementDirection(movementDir)
        val count = state.itemCount

        val isTowardsTopLeft = movementDir == TOWARDS_TOP_LEFT
        val isTowardsBottomRight = movementDir == TOWARDS_BOTTOM_RIGHT
        val isTowardsHigherIndices = adapterDirection == TOWARDS_HIGHER_INDICES
        val isTowardsLowerIndices = adapterDirection == TOWARDS_LOWER_INDICES

        val newIndex: Int
        when {
            isTowardsTopLeft && isTowardsHigherIndices -> {
                newIndex = index.loopedIncrement(count)
                if (updateIndex) topLeftIndex = newIndex
            }
            isTowardsTopLeft && isTowardsLowerIndices -> {
                newIndex = index.loopedDecrement(count)
                if (updateIndex) topLeftIndex = newIndex
            }
            isTowardsBottomRight && isTowardsHigherIndices -> {
                newIndex = index.loopedIncrement(count)
                if (updateIndex) bottomRightIndex = newIndex
            }
            isTowardsBottomRight && isTowardsLowerIndices -> {
                newIndex = index.loopedDecrement(count)
                if (updateIndex) bottomRightIndex = newIndex
            }
            else -> throw IllegalStateException("Invalid move & adapter direction combination.")
        }
        return newIndex
    }

    private fun getItemForView(movementDir: Int, view: View): ListItem {
        val isVertical = orientation == VERTICAL
        val isHorizontal = !isVertical
        val isTowardsTopLeft = movementDir == TOWARDS_TOP_LEFT
        val isTowardsBottomRight = !isTowardsTopLeft

        return when {
            isVertical && isTowardsTopLeft -> LeadingBottomListItem(view)
            isVertical && isTowardsBottomRight -> LeadingTopListItem(view)
            isHorizontal && isTowardsTopLeft -> LeadingRightListItem(view)
            isHorizontal && isTowardsBottomRight -> LeadingLeftListItem(view)
            else -> throw IllegalStateException("Invalid movement state.")
        }
    }

    /**
     * 将当前不可见的view放到scrap heap
     * scrollBy用于确保我们在添加新视图之前仅处理可见视图。
     */
    private fun scrapNonVisibleViews(recycler: RecyclerView.Recycler) {
        for (i in (childCount - 1) downTo 0) {
            val view = getChildAt(i) ?: continue
            if (!viewIsVisible(view)) {
                detachAndScrapView(view, recycler)
            }
        }
    }

    /**
     * 回收刚刚完成的不再可见的视图。
     */
    private fun recycleViews(movementDir: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val initialIndex = getInitialIndex(movementDir)
        var distanceFromStart = -1
        var foundVisibleView = false
        val viewsToRemove = mutableListOf<Int>()

        val range = if (movementDir == TOWARDS_TOP_LEFT) {
            0 until childCount
        } else {
            childCount - 1 downTo 0
        }

        for (i in range) {
            val view = getChildAt(i)!!
            if (viewIsVisible(view)) {
                if (!foundVisibleView) {
                    foundVisibleView = true
                }
                distanceFromStart++
            } else if (foundVisibleView) {
                viewsToRemove.add(i)
            }
        }

        viewsToRemove.sortedDescending().forEach { i ->
            removeAndRecycleViewAt(i, recycler)
        }

        if (viewsToRemove.count() == 0) {
            return
        }

        val adapterDirection = getAdapterDirectionFromMovementDirection(movementDir * -1)
        val changeInPosition = adapterDirection * distanceFromStart
        val count = state.itemCount
        if (movementDir == TOWARDS_TOP_LEFT) {
            bottomRightIndex = initialIndex.loop(changeInPosition, count)
        } else {
            topLeftIndex = initialIndex.loop(changeInPosition, count)
        }
    }

    private fun viewIsVisible(view: View): Boolean {
        return if (orientation == HORIZONTAL) {
            getDecoratedRight(view) > paddingLeft && getDecoratedLeft(view) < width - paddingRight
        } else {
            getDecoratedBottom(view) > paddingTop && getDecoratedTop(view) < height - paddingBottom
        }
    }

    private fun viewIsFullyVisible(view: View): Boolean {
        return if (orientation == HORIZONTAL) {
            getDecoratedLeft(view) >= paddingLeft && getDecoratedRight(view) <= width - paddingRight
        } else {
            getDecoratedTop(view) >= paddingTop && getDecoratedBottom(view) <= height - paddingBottom
        }
    }

    fun convertMovementDirToAdapterDir(movementDir: Int): Int {
        return getMovementDirectionFromAdapterDirection(movementDir)
    }

    private fun getAdapterDirectionFromMovementDirection(movementDir: Int): Int {
        val isVertical = orientation == VERTICAL
        val isHorizontal = !isVertical
        val isTowardsTopLeft = movementDir == TOWARDS_TOP_LEFT
        val isTowardsBottomRight = !isTowardsTopLeft
        val isRTL = isLayoutRTL
        val isLTR = !isRTL
        val isReversed = reverseLayout
        val isNotReversed = !isReversed

        return when {
            isVertical && isTowardsTopLeft && isNotReversed -> TOWARDS_LOWER_INDICES
            isVertical && isTowardsTopLeft && isReversed -> TOWARDS_HIGHER_INDICES
            isVertical && isTowardsBottomRight && isNotReversed -> TOWARDS_HIGHER_INDICES
            isVertical && isTowardsBottomRight && isReversed -> TOWARDS_LOWER_INDICES
            isHorizontal && isTowardsTopLeft && isLTR && isNotReversed -> TOWARDS_LOWER_INDICES
            isHorizontal && isTowardsTopLeft && isLTR && isReversed -> TOWARDS_HIGHER_INDICES
            isHorizontal && isTowardsTopLeft && isRTL && isNotReversed -> TOWARDS_HIGHER_INDICES
            isHorizontal && isTowardsTopLeft && isRTL && isReversed -> TOWARDS_LOWER_INDICES
            isHorizontal && isTowardsBottomRight && isLTR && isNotReversed -> TOWARDS_HIGHER_INDICES
            isHorizontal && isTowardsBottomRight && isLTR && isReversed -> TOWARDS_LOWER_INDICES
            isHorizontal && isTowardsBottomRight && isRTL && isNotReversed -> TOWARDS_LOWER_INDICES
            isHorizontal && isTowardsBottomRight && isRTL && isReversed -> TOWARDS_HIGHER_INDICES
            else -> throw IllegalStateException("Invalid movement state.")
        }
    }

    fun convertAdapterDirToMovementDir(adapterDir: Int): Int {
        return getMovementDirectionFromAdapterDirection(adapterDir)
    }

    private fun getMovementDirectionFromAdapterDirection(movementDir: Int): Int {
        val isVertical = orientation == VERTICAL
        val isHorizontal = !isVertical
        val isTowardsHigher = movementDir == TOWARDS_HIGHER_INDICES
        val isTowardsLower = !isTowardsHigher
        val isRTL = isLayoutRTL
        val isLTR = !isRTL
        val isReversed = reverseLayout
        val isNotReversed = !isReversed

        return when {
            isVertical && isTowardsHigher && isNotReversed -> TOWARDS_BOTTOM_RIGHT
            isVertical && isTowardsHigher && isReversed -> TOWARDS_TOP_LEFT
            isVertical && isTowardsLower && isNotReversed -> TOWARDS_TOP_LEFT
            isVertical && isTowardsLower && isReversed -> TOWARDS_BOTTOM_RIGHT
            isHorizontal && isTowardsHigher && isLTR && isNotReversed -> TOWARDS_BOTTOM_RIGHT
            isHorizontal && isTowardsHigher && isLTR && isReversed -> TOWARDS_TOP_LEFT
            isHorizontal && isTowardsHigher && isRTL && isNotReversed -> TOWARDS_TOP_LEFT
            isHorizontal && isTowardsHigher && isRTL && isReversed -> TOWARDS_BOTTOM_RIGHT
            isHorizontal && isTowardsLower && isLTR && isNotReversed -> TOWARDS_TOP_LEFT
            isHorizontal && isTowardsLower && isLTR && isReversed -> TOWARDS_BOTTOM_RIGHT
            isHorizontal && isTowardsLower && isRTL && isNotReversed -> TOWARDS_BOTTOM_RIGHT
            isHorizontal && isTowardsLower && isRTL && isReversed -> TOWARDS_TOP_LEFT
            else -> throw IllegalStateException("Invalid adapter state.")
        }
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset()
    }

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange()
    }

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent()
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset()
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange()
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent()
    }

    /**
     * 计算视图顶部的偏移，为了支持滚动条所需记录的，但也需要支持 TalkBack 可访问性手势
     * 此功能返回常数，以确保布局始终可滚动
     */
    private fun computeScrollOffset(): Int {
        if (childCount == 0) {
            return 0
        }
        return SCROLL_OFFSET
    }

    private fun computeScrollRange(): Int {
        if (childCount == 0) {
            return 0
        }
        return SCROLL_RANGE
    }

    private fun computeScrollExtent(): Int {
        return 0
    }

    override fun onInitializeAccessibilityEvent(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            event: AccessibilityEvent
    ) {
        super.onInitializeAccessibilityEvent(recycler, state, event)
        if (childCount > 0) {
            event.fromIndex = topLeftIndex
            event.toIndex = bottomRightIndex
        }
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
        return computeScrollVectorForPosition(targetPosition, itemCount)
    }

    private fun computeScrollVectorForPosition(targetPosition: Int, count: Int): PointF {
        val movementDir = smoothScrollDirectionDecider(targetPosition, this, count)
        return if (orientation == HORIZONTAL) {
            PointF(movementDir.toFloat(), 0F)
        } else {
            PointF(0F, movementDir.toFloat())
        }
    }

    override fun findViewByPosition(adapterIndex: Int): View? {
        return findViewByPosition(adapterIndex, ::defaultPicker)
    }

    fun findViewByPosition(
            adapterIndex: Int,
            strategy: (targetIndex: Int, layoutManager: LoopingLayoutManager) -> View?
    ): View? {
        return strategy(adapterIndex, this)
    }

    /**
     * 返回与给定 adapterIndex 相关的所有布局视图.
     */
    private fun findAllViewsWithPosition(adapterIndex: Int): Iterable<View> {
        val views = mutableListOf<View>()
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view != null && getPosition(view) == adapterIndex) {
                views += view
            }
        }
        return views
    }

    fun findFirstVisibleItemPosition(): Int {
        var lowestIndex = Int.MAX_VALUE;
        for (i in 0 until childCount) {
            val view = getChildAt(i);
            if (view != null && getPosition(view) < lowestIndex && viewIsVisible(view)) {
                lowestIndex = getPosition(view)
            }
        }
        return lowestIndex;
    }

    fun findFirstCompletelyVisibleItemPosition(): Int {
        var lowestIndex = Int.MAX_VALUE;
        for (i in 0 until childCount) {
            val view = getChildAt(i);
            if (view != null && getPosition(view) < lowestIndex && viewIsFullyVisible(view)) {
                lowestIndex = getPosition(view)
            }
        }
        return lowestIndex;
    }

    fun findLastVisibleItemPosition(): Int {
        var highestIndex = 0;
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view != null && getPosition(view) > highestIndex && viewIsVisible(view)) {
                highestIndex = getPosition(view)
            }
        }
        return highestIndex;
    }

    fun findLastCompletelyVisibleItemPosition(): Int {
        var highestIndex = 0;
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view != null && getPosition(view) > highestIndex && viewIsFullyVisible(view)) {
                highestIndex = getPosition(view)
            }
        }
        return highestIndex;
    }

    override fun scrollToPosition(adapterIndex: Int) {
        scrollToPosition(adapterIndex, ::defaultDecider)
    }

    fun scrollToPosition(adapterIndex: Int, strategy: (targetIndex: Int, layoutManager: LoopingLayoutManager, itemCount: Int) -> Int) {
        if (viewWithIndexIsFullyVisible(adapterIndex)) return
        layoutRequest = LayoutRequest(anchorIndex = adapterIndex, scrollStrategy = strategy)
        requestLayout()
    }

    private fun viewWithIndexIsFullyVisible(adapterIndex: Int): Boolean {
        val views = findAllViewsWithPosition(adapterIndex)
        for (view in views) {
            if (viewIsFullyVisible(view)) {
                return true
            }
        }
        return false
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val loopingSmoothScroller = LoopingSmoothScroller(recyclerView.context, state)
        loopingSmoothScroller.targetPosition = position
        startSmoothScroll(loopingSmoothScroller)
    }

    /**
     * 便于在布局管理器上下文中与视图进行交互。
     */
    private abstract inner class ListItem(protected val view: View) {

        /**
         * 隐藏视图的size
         */
        abstract val hiddenSize: Int

        /**
         * 返回第一个进入视图的视图边缘的位置
         */
        abstract val leadingEdge: Int

        /**
         * 上次查看的视图边缘的位置
         */
        abstract val followingEdge: Int

        /**
         * 视图的大小，即水平模式中的宽度、垂直模式的高度
         */
        abstract val size: Int

        abstract fun getPositionOfItemFollowingSelf(item: ListItem, rect: Rect): Rect

        abstract fun getPositionOfSelfAsFirst(rect: Rect, hiddenAmount: Int): Rect
    }

    private inner class LeadingLeftListItem(view: View) : ListItem(view) {

        override val hiddenSize: Int
            get() = (getDecoratedRight(view) - (width - paddingRight)).coerceAtLeast(0)

        override val leadingEdge: Int
            get() = getDecoratedLeft(view)

        override val followingEdge: Int
            get() = getDecoratedRight(view)

        override val size: Int
            get() = getDecoratedMeasuredWidth(view)

        override fun getPositionOfItemFollowingSelf(item: ListItem, rect: Rect): Rect {
            rect.left = followingEdge
            rect.right = rect.left + item.size
            return rect
        }

        override fun getPositionOfSelfAsFirst(rect: Rect, hiddenAmount: Int): Rect {
            rect.left = paddingLeft - hiddenAmount
            rect.right = rect.left + size
            return rect
        }
    }

    private inner class LeadingTopListItem(view: View) : ListItem(view) {

        override val hiddenSize: Int
            get() = (getDecoratedBottom(view) - (height - paddingBottom)).coerceAtLeast(0)

        override val leadingEdge: Int
            get() = getDecoratedTop(view)

        override val followingEdge: Int
            get() = getDecoratedBottom(view)

        override val size: Int
            get() = getDecoratedMeasuredHeight(view)


        override fun getPositionOfItemFollowingSelf(item: ListItem, rect: Rect): Rect {
            rect.top = followingEdge
            rect.bottom = rect.top + item.size
            return rect
        }

        override fun getPositionOfSelfAsFirst(rect: Rect, hiddenAmount: Int): Rect {
            rect.top = paddingTop - hiddenAmount
            rect.bottom = rect.top + size
            return rect
        }
    }

    private inner class LeadingRightListItem(view: View) : ListItem(view) {

        override val hiddenSize: Int
            get() = (paddingLeft - getDecoratedLeft(view)).coerceAtLeast(0)

        override val leadingEdge: Int
            get() = getDecoratedRight(view)

        override val followingEdge: Int
            get() = getDecoratedLeft(view)

        override val size: Int
            get() = getDecoratedMeasuredWidth(view)

        override fun getPositionOfItemFollowingSelf(item: ListItem, rect: Rect): Rect {
            rect.right = followingEdge
            rect.left = rect.right - item.size
            return rect
        }

        override fun getPositionOfSelfAsFirst(rect: Rect, hiddenAmount: Int): Rect {
            rect.right = (width - paddingRight) + hiddenAmount
            rect.left = rect.right - size
            return rect
        }
    }

    private inner class LeadingBottomListItem(view: View) : ListItem(view) {

        override val hiddenSize: Int
            get() = (paddingTop - getDecoratedTop(view)).coerceAtLeast(0)

        override val leadingEdge: Int
            get() = getDecoratedBottom(view)

        override val followingEdge: Int
            get() = getDecoratedTop(view)

        override val size: Int
            get() = getDecoratedMeasuredHeight(view)

        override fun getPositionOfItemFollowingSelf(item: ListItem, rect: Rect): Rect {
            rect.bottom = followingEdge
            rect.top = rect.bottom - item.size
            return rect
        }

        override fun getPositionOfSelfAsFirst(rect: Rect, hiddenAmount: Int): Rect {
            rect.bottom = (height - paddingBottom) + hiddenAmount
            rect.top = rect.bottom - size
            return rect
        }
    }

    /**
     * 自定义SmoothScroller
     */
    private inner class LoopingSmoothScroller(val context: Context, val state: RecyclerView.State) : LinearSmoothScroller(context) {

        public override fun onStart() {
            val rate = calculateSpeedPerPixel(context.resources.displayMetrics)  // MS/Pixel
            val time = 500  // MS.
            (layoutManager as LoopingLayoutManager).extraLayoutSpace = (rate * time).toInt()
        }

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return 0.2F
        }

        /**
         * Tells the LoopingLayoutManager to stop laying out extra views, b/c there's no need
         * to lay out views the user can't see.
         */
        public override fun onStop() {
            (layoutManager as LoopingLayoutManager).extraLayoutSpace = 0
        }

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            val layoutManager = layoutManager
            if (layoutManager is LoopingLayoutManager) {
                return layoutManager.computeScrollVectorForPosition(targetPosition, state.itemCount)
            }
            return null
        }
    }

    private class LayoutRequest() : Parcelable {

        var anchorIndex: Int = RecyclerView.NO_POSITION
            get() {
                if (!hasBeenInitialized) throw Exception("LayoutRequest has not been initialized.")
                return field
            }
            private set

        var scrollOffset: Int = 0
            get() {
                if (!hasBeenInitialized) throw Exception("LayoutRequest has not been initialized.")
                return field
            }
            private set

        var adapterDirection: Int = TOWARDS_LOWER_INDICES
            get() {
                if (!hasBeenInitialized) throw Exception("LayoutRequest has not been initialized.")
                return field
            }
            private set

        private var scrollStrategy: ((Int, LoopingLayoutManager, Int) -> Int)? = null

        private var hasBeenInitialized = false

        constructor(parcel: Parcel) : this() {
            anchorIndex = parcel.readInt()
            scrollOffset = parcel.readInt()
            adapterDirection = parcel.readInt()
        }

        constructor(
                anchorIndex: Int = RecyclerView.NO_POSITION,
                scrollOffset: Int = 0,
                adapterDirection: Int = TOWARDS_LOWER_INDICES,
                scrollStrategy: ((Int, LoopingLayoutManager, Int) -> Int)? = null,
                layoutManager: LoopingLayoutManager? = null,
                state: RecyclerView.State? = null
        ) : this() {
            this.anchorIndex = anchorIndex
            this.scrollOffset = scrollOffset
            this.adapterDirection = adapterDirection
            this.scrollStrategy = scrollStrategy

            if (layoutManager != null && state != null) initialize(layoutManager, state)

            if (!hasBeenInitialized
                    && anchorIndex != RecyclerView.NO_POSITION
                    && scrollStrategy == null) {
                hasBeenInitialized = true
            }
        }

        fun initialize(layoutManager: LoopingLayoutManager, state: RecyclerView.State) {
            if (hasBeenInitialized) return
            hasBeenInitialized = true

            adapterDirection = scrollStrategy?.invoke(anchorIndex, layoutManager, state.itemCount)?.let {
                layoutManager.getAdapterDirectionFromMovementDirection(it)
            } ?: adapterDirection

            if (anchorIndex == RecyclerView.NO_POSITION) {
                if (layoutManager.childCount == 0) {
                    anchorIndex = 0
                } else {
                    val direction = layoutManager.getMovementDirectionFromAdapterDirection(adapterDirection)
                    anchorIndex = layoutManager.getInitialIndex(direction)
                    scrollOffset = layoutManager.getInitialItem(direction).hiddenSize
                }
            }
        }

        fun finishProcessing() {
            anchorIndex = RecyclerView.NO_POSITION
            scrollOffset = 0
            adapterDirection = TOWARDS_LOWER_INDICES
            scrollStrategy = null
            hasBeenInitialized = false
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(anchorIndex)
            parcel.writeInt(scrollOffset)
            parcel.writeInt(adapterDirection)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LayoutRequest> {
            override fun createFromParcel(parcel: Parcel): LayoutRequest {
                return LayoutRequest(parcel)
            }

            override fun newArray(size: Int): Array<LayoutRequest?> {
                return Array(size) { LayoutRequest() }
            }
        }
    }

    private fun defaultPicker(
            targetAdapterIndex: Int,
            layoutManager: LoopingLayoutManager
    ): View? {
        return childClosestToMiddle(targetAdapterIndex, layoutManager)
    }

    /**
     * 返回与给定 targetAdapterIndex 的视图。如果与给定索引关联的多个视图，则返回最接近锚边的视图。
     * 锚边是最初与指数 0 相关的视图将针对的边缘。例如：在 RTL 水平布局中，锚边为右边缘。
     */
    fun childClosestToAnchorEdge(targetAdapterIndex: Int, layoutManager: LoopingLayoutManager): View? {
        val movementDir = layoutManager.convertAdapterDirToMovementDir(TOWARDS_HIGHER_INDICES)
        val range = if (movementDir == TOWARDS_HIGHER_INDICES) {
            0 until layoutManager.childCount
        } else {
            layoutManager.childCount - 1 downTo 0
        }

        for (i in range) {
            val view = layoutManager.getChildAt(i) ?: break
            if (layoutManager.getPosition(view) == targetAdapterIndex) {
                return view
            }
        }
        return null
    }

    /**
     * 返回与给定 targetAdapterIndex 的视图。如果与给定索引相关的多个视图，则返回中间最接近布局中间的视图。
     */
    private fun childClosestToMiddle(targetAdapterIndex: Int, layoutManager: LoopingLayoutManager): View? {
        var minDistance = Int.MAX_VALUE
        var closestView: View? = null
        val layoutMiddle = if (layoutManager.orientation == LoopingLayoutManager.HORIZONTAL) {
            layoutManager.paddingLeft + (layoutManager.layoutWidth / 2)
        } else {
            layoutManager.paddingTop + (layoutManager.layoutHeight / 2)
        }
        for (i in 0 until layoutManager.childCount) {
            val view = layoutManager.getChildAt(i) ?: return null
            if (layoutManager.getPosition(view) != targetAdapterIndex) {
                continue
            }
            val childMiddle = if (layoutManager.orientation == LoopingLayoutManager.HORIZONTAL) {
                layoutManager.getDecoratedLeft(view) +
                        (layoutManager.getDecoratedMeasuredWidth(view) / 2)
            } else {
                layoutManager.getDecoratedTop(view) +
                        (layoutManager.getDecoratedMeasuredHeight(view) / 2)
            }
            val distance = abs(childMiddle - layoutMiddle)
            if (distance < minDistance) {
                minDistance = distance
                closestView = view
            }
        }
        return closestView
    }

    /**
     * 未提供默认Decider时使用。
     * @return 用于滚动到给定适配器索引的运动方向
     */
    fun defaultDecider(
            adapterIndex: Int,
            layoutManager: LoopingLayoutManager,
            itemCount: Int
    ): Int {
        return estimateShortestRoute(adapterIndex, layoutManager, itemCount)
    }

    fun addViewsAtAnchorEdge(
            adapterIndex: Int,
            layoutManager: LoopingLayoutManager,
            itemCount: Int
    ): Int {
        return layoutManager.convertAdapterDirToMovementDir(LoopingLayoutManager.TOWARDS_LOWER_INDICES)
    }

    fun addViewsAtOptAnchorEdge(
            adapterIndex: Int,
            layoutManager: LoopingLayoutManager,
            itemCount: Int
    ): Int {
        return layoutManager.convertAdapterDirToMovementDir(LoopingLayoutManager.TOWARDS_HIGHER_INDICES)
    }

    fun estimateShortestRoute(
            adapterIndex: Int,
            layoutManager: LoopingLayoutManager,
            itemCount: Int
    ): Int {
        if (layoutManager.topLeftIndex == adapterIndex) {
            return TOWARDS_TOP_LEFT
        } else if (layoutManager.bottomRightIndex == adapterIndex) {
            return TOWARDS_BOTTOM_RIGHT
        }

        val (topLeftInLoopDist, topLeftOverSeamDist) = calculateDistances(
                adapterIndex, layoutManager.topLeftIndex, itemCount)
        val topLeftTargetSmaller = adapterIndex < layoutManager.topLeftIndex

        val (bottomRightInLoopDist, bottomRightOverSeamDist) = calculateDistances(
                adapterIndex, layoutManager.bottomRightIndex, itemCount)
        val bottomRightTargetSmaller = adapterIndex < layoutManager.bottomRightIndex

        val minDist = arrayOf(topLeftInLoopDist, topLeftOverSeamDist,
                bottomRightInLoopDist, bottomRightOverSeamDist).min()
        val minDistIsInLoop = when (minDist) {
            topLeftInLoopDist, bottomRightInLoopDist -> true
            topLeftOverSeamDist, bottomRightOverSeamDist -> false
            else -> throw IllegalStateException()  // Should never happen.
        }
        val minDistIsOverSeam = !minDistIsInLoop
        val targetIsSmaller = when (minDist) {
            topLeftInLoopDist, topLeftOverSeamDist -> topLeftTargetSmaller
            bottomRightInLoopDist, bottomRightOverSeamDist -> bottomRightTargetSmaller
            else -> throw IllegalStateException()  // Should never happen.
        }
        val targetIsLarger = !targetIsSmaller

        val adapterDir = when {
            targetIsSmaller && minDistIsInLoop -> TOWARDS_LOWER_INDICES
            targetIsSmaller && minDistIsOverSeam -> TOWARDS_HIGHER_INDICES
            targetIsLarger && minDistIsInLoop -> TOWARDS_HIGHER_INDICES
            targetIsLarger && minDistIsOverSeam -> TOWARDS_LOWER_INDICES
            else -> throw IllegalStateException()  // Should never happen.
        }
        return layoutManager.convertAdapterDirToMovementDir(adapterDir)
    }

    internal fun calculateDistances(adapterIndex: Int, anchorIndex: Int, count: Int): Pair<Int, Int> {
        val inLoopDist = abs(adapterIndex - anchorIndex)
        val smallerIndex = kotlin.math.min(adapterIndex, anchorIndex)
        val largerIndex = max(adapterIndex, anchorIndex)
        val overSeamDist = (count - largerIndex) + smallerIndex
        return Pair(inLoopDist, overSeamDist)
    }


    companion object {

        const val HORIZONTAL = OrientationHelper.HORIZONTAL
        const val VERTICAL = OrientationHelper.VERTICAL

        const val TOWARDS_TOP_LEFT = -1

        const val TOWARDS_BOTTOM_RIGHT = 1

        const val TOWARDS_LOWER_INDICES = -1

        const val TOWARDS_HIGHER_INDICES = 1

        private const val SCROLL_OFFSET = 100

        private const val SCROLL_RANGE = 200
    }

}

internal fun Int.loop(amount: Int, count: Int): Int {
    var newVal = this + amount;
    newVal %= count;
    if (newVal < 0)
        newVal += count
    return newVal
}

internal fun Int.loopedIncrement(count: Int): Int {
    return this.loop(1, count)
}

internal fun Int.loopedDecrement(count: Int): Int {
    return this.loop(-1, count)
}
