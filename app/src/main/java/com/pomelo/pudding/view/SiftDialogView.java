package com.pomelo.pudding.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.AnimUtils;
import com.pomelo.pudding.utils.Utils;

/**
 * Created by Sherry on 2019/11/4
 */

public class SiftDialogView extends RelativeLayout implements View.OnClickListener, ViewActionCallBack {
    LayoutInflater mInflater;
    View baseLayout;
    RelativeLayout rlConfirm, rlCancel;
    ImageView ivHeader;
    TextView tvContent, tvConfirmText, tvCancel, tvContentTwo;

    public SiftDialogView(Context context) {
        super(context);
        initView();
        initListener();
    }

    private void initListener() {
        baseLayout.setOnClickListener(this);
        rlCancel.setOnClickListener(this);
        rlConfirm.setOnClickListener(this);

        rlCancel.setOnTouchListener(AnimUtils.getAlphaTouchListener());
        rlConfirm.setOnTouchListener(AnimUtils.getScaleTouchListener());
    }

    private void initView() {
        mInflater = LayoutInflater.from(getContext());
        baseLayout = mInflater.inflate(R.layout.dialog_sift, null);
        RelativeLayout.LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(baseLayout, rlp);

        rlConfirm = baseLayout.findViewById(R.id.rlConfirm);
        rlCancel = baseLayout.findViewById(R.id.rlCancel);
        ivHeader = baseLayout.findViewById(R.id.ivHeader);
        tvConfirmText = baseLayout.findViewById(R.id.tvConfirmText);
        tvCancel = baseLayout.findViewById(R.id.tvCancel);
        tvContent = baseLayout.findViewById(R.id.tvContent);
        tvContentTwo = baseLayout.findViewById(R.id.tvContentTwo);
    }

    OnDialogDismissListener mListener;

    @Override
    public void setOnViewActionCallBack(OnDialogDismissListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == rlCancel) {
            if (mListener != null) {
                mListener.onDismiss();
            }
        } else if (v == rlConfirm) {
            if (mListener != null) {
                mListener.onDismiss();
            }
        } else if (v == baseLayout) {
            if (mListener != null) {
                mListener.onDismiss();
            }
        }
    }

    public SiftDialogView setHeader(int res) {
        ivHeader.setImageResource(res);
        return this;
    }

    public SiftDialogView setContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        return this;
    }

    public SiftDialogView setContentTwo(String content) {
        if (!TextUtils.isEmpty(content)) {
            tvContentTwo.setText(content);
        }
        return this;
    }

    public SiftDialogView setPositiveButton(String content, OnClickListener listener) {
        if (!TextUtils.isEmpty(content)) {
            tvConfirmText.setText(content);
            tvConfirmText.getPaint().setFakeBoldText(true);
        }
        rlConfirm.setOnClickListener(listener);
        return this;
    }

    public SiftDialogView setNegativeButton(OnClickListener listener) {
        if (listener != null) {
            rlCancel.setOnClickListener(listener);
            rlCancel.setVisibility(VISIBLE);
        } else {
            rlCancel.setVisibility(GONE);
            ((LinearLayout.LayoutParams) rlConfirm.getLayoutParams()).bottomMargin = Utils.getRealPixel(48);
        }
        return this;
    }

    public SiftDialogView setNegativeButton(String text, OnClickListener listener) {
        if (!TextUtils.isEmpty(text)) {
            tvCancel.setText(text);
            rlCancel.setVisibility(VISIBLE);
        }
        if (listener != null) {
            rlCancel.setOnClickListener(listener);
        } else {
            rlCancel.setVisibility(GONE);
            ((LinearLayout.LayoutParams) rlConfirm.getLayoutParams()).bottomMargin = Utils.getRealPixel(48);
        }
        return this;
    }

}

