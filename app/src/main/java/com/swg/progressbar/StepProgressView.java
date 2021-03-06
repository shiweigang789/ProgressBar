package com.swg.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: hiwallet
 * @ClassName: StepProgressView
 * @Author: Owen
 * @CreateDate: 2020/11/12 18:46
 * @UpdateUser: 更新者
 * @Description: 分步进度条
 */
public class StepProgressView extends View {

    /**
     * 中间间隔
     */
    private int progressMarginTop;

    /**
     * 圆圈宽度
     */
    private int progressCircleWidth;

    /**
     * 圆环半径
     */
    private int progressCircleRadius;

    /**
     * 描述文字高度
     */
    private int progressTextHeight;

    /**
     * 描述文字大小
     */
    private int progressTextSize;

    /**
     * 描述文字颜色
     */
    private int progressTextDescribeColor;

    /**
     * 日期文字颜色
     */
    private int progressTextDateColor;

    /**
     * 圆环正常颜色
     */
    private int progressCircleNormalColor;
    private int progressLineNormalColor;

    /**
     * 圆环选中颜色
     */
    private int progressCircleSelectColor;

    /**
     * 数据列表
     */
    private List<StepInfo> datas = new ArrayList<>();

    /**
     * 第几步
     */
    private int step;

    /**
     * 数据个数
     */
    private int size;

    int colors[] = new int[2];
    float positions[] = new float[2];

    private Paint progressPaint;
    private Paint textPaint;

    private int stepWidth;
    private int mWidth;
    private int mHeight;
    private int mViewHeight;

    public StepProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initPaint();
        mViewHeight = progressCircleRadius * 2 + progressTextHeight * 2 + progressMarginTop * 4;
        colors[0] = progressCircleSelectColor;
        colors[1] = progressLineNormalColor;
        positions[0] = 0;
        positions[1] = 1;
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.StepProgressView);
        progressCircleWidth = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_circle_width, dp2px(2));
        progressMarginTop = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_margin_top, dp2px(2));
        progressCircleRadius = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_circle_radius, dp2px(3));
        progressTextHeight = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_text_describe_height, dp2px(16));
        progressTextSize = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_text_describe_size, sp2px(11));
        progressTextDescribeColor = attributes.getColor(R.styleable.StepProgressView_progress_text_describe_color, Color.parseColor("#333333"));
        progressCircleNormalColor = attributes.getColor(R.styleable.StepProgressView_progress_circle_normal_color, Color.parseColor("#888888"));
        progressLineNormalColor = attributes.getColor(R.styleable.StepProgressView_progress_line_normal_color, Color.parseColor("#EDEDED"));
        progressCircleSelectColor = attributes.getColor(R.styleable.StepProgressView_progress_circle_select_color, Color.parseColor("#2A5BFF"));
        progressTextDateColor = attributes.getColor(R.styleable.StepProgressView_progress_text_date_color, Color.parseColor("#888888"));
        attributes.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStrokeWidth(progressCircleWidth);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressCircleNormalColor);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeJoin(Paint.Join.BEVEL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(progressTextSize);
        textPaint.setStrokeWidth(sp2px(2));
        textPaint.setColor(progressTextDescribeColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height));
    }

    /**
     * 测量宽度
     *
     * @param mode
     * @param width
     * @return
     */
    private int measureWidth(int mode, int width) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    /**
     * 测量高度
     *
     * @param mode
     * @param height
     * @return
     */
    private int measureHeight(int mode, int height) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                mHeight = mViewHeight;
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < datas.size(); i++) {
            drawLine(canvas, i);
            drawCircle(canvas, i);
            textPaint.setColor(progressTextDescribeColor);
            canvas.drawText(datas.get(i).getStepDesc(), stepWidth * i + stepWidth / 2,
                    progressCircleRadius * 2 + progressMarginTop * 2 + progressTextHeight, textPaint);
            textPaint.setColor(progressTextDateColor);
            canvas.drawText(datas.get(i).getStepData(), stepWidth * i + stepWidth / 2,
                    progressCircleRadius * 2 + progressMarginTop * 3 + progressTextHeight * 2, textPaint);
        }
    }

    /**
     * 画线
     *
     * @param canvas
     * @param i
     */
    private void drawLine(Canvas canvas, int i) {
        if (i == -1) {
            progressPaint.setColor(progressLineNormalColor);
            progressPaint.setShader(null);
        } else if (i < step) {
            progressPaint.setColor(progressCircleSelectColor);
            progressPaint.setShader(null);
        } else if (i == step) {
            LinearGradient shader = new LinearGradient(stepWidth * i + stepWidth / 2 + progressCircleRadius, progressMarginTop + progressCircleRadius,
                    stepWidth * (i + 1) + stepWidth / 2 - progressCircleRadius, progressMarginTop + progressCircleRadius,
                    colors,
                    positions,
                    Shader.TileMode.MIRROR);
            progressPaint.setShader(shader);
        } else {
            progressPaint.setColor(progressLineNormalColor);
            progressPaint.setShader(null);
        }
        if (i == size - 1) return;
        canvas.drawLine(stepWidth * i + stepWidth / 2 + progressCircleRadius, progressMarginTop + progressCircleRadius,
                stepWidth * (i + 1) + stepWidth / 2 - progressCircleRadius, progressMarginTop + progressCircleRadius, progressPaint);
    }

    /**
     * 画圆环
     *
     * @param canvas
     * @param i
     */
    private void drawCircle(Canvas canvas, int i) {
        if (i <= step) {
            progressPaint.setColor(progressCircleSelectColor);
        } else {
            progressPaint.setColor(progressCircleNormalColor);
        }
        canvas.drawCircle(stepWidth * i + stepWidth / 2, progressMarginTop + progressCircleRadius, progressCircleRadius, progressPaint);
    }

    /**
     * 设置数据
     *
     * @param step
     */
    public void setDatas(List<StepInfo> datas, int step) {
        if (datas == null || datas.size() == 0) return;
        this.datas = datas;
        this.step = step;
        this.size = datas.size();
        post(() -> {
            stepWidth = mWidth / size;
            invalidate();
        });
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

}
