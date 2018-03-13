package com.laisontech.chartviewlibrary;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by SDP on 2017/9/21.
 */

public class YAXisValueFormatter  implements IAxisValueFormatter
{

    private DecimalFormat mFormat;
    private String mUnit;
    public YAXisValueFormatter(String unit) {
        mUnit = unit;
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value) + " "+mUnit;
    }
}
