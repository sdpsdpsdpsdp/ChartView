package com.laisontech.chartviewlibrary;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by SDP on 2017/9/21.
 * 消费的格式
 */

public class FreezeDataAxisValueFormatter implements IAxisValueFormatter {
    private List<String> dateData;

    public FreezeDataAxisValueFormatter(List<String> dateData) {
        this.dateData = dateData;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int dateInteger = (int) value;
        if (dateData == null || dateData.size() < 1) {
            return value + "";
        }
        return formatDate(dateData, dateInteger);
    }

    //格式化和显示日期样式
    private String formatDate(List<String> dateData, int position) {
        if (position >= dateData.size()) {
            position = dateData.size() - 1;
        }
        if (position == -1) {
            position = 0;
        }
        return dateData.get(position);
    }
}
