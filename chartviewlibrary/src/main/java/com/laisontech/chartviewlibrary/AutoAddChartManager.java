package com.laisontech.chartviewlibrary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by SDP on 2017/9/22.
 * 自动添加柱状或者线状图管理类
 */

public class AutoAddChartManager {
    //上下文
    private Context mContext;
    //y轴左
    private YAxis mLeftAxis;
    //y轴右
    private YAxis mRightAxis;
    //x轴
    private XAxis mXAxis;
    //y轴的单位
    private String mUnit = "$";
    //各种颜色的标注文字
    private String mLabel = "";
    //x轴显示的value
    private List<String> xValue = new ArrayList<>();
    //每次显示的个数
    private int mShowBarCount = 5;
    //两个类型图的父类
    private Chart mChart;
    //构建x轴显示信息
    private IAxisValueFormatter mFormatter;
    //x轴要显示的个数,默认为5个
    private int mAxisShowCount = 5;
    //是否封闭上部
    private boolean mDrawBorders = false;
    //是否显示右边界？
    private boolean isShowRightAxis = true;
    //x轴文字的角度
    private float mXTextOri = 0;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //柱状图
    private BarChart mBarChart;
    //bar数据
    private List<BarEntry> mBarEntry = new ArrayList<>();
    //柱状图数据
    private BarData mBarData;
    //柱状图数据设置
    private BarDataSet mBarDateSet;
    private boolean showValueColorTransparent = false;
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~·
    //线性图
    private LineChart mLineChart;
    //线性图数据
    private LineData mLineData;
    //线性图数据设置
    private LineDataSet mLineDateSet;

    //是否开启右侧坐标轴显示
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public AutoAddChartManager setUnit(String unit) {
        mUnit = unit;
        return this;
    }

    private String getLabel() {
        return mLabel;
    }

    public AutoAddChartManager setLabel(String label) {
        this.mLabel = label;
        return this;
    }

    public AutoAddChartManager setShowChartCount(int showBarCount) {
        this.mShowBarCount = showBarCount;
        return this;
    }

    private IAxisValueFormatter getFormatter() {
        if (mFormatter == null) {
            mFormatter = new ConsumeAxisValueFormatter(xValue);
        }
        return mFormatter;
    }

    public AutoAddChartManager setFormatter(IAxisValueFormatter formatter) {
        this.mFormatter = formatter;
        return this;
    }

    public int getXAxisShowCount() {
        return mAxisShowCount;
    }

    public AutoAddChartManager setXAxisShowCount(int axisShowCount) {
        this.mAxisShowCount = axisShowCount;
        return this;
    }

    public boolean isDrawBorders() {
        return mDrawBorders;
    }

    //显示边界？
    public AutoAddChartManager setDrawBorders(boolean drawBorders) {
        this.mDrawBorders = drawBorders;
        return this;
    }

    public boolean isShowRightAxis() {
        return isShowRightAxis;
    }

    public AutoAddChartManager setShowRightAxis(boolean showRightAxis) {
        isShowRightAxis = showRightAxis;
        return this;
    }

    public float getXTextOri() {
        return mXTextOri;
    }

    public AutoAddChartManager setXTextOri(float xTextOri) {
        this.mXTextOri = xTextOri;
        return this;
    }

    public boolean getShowValueColorTransparent() {
        return showValueColorTransparent;
    }

    public AutoAddChartManager setShowValueColorTransparent(boolean showValueColorTransparent) {
        this.showValueColorTransparent = showValueColorTransparent;
        return this;
    }

    //初始化
    public AutoAddChartManager(Context context, Chart chart) {
        mContext = context;
        this.mChart = chart;
        initSameParams();
    }

    //初始化相同的参数
    private void initSameParams() {
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        //设置提示
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    //初始化barChartView
    public AutoAddChartManager initBarChartView() {
        initBarChart();
        initBarDataSet();
        return this;
    }

    /**
     * 初始化BarChar
     */
    private void initBarChart() {
        mBarChart = (BarChart) mChart;

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(false);
        mBarChart.setDrawBorders(isDrawBorders());
        //不支持缩放
//        mBarChart.setScaleEnabled(false);
        mLeftAxis = mBarChart.getAxisLeft();
        mRightAxis = mBarChart.getAxisRight();
        mXAxis = mBarChart.getXAxis();
        //设置x轴内容的文字方向
        mXAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        mRightAxis.setEnabled(isShowRightAxis());
        setXYParams();
    }


    /**
     * 初始柱状图
     */
    private void initBarDataSet() {
        mBarDateSet = new BarDataSet(mBarEntry, getLabel());
        mBarDateSet.setColors(ColorTemplate.MATERIAL_COLORS);
        mBarDateSet.setFormLineWidth(1f);
        mBarDateSet.setFormSize(15.0f);
        mBarDateSet.setValueTextSize(14f);
        mBarDateSet.setValueTextColors(getValueTextColor());
        mBarData = new BarData();
        mBarData.setValueTextSize(10f);
        mBarData.setValueTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf"));
        mBarData.setBarWidth(0.9f);
        mBarChart.setData(mBarData);
        mBarChart.invalidate();
    }


    /**
     * 动态添加数据（柱状图）
     *
     * @param xAxisValues     x轴的初值一定从0开始并且每个都是连续的
     * @param valuationXValue 根据x轴原始的float类型值，当做当前的下标，即可赋值新的值到x轴
     * @param yAxisValues     y轴的值
     */

    public void addBarEntry(List<Float> xAxisValues, List<String> valuationXValue, List<Float> yAxisValues) {
        if (xAxisValues == null || xAxisValues.size() < 1 || valuationXValue == null || yAxisValues == null) {
            return;
        }
        for (int i = 0; i < xAxisValues.size(); i++) {
            //无数据的时候 barDataSet（barDataSet 代表一个柱子）
            if (mBarDateSet.getEntryCount() == 0) {
                mBarData.addDataSet(mBarDateSet);
            }
            mBarChart.setData(mBarData);
            xValue.add(valuationXValue.get(i));
            BarEntry entry = new BarEntry(mBarDateSet.getEntryCount(), (float) yAxisValues.get(i));
            Log.e("freezeData", "addBarEntry: " + yAxisValues.get(i));
            mBarData.addEntry(entry, 0);
            //通知数据已经改变
            mBarData.notifyDataChanged();
            mBarChart.notifyDataSetChanged();
            //设置在图中显示的最大数量
            mBarChart.setVisibleXRangeMaximum(mShowBarCount);
            mBarDateSet.setLabel(getLabel());
        }
        mBarChart.moveViewToX(mBarData.getEntryCount() - mShowBarCount);
        mBarChart.invalidate();
        anim();
    }

    /**
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 线性图
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    //初始化lineChartView
    public AutoAddChartManager initLineChartView() {
        initLineChart();
        initLineDataSet();
        return this;
    }

    /**
     * 初始化lineChar
     */
    private void initLineChart() {
        mLineChart = (LineChart) mChart;

        mLineChart.setPinchZoom(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setDrawBorders(false);
        mLineChart.setDrawBorders(isDrawBorders());

        mLeftAxis = mLineChart.getAxisLeft();
        mRightAxis = mLineChart.getAxisRight();
        mXAxis = mLineChart.getXAxis();
        mRightAxis.setEnabled(isShowRightAxis());

        setXYParams();
    }


    /**
     * 初始柱状图
     */
    private void initLineDataSet() {
        mLineDateSet = new LineDataSet(null, getLabel());
        mLineDateSet.setLineWidth(1.5f);
        mLineDateSet.setCircleRadius(3f);
        mLineDateSet.setColors(getValueTextColor());
        mLineDateSet.setCircleColors(getValueTextColor());
        //设置曲线填充
        mLineDateSet.setDrawFilled(true);
        mLineDateSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mLineDateSet.setValueTextSize(10f);
        if (getShowValueColorTransparent()) {
            mLineDateSet.setValueTextColor(Color.TRANSPARENT);
        } else {
            mLineDateSet.setValueTextColors(getValueTextColor());
        }
        mLineDateSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //添加一个空的 LineData
        mLineData = new LineData();
        mLineChart.setData(mLineData);
        mLineChart.invalidate();
    }

    //添加line的资源
    public void addLineEntry(List<Float> xAxisValues, List<String> valuationXValue, List<Float> yAxisValues) {
        if (xAxisValues == null || xAxisValues.size() < 1 || valuationXValue == null || yAxisValues == null) {
            return;
        }
        //尝试设置显示的y轴两侧的值
        trySetShowY(yAxisValues);

        for (int i = 0; i < xAxisValues.size(); i++) {
            //无数据的时候 barDataSet（barDataSet 代表一个柱子）
            if (mLineDateSet.getEntryCount() == 0) {
                mLineData.addDataSet(mLineDateSet);
            }
            mLineChart.setData(mLineData);
            xValue.add(valuationXValue.get(i));
            //尝试设置
            int entryCount = mLineDateSet.getEntryCount();
            float yValue = yAxisValues.get(i);
            Entry entry = new Entry(entryCount, yValue);
            mLineData.addEntry(entry, 0);
            //通知数据已经改变
            mLineData.notifyDataChanged();
            mLineChart.notifyDataSetChanged();
            //设置在图中显示的最大数量
            mLineChart.setVisibleXRangeMaximum(mShowBarCount);
            mLineDateSet.setLabel(getLabel());
        }
        mLineChart.moveViewToX(mLineData.getEntryCount() - mShowBarCount);
        mLineChart.invalidate();
        anim();
    }

    private void trySetShowY(List<Float> yAxisValues) {
        float max = getMax(yAxisValues);
        if (max < 1) {
            mLeftAxis.setLabelCount((int) (max * 10), false);
            mRightAxis.setLabelCount((int) (max * 10), false);
        } else {
            mLeftAxis.setLabelCount(10, false);
            mRightAxis.setLabelCount(10, false);
        }
    }

    //获取y轴的最大值
    public float getMax(List<Float> yAxisValues) {
        if (yAxisValues == null || yAxisValues.size() < 1) return 0;
        return Collections.max(yAxisValues);
    }

    //显示动画
    private void anim() {
        mChart.animateY(300, Easing.EasingOption.Linear);
        mChart.animateX(300, Easing.EasingOption.Linear);
    }

    //设置x- y轴的参数
    private void setXYParams() {
        //设置x轴
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf"));
        mXAxis.setDrawGridLines(false);
        mXAxis.setGranularity(1f);
        mXAxis.setTextSize(8.0f);
        mXAxis.setValueFormatter(getFormatter());
        mXAxis.setLabelCount(getXAxisShowCount(), false);
        mXAxis.setLabelRotationAngle(getXTextOri());

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
    }

    //设置共用参数
    private List<Integer> getValueTextColor() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#2ecc71"));
        colors.add(Color.parseColor("#f1c40f"));
        colors.add(Color.parseColor("#e74c3c"));
        colors.add(Color.parseColor("#3498db"));
        return colors;
    }

    public void clearLineCache() {
        if (mLineData == null || mLineChart == null || mLineDateSet == null) {
            return;
        }
        mLineData.clearValues();

        mLineChart.clear();

        mLineDateSet.clear();

        initLineChartView();

    }

    public void clearBarCache() {
        if (mBarData == null || mBarChart == null || mBarDateSet == null) {
            return;
        }
        mBarData.clearValues();

        mBarChart.clear();

        mBarDateSet.clear();

        initBarChartView();
    }

    /**
     * 设置高限制线
     */
    public void setHeightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine heightLimit = new LimitLine(high, name);
        heightLimit.setLineWidth(4f);
        heightLimit.setTextSize(10f);
        heightLimit.setLineColor(color);
        heightLimit.setTextColor(color);
        mLeftAxis.addLimitLine(heightLimit);
        mChart.invalidate();
    }

    /**
     * 设置低限制线
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine lowLimit = new LimitLine(low, name);
        lowLimit.setLineWidth(4f);
        lowLimit.setTextSize(10f);
        mLeftAxis.addLimitLine(lowLimit);
        mChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mChart.setDescription(description);
        mChart.invalidate();
    }


    /**
     * 设置bar的点击事件
     */
    public void setOnChartValueClickListener(final OnValueSelectedListener listener) {
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (listener != null) {
                    if (e == null) {
                        return;
                    }
                    int x = (int) e.getX();
                    listener.onValueSelected(x);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    /**
     * 设置bar的滑动、拖拽事件
     */
    public void setOnChartScrollListener(final int loadOneDataSize, final int loadIndex, final int dataSize, final OnScrollListener listener) {
        final float[] startX = {0};
        mChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                Log.e("OnChartScroll", "onChartGestureStart: " + "start:" + me.getX() + "," + me.getY());
                startX[0] = me.getX();
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                String name = lastPerformedGesture.name();
                int ordinal = lastPerformedGesture.ordinal();
                String s = lastPerformedGesture.toString();
                Log.e("OnChartScroll", "start:" + startX[0] + ",onChartGestureEnd: " + "End:" + me.getX() + "," + me.getY() + "," + name + "," + ordinal + "," + s);
                if (me.getX() - startX[0] < 0) {
                    //向左滑动时监听判断，如果是最后一条数据，发送提示接口，直行加载数据处理
                    //难点：该框架并未提供哪一条为最后一条的接口，如何进行判断为最后一条？如果只是左滑则可以进行判断，但是无法获取当前是否已经滑动到数据的最后一条
                    //通过该方法可以获取到滑动的值，
                    MPPointF centerOfView = mChart.getCenterOfView();
                    Highlight highlight = mChart.getHighlightByTouchPoint(centerOfView.getX(), centerOfView.getY());
                    float midIndex = highlight.getX();
                    Log.e("OnChartScroll", "onChartGestureEnd: 中心值：" + highlight.toString() + ",midIndex" + midIndex + ",size:" + dataSize);
                    if (listener != null) {
                        if (dataSize < loadOneDataSize) {
                            listener.onScrollAllData();
                        } else {
                            int dichotomyIndex = 0;
                            if (loadOneDataSize % 2 == 0) {
                                if (loadIndex == 1) {
                                    dichotomyIndex = (loadOneDataSize - 2) / 2;
                                } else {
                                    dichotomyIndex = loadOneDataSize / 2;
                                }
                            } else {
                                dichotomyIndex = (loadOneDataSize - 1) / 2;
                            }
                            if (dataSize - midIndex == dichotomyIndex + 1) {
                                listener.onScrollLastDataFinished();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
    }

    //强制设置第一次进入为进行x轴放大部分
    public void setScale(boolean isFirst) {
        Matrix m = new Matrix();
        if (isFirst) {
            m.postScale(1.0000001f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
        } else {
            m.postScale(1f, 1f);
        }
        mChart.getViewPortHandler().refresh(m, mChart, false);//将图表动画显示之前进行缩放
    }

}
