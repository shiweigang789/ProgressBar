package com.swg.progressbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: ProgressBar
 * @ClassName: LineChartView
 * @Author: Owen
 * @CreateDate: 2021/1/13 11:35
 * @UpdateUser: 更新者
 * @Description: java类作用描述
 */
public class LineChartView extends View implements View.OnTouchListener {

    private static final String TAG = "LineChartView";

    /**
     * View宽度
     */
    private int mWidth;

    /**
     * View高度
     */
    private int mHeight;

    /**
     * 最小值
     */
    private double minValue = 0.0;

    /**
     * 最大值
     */
    private double maxValue = 20.0;

    /**
     * 纵坐标的间隔值
     */
    private double stepValue = 5.0;

    /**
     * 纵坐标数据宽度
     */
    private int yLabelWidth;

    /**
     * 横坐标数据高度
     */
    private int xLabelHeight;

    /**
     * 纵坐标数值
     */
    private List<String> yLabels = new ArrayList<>();

    /**
     * 横轴数据
     */
    private List<String> xValues = new ArrayList<>();

    /**
     * 横坐标数值
     */
    private List<String> xLabels = new ArrayList<>();

    /**
     * x轴（横向）分割线
     */
    private Paint xAxisPaint = new Paint();

    /**
     * 图表的坐标点的值的paint
     */
    private TextPaint labelPaint = new TextPaint();

    /**
     * 图表的线的paint
     */
    private Paint linePaint = new Paint();

    /**
     * 填充的paint
     */
    private Paint fillPaint = new Paint();

    /**
     * 圆点
     */
    private Paint pointPaint = new Paint();

    /**
     * 文字画笔
     */
    private TextPaint labelValuePaint = new TextPaint();

    /**
     * 文字背景画笔
     */
    private Paint labelBgPaint = new Paint();

    /**
     * 交叉线画笔
     */
    private Paint dyLinePaint = new Paint();

    /**
     * 字体范围
     */
    private Rect textBounds = new Rect();

    /**
     * 原始数据
     */
    private List<InvestRate> mDatas = new ArrayList<>();

    /**
     * 数据点的数据
     */
    private List<Double> pointValues = new ArrayList<>();

    /**
     * 坐标点集合
     */
    private List<Point> points = new ArrayList<>();

    /**
     * 圆点半径
     */
    private int pointRadius;

    /**
     * 绘制曲线的路径
     */
    private Path path = new Path();

    /**
     * 动画是否结束
     */
    private boolean isOver = false;

    /**
     * 是否播放动画
     */
    private boolean playAnim = true;

    /**
     * 是否绘制顶点
     */
    private boolean drawPoints = true;

    /**
     * 动画相关
     */
    private PathMeasure mPathMeasure;
    private ValueAnimator valueAnimator;
    private float mAnimatorValue;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;

    /**
     * 坐标文本高度
     */
    private int labelHeight;

    /**
     * 默认的动效周期 2s
     */
    private int defaultDuration = 1500;

    /**
     * 填充区域随曲线一起运动
     */
    private boolean fillAreaHasAnim = true;

    /**
     * 是否充满曲线下面的区域
     */
    private boolean isFillArea = true;

    /**
     * 绘制文字
     */
    private boolean isDrawLabel = true;

    /**
     * 绘制提示框的矩形
     */
    private RectF rectF = new RectF();

    /**
     * 三角形的高
     */
    private int triangleHeight;

    /**
     * 横坐标分割长度
     */
    private int stepWidth;

    /**
     * 选择监听
     */
    private OnSelectListener mOnSelectListener;

    /**
     * 选中的条目
     */
    private InvestRate mSelectInvestRate;

    /**
     * 选中的点
     */
    private Point mSelectPoint;

    /**
     * Y轴分割高度
     */
    private int stepYHeight;

    /**
     * 是否绘制交叉线
     */
    private boolean isShowDyLine = false;

    /**
     * 触摸开始时间
     */
    private long startOnTouchTime;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        initPaint();
        initAnimator();
    }

    /**
     * 初始化数据
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        yLabelWidth = dp2px(40);
        xLabelHeight = dp2px(30);
        pointRadius = dp2px(4);
        labelHeight = dp2px(17);
        triangleHeight = dp2px(3);
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(defaultDuration);
        valueAnimator.addUpdateListener(mUpdateListener);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOver = true;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        xAxisPaint.setAntiAlias(true);
        xAxisPaint.setStyle(Paint.Style.STROKE);
        xAxisPaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        xAxisPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        xAxisPaint.setDither(true);//防抖动
        xAxisPaint.setShader(null);
        xAxisPaint.setStrokeWidth(dp2px(1));
        xAxisPaint.setColor(Color.parseColor("#F4F4F4"));

        labelPaint.setAntiAlias(true);
        labelPaint.setColor(Color.parseColor("#BFBFBF"));
        labelPaint.setStyle(Paint.Style.FILL);
//        labelYPaint.setTextAlign(Paint.Align.RIGHT);
        labelPaint.setTextSize(dp2px(12));
//        labelHeight = labelPaint.getFontMetrics().bottom - labelPaint.getFontMetrics().top;

        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        linePaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        linePaint.setDither(true);//防抖动
        linePaint.setShader(null);
        linePaint.setStrokeWidth(dp2px(2));
        linePaint.setColor(Color.parseColor("#2A5BFF"));

        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        fillPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        fillPaint.setDither(true);//防抖动
        fillPaint.setStrokeWidth(dp2px(1));
        fillPaint.setColor(Color.parseColor("#D1E4FF"));

        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        pointPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        pointPaint.setDither(true);//防抖动
        pointPaint.setStrokeWidth(dp2px(2));
        pointPaint.setColor(Color.parseColor("#2A5BFF"));

        labelValuePaint.setAntiAlias(true);
        labelValuePaint.setColor(Color.WHITE);
        labelValuePaint.setTextAlign(Paint.Align.LEFT);
        labelValuePaint.setTextSize(sp2px(14));
//        Typeface font = Typeface.createFromAsset(getResources().getAssets(),"");
//        labelValuePaint.setTypeface( font );
//        labelValueHeight = labelValuePaint.getFontMetrics().bottom - labelValuePaint.getFontMetrics().top;

        labelBgPaint.setAntiAlias(true);
        labelBgPaint.setStyle(Paint.Style.FILL);
        labelBgPaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        labelBgPaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        labelBgPaint.setDither(true);//防抖动
        labelBgPaint.setStrokeWidth(dp2px(2));
        labelBgPaint.setColor(Color.parseColor("#FF4156"));

        dyLinePaint.setAntiAlias(true);
        dyLinePaint.setStyle(Paint.Style.FILL);
        dyLinePaint.setStrokeJoin(Paint.Join.ROUND);// 笔刷图形样式
        dyLinePaint.setStrokeCap(Paint.Cap.ROUND);// 设置画笔转弯的连接风格
        dyLinePaint.setDither(true);//防抖动
        dyLinePaint.setStrokeWidth(dp2px(1));
        dyLinePaint.setColor(Color.parseColor("#73A7FF"));
        DashPathEffect effect = new DashPathEffect(new float[]{dp2px(2), dp2px(6)}, 1);
        dyLinePaint.setPathEffect(effect);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawYAxis(canvas);
        drawXAxis(canvas);
        drawPath(canvas);
        drawPoints(canvas);
        drawLabel(canvas);
        drawDyLine(canvas);
    }

    /**
     * 绘制交叉线
     *
     * @param canvas
     */
    private void drawDyLine(Canvas canvas) {
        if (mSelectPoint == null) return;
        canvas.drawLine(yLabelWidth, mSelectPoint.y, mWidth, mSelectPoint.y, dyLinePaint);
        canvas.drawLine(mSelectPoint.x, stepYHeight, mSelectPoint.x, mHeight - xLabelHeight, dyLinePaint);
    }

    /**
     * 绘制Label
     *
     * @param canvas
     */
    private void drawLabel(Canvas canvas) {
        if (!isDrawLabel || points.isEmpty()) return;
        if (isOver || !playAnim) {
            int size = mDatas.size();
            Point point = points.get(size - 1);
            String value = pointValues.get(size - 1) + "%";
            labelPaint.getTextBounds(value, 0, value.length(), textBounds);
            int textWidth = textBounds.right - textBounds.left;
            int textHeight = textBounds.bottom - textBounds.top;
            int left = point.x - textWidth - pointRadius * 4;
            int top = point.y - textHeight - pointRadius * 2 - triangleHeight * 3;
            if (top < 0) {
                top = 0;
            }
            int bottom = top + textHeight + pointRadius * 2;
            rectF.set(left, top, point.x, bottom);
            canvas.drawRoundRect(rectF, pointRadius, pointRadius, labelBgPaint);
            Path path = new Path();
            path.moveTo(point.x - pointRadius, bottom);
            path.lineTo(point.x, bottom + triangleHeight);
            path.lineTo(point.x, bottom - pointRadius);
            canvas.drawPath(path, labelBgPaint);
            path.reset();
            canvas.drawText(value, 0, value.length(), left + pointRadius, bottom - pointRadius, labelValuePaint);
        }
    }

    /**
     * 画点
     *
     * @param canvas
     */
    private void drawPoints(Canvas canvas) {
        if (isOver || !playAnim) {
            Point point;
            int size = points.size();
            if (drawPoints) {
                for (int i = 0; i < size; i++) {
                    point = points.get(i);
                    if (i == size - 1 && isDrawLabel) {
                        pointPaint.setStyle(Paint.Style.FILL);
                        pointPaint.setColor(Color.WHITE);
                        canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
                        pointPaint.setStyle(Paint.Style.STROKE);
                        pointPaint.setColor(Color.parseColor("#2A5BFF"));
                        canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
                    } else {
                        pointPaint.setStyle(Paint.Style.FILL);
                        pointPaint.setColor(Color.parseColor("#2A5BFF"));
                        canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
                    }
                }
            } else {
                if (isDrawLabel) {
                    point = points.get(size - 1);
                    pointPaint.setStyle(Paint.Style.FILL);
                    pointPaint.setColor(Color.WHITE);
                    canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
                    pointPaint.setStyle(Paint.Style.STROKE);
                    pointPaint.setColor(Color.parseColor("#2A5BFF"));
                    canvas.drawCircle(point.x, point.y, pointRadius, pointPaint);
                }
            }
            if (mOnSelectListener != null && !isShowDyLine) {
                mOnSelectListener.onSelect(mDatas.get(size - 1));
            }
        }
    }

    /**
     * 绘制Path
     *
     * @param canvas
     */
    private void drawPath(Canvas canvas) {
        if (mDatas.size() > 1) {
            Point endPoint = points.get(mDatas.size() - 1);
            Point startPoint = points.get(0);
            if (playAnim) {
                Path dst = new Path();
                //根据动画值从线段总长度不断截取绘制造成动画效果
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst, true);
                if (fillAreaHasAnim) {
                    float currX = (endPoint.x - startPoint.x) * (1 - mAnimatorValue) + startPoint.x;
                    if (isFillArea) {
                        dst.lineTo(startPoint.x, mHeight - xLabelHeight);
                        dst.lineTo(currX, mHeight - xLabelHeight);
                        dst.close();
                        canvas.drawPath(dst, fillPaint);
                        dst.reset();
                    }
                }
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength(), dst, true);
                canvas.drawPath(dst, linePaint);
            } else {
                if (isFillArea) {
                    Path pa = new Path(path);
                    pa.lineTo(startPoint.x, mHeight - xLabelHeight);
                    pa.lineTo(endPoint.x, mHeight - xLabelHeight);
                    pa.close();
                    canvas.drawPath(pa, fillPaint);
                }
                canvas.drawPath(path, linePaint);
            }
        }
    }

    /**
     * 绘制Y轴线
     *
     * @param canvas
     */
    private void drawYAxis(Canvas canvas) {
        if (yLabels.isEmpty()) return;
        int size = yLabels.size();
        stepYHeight = (mHeight - xLabelHeight) / size;
        String value = yLabels.get(0);
        labelPaint.getTextBounds(value, 0, value.length(), textBounds);
        int valueHeight = textBounds.bottom - textBounds.top;
        for (int i = 0; i < size; i++) {
            value = yLabels.get(i);
            canvas.drawLine(yLabelWidth, stepYHeight * (i + 1), mWidth, stepYHeight * (i + 1), xAxisPaint);
            canvas.drawText(value, 0, value.length(), 0, stepYHeight * (i + 1) + valueHeight / 2, labelPaint);
        }
    }

    /**
     * 绘制Y轴线
     *
     * @param canvas
     */
    private void drawXAxis(Canvas canvas) {
        if (xLabels.isEmpty()) return;
        int size = xLabels.size();
        int valueWidth, valueHeight, left;
        if (size > 1) {
            String value = xLabels.get(0);
            labelPaint.getTextBounds(value, 0, value.length(), textBounds);
            valueWidth = textBounds.right - textBounds.left;
            valueHeight = textBounds.bottom - textBounds.top;
            int stepWidth = (mWidth - yLabelWidth - valueWidth) / (size - 1);
            for (int i = 0; i < size; i++) {
                value = xLabels.get(i);
                if (i == 0) {
                    left = yLabelWidth;
                } else if (i == size - 1) {
                    left = mWidth - valueWidth - dp2px(2);
                } else {
                    left = yLabelWidth + stepWidth * i;
                }
                canvas.drawText(value, 0, value.length(), left, mHeight - valueHeight - dp2px(2), labelPaint);
            }
        } else {
            String value = xLabels.get(0);
            labelPaint.getTextBounds(value, 0, value.length(), textBounds);
            valueWidth = textBounds.right - textBounds.left;
            valueHeight = textBounds.bottom - textBounds.top;
            canvas.drawText(value, 0, value.length(), yLabelWidth + (mWidth - yLabelWidth) / 2 - valueWidth / 2,
                    mHeight - valueHeight - dp2px(2), labelPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startOnTouchTime = System.currentTimeMillis();
                if (!isShowDyLine && !isDrawLabel) {
                    mSelectPoint = null;
                    isDrawLabel = true;
                    invalidate();
                }
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - startOnTouchTime > 1000) {
                    isShowDyLine = true;
                }
                if (mDatas.size() > 1 && isShowDyLine) {
                    isInArea(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            default:
                isShowDyLine = false;
                return super.onTouchEvent(event);
        }
    }

    /**
     * 是否在范围内
     *
     * @param x
     * @param y
     * @return
     */
    private void isInArea(float x, float y) {
        int size = points.size();
        int centerX;
        Point point;
        InvestRate investRate;
        for (int i = 0; i < size; i++) {
            point = points.get(i);
            centerX = point.x;
            if (x < centerX + stepWidth / 2 && x > centerX - stepWidth / 2) {
                mSelectPoint = point;
                investRate = mDatas.get(i);
                if (mOnSelectListener != null && mSelectInvestRate != investRate) {
                    mSelectInvestRate = investRate;
                    mOnSelectListener.onSelect(mSelectInvestRate);
                    isDrawLabel = false;
                    invalidate();
                }
            }
        }
    }

    /**
     * 设置数据
     */
    public void setDatas(List<InvestRate> datas) {
        mDatas.clear();
        pointValues.clear();
        xValues.clear();
        xLabels.clear();
        yLabels.clear();
        points.clear();
        mDatas.addAll(datas);
        if (mDatas.isEmpty()) return;
        try {
            int size = mDatas.size();
            InvestRate investRate;
            for (int i = 0; i < size; i++) {
                investRate = mDatas.get(i);
                xValues.add(TimeUtils.millis2String(investRate.getRateDate(), "MM/dd"));
                pointValues.add(investRate.getInvestRate());
            }
            Log.d(TAG, pointValues.toString());
            calcYAxis();
            calcPoints();
            if (size <= 7) {
                for (int i = 0; i < size; i++) {
                    xLabels.add(TimeUtils.millis2String(mDatas.get(i).getRateDate(), "MM/dd"));
                }
            } else {

            }
            if (size == 1) {
                playAnim = false;
                isFillArea = false;
            }
            if (playAnim) {
                initAnimator();
                valueAnimator.start();
            }
            if (isFillArea) {
                fillPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), fillPaint.getColor(),
                        0x00000000, Shader.TileMode.MIRROR));
            }
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算点的集合
     */
    private void calcPoints() {
        if (pointValues.isEmpty()) return;
        int size = pointValues.size();
        if (size > 1) {
            stepWidth = (mWidth - yLabelWidth - (pointRadius + dp2px(1)) * 2) / (size - 1);
        } else {
            stepWidth = (mWidth - yLabelWidth - (pointRadius + dp2px(1)) * 2) / 2;
        }
        int stepHeight = (int) ((mHeight - xLabelHeight) / (maxValue + stepValue - minValue));
        Point point;
        if (size == 1) {
            point = new Point();
            point.x = yLabelWidth + pointRadius + stepWidth;
            point.y = (int) (stepHeight * (maxValue + stepValue - pointValues.get(0)));
            points.add(point);
        } else {
            for (int i = 0; i < size; i++) {
                point = new Point();
                point.x = yLabelWidth + pointRadius + stepWidth * i;
                point.y = (int) (stepHeight * (maxValue + stepValue - pointValues.get(i)));
                points.add(point);
            }
        }
        Log.d(TAG, points.toString());
        if (size > 1) {
            point = points.get(size - 1);
            path.moveTo(point.x, point.y);
            for (int i = size - 1; i >= 0; i--) {
                point = points.get(i);
                path.lineTo(point.x, point.y);
            }
            if (playAnim) {
                mPathMeasure = new PathMeasure(path, false);
            }
        }
    }

    /**
     * 计算纵轴数据
     */
    private void calcYAxis() {
        if (pointValues.size() == 1) {
            Double pointValue = pointValues.get(0);
            int step = (int) (pointValue / stepValue);
            if (pointValue >= 0) {
                maxValue = (step + 1) * stepValue;
                minValue = 0.0;
            } else {
                maxValue = 0.0;
                minValue = (step - 1) * stepValue;
            }
            int size = Math.abs(step) + 1;
            for (int i = 0; i < size; i++) {
                yLabels.add((maxValue - i * stepValue) + "%");
            }
            yLabels.add(minValue + "%");
        } else {
            Double max = Collections.max(pointValues);
            Double min = Collections.min(pointValues);
            int step = 0;
            if (min >= 0) {
                step = (int) (max / stepValue) + 1;
                maxValue = step * stepValue;
                minValue = 0.0;
            } else if (max <= 0) {
                step = (int) (min / stepValue) - 1;
                maxValue = 0.0;
                minValue = step * stepValue;
            } else {
                int step1 = (int) (max / stepValue) + 1;
                maxValue = step1 * stepValue;
                int step2 = (int) (min / stepValue) - 1;
                minValue = step2 * stepValue;
                step = step1 - step2;
            }
            int size = Math.abs(step);
            for (int i = 0; i < size; i++) {
                yLabels.add((maxValue - i * stepValue) + "%");
            }
            yLabels.add(minValue + "%");
        }
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mOnSelectListener = listener;
    }

    public interface OnSelectListener {
        void onSelect(InvestRate investRate);
    }

}
