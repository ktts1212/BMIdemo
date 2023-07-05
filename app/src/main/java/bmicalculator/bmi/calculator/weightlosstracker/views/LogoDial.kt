package bmicalculator.bmi.calculator.weightlosstracker.views

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class LogoDial(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint()

    private lateinit var rectF: RectF

    private lateinit var rectF1: RectF

    init {
        paint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        rectF1 = RectF(80f, 80f, width.toFloat()-80f, height.toFloat()-80f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = ContextCompat.getColor(context, R.color.holo_blue_light)
        canvas!!.drawArc(rectF, 154f, 52f, true, paint)

        paint.color = ContextCompat.getColor(context, R.color.holo_green_dark)
        canvas.drawArc(rectF, 214f, 52f, true, paint)

        paint.color = ContextCompat.getColor(context, R.color.holo_orange_dark)
        canvas.drawArc(rectF, 274f, 52f, true, paint)

        paint.color = ContextCompat.getColor(context, R.color.holo_red_light)
        canvas.drawArc(rectF, 334f, 52f, true, paint)


        paint.color = ContextCompat.getColor(context, R.color.holo_blue_dark)
        canvas.drawArc(rectF1,150f,390f,true,paint)
    }

}