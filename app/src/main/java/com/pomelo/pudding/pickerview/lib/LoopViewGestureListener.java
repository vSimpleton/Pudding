package com.pomelo.pudding.pickerview.lib;

import android.view.GestureDetector;
import android.view.MotionEvent;

final class LoopViewGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final WheelView loopView;

    LoopViewGestureListener(WheelView loopView) {
        this.loopView = loopView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
