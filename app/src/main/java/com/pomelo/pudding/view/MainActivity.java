package com.pomelo.pudding.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pomelo.pudding.R;
import com.pomelo.pudding.utils.MeasureUtils;
import com.pomelo.pudding.utils.NetWorkUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        MeasureUtils.setHeight(tv, 500);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkUtils.goSetting(MainActivity.this);
            }
        });
    }
}