package com.laisontech.chartviewlibrary;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by SDP on 2017/9/21.
 * 消费的格式
 */

public class ConsumeAxisValueFormatter implements IAxisValueFormatter {
    private List<String> dateData;

    public ConsumeAxisValueFormatter(List<String> dateData) {
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
        if (position == dateData.size()) {
            position = dateData.size() - 1;
        }
        if (position == -1) {
            position = 0;
        }
        return format(dateData.get(position));
    }

    private String format(String dateStr) {
        StringBuilder builder = new StringBuilder();
        if (dateStr.length() == 14) {
            builder.append(dateStr.substring(4, 6)).append("/")
                    .append(dateStr.substring(6, 8)).append(" ")
                    .append(dateStr.substring(8, 10)).append(":")
                    .append(dateStr.substring(10, 12));
        } else if (dateStr.length() == 6) {
            builder.append(dateStr.substring(4, 6))
                    .append("/")
                    .append(dateStr.substring(0, 4));
        } else if (dateStr.length() == 8) {
            builder.append(dateStr.substring(4, 6))
                    .append("/")
                    .append(dateStr.substring(6, 8))
                    .append("/")
                    .append(dateStr.substring(0, 4));
        } else {
            builder.append("");
        }
        return builder.toString();
    }
}
