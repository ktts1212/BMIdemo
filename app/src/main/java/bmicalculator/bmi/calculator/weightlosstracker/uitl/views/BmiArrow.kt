package bmicalculator.bmi.calculator.weightlosstracker.uitl.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import bmicalculator.bmi.calculator.weightlosstracker.R


class BmiArrow(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

                                        //开启抗锯齿
    private val paint= Paint().apply {
        style=Paint.Style.FILL   //绘制的图形填充
        isAntiAlias=true
    }
    private val path=Path()
    private var bradius:Float=0f
    private var sradius:Float=0f
    private var rF=RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bradius= (w.toFloat()/2)
        sradius=(w.toFloat()/12)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
       // paint.color=Color.WHITE
//        canvas!!.drawRect(0f,
//            0f,
//            bradius*2,
//            (bradius-sradius)*Math.tan(80*Math.PI/180).toFloat()+sradius,paint)
        path.moveTo(0f,bradius)
        path.lineTo(bradius*2,bradius)
        path.lineTo(bradius+sradius,(bradius-sradius)*Math.tan(82*Math.PI/180).toFloat())
        path.lineTo(bradius-sradius,(bradius-sradius)*Math.tan(82*Math.PI/180).toFloat())
        path.close()
        paint.color=Color.BLACK
        canvas!!.drawPath(path,paint)
        canvas.drawCircle(bradius,bradius,bradius,paint)
        canvas.drawCircle(bradius,(bradius - sradius) * Math.tan(82 * Math.PI / 180).toFloat(),sradius,paint)
        val radiusDebug = (bradius - sradius) * Math.tan(82 * Math.PI / 180).toFloat()
        paint.color=Color.WHITE
        Log.d("DEBUG", "Second Circle Radius: $radiusDebug")
        Log.d("DEBUG", " Circle bradius: $bradius")
        Log.d("DEBUG", " Circle rradius: $sradius")

//        val rectF=RectF(0f,0f,width.toFloat(),height.toFloat()/4)
//        canvas!!.drawArc(rectF,180f,180f,true,paint)
//
//        canvas.drawCircle(width.toFloat()/2,height.toFloat()-width.toFloat()/12,width.toFloat()/12,paint)




    }
}