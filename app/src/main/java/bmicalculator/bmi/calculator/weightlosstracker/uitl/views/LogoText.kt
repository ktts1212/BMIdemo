package bmicalculator.bmi.calculator.weightlosstracker.uitl.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class LogoText(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint= Paint().apply {
        color= Color.WHITE
        textAlign=Paint.Align.LEFT
        typeface=Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.textSize=220f
        canvas?.drawText("BMI",150f,150f,paint)
        paint.textAlign=Paint.Align.LEFT
        paint.textSize=150f
        canvas?.drawText("Calculator",150f,300f,paint)
    }
}