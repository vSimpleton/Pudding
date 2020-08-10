package com.pomelo.pudding.ui.widget;

import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.pomelo.pudding.MyApplication;
import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.Utils;

import java.util.ArrayList;

import oms.masm.mvvm.utils.DensityUtil;

/**
 * NAME: 柚子啊
 * DATE: 2020/8/10
 * DESC:
 */
public class IconGroupView extends RelativeLayout {

    private int DEFAULT_MARGIN = 20;
    private int DEFAULT_SIZE = 34;

    private int leftMargin;
    private int itemSize;

    private UserIconClickListener mUserIconClickListener;

    public IconGroupView(Context context) {
        this(context, null);
    }

    public IconGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconGroupView, defStyle, 0);
        leftMargin = a.getInteger(R.styleable.IconGroupView_left_margin, 0);
        itemSize = a.getInteger(R.styleable.IconGroupView_item_size, 0);

        if (leftMargin == 0) {
            leftMargin = DEFAULT_MARGIN;
        }

        if (itemSize == 0) {
            itemSize = DEFAULT_SIZE;
        }

        a.recycle();
    }

    public void setData(ArrayList<String> lists) {
        if (lists != null && lists.size() > 0) {
//            this.setVisibility(VISIBLE);
            layoutItem(lists);
        } else {
//            this.setVisibility(GONE);
        }
    }

    private void layoutItem(ArrayList<String> lists) {
        removeAllViews();
        for (int i = 0; i < lists.size(); i++) {
            RoundedImageView item = new RoundedImageView(getContext());
            item.setBackgroundResource(R.drawable.shape_round_white_bgk);
            Glide.with(getContext()).load(lists.get(i)).centerCrop().into(item);
            if (mUserIconClickListener != null) {
                int finalI = i;
                item.setOnClickListener(v ->
                        mUserIconClickListener.onClick(finalI));
            }

            LayoutParams rlp = new LayoutParams(Utils.getRealPixel(itemSize * 2), Utils.getRealPixel(itemSize * 2));
            rlp.leftMargin = i * Utils.getRealPixel(leftMargin * 2);
            addView(item, rlp);
        }
    }

    public interface UserIconClickListener {
        void onClick(int position);
    }

    public void setUserIconClickListener(UserIconClickListener listener) {
        mUserIconClickListener = listener;
    }

}
