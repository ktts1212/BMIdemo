package bmicalculator.bmi.calculator.weightlosstracker.util.views

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils

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
        rectF1 = RectF(Utils.dip2px(context,23.3f).toFloat(),
            Utils.dip2px(context,23.3f).toFloat(),
            width.toFloat()-Utils.dip2px(context,23.3f).toFloat(),
            height.toFloat()-Utils.dip2px(context,23.3f).toFloat())
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