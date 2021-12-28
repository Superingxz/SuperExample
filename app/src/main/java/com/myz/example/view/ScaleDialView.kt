package com.myz.example.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.myz.example.R


/**
 * <pre>
 *     author: myz
 *     email : myz@huxijia.com
 *     time  : 2021/12/23 18:46
 *     desc  : 自定义刻度盘
 * </pre>
 */
class ScaleDialView : View {
    private val DEFAULT_TEXT_SIZE = 12
    private val DEFAULT_STROKE_WIDTH = dp2px(1)
    private val DEFAULT_INNER_STROKE_WIDTH = dp2px(10)
    private val DEFAULT_RADIUS = 150
    private val DEFAULT_SCALE_MARGIN = 18
    private val DEFAULT_SCALE_STROKE_WIDTH = 1
    private val DEFAULT_SCALE_STROKE_WIDTH_LARGE = 1
    private val DEFAULT_SCALE_INNER_MARGIN = 2

    private var scaleWidth: Int = 0

    private var radiusDial = dp2px(DEFAULT_RADIUS)

    private var textSize: Int = 0
    private var scaleColor: Int = 0 // 外环颜色
    private var strokeWidth: Int = 0 // 外环宽度
    private var mRealRadius: Int = 0 // 实际半径
    private var scaleStrokeWidth = 0 // 小刻度长度
    private var scaleStrokeWidthLarge = 0 // 大刻度长度
    private var scaleMargin: Float = 0f // 刻度外边距
    private var innerColor1: Int = 0 // 内环1
    private var innerColor2: Int = 0 // 内环2
    private var innerColor3: Int = 0 // 内环3
    private var innerStrokeWidth: Float = 0f // 内环宽度
    private var scaleInnerMargin: Float = 0f // 刻度内边距
    private lateinit var mRect: RectF // 外环
    private lateinit var borderPaint: Paint
    private lateinit var bgPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var titlePaint: Paint
    private lateinit var innerPaint1: Paint // 内环1
    private lateinit var innerPaint2: Paint // 内环2
    private lateinit var innerPaint3: Paint // 内环3
    private lateinit var mInnerRect: RectF // 内环
    private lateinit var innerLeftPaint: Paint // 内环左边
    private lateinit var innerRightPaint: Paint // 内环右边
    private lateinit var mInnerGrayRect: RectF // 灰色内环

    private lateinit var circlePaint: Paint // 圆心

    private lateinit var scaleProgressPaint: Paint

    private var maxValue: Int = 1500
    private var currentValue: Int = 0
    private var oldValue: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet) {
        initAttrs(context, attrs)
        initPaint()
    }

    /**
     * 属性
     */
    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ScaleDialView)
        textSize = attributes.getDimension(
            R.styleable.ScaleDialView_text_size,
            sp2px(DEFAULT_TEXT_SIZE).toFloat()
        )
            .toInt()
        scaleColor =
            attributes.getColor(
                R.styleable.ScaleDialView_scale_color,
                Color.parseColor("#C4C5CD")
            )
        strokeWidth = attributes.getDimension(
            R.styleable.ScaleDialView_stroke_width,
            dp2px(DEFAULT_STROKE_WIDTH).toFloat()
        )
            .toInt()
        scaleMargin =
            attributes.getDimension(
                R.styleable.ScaleDialView_scale_margin,
                dp2px(DEFAULT_SCALE_MARGIN).toFloat()
            )
        scaleStrokeWidth =
            attributes.getDimension(
                R.styleable.ScaleDialView_scale_stroke_width,
                dp2px(DEFAULT_SCALE_STROKE_WIDTH).toFloat()
            )
                .toInt()
        scaleStrokeWidthLarge =
            attributes.getDimension(
                R.styleable.ScaleDialView_scale_stroke_width_large,
                dp2px(DEFAULT_SCALE_STROKE_WIDTH_LARGE).toFloat()
            )
                .toInt()
        innerColor1 =
            attributes.getColor(
                R.styleable.ScaleDialView_inner_color1,
                Color.parseColor("#3BA6DD")
            )
        innerColor2 =
            attributes.getColor(
                R.styleable.ScaleDialView_inner_color2,
                Color.parseColor("#52CC52")
            )
        innerColor3 =
            attributes.getColor(
                R.styleable.ScaleDialView_inner_color3,
                Color.parseColor("#FF7E2D")
            )
        innerStrokeWidth =
            attributes.getDimension(
                R.styleable.ScaleDialView_inner_stroke_width,
                dp2px(DEFAULT_INNER_STROKE_WIDTH).toFloat()
            )
        scaleInnerMargin =
            attributes.getDimension(
                R.styleable.ScaleDialView_scale_inner_margin,
                dp2px(DEFAULT_SCALE_INNER_MARGIN).toFloat()
            )
        attributes.recycle()
    }

    /**
     * 画笔
     */
    private fun initPaint() {
        borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = strokeWidth.toFloat()
        borderPaint.color = scaleColor

        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = Color.WHITE

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize.toFloat()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = scaleColor

        titlePaint = Paint()
        titlePaint.isAntiAlias = true
        titlePaint.textSize = textSize.toFloat()
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.color = Color.parseColor("#ff333333")

        innerPaint1 = Paint()
        innerPaint1.style = Paint.Style.STROKE
        innerPaint1.isAntiAlias = true
        innerPaint1.color = innerColor1
        innerPaint1.strokeWidth = innerStrokeWidth

        innerPaint2 = Paint()
        innerPaint2.style = Paint.Style.STROKE
        innerPaint2.isAntiAlias = true
        innerPaint2.color = innerColor2
        innerPaint2.strokeWidth = innerStrokeWidth

        innerPaint3 = Paint()
        innerPaint3.style = Paint.Style.STROKE
        innerPaint3.isAntiAlias = true
        innerPaint3.color = innerColor3
        innerPaint3.strokeWidth = innerStrokeWidth

        innerLeftPaint = Paint()
        innerLeftPaint.style = Paint.Style.FILL
        innerLeftPaint.isAntiAlias = true
        innerLeftPaint.color = innerColor1

        innerRightPaint = Paint()
        innerRightPaint.style = Paint.Style.FILL
        innerRightPaint.isAntiAlias = true
        innerRightPaint.color = innerColor3

        scaleProgressPaint = Paint()
        scaleProgressPaint.isAntiAlias = true
        scaleProgressPaint.style = Paint.Style.STROKE
        scaleProgressPaint.color = Color.parseColor("#666666")
        scaleProgressPaint.strokeWidth = dp2px(2).toFloat()

        circlePaint = Paint()
        circlePaint.isAntiAlias = true
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = Color.parseColor("#666666")
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var mWidth: Int
        var mHeight: Int
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize
        } else {
            mWidth = paddingLeft + radiusDial * 2 + paddingRight
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = mWidth.coerceAtMost(widthSize)
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize
        } else {
            mHeight = paddingTop + (radiusDial * 1.5f).toInt() + paddingBottom
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = mHeight.coerceAtMost(heightSize)
            }
        }
        scaleWidth = mWidth
        setMeasuredDimension(scaleWidth, mHeight)

        radiusDial =
            (measuredWidth - paddingLeft - paddingRight) / 2
        mRealRadius = radiusDial - strokeWidth / 2
        mRect = RectF(
            (-mRealRadius).toFloat(), (-mRealRadius).toFloat(),
            mRealRadius.toFloat(), mRealRadius.toFloat()
        )
        val offset: Int =
            strokeWidth + dp2px(25) + (scaleMargin.toInt() * 1.75f).toInt() + scaleInnerMargin.toInt()
        mInnerRect = RectF(
            (-mRealRadius + offset).toFloat(), (-mRealRadius + offset).toFloat(),
            (mRealRadius - offset).toFloat(), (mRealRadius - offset).toFloat()
        )

        mInnerGrayRect = RectF(
            (-mRealRadius.toFloat() + offset.toFloat() + innerStrokeWidth / 2f + dp2px(8)),
            (-mRealRadius.toFloat() + offset.toFloat() + innerStrokeWidth / 2f + dp2px(8)),
            (mRealRadius.toFloat() - offset.toFloat() - innerStrokeWidth / 2f - dp2px(8)),
            (mRealRadius.toFloat() - offset.toFloat() - innerStrokeWidth / 2f - dp2px(8))
        )
    }

    /**
     * 绘制中心圆
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBorder(canvas)
        drawPointer(canvas)
        drawCircle(canvas)
    }

    /**
     * 画边框
     */
    private fun drawBorder(canvas: Canvas?) {
        // 外边框
        canvas?.translate(
            (paddingLeft + radiusDial).toFloat(),
            (paddingTop + radiusDial).toFloat()
        )
        canvas?.drawArc(mRect, 180f, 180f, false, borderPaint)


        // 刻度
        canvas?.translate(
            -(paddingLeft + radiusDial).toFloat(),
            -(paddingTop + radiusDial).toFloat()
        )
        for (i in 0..15) {
            val paintDegree = Paint()
            paintDegree.isAntiAlias = true
            paintDegree.color = Color.parseColor("#ff666666")
            if (i % 5 == 0) {
                paintDegree.strokeWidth = dp2px(scaleStrokeWidthLarge).toFloat()
                paintDegree.textSize = sp2px(15).toFloat()
                canvas?.drawLine(
                    scaleMargin,
                    radiusDial.toFloat(),
                    scaleMargin + dp2px(22).toFloat(),
                    radiusDial.toFloat(),
                    paintDegree
                )
            } else {
                paintDegree.strokeWidth = dp2px(scaleStrokeWidth).toFloat()
                canvas?.drawLine(
                    scaleMargin * 1.5f,
                    radiusDial.toFloat(),
                    scaleMargin * 1.5f + dp2px(10).toFloat(),
                    radiusDial.toFloat(),
                    paintDegree
                )
            }

            canvas?.rotate(180f / 15f, scaleWidth / 2f, scaleWidth / 2f)

        }

        canvas?.rotate(-(180 / 15f) * 16f, scaleWidth / 2f, scaleWidth / 2f)

        // 绘制刻度
        drawScaleNumber(canvas)

        // 内环
        canvas?.translate(
            (paddingLeft + radiusDial).toFloat(),
            (paddingTop + radiusDial).toFloat()
        )
        canvas?.drawArc(mInnerRect, 180f, 60f, false, innerPaint1)
        canvas?.drawArc(mInnerRect, 240f, 60f, false, innerPaint2)
        canvas?.drawArc(mInnerRect, 300f, 60f, false, innerPaint3)

        // 内环灰色半圈
        canvas?.drawArc(mInnerGrayRect, 180f, 180f, false, borderPaint)

        // 绘制左右半圆
        drawHalfCircle(canvas)

        // 绘制中间文本
        val textBaseLine =
            (0 + (textPaint.fontMetrics!!.bottom - textPaint.fontMetrics!!.top) / 2 - textPaint.fontMetrics!!.bottom).toInt() - dp2px(
                25
            )
        val data: Double = (currentValue / 100f).toDouble()
        canvas?.drawText(
            "$data (L/s)",
            0f,
            (textBaseLine + (mRealRadius / 2 + 20)).toFloat(),
            titlePaint
        )
    }

    /**
     * 绘制左右半圆
     */
    private fun drawHalfCircle(canvas: Canvas?) {
        canvas?.save()

        // 内环左边偏移量
        val offset: Int =
            strokeWidth + dp2px(26) + (scaleMargin.toInt() * 1.75f).toInt() + scaleInnerMargin.toInt()

        // 左半圆
        canvas?.translate(
            -(paddingLeft.toFloat() + radiusDial.toFloat()) + offset,
            0f
        )

        val halfRectLeft = RectF(
            -innerStrokeWidth / 2f,
            -innerStrokeWidth / 2f,
            innerStrokeWidth / 2f,
            innerStrokeWidth / 2f
        )

        canvas?.drawArc(halfRectLeft, 0f, 180f, true, innerLeftPaint)

        // 右半圆
        canvas?.translate(
            ((paddingLeft.toFloat() + radiusDial.toFloat()) - offset) * 2f,
            0f
        )

        val halfRectRight = RectF(
            -innerStrokeWidth / 2f,
            -innerStrokeWidth / 2f,
            innerStrokeWidth / 2f,
            innerStrokeWidth / 2f
        )

        canvas?.drawArc(halfRectRight, 0f, 180f, true, innerRightPaint)

        canvas?.restore()
    }

    private fun drawScaleNumber(canvas: Canvas?) {
        canvas?.save()

        canvas?.translate(
            (paddingLeft + radiusDial).toFloat(),
            (paddingTop + radiusDial).toFloat()
        )

        val paintDegree = Paint()
        paintDegree.isAntiAlias = true
        paintDegree.color = Color.parseColor("#ff666666")
        paintDegree.strokeWidth = dp2px(scaleStrokeWidthLarge).toFloat()
        paintDegree.textSize = sp2px(15).toFloat()

        canvas?.rotate(-30f)

        // 三分之一位置刻度值
        val textWidthOneThirdLengthHalf =
            paintDegree.measureText((maxValue / 3).toString()) / 2f
        canvas?.drawText(
            (maxValue / 3).toString(),
            -textWidthOneThirdLengthHalf,
            -(mRealRadius.toFloat() - strokeWidth.toFloat() - dp2px(25) - scaleMargin + innerStrokeWidth + dp2px(
                6
            )),
            paintDegree
        )

        canvas?.rotate(60f)

        // 三分之二位置刻度值
        val textWidthTwoThirdLengthHalf =
            paintDegree.measureText(((maxValue / 3) * 2).toString()) / 2f
        canvas?.drawText(
            ((maxValue / 3) * 2).toString(),
            -textWidthTwoThirdLengthHalf,
            -(mRealRadius.toFloat() - strokeWidth.toFloat() - dp2px(25) - scaleMargin + innerStrokeWidth + dp2px(
                6
            )),
            paintDegree
        )

        canvas?.rotate(-30f)

        // 0位置刻度值
        canvas?.drawText(
            "0",
            -(mRealRadius - strokeWidth - dp2px(
                5
            )).toFloat(),
            dp2px(5).toFloat(),
            paintDegree
        )

        // 最大值位置刻度值
        canvas?.drawText(
            maxValue.toString(),
            (mRealRadius - strokeWidth - scaleMargin - dp2px(22).toFloat() - dp2px(
                5
            )),
            dp2px(15).toFloat(),
            paintDegree
        )

        canvas?.restore()
    }

    /**
     * 绘制指针
     */
    private fun drawPointer(canvas: Canvas?) {
        canvas?.save()
        val offset: Int =
            strokeWidth + dp2px(25) + scaleMargin.toInt() * 2 +
                    scaleInnerMargin.toInt() + innerStrokeWidth.toInt() // 指针顶端偏移量
        val pointerRadius = mRealRadius.toFloat() - offset // 指针长度
        val degrees = (currentValue / maxValue.toFloat()) * 180f + 180f
        canvas?.rotate(degrees)
        canvas?.drawLine(
            0f,
            0f,
            pointerRadius,
            0f, scaleProgressPaint
        )
        canvas?.restore()
    }

    /**
     * 绘制中心圆
     */
    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(
            0f,
            0f,
            dp2px(10).toFloat(),
            circlePaint
        )
    }

    /**
     * 设置最大值
     */
    fun setMax(max: Int) {
        maxValue = max
        invalidate()
    }

    /**
     * 获取值
     */
    fun setData(newData: Int) {
        currentValue = if (newData <= 0) 0 else if (newData >= maxValue) maxValue else newData
        if (currentValue >= oldValue) oldValue = currentValue
        invalidate()
    }

    private fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal.toFloat(),
            resources.displayMetrics
        )
            .toInt()
    }

    private fun sp2px(spVal: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spVal.toFloat(),
            resources.displayMetrics
        )
            .toInt()
    }
}