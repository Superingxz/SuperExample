package com.myz.example.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


/**
 * <pre>
 *     author: myz
 *     email : myz@huxijia.com
 *     time  : 2021/12/24 17:10
 *     desc  :
 * </pre>
 */
class MyView : View {
    private var scaleWidth: Int = 0 //view宽度
    private var scaleHeight //view的高度
            = 0
    private var radius //外层圆的半径
            = 0
    private var hcount = 0
    private var mcount = 0

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
    }


    override fun onDraw(canvas: Canvas) {
        scaleWidth = width
        scaleHeight = height
        radius = scaleWidth / 2 //外部圆盘的半径
        val paintCircle = Paint()
        paintCircle.style = Paint.Style.STROKE
        paintCircle.strokeWidth = 5f
        paintCircle.isAntiAlias = true
        //画出外层的圆盘
        canvas.drawCircle(
            (scaleWidth / 2).toFloat(),
            (scaleHeight / 2).toFloat(),
            radius.toFloat(),
            paintCircle
        )
        /**
         * 下面的代码要画出刻度值
         */
        for (i in 0..23) {
            val paintDegree = Paint()
            if (i % 6 == 0) { // 画的是大的刻度值
                paintDegree.strokeWidth = 5f
                paintDegree.textSize = 30f
                canvas.drawLine(
                    (scaleWidth / 2).toFloat(),
                    (scaleHeight / 2 - radius).toFloat(),
                    (scaleWidth / 2).toFloat(),
                    (
                            scaleHeight / 2 - radius + 60).toFloat(),
                    paintDegree
                )
                canvas.drawText(
                    i.toString(), scaleWidth / 2 - paintDegree.measureText(i.toString()) / 2, (
                            scaleHeight / 2 - radius + 90).toFloat(), paintDegree
                )
            } else { // 画的是小刻度
                paintDegree.strokeWidth = 3f
                paintDegree.textSize = 25f
                canvas.drawLine(
                    (scaleWidth / 2).toFloat(),
                    (scaleHeight / 2 - radius).toFloat(),
                    (scaleWidth / 2).toFloat(),
                    (
                            scaleHeight / 2 - radius + 30).toFloat(),
                    paintDegree
                )
                canvas.drawText(
                    i.toString(), scaleWidth / 2 - paintDegree.measureText(i.toString()) / 2, (
                            scaleHeight / 2 - radius + 60).toFloat(), paintDegree
                )
            }

            //将坐标系绕点（width/2，height/2）旋转15度
            canvas.rotate(
                (360 / 24).toFloat(),
                (scaleWidth / 2).toFloat(),
                (scaleHeight / 2).toFloat()
            )
        }
        canvas.save() //先保存下，因为下面要用到坐标的平移

        //将坐标系的平移至原点为（wdith/2,height/2）的地方
        canvas.translate((scaleWidth / 2).toFloat(), (scaleHeight / 2).toFloat())
        val hourRadius = radius * 2 / 4
        val minuteRaidus = radius * 3 / 4
        val hx = (hourRadius * Math.cos(hcount.toDouble())).toInt()
        val hy = (hourRadius * Math.sin(hcount.toDouble())).toInt()
        val mx = (minuteRaidus * Math.cos(mcount.toDouble())).toInt()
        val my = (minuteRaidus * Math.sin(mcount.toDouble())).toInt()
        val paintHour = Paint()
        paintHour.strokeWidth = 7f
        canvas.drawLine(0f, 0f, hx.toFloat(), hy.toFloat(), paintHour)
        val paintMinute = Paint()
        paintMinute.strokeWidth = 3f
        canvas.drawLine(0f, 0f, mx.toFloat(), my.toFloat(), paintMinute)
        canvas.restore()
        mcount++
        if (mcount % 10 == 0) {
            hcount++
        }
        postInvalidateDelayed(500)
    }
}