package com.myz.example.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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
    private val DEFAULT_RADIUS = 128
    private val DEFAULT_SCALE_STROKE_WIDTH = 20
    private val DEFAULT_ANIM_TIME = 1 //动画默认时间1s

    private var scaleWidth: Int = 0

    private var radiusDial = dp2px(DEFAULT_RADIUS)

    private var textSize: Int = 0
    private var scaleColor: Int = 0
    private var strokeWidth: Int = 0
    private var mRealRadius: Int = 0
    private var scaleStrokeWidth = 0
    private var scaleMargin: Float = 0f // 刻度外边距

    private lateinit var mRect: RectF
    private lateinit var borderPaint: Paint
    private lateinit var pointerPath: Path
    private lateinit var bgPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var paintDegree: Paint // 刻度

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
        scaleStrokeWidth =
            attributes.getDimension(
                R.styleable.ScaleDialView_scale_stroke_width,
                dp2px(DEFAULT_SCALE_STROKE_WIDTH).toFloat()
            )
                .toInt()
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
            mHeight = paddingTop + radiusDial * 2 + paddingBottom
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = mHeight.coerceAtMost(heightSize)
            }
        }
        scaleWidth = mWidth.coerceAtMost(mHeight)
        setMeasuredDimension(scaleWidth, scaleWidth)

        radiusDial =
            (measuredWidth - paddingLeft - paddingRight).coerceAtMost(measuredHeight - paddingTop - paddingBottom) / 2
        mRealRadius = radiusDial - strokeWidth / 2
        mRect = RectF(
            (-mRealRadius).toFloat(), (-mRealRadius).toFloat(),
            mRealRadius.toFloat(), mRealRadius.toFloat()
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

        canvas?.translate(-radiusDial.toFloat(), -radiusDial.toFloat())
        // 刻度
        for (i in 0..15) {
            var paintDegree = Paint()
            paintDegree.color = scaleColor
            if (i % 5 == 0) {
                paintDegree.strokeWidth = dp2px(2).toFloat()
                paintDegree.textSize = sp2px(30).toFloat()
                canvas?.drawLine(
                    scaleStrokeWidth + scaleMargin,
                    radiusDial.toFloat(),
                    scaleStrokeWidth + scaleMargin + dp2px(25).toFloat(),
                    radiusDial.toFloat(),
                    paintDegree
                )
            } else {
                paintDegree.strokeWidth = dp2px(1).toFloat()
                paintDegree.textSize = sp2px(20).toFloat()
                canvas?.drawLine(
                    scaleStrokeWidth + scaleMargin,
                    radiusDial.toFloat(),
                    scaleStrokeWidth + scaleMargin + dp2px(10).toFloat(),
                    radiusDial.toFloat(),
                    paintDegree
                )
            }

            canvas?.rotate(180f / 15f, scaleWidth / 2f, scaleWidth / 2f)

        }
    }

    /**
     * 绘制指针
     */
    private fun drawPointer(canvas: Canvas?) {

    }

    private fun drawCircle(canvas: Canvas?) {

    }

    /**
     * 设置最大值
     */
    fun setMax(max: Int) {
        maxValue = max
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