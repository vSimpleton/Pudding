package com.pomelo.pudding.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.pomelo.pudding.R;
import com.pomelo.pudding.event.SingleClick;
import com.pomelo.pudding.event.SingleClickListener;
import com.pomelo.pudding.mvp.bean.DailyInfo;
import com.pomelo.pudding.mvp.DailyContract;
import com.pomelo.pudding.mvp.DailyPresenter;
import com.pomelo.pudding.pickerview.DatePickerView;
import com.pomelo.pudding.ui.widget.BottomPopupWindow;
import com.pomelo.pudding.ui.widget.CustomFlowLayout;
import com.pomelo.pudding.utils.Configure;
import com.pomelo.pudding.view.utils.CustomPopupWindow;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DailyContract.UserInfoView {

    private TextView tv;
    private ImageView iv;
    private CustomFlowLayout mFlowLayout;
    private DailyPresenter mPresenter;
    private Button button;

    private String[] datas = new String[]{
            "奥利奥", "奥利奥奥利奥", "蒙娜丽莎", "蒙娜丽莎的微笑", "向日葵"
            , "规划", "哈哈哈哈哈哈哈", "啦啦啦啦", "杨利伟", "北理工", "规划",
            "哈哈哈哈哈哈哈", "啦啦啦啦", "杨利伟", "北理工", "蒙娜丽莎", "蒙娜丽莎的微笑", "向日葵"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        initData();
        initPresenter();

    }

    private void initView() {
        mFlowLayout = findViewById(R.id.flowLayout);
        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        button = findViewById(R.id.button);
    }

    private void initListener() {
        iv.setOnClickListener(this);
        tv.setOnClickListener(this);
        button.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                DatePickerView datePickerView = new DatePickerView(MainActivity.this);
                datePickerView.setCyclic(false).setPicker(1996, 1, 1);
                CustomPopupWindow popupWindow = new CustomPopupWindow(MainActivity.this);
                datePickerView.setOnTimeSelectListener((year, month, day) -> {
                    Toast.makeText(MainActivity.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
                });
                popupWindow.show(v, datePickerView);
            }
        });
    }

    private void initData() {
        Configure.setUserId(this, "123456");
        tv.setText(Configure.getUserId(this));
        for (int i = 0; i < datas.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item, mFlowLayout, false);
            TextView textView = view.findViewById(R.id.tv);
            textView.setText(datas[i]);
            mFlowLayout.addView(view);
        }
    }

    private void initPresenter() {
        mPresenter = new DailyPresenter(this);
        mPresenter.attachView(this);

    }

    @SingleClick(5000)
    @Override
    public void onClick(View v) {
        if (v == iv) {
            Log.d("youzi", "onClick: 点击了");
            mPresenter.getDaily();
        } else if (v == tv) {
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
        } else if (v == button) {
//            DatePickerView datePickerView = new DatePickerView(this);
//            datePickerView.setCyclic(false).setPicker(1996, 1, 1);
//            CustomPopupWindow popupWindow = new CustomPopupWindow(this);
//            datePickerView.setOnTimeSelectListener((year, month, day) -> {
//                Toast.makeText(this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
//            });
//            popupWindow.show(v, datePickerView);
        }
    }

    @Override
    public void getDailySuccess(DailyInfo dailyInfo) {
        Log.e("youzi", "content: " + dailyInfo.getContent());
    }

    @Override
    public void getDailyError(String result) {
        Log.e("youzi", "error: " + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
