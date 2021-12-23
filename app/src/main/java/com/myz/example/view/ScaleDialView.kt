package com.myz.example.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * <pre>
 *     author: myz
 *     email : myz@huxijia.com
 *     time  : 2021/12/23 18:46
 *     desc  : 自定义刻度盘
 * </pre>
 */
class ScaleDialView : View {
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

    }

    /**
     * 画笔
     */
    private fun initPaint() {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
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