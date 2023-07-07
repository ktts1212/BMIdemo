package bmicalculator.bmi.calculator.weightlosstracker.uitl.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import bmicalculator.bmi.calculator.weightlosstracker.R


class LogoArrow(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

                                        //开启抗锯齿
    private val paint= Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.FILL      //绘制的图形填充
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width=width
        val height=height

        paint.color=Color.WHITE

        val rectF=RectF(0f,0f,width.toFloat(),height.toFloat()/2)
        paint.color=ContextCompat.getColor(context, R.color.white)
        canvas?.drawArc(rectF,0f,360f,true,paint)

        //绘制箭头
        val pathTriangle=Path()
        pathTriangle.moveTo(0f,height.toFloat()/4)
        pathTriangle.lineTo(width.toFloat(),height.toFloat()/4)
        pathTriangle.lineTo(width.toFloat()/2,height.toFloat())
        pathTriangle.close()
        canvas?.drawPath(pathTriangle,paint)
    }
}