package com.pomelo.pudding.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sherry on 2019/9/30
 * 各种工具类
 */

public class Utils {

    public static float sDensity;
    public static float sDensityDpi;
    public static int sScreenW;
    public static int sScreenH;

    public static int sRelativeScreenW = 720;
    public static int sRelativeScreenH = 1280;

    // 可在baseActivity中使用或在需要时单独使用
    public static void init(Activity activity) {
        Display dis = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dis.getRealMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;
        sScreenW = w < h ? w : h;
        sScreenH = w < h ? h : w;
        sDensity = dm.density;
        sDensityDpi = dm.densityDpi;
    }

    // 获取屏幕宽度
    public static int getScreenW() {
        return sScreenW;
    }

    // 获取屏幕高度
    public static int getScreenH() {
        return sScreenH;
    }

    public static int getRealPixel(int pxSrc) {
        int pix = (int) (pxSrc * sDensity / 2.0);
        if (pxSrc == 1 && pix == 0) {
            pix = 1;
        }
        return pix;
    }

    /**
     * 判断星座
     *
     * @param m 月
     * @param d 日
     * @return 星座
     */
    public static String dateMatchXingzuo(int m, int d) {
        String temp = "";
        switch (m) {
            case 1:
                if (d >= 1 && d <= 19) {
                    temp = "摩羯座";
                } else {
                    temp = "水瓶座";
                }
                break;
            case 2:
                if (d >= 1 && d <= 18) {
                    temp = "水瓶座";
                } else {
                    temp = "双鱼座";
                }
                break;
            case 3:
                if (d >= 1 && d <= 20) {
                    temp = "双鱼座";
                } else {
                    temp = "白羊座";
                }
                break;
            case 4:
                if (d >= 1 && d <= 19) {
                    temp = "白羊座";
                } else {
                    temp = "金牛座";
                }
                break;
            case 5:
                if (d >= 1 && d <= 20) {
                    temp = "金牛座";
                } else {
                    temp = "双子座";
                }
                break;
            case 6:
                if (d >= 1 && d <= 21) {
                    temp = "双子座";
                } else {
                    temp = "巨蟹座";
                }
                break;
            case 7:
                if (d >= 1 && d <= 22) {
                    temp = "巨蟹座";
                } else {
                    temp = "狮子座";
                }
                break;
            case 8:
                if (d >= 1 && d <= 22) {
                    temp = "狮子座";
                } else {
                    temp = "处女座";
                }
                break;
            case 9:
                if (d >= 1 && d <= 22) {
                    temp = "处女座";
                } else {
                    temp = "天秤座";
                }
                break;
            case 10:
                if (d >= 1 && d <= 23) {
                    temp = "天秤座";
                } else {
                    temp = "天蝎座";
                }
                break;
            case 11:
                if (d >= 1 && d <= 22) {
                    temp = "天蝎座";
                } else {
                    temp = "射手座";
                }
                break;
            case 12:
                if (d >= 1 && d <= 21) {
                    temp = "射手座";
                } else {
                    temp = "摩羯座";
                }
                break;

            default:
                break;
        }
        return temp;
    }

    /**
     * 关键字高亮
     *
     * @param result  传入要匹配的目标字符串
     * @param keyword 传入需要高亮的关键字
     * @param color   传入颜色值，默认0xff96a8d0
     * @return
     */
    public static SpannableString setHighLightText(String result, String keyword, int color, boolean isBold) {
        SpannableString ss = new SpannableString(result);
        Pattern p = Pattern.compile("[" + keyword.replace("\\", "") + "]");
        Matcher m = p.matcher(result);
        while (m.find()) {
            if (!"".equals(m.group())) {
                int start = m.start();
                int end = m.end();
                ss.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (isBold) {
                    ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return ss;
    }

    /**
     * 手机振动
     *
     * @param duration 持续时间
     */
    public static void shakePhone(Context context, int duration, boolean isPattern) {
        Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator()) {
            if (!isPattern) {
                mVibrator.vibrate(duration);
            } else {
                // 以指定的模式振动
                mVibrator.vibrate(new long[]{100, 100, 50, 100}, 0);
            }
        }
    }

    /**
     * 把指定颜色加上指定透明度
     *
     * @param color  颜色
     * @param degree 透明度 0-1
     * @return 加上透明度的颜色
     */
    public static int getColorWithAlpha(int color, float degree) {
        if (color == 0) {
            return 0;
        }
        if (degree < 0) {
            degree = 0;
        }
        if (degree > 1) {
            degree = 1;
        }
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb((int) (255 * degree), red, green, blue);
    }

    /**
     * shape加上皮肤
     */
    public static void viewBgAddColor(View view, int color) {
        if (view != null) {
            GradientDrawable drawable = (GradientDrawable) view.getBackground();
            if (drawable != null) {
                drawable.setColor(color);
            }
        }
    }

    /**
     * drawable换颜色
     */
    public static void addDrawableColor(ImageView view, int color) {
        if (view != null) {
            Drawable drawable = view.getDrawable();
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    /**
     * 判断是否为gif
     */
    public static boolean isGif(String url) {
        return !TextUtils.isEmpty(url) && (url.contains(".gif") || url.contains(".GIF"));
    }

    //字符串转时间戳
    public static long getTime(String timeString) {
        long time = 0;
        timeString = timeString.replace("Z", " UTC");//注意是空格+UTC
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
        Date d;
        try {
            d = sdf.parse(timeString);
            time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    //时间戳转字符串
    public static String getStrTime(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    /**
     * bitmap制作圆角
     */
    public static Bitmap bitmap2Round(Bitmap mBitmap, float radius) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //设置矩形大小
        Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        RectF rectf = new RectF(rect);

        // 相当于清屏
        canvas.drawARGB(0, 0, 0, 0);
        //画圆角
        canvas.drawRoundRect(rectf, radius, radius, paint);
        // 取两层绘制，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 把原生的图片放到这个画布上，使之带有画布的效果
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        return bitmap;
    }

    /**
     * 数字转换为文字
     */
    public static String transferCount2Text(int count) {
        String result = "";
        if (count >= 10000) {
            result = count / 10000 + "W";
        } else {
            result = count + "";
        }
        return result;
    }

    /**
     * @param context 上下文对象
     * @param image   需要模糊的图片
     * @return 模糊处理后的Bitmap
     */
    public static Bitmap blurBitmap(Context context, Bitmap image, float blurRadius) {
        // 计算图片缩小后的长宽
        int width = image.getWidth();
        int height = image.getHeight();

        // 将缩小后的图片做为预渲染的图片
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        // 创建一张渲染后的输出图片
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(blurRadius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * 判断输入的字符或标点符号是否为中文
     *
     * @param ch
     * @return
     */
    public static boolean checkChar(char ch) {
        if ((ch + "").getBytes().length == 1) {
            return false;//英文
        } else {
            return true;//中文
        }
    }

    /**
     * 判定是否输入汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 判定是否输入表情
     */
    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 判断app是否处于前台
     *
     * @param context
     * @return
     */
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName()) && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断context是否已被销毁
     */
    public static boolean checkContextValid(Context context) {
        if (context != null) {
            if (context instanceof Activity && !((Activity) context).isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为整数
     */
    public static boolean isInteger(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\d+$");
        return pattern.matcher(str).matches();
    }

    public static void darkenBackground(Float bgColor, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgColor;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
        if (bgColor == 1.0f) {
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    /**
     * 解决直接设置 WindowManager.LayoutParams#alpha 导致闪屏或者 Activity 透明度异常的情况
     * 注意：禁止在 popupWindow.dismiss 时调用
     *
     * @param bgColor     设置给{@link WindowManager.LayoutParams#dimAmount}的值
     * @param popupWindow 需要设置底层变灰的popupWindow
     */
    public static void darkenBackground(float bgColor, @NonNull PopupWindow popupWindow) {
        // https://stackoverflow.com/questions/35874001/dim-the-background-using-popupwindow-in-android
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = bgColor;
        if (wm != null) {
            wm.updateViewLayout(container, p);
        }
    }

    /**
     * 60分钟内的显示：xx分钟前
     * 一天内的显示：xx小时前
     * 昨天的显示：昨天 xx:xx
     * 前天的显示：前天 xx:xx
     * 同一年的显示：MM-dd
     * 不同年的显示：yyyy-MM-dd
     *
     * @param time 时间戳
     * @return
     */
    public static String getPublishTime2(int time) {

        String msg;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");// 指定时间格式
        long reset = time * 1000L; // 获取指定时间的毫秒数
        long nowTime = System.currentTimeMillis(); // 获取当前时间的毫秒数
        long dateDiff = nowTime - reset;
        long dateTemp1 = dateDiff / 1000; // 秒
        long dateTemp2 = dateTemp1 / 60; // 分钟
        long dateTemp3 = dateTemp2 / 60; // 小时

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(reset);
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        long time1 = calendar1.getTimeInMillis();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(nowTime);
        calendar2.set(Calendar.HOUR, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        long time2 = calendar2.getTimeInMillis();

        int between_days = Integer.parseInt(String.valueOf((time2 - time1) / (1000 * 3600 * 24)));
        sdf.applyPattern("yyyy年");
        String year1 = sdf.format(new Date(reset));
        String year2 = sdf.format(new Date(nowTime));

        if (dateTemp1 < 3600L) {
            msg = dateTemp2 + "分钟前";
        } else if (dateTemp1 < 3600L * 24L) {
            msg = dateTemp3 + "小时前";
        } else if (between_days == 1) {
            sdf.applyPattern("HH:mm");
            msg = "昨天 " + sdf.format(new Date(reset));
        } else if (between_days == 2) {
            sdf.applyPattern("HH:mm");
            msg = "前天 " + sdf.format(new Date(reset));
        } else {
            //年份不同显示年份
            if (!year1.equals(year2)) {
                sdf.applyPattern("yyyy年MM月dd日");
            } else {
                sdf.applyPattern("MM月dd日");
            }
            msg = sdf.format(new Date(reset));
        }
        return msg;
    }

}
