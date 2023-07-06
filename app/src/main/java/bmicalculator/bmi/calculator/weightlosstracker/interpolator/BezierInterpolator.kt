package bmicalculator.bmi.calculator.weightlosstracker.interpolator

import android.animation.TimeInterpolator

class BezierInterpolator(x1:Float,y1:Float,x2:Float,y2:Float) :TimeInterpolator{

    var x1:Float?=0f

    var y1:Float?=0f

    var x2:Float?=0f

    var y2:Float?=0f

    init {
        this.x1=x1
        this.y1=y1
        this.x2=x2
        this.y2=y2
    }

    override fun getInterpolation(p0: Float): Float {
        return calculateBezierFunction(p0, x1!!,y1!!,x2!!,y2!!)
    }

    fun calculateBezierFunction(t:Float,x1:Float,y1:Float,x2:Float,y2:Float): Float {
        val u:Float=1-t
        val tt=t*t
        val uu=u*u
        val ttt=tt*t
        val uuu=uu*u
        val y=uuu*0+3*uu*t*y1+3*u*tt*y2+ttt*1
        return y
    }
}