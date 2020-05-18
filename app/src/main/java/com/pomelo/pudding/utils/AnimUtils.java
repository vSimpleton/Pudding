package com.pomelo.pudding.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.FloatRange;

import com.pomelo.pudding.R;

/**
 * Created by Sherry on 2019/9/30
 * 与动画相关的方法
 */

public class AnimUtils {

    /**
     * 实现抖动的动画
     * 使用时只需调用：需要抖动的控件.startAnimation(anim);
     */
    public static Animation getShakeAnim(Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_shake);
        return animation;
    }

    /**
     * 旋转动画（持续时间可自行设置）
     */
    public static RotateAnimation doRotateAnimation(int dur) {
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(dur); //设置动画持续周期
        rotate.setRepeatCount(Animation.INFINITE); //设置重复次数
        rotate.setFillAfter(false); //动画执行完后是否停留在执行完的状态
        return rotate;
    }

    /**
     * 实现透明度变化的动画
     */
    public static AlphaAnimation getAlphaAnimation(float start, float end, int dur) {
        AlphaAnimation alpha = new AlphaAnimation(start, end);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(dur);//设置动画持续周期
        alpha.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        return alpha;
    }

    /**
     * 控件的点击效果（透明+缩小）---适用于按钮
     * @param rate
     * @return
     */
    public static View.OnTouchListener getTouchBackListener(@FloatRange(from = 0f, to = 1.0f) final float rate) {

        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        AnimatorSet set = new AnimatorSet();
                        Animator animatorX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, rate);
                        Animator animatorY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, rate);
                        Animator animatorAlpha = ObjectAnimator.ofFloat(v, "alpha", 1.0f, rate * 2 / 3f);
                        set.play(animatorX).with(animatorY).with(animatorAlpha);
                        set.setDuration(100);
                        set.start();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP: {

                        AnimatorSet set = new AnimatorSet();
                        Animator animatorX = ObjectAnimator.ofFloat(v, "scaleX", v.getScaleX(), 1.0f);
                        Animator animatorY = ObjectAnimator.ofFloat(v, "scaleY", v.getScaleY(), 1.0f);
                        Animator animatorAlpha = ObjectAnimator.ofFloat(v, "alpha", v.getAlpha(), 1.0f);
                        set.play(animatorX).with(animatorY).with(animatorAlpha);
                        set.setDuration(200);
                        set.start();

                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        };
        return mTouchListener;
    }

    /**
     * 仅改变透明度---适用于item的点击效果
     * @return
     */
    public static View.OnTouchListener getAlphaTouchListener() {
        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.setAlpha(0.5f);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP: {
                        v.setAlpha(1f);
                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        };
        return mTouchListener;
    }

    public static View.OnTouchListener getScaleTouchListener() {
        return getScaleTouchListener(0.9f);
    }

    /**
     * 控件的点击效果（仅缩小）
     * @param rate
     * @return
     */
    public static View.OnTouchListener getScaleTouchListener(@FloatRange(from = 0f, to = 1.0f) final float rate) {

        View.OnTouchListener mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        AnimatorSet set = new AnimatorSet();
                        Animator animatorX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, rate);
                        Animator animatorY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, rate);
                        set.play(animatorX).with(animatorY);
                        set.setDuration(100);
                        set.start();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_UP: {

                        AnimatorSet set = new AnimatorSet();
                        Animator animatorX = ObjectAnimator.ofFloat(v, "scaleX", v.getScaleX(), 1.0f);
                        Animator animatorY = ObjectAnimator.ofFloat(v, "scaleY", v.getScaleY(), 1.0f);
                        set.play(animatorX).with(animatorY);
                        set.setDuration(200);
                        set.start();

                        break;
                    }
                    default:
                        break;
                }
                return false;
            }
        };
        return mTouchListener;
    }


}
