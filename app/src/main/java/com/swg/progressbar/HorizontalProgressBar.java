package com.swg.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

/**
 * Created by Allen on 2017/5/14.
 * <p>
 * 自定义水平进度条
 */

public class HorizontalProgressBar extends View {

    /**
     * 进度条背景颜色
     */
    private int bgColor = 0xFFe1e5e8;
    /**
     * 进度条颜色
     */
    private int progressColor = 0xFFf66b12;
    /**
     * 进度条画笔的宽度
     */
    private int progressWidth;

    /**
     * 进度数字背景颜色1
     */
    private int tipRectColor1;

    /**
     * 进度数字背景颜色1
     */
    private int tipRectColor2;

    /**
     * 进度数字颜色1
     */
    private int textColor1;

    /**
     * 进度数字颜色2
     */
    private int textColor2;

    /**
     * 百分比文字字体大小
     */
    private int textSize;


    /**
     * 百分比提示框画笔的宽度
     */
    private int tipPaintWidth;

    /**
     * 百分比提示框的高度
     */
    private int tipRectHeight;

    /**
     * 百分比提示框的宽度
     */
    private int tipRectWidth;

    /**
     * 三角形的高
     */
    private int triangleHeight;

    /**
     * 是否显示动画
     */
    private boolean mShowAnim;

    /**
     * 圆角矩形的圆角半径
     */
    private int roundRectRadius;

    /**
     * 动画执行时间
     */
    private int duration = 1000;

    /**
     * 动画延时启动时间
     */
    private int startDelay = 500;

    /**
     * 进度条距离提示框的高度
     */
    private int progressMarginTop;

    /**
     * 进度条画笔
     */
    private Paint progressPaint;

    /**
     * 提示框背景画笔
     */
    private Paint tipPaint;

    /**
     * 文字画笔
     */
    private Paint textPaint;

    private int mWidth;
    private int mHeight;
    private int mViewHeight;
    /**
     * 进度
     */
    private float mProgress;

    /**
     * 当前进度
     */
    private float currentProgress;

    /**
     * 进度动画
     */
    private ValueAnimator progressAnimator;


    /**
     * 画三角形的path
     */
    private Path path = new Path();

    private Rect textRect = new Rect();

    private String textString = "0.00";


    /**
     * 绘制提示框的矩形
     */
    private RectF rectF = new RectF();

    /**
     * 进度监听回调
     */
    private ProgressListener progressListener;

    private String TAG = "HorizontalProgressBar";

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initPaint();
    }

    /**
     * 初始化画笔宽度及view大小
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        progressColor = attributes.getColor(R.styleable.HorizontalProgressBar_progress_color, Color.parseColor("#2A5BFF"));
        bgColor = attributes.getColor(R.styleable.HorizontalProgressBar_progress_color, Color.WHITE);
        progressWidth = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_progress_width, dp2px(6));
        tipRectColor1 = attributes.getColor(R.styleable.HorizontalProgressBar_text_bg_color1, Color.parseColor("#DEDEDE"));
        tipRectColor2 = attributes.getColor(R.styleable.HorizontalProgressBar_text_bg_color2, Color.parseColor("#2A5BFF"));
        textColor1 = attributes.getColor(R.styleable.HorizontalProgressBar_text_color1, Color.parseColor("#333333"));
        textColor2 = attributes.getColor(R.styleable.HorizontalProgressBar_text_color2, Color.parseColor("#FFFFFF"));
        textSize = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_progress_width, sp2px(10));
        tipRectWidth = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_text_bg_width, dp2px(45));
        tipRectHeight = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_text_bg_height, dp2px(20));
        tipPaintWidth = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_text_paint_width, sp2px(2));
        triangleHeight = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_triangle_height, dp2px(5));
        roundRectRadius = attributes.getDimensionPixelSize(R.styleable.HorizontalProgressBar_triangle_height, dp2px(10));
        mShowAnim = attributes.getBoolean(R.styleable.HorizontalProgressBar_progress_anim, true);
        duration = attributes.getInt(R.styleable.HorizontalProgressBar_progress_anim_duration, 1000);
        attributes.recycle();
        progressMarginTop = dp2px(5);
        //view真实的高度
        mViewHeight = tipRectHeight + triangleHeight + progressWidth + progressMarginTop;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        progressPaint = getPaint(progressWidth, progressColor, Paint.Style.STROKE);
        tipPaint = getPaint(tipRectWidth, progressColor, Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }


    /**
     * 统一处理paint
     *
     * @param strokeWidth
     * @param color
     * @param style
     * @return
     */
    private Paint getPaint(int strokeWidth, int color, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        return paint;
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mWidth = w;
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
        progressPaint.setColor(bgColor);
        int startY = tipRectHeight + triangleHeight + progressMarginTop;
        canvas.drawLine(getPaddingLeft() + progressWidth / 2,
                startY,
                mWidth - getPaddingEnd() - progressWidth / 2,
                startY,
                progressPaint);
        if (currentProgress > 0) {
            progressPaint.setColor(progressColor);
            if (currentProgress > mWidth) {
                currentProgress = mWidth;
            }
            Log.d(TAG, getPaddingLeft() + progressWidth / 2 + "");
            Log.d(TAG, currentProgress + "");
            canvas.drawLine(getPaddingLeft() + progressWidth / 2,
                    startY,
                    currentProgress - getPaddingEnd() - progressWidth / 2,
                    startY,
                    progressPaint);
        }
        drawRoundRectAndText(canvas);
    }

    /**
     * 绘制圆角矩形
     *
     * @param canvas
     */
    private void drawRoundRectAndText(Canvas canvas) {
        if (currentProgress >= mWidth) {
            tipPaint.setColor(tipRectColor2);
            textPaint.setColor(textColor2);
        } else {
            tipPaint.setColor(tipRectColor1);
            textPaint.setColor(textColor1);
        }
        rectF.set(mWidth - tipRectWidth, 0, mWidth, tipRectHeight);
        canvas.drawRoundRect(rectF, roundRectRadius, roundRectRadius, tipPaint);

        path.moveTo(mWidth - tipRectWidth / 2 - triangleHeight, tipRectHeight);
        path.lineTo(mWidth - tipRectWidth / 2, tipRectHeight + triangleHeight);
        path.lineTo(mWidth - tipRectWidth / 2 + triangleHeight, tipRectHeight);
        path.reset();

        Rect textBounds = new Rect();
        textPaint.getTextBounds(textString, 0, textString.length(), textBounds);
        int textWidth = textBounds.right - textBounds.left;
        int textHeight = textBounds.bottom - textBounds.top;
        textRect.left = mWidth - tipRectWidth / 2 - textWidth / 2;
        textRect.top = tipRectHeight / 2 - textHeight / 2;
        textRect.right = mWidth - tipRectWidth / 2 + textWidth / 2;
        textRect.bottom = tipRectHeight / 2 + textHeight / 2;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString + "%", textRect.centerX(), baseline, textPaint);
    }

    /**
     * 进度移动动画  通过插值的方式改变移动的距离
     */
    private void initAnimation() {
        Log.d(TAG, mProgress + "");
        progressAnimator = ValueAnimator.ofFloat(0, mProgress);
        progressAnimator.setDuration(duration);
        progressAnimator.setStartDelay(startDelay);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                //进度数值只显示整数，我们自己的需求，可以忽略
                textString = formatNumTwo(value);
                //把当前百分比进度转化成view宽度对应的比例
                currentProgress = value * mWidth / 100;
                //进度回调方法
                if (progressListener != null) {
                    progressListener.currentProgressListener(value);
                }
                invalidate();
            }
        });
        progressAnimator.start();
    }

    /**
     * 设置进度条带动画效果
     *
     * @param progress
     * @return
     */
    public void setCurrentProgress(float progress) {
        if(mProgress != progress){
            mProgress = progress;
            post(() -> {
                if (mShowAnim) {
                    initAnimation();
                } else {
                    Log.d(TAG, "mWidth = " + mWidth);
                    currentProgress = progress * mWidth / 100;
                    textString = formatNumTwo(progress);
                    invalidate();
                }
            });
        }

    }

    /**
     * 回调接口
     */
    public interface ProgressListener {
        void currentProgressListener(float currentProgress);
    }

    /**
     * 回调监听事件
     *
     * @param listener
     * @return
     */
    public HorizontalProgressBar setProgressListener(ProgressListener listener) {
        progressListener = listener;
        return this;
    }

    /**
     * 格式化数字(保留两位小数)
     *
     * @param money
     * @return
     */
    public static String formatNumTwo(double money) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(money);
    }

    /**
     * 格式化数字(保留一位小数)
     *
     * @param money
     * @return
     */
    public static String formatNum(int money) {
        DecimalFormat format = new DecimalFormat("0");
        return format.format(money);
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