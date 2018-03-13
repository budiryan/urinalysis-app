package com.example.urinalysis.urinalysis.util;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Arrays;

/**
 * Created by budiryan on 2/22/18.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int intValue = (int) value;

        if (mValues.length > intValue && intValue >= 0)
            return mValues[intValue];

        return "";
    }

}