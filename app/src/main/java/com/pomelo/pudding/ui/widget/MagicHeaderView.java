package com.pomelo.pudding.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MagicHeaderView extends View {

    public MagicHeaderView(Context context) {
        super(context);
        init();
    }

    public MagicHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MagicHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mViewW;
    private int mViewH;

    private int mBgColor = 0xffffffff;
    private Bitmap mHeadBmp;
    private Bitmap mBodyBmp;
    private Bitmap mHandBmp;
    private Bitmap mHatBmp;
    private Bitmap mGlassesBmp;
    private Bitmap mTextureBmp;

    private Matrix mMatrix = new Matrix();
    private Paint temp_paint = new Paint();

    private void init() {
        temp_paint.reset();
        temp_paint.setAntiAlias(true);
        temp_paint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = GetMyMeasureSpec(0, widthMeasureSpec);
        heightMeasureSpec = GetMyMeasureSpec(0, heightMeasureSpec);
        mViewW = MeasureSpec.getSize(widthMeasureSpec);
        mViewH = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mViewW, mViewH);
    }

    public static int GetMyMeasureSpec(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return MeasureSpec.makeMeasureSpec(result, specMode);
    }


    public void setBgColor(int color) {
        mBgColor = color;
        invalidate();
    }

    public void setHeaderBmp(Bitmap head) {
        mHeadBmp = head;
        invalidate();
    }

    public void setBodyBmp(Bitmap body) {
        mBodyBmp = body;
        invalidate();
    }

    public void setHandBmp(Bitmap hand) {
        mHandBmp = hand;
        invalidate();
    }

    public void setGlassesBmp(Bitmap glasses) {
        mGlassesBmp = glasses;
        invalidate();
    }

    public void setHatBmp(Bitmap hat) {
        mHatBmp = hat;
        invalidate();
    }

    public void setTextureBmp(Bitmap texture) {
        mTextureBmp = texture;
        invalidate();
    }

    public void getDrawMatrix(Matrix matrix, Bitmap bmp) {
        getOutPutMatrix(matrix, bmp, mViewW, mViewH);
    }

    public void getOutPutMatrix(Matrix matrix, Bitmap bmp, int width, int heigth) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        if (bmp != null) {
            int bmpW = bmp.getWidth();
            int bmpH = bmp.getHeight();

            int centerX = width / 2;
            int centerY = heigth / 2;

            int traX = centerX - bmpW / 2;
            int traY = centerY - bmpH / 2;

            float scale1 = (float) width / (float) bmpW;
            float scale2 = (float) heigth / (float) bmpH;
            float m_scaleX = (scale1 > scale2) ? scale1 : scale2;
            float m_scaleY = m_scaleX;

            matrix.reset();
            matrix.postTranslate(traX, traY);
            matrix.postScale(m_scaleX, m_scaleY, centerX, centerY);
        }
    }

    public void getOutPutMatrix4Clip(Matrix matrix, Bitmap bmp, int width, int height) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        if (bmp != null) {
            int bmpW = bmp.getWidth();
            int bmpH = bmp.getHeight();

            int centerX = height / 2;
            int centerY = height / 2;

            int traX = centerX - bmpW / 2;
            int traY = centerY - bmpH / 2;

            float scale1 = (float) height / (float) bmpW;
            float scale2 = (float) height / (float) bmpH;
            float m_scaleX = (scale1 > scale2) ? scale1 : scale2;
            float m_scaleY = m_scaleX;

            matrix.reset();
            matrix.postTranslate(traX, traY);
            matrix.postScale(m_scaleX, m_scaleY, centerX, centerY);
            matrix.postTranslate(-(height - width) / 2f, 0);
        }
    }


    public Bitmap getOutputBitmap(int size) {
        return getOutputBitmap(size, true);
    }

    public Bitmap getOutputBitmap(int size, boolean hasBg) {
        if (mHeadBmp == null) {
            return null;
        }
        if (size == -1) {
            size = mHeadBmp.getWidth() > mHeadBmp.getHeight() ? mHeadBmp.getWidth() : mHeadBmp.getHeight();
        }
        float outW = size;
        float outH = size;

        float whscale = (float) mViewW / (float) mViewH;
        if (whscale > 1) {
            outW = size;
            outH = (outW / whscale);
        } else {
            outH = size;
            outW = (outH * whscale);
        }

        int finalOutW = (int) (outW);
        int finalOutH = (int) (outH);

        Bitmap outBmp = Bitmap.createBitmap(finalOutW, finalOutH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);

        if (hasBg) {
            canvas.drawColor(mBgColor, PorterDuff.Mode.SRC_OVER);

            if (mTextureBmp != null) {
                float scal = finalOutW / (float) 840.0;
                int drawWidth = (int) (mTextureBmp.getWidth() * scal);
                int drawHeight = (int) (mTextureBmp.getHeight() * scal);
                //横轴循环次数
                int count_x = (finalOutW + drawWidth - 1) / drawWidth;
                //纵轴循环次数
                int count_y = (finalOutH + drawHeight - 1) / drawHeight;
                Matrix m = new Matrix();
                for (int idy = 0; idy < count_y; idy++) {
                    for (int idx = 0; idx < count_x; idx++) {
                        m.reset();
                        m.setScale(scal, scal);
                        m.postTranslate(idx * drawWidth, idy * drawHeight);
                        canvas.drawBitmap(mTextureBmp, m, null);
                    }
                }
            }
        }

        if (mBodyBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix(mMatrix, mBodyBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mBodyBmp, mMatrix, temp_paint);
        }

        if (mHeadBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix(mMatrix, mHeadBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHeadBmp, mMatrix, temp_paint);
        }

        if (mGlassesBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix(mMatrix, mGlassesBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mGlassesBmp, mMatrix, temp_paint);
        }

        if (mHatBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix(mMatrix, mHatBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHatBmp, mMatrix, temp_paint);
        }

        if (mHandBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix(mMatrix, mHandBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHandBmp, mMatrix, temp_paint);
        }

        return outBmp;
    }

    public Bitmap getOutputBitmapWithBg(int size) {
        if (mHeadBmp == null) {
            return null;
        }
        if (size == -1) {
            size = mHeadBmp.getWidth() > mHeadBmp.getHeight() ? mHeadBmp.getWidth() : mHeadBmp.getHeight();
        }
        float outH = size;
        float outW = size * 3 / 4;
        int finalOutW = (int) (outW);
        int finalOutH = (int) (outH);

        Bitmap outBmp = Bitmap.createBitmap(finalOutW, finalOutH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);

        canvas.drawColor(mBgColor, PorterDuff.Mode.SRC_OVER);

        if (mTextureBmp != null) {
            float scal = finalOutW / (float) 840.0;
            int drawWidth = (int) (mTextureBmp.getWidth() * scal);
            int drawHeight = (int) (mTextureBmp.getHeight() * scal);
            //横轴循环次数
            int count_x = (finalOutW + drawWidth - 1) / drawWidth;
            //纵轴循环次数
            int count_y = (finalOutH + drawHeight - 1) / drawHeight;
            Matrix m = new Matrix();
            for (int idy = 0; idy < count_y; idy++) {
                for (int idx = 0; idx < count_x; idx++) {
                    m.reset();
                    m.setScale(scal, scal);
                    m.postTranslate(idx * drawWidth, idy * drawHeight);
                    canvas.drawBitmap(mTextureBmp, m, null);
                }
            }
        }

        if (mBodyBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix4Clip(mMatrix, mBodyBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mBodyBmp, mMatrix, temp_paint);
        }

        if (mHeadBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix4Clip(mMatrix, mHeadBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHeadBmp, mMatrix, temp_paint);
        }

        if (mGlassesBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix4Clip(mMatrix, mGlassesBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mGlassesBmp, mMatrix, temp_paint);
        }

        if (mHatBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix4Clip(mMatrix, mHatBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHatBmp, mMatrix, temp_paint);
        }

        if (mHandBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getOutPutMatrix4Clip(mMatrix, mHandBmp, finalOutW, finalOutH);
            canvas.drawBitmap(mHandBmp, mMatrix, temp_paint);
        }

        return outBmp;
    }

    public Bitmap getOutputBitmapWithoutBg(int size) {
        return getOutputBitmap(size, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(mBgColor, PorterDuff.Mode.SRC_OVER);

        if (mTextureBmp != null) {
            float scal = (float) mViewH / (float) 840.0;
            int drawWidth = (int) (mTextureBmp.getWidth() * scal);
            int drawHeight = (int) (mTextureBmp.getHeight() * scal);
            //横轴循环次数
            int count_x = (mViewW + drawWidth - 1) / drawWidth;
            //纵轴循环次数
            int count_y = (mViewH + drawHeight - 1) / drawHeight;
            Matrix m = new Matrix();
            for (int idy = 0; idy < count_y; idy++) {
                for (int idx = 0; idx < count_x; idx++) {
                    m.reset();
                    m.setScale(scal, scal);
                    m.postTranslate(idx * drawWidth, idy * drawHeight);
                    canvas.drawBitmap(mTextureBmp, m, null);
                }
            }
        }

        if (mBodyBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getDrawMatrix(mMatrix, mBodyBmp);
            canvas.drawBitmap(mBodyBmp, mMatrix, temp_paint);
        }

        if (mHeadBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getDrawMatrix(mMatrix, mHeadBmp);
            canvas.drawBitmap(mHeadBmp, mMatrix, temp_paint);
        }

        if (mGlassesBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getDrawMatrix(mMatrix, mGlassesBmp);
            canvas.drawBitmap(mGlassesBmp, mMatrix, temp_paint);
        }

        if (mHatBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getDrawMatrix(mMatrix, mHatBmp);
            canvas.drawBitmap(mHatBmp, mMatrix, temp_paint);
        }

        if (mHandBmp != null) {
            temp_paint.reset();
            temp_paint.setAntiAlias(true);
            temp_paint.setFilterBitmap(true);
            getDrawMatrix(mMatrix, mHandBmp);
            canvas.drawBitmap(mHandBmp, mMatrix, temp_paint);
        }
    }

    public void release() {
        if (mBodyBmp != null) {
            mBodyBmp = null;
        }

        if (mHeadBmp != null) {
            mHeadBmp = null;
        }

        if (mHandBmp != null) {
            mHandBmp = null;
        }

        if (mHatBmp != null) {
            mHatBmp = null;
        }
        if (mGlassesBmp != null) {
            mGlassesBmp = null;
        }
        if (mTextureBmp != null) {
            mTextureBmp = null;
        }
        mMatrix = null;
        temp_paint = null;
    }
}
