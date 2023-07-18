package bmicalculator.bmi.calculator.weightlosstracker.uitl.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils


class BmiDial(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var mArcRectF: RectF
    private lateinit var mSmallArcRectF: RectF
    private lateinit var mScalePaint: Paint
    private var numtext="17"


    private var mArcRadius = 0  //外圆弧半径
    private var mScaleRadius = 0 //刻度线半径
    private var mScaleTextSize = 0  //刻度线字体大小
    private var mPointCount = 0
    private var mSmallArcRadius=0
    private lateinit var mArc:Path

    fun setPoint(count: Int) {
        mPointCount = count
        //重新绘制
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mArcRadius = Math.min(w, h) / 2-30
        mSmallArcRadius=mArcRadius/2
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
        mArc.addArc(mArcRectF,0f,360f)

        mSmallArcRectF.set(
            (centerX-mSmallArcRadius).toFloat(),
            (centerY-mSmallArcRadius).toFloat(),
            (centerX+mSmallArcRadius).toFloat(),
            (centerY+mSmallArcRadius).toFloat()

        )

        //绘制半圆弧
        //mScalePaint.strokeWidth=1f
        mScalePaint.color= Color.parseColor("#286DE6")
        canvas!!.drawArc(mArcRectF,180f,2f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#349CEA")
        canvas.drawArc(mArcRectF,182f,8f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#5BB1F5")
        canvas.drawArc(mArcRectF,190f,15f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#A8C526")
        canvas.drawArc(mArcRectF,205f,50f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#FECD2E")
        canvas.drawArc(mArcRectF,255f,40f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#FFA110")
        canvas.drawArc(mArcRectF,295f,40f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#FF7137")
        canvas.drawArc(mArcRectF,335f,20f,true,mScalePaint)
        mScalePaint.color=Color.parseColor("#D3333B")
        canvas.drawArc(mArcRectF,355f,5f,true,mScalePaint)


        mScalePaint.color=Color.WHITE
        canvas.drawArc(mSmallArcRectF,180f,180f,true,mScalePaint)

        //绘制刻度
        mScalePaint.color=Color.BLACK
        mScalePaint.typeface= ResourcesCompat.getFont(context,R.font.montserrat_extrabold)
        mScalePaint.textSize=40f
//        for (i in 0..mPointCount){
        //将角度转化为弧度
        val location=(190)*Math.PI/180
        val stopX=(centerX+mScaleRadius*Math.cos(location)).toFloat()
        val stopY=(centerY+mScaleRadius*Math.sin(location)).toFloat()
//            canvas.drawLine(centerX.toFloat(),centerY.toFloat(),stopX,stopY,mScalePaint)
//
//            val text=i.toString()

        canvas.drawTextOnPath(numtext,mArc,getArcl(10f).toFloat(),-10f,mScalePaint)
//        }
        numtext="18.5"
        canvas.drawTextOnPath(numtext,mArc,getArcl(25f).toFloat(),-10f,mScalePaint)

        numtext="25"
        canvas.drawTextOnPath(numtext,mArc,getArcl(75f).toFloat(),-10f,mScalePaint)

        numtext="30"
        canvas.drawTextOnPath(numtext,mArc,getArcl(115f).toFloat(),-10f,mScalePaint)

        numtext="35"
        canvas.drawTextOnPath(numtext,mArc,getArcl(155f).toFloat(),-10f,mScalePaint)

        numtext="40"
        canvas.drawTextOnPath(numtext,mArc,getArcl(175f).toFloat(),-10f,mScalePaint)
    }

    init {
        mScalePaint= Paint()
        mScalePaint.isAntiAlias=true
        mScalePaint.textAlign=Paint.Align.CENTER
        mScaleTextSize=Utils.dip2px(context!!,10f)
        mScalePaint.textSize=mScaleTextSize.toFloat()
        mArcRectF=RectF()
        mSmallArcRectF=RectF()
        mArc=Path()
    }

    fun getArcl(radius:Float): Double {
        return radius*mArcRadius*Math.PI/180
    }
}