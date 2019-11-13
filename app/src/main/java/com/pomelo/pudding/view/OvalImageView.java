package com.pomelo.pudding.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 自定义圆角矩形图片
 */
public class OvalImageView extends AppCompatImageView {

    private float radius = dip2px(getContext(), 12);
    private float[] rids = {radius, radius, radius, radius, 0f, 0f, 0f, 0f};

    public OvalImageView(Context context) {
        super(context);
    }

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        path.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    //根据手机像素，把dp值转换成px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
