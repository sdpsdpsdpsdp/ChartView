package com.laisontech.chartviewlibrary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SDP on 2017/9/21.
 * 图管理类
 * 包含柱状图和线性图
 * 可以实现一次性数据的添加
 */

public class ChartManager {
    //柱状类
    private BarChart mBarChart;
    //线状类
    private LineChart mLineChart;
    //饼状体
    private PieChart mPieChart;
    //y轴左侧的标注线
    private YAxis mLeftAxis;
    //y轴右侧的标注线
    private YAxis mRightAxis;
    //x轴的标注线
    private XAxis mXAxis;
    //map缓存信息
    private Map<Float, Integer> mMapCache;
    //y轴的单位
    private String mUnit = "$";
    private Context mContext;
    //各种颜色的标注文字
    private String mLabel = "";
    //图标的描述
    private String mDescription = "";
    //chart父类
    private Chart mFatherChart;

    public ChartManager(Context context, Chart chart) {
        mFatherChart = chart;
        mContext = context;
        initSameParams();
    }

    //初始化相同的参数
    private void initSameParams() {
        //设置动画效果
        mFatherChart.getDescription().setEnabled(false);
        //设置提示
        Legend l = mFatherChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void anim() {
        mFatherChart.animateY(1000, Easing.EasingOption.Linear);
        mFatherChart.animateX(1000, Easing.EasingOption.Linear);
    }

    private void clearCacheMap() {
        if (mMapCache != null) {
            mMapCache.clear();
        } else {
            mMapCache = new HashMap<>();
        }
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    private String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public Map<Float, Integer> getMapCache() {
        return mMapCache;
    }

    /**
     * 初始化BarChart
     */
    private void initBarChartParams(List<String> xValue) {
        clearCacheMap();
        if (mBarChart == null) {
            mBarChart = (BarChart) mFatherChart;
        }
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(false);

        mXAxis = mBarChart.getXAxis();
        mLeftAxis = mBarChart.getAxisLeft();
        mRightAxis = mBarChart.getAxisRight();
        setXYParams(xValue);
    }


    /**
     * 展示柱状图
     *
     * @param xAxisValues     x轴的初值一定从0开始并且每个都是连续的
     * @param valuationXValue 根据x轴原始的float类型值，当做当前的下标，即可赋值新的值到x轴
     * @param yAxisValues     y轴的值
     */
    public void setBarChartData(List<Float> xAxisValues, List<String> valuationXValue, List<Float> yAxisValues) {
        initBarChartParams(valuationXValue);
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisValues.size(); i++) {
            float x = xAxisValues.get(i);
            float y = yAxisValues.get(i);
            entries.add(new BarEntry(x, y));
            mMapCache.put(x, i);
        }
        BarDataSet barDataSet;
        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() > 0) {
            barDataSet = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(entries);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            barDataSet = new BarDataSet(entries, getLabel());
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setFormLineWidth(1f);
            barDataSet.setFormSize(15.f);
            barDataSet.setValueTextSize(15f);
            barDataSet.setValueTextColors(getValueTextColor());

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barDataSet);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf"));
            data.setBarWidth(0.9f);
            mBarChart.setData(data);
        }
    }

    private List<Integer> getValueTextColor() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#2ecc71"));
        colors.add(Color.parseColor("#f1c40f"));
        colors.add(Color.parseColor("#e74c3c"));
        colors.add(Color.parseColor("#3498db"));
        return colors;
    }


    /**
     * 设置LineChartView参数
     */
    private void initLineChartParams(List<String> xValue) {
        clearCacheMap();
        if (mLineChart == null) {
            mLineChart = (LineChart) mFatherChart;
        }
        mLineChart.setDrawGridBackground(false);

        mXAxis = mLineChart.getXAxis();
        mLeftAxis = mLineChart.getAxisLeft();
        mRightAxis = mLineChart.getAxisRight();

        setXYParams(xValue);
    }

    /**
     * 设置LineChartView的资源
     *
     * @param xAxisValues x轴的初值一定从0开始并且每个都是连续的
     * @param yAxisValues 根据x轴原始的float类型值，当做当前的下标，即可赋值新的值到x轴
     * @param yAxisValues y轴的值
     */
    public void setLineChartData(List<Float> xAxisValues, List<String> xValue, List<Float> yAxisValues) {
        initLineChartParams(xValue);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < xAxisValues.size(); i++) {
            float x = xAxisValues.get(i);
            float y = yAxisValues.get(i);
            entries.add(new Entry(x, y));
            mMapCache.put(x, i);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, getLabel());
        initLineDataSet(lineDataSet, true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        //设置X轴的刻度数
        mLineChart.setData(data);
    }

    /**
     * 初始化曲线 每一个LineDataSet代表一条线
     *
     * @param lineDataSet
     * @param mode        折线图是否填充
     */
    private void initLineDataSet(LineDataSet lineDataSet, boolean mode) {
        lineDataSet.setColors(getValueTextColor());
        lineDataSet.setCircleColors(getValueTextColor());
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        //设置折线图填充
        lineDataSet.setDrawFilled(mode);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        //线模式为圆滑曲线（默认折线）
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
    }

    //设置x，y轴的不同参数
    private void setXYParams(List<String> xValue) {
        //设置x轴
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf"));
        mXAxis.setDrawGridLines(false);
        mXAxis.setGranularity(1f);
        if (xValue.size() <= 5) {
            mXAxis.setLabelCount(5);
        } else {
            mXAxis.setLabelCount(4);
        }
        mXAxis.setValueFormatter(new ConsumeAxisValueFormatter(xValue));


        IAxisValueFormatter yFormatter = new YAXisValueFormatter(mUnit);
        //设置y左
        mLeftAxis.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf"));
        mLeftAxis.setLabelCount(10, false);
        mLeftAxis.setValueFormatter(yFormatter);
        mLeftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mLeftAxis.setSpaceTop(15f);
        mLeftAxis.setAxisMinimum(0f);

        //设置y右
        mRightAxis.setDrawGridLines(false);
        mRightAxis.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Light.ttf"));
        mRightAxis.setLabelCount(5, false);
        mRightAxis.setValueFormatter(yFormatter);
        mRightAxis.setSpaceTop(15f);
        mRightAxis.setAxisMinimum(0f);
        //开始动画
        anim();
    }


    /**
     * 设置PieChartView参数
     */
    private void initPieChartParams() {
        mPieChart = (PieChart) mFatherChart;
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件
        mPieChart.setCenterText("");

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 触摸旋转
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);


    }

    /**
     * 设置PieChartView的资源
     */
    public void setPieChartData(ArrayList<PieEntry> entries) {
        initPieChartParams();
        PieDataSet dataSet = new PieDataSet(entries, getLabel());
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
        anim();
    }


    /**
     * 设置bar的点击事件
     */
    public void setOnBarValueClickListener(final OnValueSelectedListener listener) {
        mFatherChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (listener != null) {
                    if (e == null) {
                        return;
                    }
                    float x = e.getX();
                    Integer index = getMapCache().get(x);
                    Log.e("position", "index:" + index);
                    listener.onValueSelected(index);
                }
            }

            @Override
            public void onNothingSelected() {
                Log.e("position", "index:" + "onNothingSelected");
            }
        });
    }
}
