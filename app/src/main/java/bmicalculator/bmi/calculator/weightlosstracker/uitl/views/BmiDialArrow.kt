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


class BmiDialArrow(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

                                        //开启抗锯齿
    private val paint= Paint().apply {
        style=Paint.Style.FILL   //绘制的图形填充
        color=Color.BLACK
        isAntiAlias=true
    }
    private val path=Path()
    private var arrowHeadRect=RectF()
    private var bradius=0
    private var sradius=0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bradius=width/2
        sradius=width/12
//        val width=width
//        val height=height

//        arrowHeadRect.set(
//            50f,
//           80f,
//            90f,
//            120f
//
//        )
//
//        path.reset()
//        path.arcTo(arrowHeadRect,180f,180f)
//
//        path.moveTo(70f,100f)
//        path.lineTo(270f,100f)
//
//
//        arrowHeadRect.offset(200f,0f)
//        path.arcTo(arrowHeadRect,0f,180f)
//        canvas!!.drawPath(path,paint)

//        val rectF=RectF(0f,0f,width.toFloat(),height.toFloat()/4)
//        paint.color=ContextCompat.getColor(context, R.color.black)
        //canvas?.drawArc(rectF,0f,360f,true,paint)
        val rectF=RectF(0f,0f,width.toFloat(),height.toFloat()/4)
        canvas!!.drawArc(rectF,180f,180f,true,paint)
       // path.moveTo(0f,height.toFloat()/8)
        //path.lineTo(bradius.toFloat()/8-sradius.toFloat()/8f,(bradius.toFloat()/8-sradius.toFloat()/8)*Math.tan(75.0).toFloat())

//        arrowHeadRect= RectF(
//            0f,
//            0f,
//            width.toFloat()/9,
//            height.toFloat()/72,)
//        arrowHeadRect.offset(bradius.toFloat()/2-sradius.toFloat()/2,
//        height.toFloat()+sradius.toFloat()/2)
        canvas.drawCircle(width.toFloat()/2,height.toFloat()-width.toFloat()/12,width.toFloat()/12,paint)
//        path.moveTo(bradius.toFloat()/8-sradius.toFloat()/8f,(bradius.toFloat()/8-sradius.toFloat()/8)*Math.tan(75.0).toFloat())
//        path.lineTo(sradius.toFloat(),(bradius.toFloat()/8-sradius.toFloat()/8)*Math.tan(75.0).toFloat())
       // path.lineTo(bradius.toFloat(),bradius.toFloat()/2)
        path.moveTo(0f,bradius.toFloat())
        path.lineTo(bradius.toFloat()-sradius.toFloat(),(bradius.toFloat()-sradius.toFloat())*Math.tan(75.0).toFloat())
        path.lineTo(bradius.toFloat()+sradius.toFloat(),(bradius.toFloat()-sradius.toFloat())*Math.tan(75.0).toFloat())
        path.lineTo(bradius.toFloat()*2,bradius.toFloat())
        path.close()
        canvas.drawPath(path,paint)


        //绘制箭头
//        val pathTriangle=Path()
//        pathTriangle.moveTo(0f,height.toFloat()/8)
//        pathTriangle.lineTo(width.toFloat(),height.toFloat()/8)
//        pathTriangle.lineTo(width.toFloat()/2,height.toFloat())
//        pathTriangle.close()
//        canvas?.drawPath(pathTriangle,paint)
    }
}