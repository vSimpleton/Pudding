package com.pomelo.pudding.pickerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pomelo.pudding.R;
import com.pomelo.pudding.pickerview.adapter.NumericWheelAdapter;
import com.pomelo.pudding.pickerview.lib.WheelView;
import com.pomelo.pudding.pickerview.listener.OnItemSelectedListener;
import com.pomelo.pudding.utils.ScreenUtils;
import com.pomelo.pudding.view.utils.OnDialogDismissListener;
import com.pomelo.pudding.view.utils.ViewActionCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * NAME: 柚子啊
 * DATE: 2020/5/21
 * DESC: 时间选择器
 */
public class DatePickerView extends RelativeLayout implements ViewActionCallBack {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private FrameLayout mContainer;
    private Context mContext;

    public static final int DEFAULT_START_YEAR = 1900;
    private int startYear = DEFAULT_START_YEAR;
    private int endYear = 2018;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;

    LinearLayout mContentLayout;
    TextView mCancelBtn, mOKBtn;

    OnDialogDismissListener mListener;
    OnTimeSelectListener timeSelectListener;

    public DatePickerView(Context context) {
        this(context, null);
        mContext = context;
        initUI();
        initData();
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initUI() {
        FrameLayout.LayoutParams fParams;
        LinearLayout.LayoutParams lParams;
        LayoutParams rParams;
        this.setOnClickListener(mOnClickListener);

        //内容布局
        mContentLayout = new LinearLayout(mContext);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        rParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(ALIGN_PARENT_BOTTOM);
        this.addView(mContentLayout, rParams);
        mContentLayout.setBackgroundColor(0xffffffff);

        //button布局（取消or确定）
        RelativeLayout btnGroup = new RelativeLayout(mContext);
        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtils.dp2px(mContext, 40));
        btnGroup.setGravity(Gravity.CENTER_VERTICAL);
        mContentLayout.addView(btnGroup, lParams);
        {
            mCancelBtn = new TextView(mContext);
            rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rParams.leftMargin = ScreenUtils.dp2px(mContext, 15);
            btnGroup.addView(mCancelBtn, rParams);
            mCancelBtn.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            mCancelBtn.setText("取消");
            mCancelBtn.setTextColor(0xff898989);
            mCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mCancelBtn.setOnClickListener(v -> {
                timeSelectListener = null;
                if (mListener != null) {
                    mListener.onDismiss();
                }
            });

            mOKBtn = new TextView(mContext);
            rParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rParams.rightMargin = ScreenUtils.dp2px(mContext, 15);
            btnGroup.addView(mOKBtn, rParams);
            mOKBtn.setText("确定");
            mOKBtn.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            mOKBtn.setTextColor(getResources().getColor(R.color.social_app_main_color));
            mOKBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            mOKBtn.setOnClickListener(v -> {
                if (timeSelectListener != null) {
                    try {
                        Date date = dateFormat.parse(getTime());
                        if ((date.getYear() + DEFAULT_START_YEAR) < (currentYear - 100) || (date.getYear() + DEFAULT_START_YEAR) > currentYear) {
                            Toast.makeText(mContext, "你的年龄必须在0~100周岁之间", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        timeSelectListener.onTimeSelect(date.getYear() + DEFAULT_START_YEAR, date.getMonth() + 1, date.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (mListener != null) {
                    mListener.onDismiss();
                }
            });
        }

        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContainer = new FrameLayout(mContext);
        mContentLayout.addView(mContainer, lParams);

        fParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        fParams.gravity = Gravity.CENTER_HORIZONTAL;
        fParams.leftMargin = ScreenUtils.dp2px(mContext, 20);
        fParams.rightMargin = ScreenUtils.dp2px(mContext, 15);
        LinearLayout linerLayout = new LinearLayout(mContext);
        mContainer.addView(linerLayout, fParams);
        linerLayout.setOrientation(LinearLayout.HORIZONTAL);
        {
            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.weight = 1;
            wv_year = new WheelView(mContext);
            linerLayout.addView(wv_year, lParams);
            wv_year.setGravity(Gravity.CENTER);

            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.weight = 1;
            wv_month = new WheelView(mContext);
            linerLayout.addView(wv_month, lParams);
            wv_month.setGravity(Gravity.CENTER);

            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lParams.weight = 1;
            wv_day = new WheelView(mContext);
            linerLayout.addView(wv_day, lParams);
            wv_day.setGravity(Gravity.CENTER);
        }
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        endYear = currentYear;
    }

    /**
     * 初始化并弹出日期选择器
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return -
     */
    public DatePickerView setPicker(int year, int month, int day) {
        if (year < DEFAULT_START_YEAR) {
            year = DEFAULT_START_YEAR;
        }
        if (month <= 0) {
            month = 1;
        }
        if (day <= 0) {
            day = 1;
        }
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        // 年
        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear, "年"));// 设置"年"的显示数据
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据

        // 月
        wv_month.setAdapter(new NumericWheelAdapter(1, 12, "月"));
        wv_month.setCurrentItem(month - 1);

        // 日
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31, "日"));
        } else if (list_little.contains(String.valueOf(month))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30, "日"));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29, "日"));
            else wv_day.setAdapter(new NumericWheelAdapter(1, 28, "日"));
        }
        wv_day.setCurrentItem(day - 1);

        // 添加"年"监听
        OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                // 判断大小月及是否闰年,用来确定"日"的数据
                int maxItem = 30;
                if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31, "日"));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30, "日"));
                    maxItem = 30;
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29, "日"));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28, "日"));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
        };

        // 添加"月"监听
        OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month_num = index + 1;
                int maxItem = 30;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31, "日"));
                    maxItem = 31;
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30, "日"));
                    maxItem = 30;
                } else {
                    if (((wv_year.getCurrentItem() + startYear) % 4 == 0 && (wv_year.getCurrentItem() + startYear) % 100 != 0) || (wv_year.getCurrentItem() + startYear) % 400 == 0) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29, "日"));
                        maxItem = 29;
                    } else {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28, "日"));
                        maxItem = 28;
                    }
                }
                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
        };
        wv_year.setOnItemSelectedListener(wheelListener_year);
        wv_month.setOnItemSelectedListener(wheelListener_month);

        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        wv_day.setCustomTextSizeEnable(true);
        wv_month.setCustomTextSizeEnable(true);
        wv_year.setCustomTextSizeEnable(true);

        int textSize = 20;
        wv_day.setTextSize(textSize);
        wv_month.setTextSize(textSize);
        wv_year.setTextSize(textSize);

        return this;
    }

    /**
     * 设置是否循环滚动
     */
    public DatePickerView setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        return this;
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != mContainer) {
                if (mListener != null) {
                    mListener.onDismiss();
                }
            }
        }
    };

    public String getTime() {
        StringBuilder sb = new StringBuilder();
        if (wv_year.getCurrentItem() + startYear == currentYear) {
            if (wv_month.getCurrentItem() > currentMonth || wv_day.getCurrentItem() + 1 > currentDay) {
                sb.append(currentYear).append("-").append(currentMonth + 1).append("-").append(currentDay).append(" ");
                return sb.toString();
            }
        }
        sb.append((wv_year.getCurrentItem() + startYear)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1)).append(" ");
        return sb.toString();
    }

    @Override
    public void setOnViewActionCallBack(OnDialogDismissListener listener) {
        mListener = listener;
    }

    public interface OnTimeSelectListener {
        void onTimeSelect(int year, int month, int day);
    }

    public DatePickerView setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
        return this;
    }

}
