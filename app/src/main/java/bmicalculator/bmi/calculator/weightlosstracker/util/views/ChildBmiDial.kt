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
import java.util.Arrays


class ChildBmiDial(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mArcRectF: RectF
    private var mSmallArcRectF: RectF
    private var mScalePaint: Paint


    private var mArcRadius = 0  //外圆弧半径
    private var mScaleRadius = 0 //刻度线半径
    private var mScaleTextSize = 0  //刻度线字体大小
    private var mSmallArcRadius = 0
    private var mArc: Path

    private var cScaleList = ArrayList<String>()
    private var scaleRange: Float = 0f

    init {
        mScalePaint = Paint()
        mScalePaint.isAntiAlias = true
        mScalePaint.textAlign = Paint.Align.CENTER
        mScaleTextSize = Utils.dip2px(context!!, 10f)
        mScalePaint.textSize = mScaleTextSize.toFloat()
        mArcRectF = RectF()
        mSmallArcRectF = RectF()
        mArc = Path()
        cScaleList.addAll(Arrays.asList("13.0","14.4", "18.0", "19.1","20.0"))
        scaleRange = 7.1f
    }

    fun getData(list:ArrayList<String>,sr:Float){
        cScaleList=list
        scaleRange=sr
    }

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

        mScalePaint.color = Color.parseColor("#5BB1F5")
        canvas!!.drawArc(mArcRectF, 180f,
            sweepAngle(cScaleList[1].toFloat()-cScaleList[0].toFloat()),
            true,
            mScalePaint)
        mScalePaint.color = Color.parseColor("#A8C526")
        canvas.drawArc(mArcRectF, startAngle(cScaleList[1].toFloat()-cScaleList[0].toFloat()),
            sweepAngle(cScaleList[2].toFloat()-cScaleList[1].toFloat()),
            true,
            mScalePaint)
        mScalePaint.color = Color.parseColor("#FECD2E")
        canvas.drawArc(mArcRectF, startAngle(cScaleList[2].toFloat()-cScaleList[0].toFloat()),
            sweepAngle(cScaleList[3].toFloat()-cScaleList[2].toFloat()),
            true,
            mScalePaint)
        mScalePaint.color = Color.parseColor("#FFA110")
        canvas.drawArc(mArcRectF, startAngle(cScaleList[3].toFloat()-cScaleList[0].toFloat()),
            sweepAngle(cScaleList[4].toFloat()-cScaleList[3].toFloat()+0.1f),
            true,
            mScalePaint)


        mScalePaint.color = Color.WHITE
        canvas.drawArc(mSmallArcRectF, 180f, 180f, true, mScalePaint)

        //绘制刻度
        mScalePaint.color = Color.BLACK
        mScalePaint.typeface = ResourcesCompat.getFont(context, R.font.montserrat_extrabold)
        mScalePaint.textSize = Utils.dip2px(context,13.3f).toFloat()

        canvas.drawTextOnPath(cScaleList[1], mArc,
            getArcl(startAngle(cScaleList[1].toFloat()-cScaleList[0].toFloat())).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)
        canvas.drawTextOnPath(cScaleList[2], mArc,
            getArcl(startAngle(cScaleList[2].toFloat()-cScaleList[0].toFloat())).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)


        canvas.drawTextOnPath(cScaleList[3], mArc,
            getArcl(startAngle(cScaleList[3].toFloat()-cScaleList[0].toFloat())).toFloat(),
            Utils.dip2px(context,-3.3f).toFloat(), mScalePaint)

    }


    fun getArcl(radius: Float): Double {

        return (radius - 180) * mArcRadius * Math.PI / 180
    }

    fun startAngle(num: Float): Float {
        return num / scaleRange * 180 + 180
    }

    fun sweepAngle(num: Float): Float {
        return num / scaleRange * 180
    }
}