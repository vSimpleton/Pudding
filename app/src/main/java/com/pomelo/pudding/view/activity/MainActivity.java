package com.pomelo.pudding.view.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pomelo.pudding.R;
import com.pomelo.pudding.ui.widget.BottomPopupWindow;
import com.pomelo.pudding.ui.widget.CustomFlowLayout;
import com.pomelo.pudding.utils.Configure;
import com.pomelo.pudding.utils.DialogUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv;
    private ImageView iv;
    private CustomFlowLayout mFlowLayout;

    private String[] datas = new String[]{
            "奥利奥", "奥利奥奥利奥", "蒙娜丽莎", "蒙娜丽莎的微笑", "向日葵"
            , "规划", "哈哈哈哈哈哈哈", "啦啦啦啦", "杨利伟", "北理工", "规划",
            "哈哈哈哈哈哈哈", "啦啦啦啦", "杨利伟", "北理工", "蒙娜丽莎", "蒙娜丽莎的微笑", "向日葵"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configure.setUserId(this, "123456");

        mFlowLayout = findViewById(R.id.flowLayout);
        tv = findViewById(R.id.tv);
        tv.setText(Configure.getUserId(this));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showSiftDialog(MainActivity.this, tv,null);
            }
        });

        iv = findViewById(R.id.iv);
        iv.setOnClickListener(this);

        Glide.with(this).asBitmap().load(R.drawable.image_test).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        for (int i = 0; i < datas.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item, mFlowLayout,false);
            TextView textView = view.findViewById(R.id.tv);
            textView.setText(datas[i]);
            mFlowLayout.addView(view);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == iv) {
            final BottomPopupWindow popupWindow = new BottomPopupWindow(this);
            popupWindow.addCustomBtn("常见问题", false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "常见问题", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
            });

            popupWindow.addCustomBtn("举报", true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "举报", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
            });

            popupWindow.show(iv);
        }
    }
}
