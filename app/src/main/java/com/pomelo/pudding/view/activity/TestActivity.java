package com.pomelo.pudding.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pomelo.pudding.R;
import com.pomelo.pudding.ui.widget.CustomBigView;

import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AppCompatActivity {

    CustomBigView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        image = findViewById(R.id.image);
        InputStream is = null;
        try {
            is = getAssets().open("big.png");
            image.setImage(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
