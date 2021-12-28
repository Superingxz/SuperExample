package com.myz.example.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.myz.example.R;


/**
 * 刻度盘
 */
public class DialView extends View {
    private final int DEFAULT_TEXT_SIZE = 12;
    private final int DEFAULT_STROKE_WIDTH = 8;
    private final int DEFAULT_RADIUS = 128;
    private final int DEFAULT_SCALE_STROKE_WIDTH = 20;
    private final int DEFAULT_ANIM_TIME = 1;//动画默认时间1s
    Path borderPath;
    RectF backRectF;
    Path zzPath;
    private int textSize;
    private int scaleColor;
    private int strokeWidth;
    private int mRealRadius;
    private RectF mRect;
    private RectF mScaleRect;
    private int scaleStrokeWidth;
    private int scaleRadius;
    private int smallScaleRadius;
    private int circleSmallScaleRadius;
    private int animTime;
    private double currentValue = 0;
    private double oldValue = 0;
    private int radiusDial = dp2px(DEFAULT_RADIUS);
    private Paint borderPaint;
    private Paint bgPaint;
    private Paint scalePaint;
    private Paint scaleBorderPaint;
    private Paint scaleProgressPaint;
    private Paint numberBgPaint;
    private Paint numberPaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint bigTextPaint;
    private Paint linePaint;
    private Paint circleBluePaint;
    private Paint circleWhitePaint;
    private Path pointerPath;
    private Path linePath;
    private Paint.FontMetrics fontMetrics;
    private Paint.FontMetrics numFontMetrics;
    private int width;

    public DialView(Context context) {
        this(context, null);
    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    //初始化属性
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        textSize = (int) attributes.getDimension(R.styleable.ScaleView_text_size, sp2px(DEFAULT_TEXT_SIZE));
        scaleColor = attributes.getColor(R.styleable.ScaleView_scale_color, Color.parseColor("#6D77F5"));
        strokeWidth = (int) attributes.getDimension(R.styleable.ScaleView_stroke_width, dp2px(DEFAULT_STROKE_WIDTH));
        scaleStrokeWidth = (int) attributes.getDimension(R.styleable.ScaleView_scale_stroke_width, dp2px(DEFAULT_SCALE_STROKE_WIDTH));
        animTime = attributes.getInteger(R.styleable.ScaleView_anim_time, DEFAULT_ANIM_TIME) * 1000;
        attributes.recycle();
    }

    //初始化画笔
    private void initPaint() {
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setColor(scaleColor);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(scaleColor);

        bigTextPaint = new Paint();
        bigTextPaint.setAntiAlias(true);
        bigTextPaint.setTextSize(dp2px(22));
        bigTextPaint.setTextAlign(Paint.Align.CENTER);
        bigTextPaint.setColor(scaleColor);

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setTextSize(dp2px(20));
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setColor(scaleColor);

        scalePaint = new Paint();
        scalePaint.setAntiAlias(true);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeWidth(scaleStrokeWidth);
        scalePaint.setColor(scaleColor);

        scaleBorderPaint = new Paint();
        scaleBorderPaint.setAntiAlias(true);
        scaleBorderPaint.setStyle(Paint.Style.STROKE);
        scaleBorderPaint.setStrokeWidth(dp2px(2));
        scaleBorderPaint.setColor(Color.parseColor("#384D78"));

        circleBluePaint = new Paint();
        circleBluePaint.setAntiAlias(true);
        circleBluePaint.setStyle(Paint.Style.FILL);
        circleBluePaint.setColor(scaleColor);

        circleWhitePaint = new Paint();
        circleWhitePaint.setAntiAlias(true);
        circleWhitePaint.setStyle(Paint.Style.STROKE);
        circleWhitePaint.setColor(Color.parseColor("#EDEEFE"));
        circleWhitePaint.setStrokeWidth(dp2px(8));

        scaleProgressPaint = new Paint();
        scaleProgressPaint.setAntiAlias(true);
        scaleProgressPaint.setStyle(Paint.Style.FILL);
        scaleProgressPaint.setColor(Color.parseColor("#E74D4F"));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setColor(Color.parseColor("#E39459"));
        pointPaint.setStrokeWidth(dp2px(3));
        pointPaint.setStrokeCap(Paint.Cap.ROUND);

        numberBgPaint = new Paint();
        numberBgPaint.setAntiAlias(true);
        numberBgPaint.setStyle(Paint.Style.FILL);
        numberBgPaint.setColor(Color.parseColor("#F1C250"));

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(3));
        linePaint.setColor(Color.parseColor("#E3A79C"));

        pointerPath = new Path();
        linePath = new Path();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int mWidth, mHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + radiusDial * 2 + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(mWidth, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + radiusDial * 2 + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(mHeight, heightSize);
            }
        }
        width = Math.min(mWidth, mHeight);
        setMeasuredDimension(width, width);

        radiusDial = Math.min((getMeasuredWidth() - getPaddingLeft() - getPaddingRight()),
                (getMeasuredHeight() - getPaddingTop() - getPaddingBottom())) / 2;
        mRealRadius = radiusDial - strokeWidth / 2;
        mRect = new RectF(-mRealRadius, -mRealRadius, mRealRadius, mRealRadius);

        pointerPath.addArc(mRect, 130, 280);
        pointerPath.moveTo((float) (radiusDial * Math.cos(Math.toRadians(130))), (float) (radiusDial * Math.sin(Math.toRadians(130))) - (strokeWidth / 2));
        pointerPath.lineTo((float) (radiusDial * Math.cos(Math.toRadians(50))), (float) (radiusDial * Math.sin(Math.toRadians(130))) - (strokeWidth / 2));


        //画边框底部直线
        linePath = new Path();
        RectF rectF = new RectF();
        rectF.left = mRect.left + (float) (strokeWidth / 2);
        rectF.top = mRect.top + (float) (strokeWidth / 2);
        rectF.right = mRect.right - (float) (strokeWidth / 2);
        rectF.bottom = mRect.bottom - (float) (strokeWidth / 2);
        linePath.arcTo(rectF, 132, 276, false);
        linePath.moveTo((float) ((radiusDial - (strokeWidth / 2)) * Math.cos(Math.toRadians(131))) + 6
                , (float) ((radiusDial - (strokeWidth / 2)) * Math.sin(Math.toRadians(131))) - (strokeWidth / 2));
        linePath.lineTo((float) ((radiusDial - (strokeWidth / 2)) * Math.cos(Math.toRadians(50)))
                , (float) ((radiusDial - (strokeWidth / 2)) * Math.sin(Math.toRadians(131))) - (strokeWidth / 2));

        //画刻度边框
        scaleRadius = mRealRadius - (strokeWidth / 2) - 10;
        mScaleRect = new RectF(-scaleRadius, -scaleRadius, scaleRadius, scaleRadius);

        //画边框开始
        borderPath = new Path();
        borderPath.moveTo((float) (scaleRadius * Math.cos(Math.toRadians(145))), (float) (scaleRadius * Math.sin(Math.toRadians(145))));
        borderPath.arcTo(mScaleRect, 145, 250);
        smallScaleRadius = scaleRadius - scaleStrokeWidth;//计算小圆半径
        borderPath.moveTo((float) (scaleRadius * Math.cos(Math.toRadians(145))), (float) (scaleRadius * Math.sin(Math.toRadians(145))));
        borderPath.lineTo((float) (smallScaleRadius * Math.cos(Math.toRadians(145))), (float) (smallScaleRadius * Math.sin(Math.toRadians(145))));
        RectF smallRectF = new RectF(-scaleRadius + scaleStrokeWidth, -scaleRadius + scaleStrokeWidth, scaleRadius - scaleStrokeWidth, scaleRadius - scaleStrokeWidth);
        borderPath.arcTo(smallRectF, 145, 250);
        borderPath.lineTo((float) (scaleRadius * Math.cos(Math.toRadians(35))), (float) (scaleRadius * Math.sin(Math.toRadians(35))));
        float angle = 0;
        for (int i = 1; i <= 10; i++) {
            angle += 250 / 10;
            borderPath.moveTo((float) (scaleRadius * Math.cos(Math.toRadians(145 + angle)))
                    , (float) (scaleRadius * Math.sin(Math.toRadians(145 + angle))));
            borderPath.lineTo((float) (smallScaleRadius * Math.cos(Math.toRadians(145 + angle)))
                    , (float) (smallScaleRadius * Math.sin(Math.toRadians(145 + angle))));
        }
        circleSmallScaleRadius = smallScaleRadius + (smallScaleRadius / 14);
        borderPath.moveTo((float) (circleSmallScaleRadius * Math.cos(Math.toRadians(145)))
                , (float) (circleSmallScaleRadius * Math.sin(Math.toRadians(145))));
        RectF circleRectF = new RectF(-circleSmallScaleRadius, -circleSmallScaleRadius, circleSmallScaleRadius, circleSmallScaleRadius);
        borderPath.arcTo(circleRectF, 145, 250, false);

        fontMetrics = bigTextPaint.getFontMetrics();
        numFontMetrics = numberPaint.getFontMetrics();

        backRectF = new RectF(-(mRealRadius / 5), mRealRadius / 6 + 20, mRealRadius / 5, mRealRadius / 6 + 80);


        zzPath = new Path();
        zzPath.moveTo(mRealRadius - strokeWidth, -3);
        zzPath.lineTo(0, -dp2px(5));
        zzPath.lineTo(0, dp2px(5));
        zzPath.lineTo(mRealRadius - strokeWidth, 3);
        zzPath.close();
    }

    //画边框
    private void drawBorder(Canvas canvas) {

        canvas.translate(getPaddingLeft() + radiusDial, getPaddingTop() + radiusDial);
        canvas.drawArc(mRect, 130, 280, false, bgPaint);
        canvas.drawPath(pointerPath, borderPaint);


        //画边框底部直线
        canvas.drawPath(linePath, linePaint);

        if (oldValue != 0) {
            //画旧数据进度
            RectF oldRectF = new RectF(-scaleRadius + (scaleStrokeWidth / 2), -scaleRadius + (scaleStrokeWidth / 2), scaleRadius - (scaleStrokeWidth / 2), scaleRadius - (scaleStrokeWidth / 2));
            canvas.drawArc(oldRectF, 145, (float) (oldValue / 10 * 250), false, scalePaint);
        }

        canvas.drawPath(borderPath, scaleBorderPaint);


        //刻度点
        int pointRadiu = smallScaleRadius - 6;
        int textRadiu = (int) (pointRadiu / 7 * 6.5);
        int index = 0;
        for (float i = 0; i < 100; i++) {
            float pointAngle = i / 100 * 250;
            canvas.drawPoint((float) (pointRadiu * Math.cos(Math.toRadians(145 + pointAngle))), (float) (pointRadiu * Math.sin(Math.toRadians(145 + pointAngle))), pointPaint);

        }
        float pointAngle = 0;
        for (int i = 0; i < 11; i++) { //画数字 0-100
            canvas.drawText(String.valueOf(index * 1), (float) (textRadiu * Math.cos(Math.toRadians(145 + pointAngle))), (float) (textRadiu * Math.sin(Math.toRadians(145 + pointAngle))), textPaint);
            pointAngle += 250 / 10;
            index += 1;
        }


        int textBaseLine = (int) (0 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        canvas.drawText("功率", 0, textBaseLine + (mRealRadius / 2 + 25), bigTextPaint);
        canvas.drawRoundRect(backRectF, 10f, 10f, numberBgPaint);

        int textNumLine = (int) ((numFontMetrics.bottom - fontMetrics.top) / -fontMetrics.bottom);
        canvas.drawText(String.format("%.2f", currentValue) + "W", 0, textNumLine + (mRealRadius / 6 + 65), numberPaint);

        canvas.save();
    }

    //绘制指针
    private void drawPointer(Canvas canvas) {
        int currentDegree = (int) (currentValue * 25 + 145);
        canvas.rotate(currentDegree);

        canvas.drawPath(zzPath, scaleProgressPaint);
    }

    //绘制中心圆
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, dp2px(30), circleBluePaint);
        canvas.drawCircle(0, 0, dp2px(20), circleWhitePaint);
    }

    //获取值
    public void setData(double newData) {
        if (newData <= 0) currentValue = 0;
        else if (newData >= 10) currentValue = 10;
        else currentValue = newData;
        if (currentValue >= oldValue)
            oldValue = currentValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBorder(canvas);
        drawPointer(canvas);
        drawCircle(canvas);
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}
