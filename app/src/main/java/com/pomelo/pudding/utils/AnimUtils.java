package com.pomelo.pudding.utils;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

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

}
