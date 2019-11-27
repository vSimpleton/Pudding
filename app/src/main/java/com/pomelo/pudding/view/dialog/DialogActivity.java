package com.pomelo.pudding.view.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.AnimUtils;
import com.pomelo.pudding.utils.StatusBarUtils;

/**
 * 弹窗形式的activity
 */
public class DialogActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private RelativeLayout baseLayout;
    private LinearLayout llMainLayout;
    private ImageView ivWhatIsLevel;
    private TextView tvCurLevel, tvLikeCount;
    private TextView tvSweetCount, tvBeLikeCount;
    private TextView tvConfirm;
    private boolean isFinishAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        mContext = this;
        StatusBarUtils.transparencyBar(this);

        initView();
        initListener();

        baseLayout.post(new Runnable() {
            @Override
            public void run() {
                doEnterAni();
            }
        });
    }

    public void initView() {
        llMainLayout = findViewById(R.id.llMainLayout);
        baseLayout = findViewById(R.id.baseLayout);
        ivWhatIsLevel = findViewById(R.id.ivWhatIsLevel);
        tvCurLevel = findViewById(R.id.tvCurLevel);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvSweetCount = findViewById(R.id.tvSweetCount);
        tvBeLikeCount = findViewById(R.id.tvBeLikeCount);
        tvConfirm = findViewById(R.id.tvConfirm);
    }

    public void initListener() {
        ivWhatIsLevel.setOnClickListener(this);
        ivWhatIsLevel.setOnTouchListener(AnimUtils.getTouchBackListener(0.8f));
        tvConfirm.setOnClickListener(this);
        tvConfirm.setOnTouchListener(AnimUtils.getAlphaTouchListener());
        baseLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == tvConfirm || view == baseLayout) {
            onBackPressed();
        }
    }

    private void doEnterAni() {
        final AnimatorSet aniSet = new AnimatorSet();
        aniSet.setDuration(500);
        aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ValueAnimator layoutAni = ValueAnimator.ofInt(0x00000000, getResources().getColor(R.color.activity_mask_bgk_color));
        layoutAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                baseLayout.setBackgroundColor(color);
            }
        });
        layoutAni.setEvaluator(new ArgbEvaluator());

        ObjectAnimator dialogAni = ObjectAnimator.ofFloat(llMainLayout, "translationY", llMainLayout.getHeight(), 0);
        aniSet.playTogether(layoutAni, dialogAni);
        aniSet.start();
    }

    private void doQuitAni() {
        if (isFinishAni) {
            return;
        }
        isFinishAni = true;

        final AnimatorSet aniSet = new AnimatorSet();
        aniSet.setDuration(200);
        aniSet.setInterpolator(new AccelerateInterpolator());
        ValueAnimator layoutAni = ValueAnimator.ofInt(0x80000000, 0x00000000);
        layoutAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                baseLayout.setBackgroundColor(color);
            }
        });
        layoutAni.setEvaluator(new ArgbEvaluator());

        ObjectAnimator dialogAni = ObjectAnimator.ofFloat(llMainLayout, "translationY", 0, llMainLayout.getHeight());
        aniSet.playTogether(layoutAni, dialogAni);
        aniSet.start();
        aniSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        doQuitAni();
    }
}

