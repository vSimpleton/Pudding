package com.pomelo.pudding.view.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.FloatRange;

import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.Utils;

/**
 * Created by Sherry on 2019/11/1
 */

public class CustomPopupWindow implements OnDialogDismissListener {

    private Context mContext;
    private PopupWindow mWindow;
    private static boolean IsShow;
    private float bgk_color_depth = 0.4f;

    public CustomPopupWindow(Context context) {
        mContext = context;
    }

    public void dismiss() {
//        GradientDrawable tagBg = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.shape_dialog_confirm_bgk);
//        tagBg.setColor(mContext.getResources().getColor(R.color.social_app_main_color));
        if (mWindow != null && !checkActivity()) {
            mWindow.dismiss();
        }
    }

    public void show(View baseLayout, View contentLayout) {
        show(baseLayout, contentLayout, R.style.popwin_anim_style);
    }

    public void show(final View baseLayout, View contentLayout, int aniStyle) {
        if (IsShow || baseLayout == null || contentLayout == null || checkActivity()) return;
        IsShow = true;
        mWindow = new PopupWindow(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mWindow.setContentView(contentLayout);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(false);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setAnimationStyle(aniStyle);
        baseLayout.post(new Runnable() {
            @Override
            public void run() {
                //这样做是防止当前activity被销毁重新构建的时候，等待页面完全创建出来才能popup出窗体
                mWindow.showAtLocation(baseLayout, Gravity.BOTTOM, 0, 0);
                doBackgroundAni(true);
            }
        });

        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onDialogDismissListener != null) {
                    onDialogDismissListener.onDismiss();
                }
                IsShow = false;
                doBackgroundAni(false);
            }
        });
        try {
            ((ViewActionCallBack) contentLayout).setOnViewActionCallBack(new OnDialogDismissListener() {
                @Override
                public void onDismiss() {
                    dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(mContext, "内容View需要先实现ViewActionCallBack接口", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkActivity() {
        return mContext == null || !(mContext instanceof Activity) || ((Activity) mContext).isDestroyed();
    }

    private void doBackgroundAni(boolean isIn) {
        ValueAnimator valueAni = null;
        if (isIn) {
            valueAni = ObjectAnimator.ofFloat(1f, bgk_color_depth);
            valueAni.setDuration(300);
        } else {
            valueAni = ObjectAnimator.ofFloat(bgk_color_depth, 1.0f);
            valueAni.setDuration(400);
        }

        valueAni.setInterpolator(new LinearInterpolator());
        valueAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Utils.darkenBackground(value, mContext);
            }
        });
        valueAni.start();
    }

    public void setBgkDepth(@FloatRange(from = 0f, to = 1.0f) float depth) {
        bgk_color_depth = depth;
    }

    @Override
    public void onDismiss() {
        dismiss();
    }

    OnDialogDismissListener onDialogDismissListener;

    public void setOnDismissListener(OnDialogDismissListener listener) {
        onDialogDismissListener = listener;
    }

}
