package com.swg.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
public class StepProgressView2 extends View {

    public enum Type {
        NORMAL,
        FAIL,
        SUCCESS
    }

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
     * 进度
     */
    private int progress;

    /**
     * 数据个数
     */
    private int size;

    private Paint progressPaint;
    private Paint textPaint;

    private int stepWidth;
    private int mWidth;
    private int mHeight;
    private int mViewHeight;

    private Rect textRect = new Rect();
    private Rect textBounds = new Rect();

    private Type mType = Type.NORMAL;

    public StepProgressView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        initPaint();
        mViewHeight = progressCircleRadius * 2 + progressTextHeight * 2 + progressMarginTop * 4;
    }

    /**
     * 初始化
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.StepProgressView);
        progressCircleWidth = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_circle_width, dp2px(2));
        progressMarginTop = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_margin_top, dp2px(4));
        progressCircleRadius = attributes.getDimensionPixelSize(R.styleable.StepProgressView_progress_circle_radius, dp2px(4));
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
        progressPaint.setStyle(Paint.Style.FILL);
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
            drawText(canvas, i);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     * @param i
     */
    private void drawText(Canvas canvas, int i) {
        StepInfo stepInfo = datas.get(i);
        String stepDesc = stepInfo.getStepDesc();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.getTextBounds(stepDesc, 0, stepDesc.length(), textBounds);
        int textWidth = textBounds.right - textBounds.left;
        int textHeight = textBounds.bottom - textBounds.top;
        int left = stepWidth * i + progressCircleRadius * (2 * i + 1) - textWidth / 2;
        if (i == 0) {
            left = 2;
        } else if (i == size - 1) {
            left = mWidth - textWidth - 2;
        }
        textRect.left = left;
        textRect.top = progressCircleRadius + progressTextHeight;
        textRect.right = left + textWidth;
        textRect.bottom = textRect.top + textHeight;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        textPaint.setColor(progressTextDescribeColor);
        //文字绘制
        canvas.drawText(stepDesc, textRect.centerX(), baseline, textPaint);

        String stepData = stepInfo.getStepData();
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.getTextBounds(stepData, 0, stepData.length(), textBounds);
        textWidth = textBounds.right - textBounds.left;
        textHeight = textBounds.bottom - textBounds.top;
        left = stepWidth * i + progressCircleRadius * (2 * i + 1) - textWidth / 2;
        if (i == 0) {
            left = 2;
        } else if (i == size - 1) {
            left = mWidth - textWidth - 2;
        }
        textRect.left = left;
        textRect.top = progressCircleRadius + progressTextHeight * 2;
        textRect.right = left + textWidth;
        textRect.bottom = textRect.top + textHeight;
        fontMetrics = textPaint.getFontMetricsInt();
        baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        textPaint.setColor(progressTextDateColor);
        //文字绘制
        canvas.drawText(stepData, textRect.centerX(), baseline, textPaint);
    }

    /**
     * 画线
     *
     * @param canvas
     * @param i
     */
    private void drawLine(Canvas canvas, int i) {
        if (i == size - 1) return;
        if (i >= step) {
            progressPaint.setColor(progressLineNormalColor);
            canvas.drawLine(stepWidth * i + progressCircleRadius * (i + 1) * 2, progressMarginTop + progressCircleRadius,
                    stepWidth * (i + 1) + progressCircleRadius * (i + 1) * 2, progressMarginTop + progressCircleRadius, progressPaint);
            if (i == step) {
                progressPaint.setColor(progressCircleSelectColor);
                int progressWith = stepWidth * progress / 100;
                canvas.drawLine(stepWidth * i + progressCircleRadius * (i + 1) * 2, progressMarginTop + progressCircleRadius,
                        stepWidth * i + progressCircleRadius * (i + 1) * 2 + progressWith, progressMarginTop + progressCircleRadius, progressPaint);
            }
        } else {
            progressPaint.setColor(progressCircleSelectColor);
            canvas.drawLine(stepWidth * i + progressCircleRadius * (i + 1) * 2, progressMarginTop + progressCircleRadius,
                    stepWidth * (i + 1) + progressCircleRadius * (i + 1) * 2, progressMarginTop + progressCircleRadius, progressPaint);
        }
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
        if (mType == Type.FAIL && i == step) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_step_progress_fail);
            canvas.drawBitmap(bitmap, stepWidth * i + progressCircleRadius * (i * 2 + 1) - bitmap.getWidth() / 2,
                    progressMarginTop + progressCircleRadius - bitmap.getHeight() / 2, progressPaint);
            bitmap.recycle();
            return;
        }
        if (step == size - 1 && step == i) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_step_progress_success);
            canvas.drawBitmap(bitmap, stepWidth * i + progressCircleRadius * (i * 2 + 1) - bitmap.getWidth() / 2 - dp2px(3),
                    progressMarginTop + progressCircleRadius - bitmap.getHeight() / 2, progressPaint);
            bitmap.recycle();
            return;
        }
        canvas.drawCircle(stepWidth * i + progressCircleRadius * (i * 2 + 1), progressMarginTop + progressCircleRadius, progressCircleRadius, progressPaint);
    }

    /**
     * 设置数据
     *
     * @param step
     */
    public void setDatas(List<StepInfo> datas, int step, int progress, Type type) {
        if (datas == null || datas.size() == 0) return;
        this.datas = datas;
        this.step = step;
        this.progress = progress;
        this.mType = type;
        this.size = datas.size();
        post(() -> {
            stepWidth = (mWidth - progressCircleRadius * size * 2) / (size - 1);
            invalidate();
        });
    }

    /**
     * 设置数据
     *
     * @param step
     */
    public void setDatas(List<StepInfo> datas, int step, int progress) {
        setDatas(datas, step, progress, Type.NORMAL);
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
