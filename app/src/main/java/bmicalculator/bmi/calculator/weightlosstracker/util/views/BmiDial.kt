package bmicalculator.bmi.calculator.weightlosstracker.util.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils


class BmiDial(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mArcRectF: RectF
    private var mSmallArcRectF: RectF
    private var mScalePaint: Paint
    private var numtext = "17"


    private var mArcRadius = 0  //外圆弧半径
    private var mScaleRadius = 0 //刻度线半径
    private var mScaleTextSize = 0  //刻度线字体大小
    private var mPointCount = 0
    private var mSmallArcRadius = 0
    private var mArc: Path

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mArcRadius = Math.min(w, h) / 2 - Utils.dip2px(context,13.3f)
        mSmallArcRadius = mArcRadius / 2
        mScaleRadius = mArcRadius + Utils.dip2px(context, 8f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val centerX = width / 2
        val centerY = height / 2


        mArcRectF.set(
            (centerX - mArcRadius).toFloat(),
            (centerY - mArcRadius).toFloat(),
            (centerX + mArcRadius).toFloat(),
            (centerY + mArcRadius).toFloat()
        )
        mArc.addArc(mArcRectF, 0f, 360f)

        mSmallArcRectF.set(
            (centerX - mSmallArcRadius).toFloat(),
            (centerY - mSmallArcRadius).toFloat(),
            (centerX + mSmallArcRadius).toFloat(),
            (centerY + mSmallArcRadius).toFloat()
        )

        //绘制半圆弧
        //mScalePaint.strokeWidth=1f
        mScalePaint.color = Color.parseColor("#286DE6")
        canvas!!.drawArc(mArcRectF, 180f, sweepAngle(0.4f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#349CEA")
        canvas.drawArc(mArcRectF, startAngle(0.4f), sweepAngle(1f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#5BB1F5")
        canvas.drawArc(mArcRectF, startAngle(1.4f) ,sweepAngle(1.5f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#A8C526")
        canvas.drawArc(mArcRectF, startAngle(2.9f), sweepAngle(6.5f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#FECD2E")
        canvas.drawArc(mArcRectF, startAngle(9.4f), sweepAngle(5f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#FFA110")
        canvas.drawArc(mArcRectF, startAngle(14.4f), sweepAngle(5f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#FF7137")
        canvas.drawArc(mArcRectF, startAngle(19.4f), sweepAngle(5f), true, mScalePaint)
        mScalePaint.color = Color.parseColor("#D3333B")
        canvas.drawArc(mArcRectF, startAngle(24.4f), sweepAngle(0.4f), true, mScalePaint)


        mScalePaint.color = Color.WHITE
        canvas.drawArc(mSmallArcRectF, 180f, 180f, true, mScalePaint)

        //绘制刻度
        mScalePaint.color = Color.BLACK
        mScalePaint.typeface = ResourcesCompat.getFont(context, R.font.montserrat_extrabold)
        mScalePaint.textSize = Utils.sp2px(context,10f).toFloat()


        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(1.4f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "18.5"
        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(2.9f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "25"
        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(9.4f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "30"
        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(14.4f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "35"
        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(19.4f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "40"
        canvas.drawTextOnPath(numtext, mArc, getArcl(startAngle(24.4f)).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

        numtext = "17"
    }

    init {
        mScalePaint = Paint()
        mScalePaint.isAntiAlias = true
        mScalePaint.textAlign = Paint.Align.CENTER
        mScaleTextSize = Utils.dip2px(context!!, 10f)
        mScalePaint.textSize = mScaleTextSize.toFloat()
        mArcRectF = RectF()
        mSmallArcRectF = RectF()
        mArc = Path()
    }

    fun getArcl(radius: Float): Double {

        return (radius-180) * mArcRadius * Math.PI / 180
    }

    fun startAngle(num: Float): Float {
        return num / 24.8f * 180 + 180
    }

    fun sweepAngle(num:Float):Float{
        return num/24.8f *180
    }
}