package com.miya.magicindicator.abs

import android.content.Context
import android.database.DataSetObservable
import android.database.DataSetObserver

/**
 * NAME: masimin
 * DATE: 2021-06-15
 * DESC: 适配器，可切换不同的指示器样式
 */
abstract class CommonNavigatorAdapter {

    private val mDataSetObservable by lazy { DataSetObservable() }

    abstract val count: Int
    abstract fun getTitleView(context: Context?, index: Int): IPagerTitleView?
    abstract fun getIndicator(context: Context?): IPagerIndicator?

    fun getTitleWeight(context: Context?, index: Int): Float {
        return 1f
    }

    fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    fun notifyDataSetChanged() {
        mDataSetObservable.notifyChanged()
    }

    fun notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated()
    }
}