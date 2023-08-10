package bmicalculator.bmi.calculator.weightlosstracker.util.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils

class LogoText(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint= Paint().apply {
        color= Color.WHITE
        textAlign=Paint.Align.LEFT
        typeface=Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.textSize=Utils.dip2px(context,71f).toFloat()
        canvas?.drawText("BMI",Utils.dip2px(context,50f).toFloat(),
            Utils.dip2px(context,50f).toFloat(),paint)
        paint.textAlign=Paint.Align.LEFT
        paint.textSize=Utils.dip2px(context,50f).toFloat()
        canvas?.drawText("Calculator",Utils.dip2px(context,50f).toFloat(),
            Utils.dip2px(context,100f).toFloat(),paint)
    }
}