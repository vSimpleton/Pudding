package com.pomelo.pudding.pickerview.adapter;


import android.text.TextUtils;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;
    private String label;
    private int labelLen = 0;

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, "");
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue, String label) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.label = label;
        if (!TextUtils.isEmpty(label)) {
            labelLen = label.length();
        }
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return value + label;
        }
        return 0;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int indexOf(Object o) {
        String temp = ((String) o).substring(0, ((String) o).length() - labelLen);
        return Integer.parseInt(temp) - minValue;
    }
}
