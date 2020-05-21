package com.pomelo.pudding.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.ScreenUtils;
import com.pomelo.pudding.utils.Utils;
import com.pomelo.pudding.view.utils.OnDialogDismissListener;

/**
 * Created by Sherry on 2019/11/27
 * 封装底部弹框
 */

public class BottomPopupWindow {

    private Context mContext;
    private RelativeLayout contentLayout;
    private LinearLayout mCustomBtnContainer;
    private LayoutInflater mInflater;
    private PopupWindow mWindow;
    private TextView mCancel;
    private static boolean isShow = false;
    private float bgk_color_depth = 0.6f;

    public BottomPopupWindow(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        contentLayout = (RelativeLayout) mInflater.inflate(R.layout.bottom_dialog_page, null);
        contentLayout.setBackgroundColor(0x00000000);
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCustomBtnContainer = contentLayout.findViewById(R.id.bottom_custom_container);
        mCancel = contentLayout.findViewById(R.id.bottom_dialog_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * @param text 底部最后一行显示的文字
     */
    public void setBottomBtnText(String text) {
        mCancel.setText(text != null && text.length() > 0 ? text : "取消");
    }

    /**
     * @param name     设置该button的文本
     * @param isRed    设置文本是否为红色
     * @param listener 点击监听
     */
    public void addCustomBtn(String name, boolean isRed, View.OnClickListener listener) {
        addCustomBtn(name, isRed, false, listener);
    }

    public void addCustomBtn(String name, boolean isRed, boolean isCheck, View.OnClickListener l) {
        if (mCustomBtnContainer != null && !TextUtils.isEmpty(name)) {
            TextView btn = new TextView(mContext);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            if (isRed) {
                btn.setTextColor(mContext.getResources().getColorStateList(R.color.popup_red_text_selector));
            } else {
                if (isCheck) {
                    btn.getPaint().setFakeBoldText(true);
                    btn.setTextColor(mContext.getResources().getColorStateList(R.color.social_app_main_color));
                } else {
                    btn.setTextColor(mContext.getResources().getColorStateList(R.color.popup_black_text_selector));
                }
            }
            btn.setGravity(Gravity.CENTER);
            btn.setText(name);
            btn.setBackgroundResource(R.drawable.popup_item_bgk_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(mContext, 55));
            if (mCustomBtnContainer.getChildCount() > 0) {
                params.topMargin = 1;
            }
            mCustomBtnContainer.addView(btn, mCustomBtnContainer.getChildCount(), params);
            btn.setOnClickListener(l);
        }
    }

    /**
     * 没有特殊说明的标题样式
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        setTitle(title, 15, 110);
    }

    /**
     * @param title    标题
     * @param textSize 文本大小dp
     * @param height   标题行高度
     */
    public void setTitle(String title, int textSize, int height) {
        if (mCustomBtnContainer != null && !TextUtils.isEmpty(title)) {
            TextView btn = new TextView(mContext);
            btn.setLineSpacing(Utils.getRealPixel(10), 1.0f);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize - 1);
            btn.setTextColor(0xff343434);
            btn.setGravity(Gravity.CENTER);
            btn.setPadding(Utils.getRealPixel(28), 0, Utils.getRealPixel(28), 0);
            btn.setText(title);
            btn.setBackgroundColor(0xffffffff);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getRealPixel(height));
            if (mCustomBtnContainer.getChildCount() > 0) {
                params.topMargin = 1;
            }
            mCustomBtnContainer.addView(btn, 0, params);
            btn.setOnClickListener(null);
        }
    }

    /**
     * 有特殊样式的标题View
     *
     * @param view 添加xml布局
     */
    public void addTitleView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (mCustomBtnContainer.getChildCount() > 0) {
            params.topMargin = 1;
        }
        mCustomBtnContainer.addView(view, 0, params);
        view.setOnClickListener(null);
    }

    /**
     * @param view     添加xml布局
     * @param listener 点击监听
     */
    public void addCustomView(View view, View.OnClickListener listener) {
        if (mCustomBtnContainer != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getRealPixel(110));
            if (mCustomBtnContainer.getChildCount() > 0) {
                params.topMargin = 1;
            }
            mCustomBtnContainer.addView(view, mCustomBtnContainer.getChildCount(), params);
            view.setOnClickListener(listener);
        }
    }

    public void dismiss() {
        if (mWindow != null && !checkActivity()) {
            isShow = false;
            mWindow.dismiss();
        }
    }

    /***
     * @param baseLayout 相对的父控件,传入当前的page即可，如XXX.this
     */
    public void show(final View baseLayout) {
        if (isShow || baseLayout == null || contentLayout == null || checkActivity()) return;
        isShow = true;
        mWindow = new PopupWindow(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mWindow.setContentView(contentLayout);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setBackgroundDrawable(new ColorDrawable());
        mWindow.setAnimationStyle(R.style.popwin_anim_style);
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
                if (mDismissListener != null) {
                    mDismissListener.onDismiss();
                }
                isShow = false;
//                doBackgroundAni(false);
            }
        });
    }

    private boolean checkActivity() {
        return mContext == null || !(mContext instanceof Activity) || ((Activity) mContext).isDestroyed();
    }

    private void doBackgroundAni(boolean isIn) {
        ValueAnimator valueAni;
        if (isIn) {
            valueAni = ObjectAnimator.ofFloat(0f, bgk_color_depth);
            valueAni.setDuration(300);
        } else {
            valueAni = ObjectAnimator.ofFloat(bgk_color_depth, 0f);
            valueAni.setDuration(300);
        }

        valueAni.setInterpolator(new LinearInterpolator());
        valueAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Utils.darkenBackground(value, mWindow);
            }
        });
        valueAni.start();
    }

    OnDialogDismissListener mDismissListener;

    public void setOnDialogDismissListener(OnDialogDismissListener l) {
        mDismissListener = l;
    }

}
