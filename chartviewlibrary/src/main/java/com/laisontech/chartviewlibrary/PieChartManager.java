package com.laisontech.chartviewlibrary;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDP on 2017/11/8.
 * 饼状图管理类
 */

public class PieChartManager {
    private PieChart mPieChart;
    //是否需要加载动画
    private boolean needAnim = false;
    //是否需要显示legend
    private boolean needShowLegend = false;
    //是否需要旋转
    private boolean needRotation = false;
    //上下文
    private Context mContext;
    private PieData mPieData;
    private PieDataSet mPieDataSet;

    private boolean isNeedAnim() {
        return needAnim;
    }

    public PieChartManager setNeedAnim(boolean needAnim) {
        this.needAnim = needAnim;
        return this;
    }

    private boolean isNeedShowLegend() {
        return needShowLegend;
    }

    public PieChartManager setNeedShowLegend(boolean needShowLegend) {
        this.needShowLegend = needShowLegend;
        return this;
    }

    private boolean isNeedRotation() {
        return needRotation;
    }

    public PieChartManager setNeedRotation(boolean needRotation) {
        this.needRotation = needRotation;
        return this;
    }


    public PieChartManager(Context context, PieChart pieChart) {
        mPieChart = pieChart;
        mContext = context;
    }

    public PieChartManager initBasePieParams() {
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(0, 5, 0, 0);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件
        mPieChart.setCenterText("");

        mPieChart.setDrawHoleEnabled(true);

        mPieChart.setHoleColor(Color.parseColor("#FFFFFF"));

        mPieChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(30f);
        mPieChart.setTransparentCircleRadius(30f);

        mPieChart.setDrawCenterText(false);
        //不显示文字，只显示百分比
//        mPieChart.setDrawEntryLabels(false);
        mPieChart.setEntryLabelColor(Color.TRANSPARENT);
        // 触摸旋转
        mPieChart.setRotationEnabled(false);
        //设置初始旋转角度
        mPieChart.setRotationAngle(270);
        mPieChart.setHighlightPerTapEnabled(true);
        if (isNeedAnim()) {
            mPieChart.animateXY(1000, 1000);
        }
        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setEnabled(isNeedShowLegend());
        return this;
    }

    public void setPieData(ArrayList<PieEntry> entries) {
        mPieDataSet = new PieDataSet(entries, "");
        //设置各个饼状图之间的距离
        mPieDataSet.setSliceSpace(0.1f);
        // 部分区域被选中时多出的长度
        mPieDataSet.setSelectionShift(2f);
        //设置颜色的显示
        mPieDataSet.setColors(getValueTextColor(entries));
        //将文字设在外部
//        dataSet.setValueLineColor(Color.parseColor("#8a8a8a"));
//        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        mPieData = new PieData(mPieDataSet);
        mPieData.setValueFormatter(new PercentFormatter());
        //区域文字的大小
        mPieData.setValueTextSize(8f);
        mPieData.setValueTextColor(Color.WHITE);
        mPieChart.setData(mPieData);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }

    //设置共用参数
    private List<Integer> getValueTextColor(ArrayList<PieEntry> entries) {
        List<Integer> colors = new ArrayList<>();
        if (entries == null) {
            colors.add(Color.parseColor("#8A8A8A"));
            return colors;
        }
        for (int i = 0; i < entries.size(); i++) {
            PieEntry pieEntry = entries.get(i);
            if (pieEntry.getLabel().equals(getResStr(R.string.ReadSuccess))) {
                colors.add(Color.parseColor("#29AB91"));
            } else if (pieEntry.getLabel().equals(getResStr(R.string.NoReadMeter))) {
                colors.add(Color.parseColor("#3876C1"));
            } else if (pieEntry.getLabel().equals(getResStr(R.string.ReadFail))) {
                colors.add(Color.parseColor("#F05A4A"));
            }
        }
        return colors;
    }

    private String getResStr(int resId) {
        return mContext.getResources().getString(resId);
    }

    public PieChartManager setOnValueClickListener(final onPieValueClickListener valueClickListener) {
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null) {
                    if (valueClickListener != null) {
                        valueClickListener.onPieValueClick(null);
                    }
                    return;
                }
                PieEntry entry = (PieEntry) e;
                if (valueClickListener != null) {
                    valueClickListener.onPieValueClick(entry);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        return this;
    }

    public void clearBarCache() {
        if (mPieChart == null || mPieData == null || mPieDataSet == null) {
            return;
        }
        mPieData.clearValues();

        mPieChart.clear();

        mPieDataSet.clear();

    }

    public interface onPieValueClickListener {
        void onPieValueClick(PieEntry entry);
    }
}
