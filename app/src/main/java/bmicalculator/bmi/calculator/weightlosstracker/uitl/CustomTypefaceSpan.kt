package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(private val newType:Typeface?):TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyTypeface(ds,newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyTypeface(paint,newType)
    }

    private fun applyTypeface(paint: Paint,tf:Typeface?){
        val oldStyle:Int=paint.typeface?.style?:0
        val fakeStyle= (oldStyle and tf?.style!!.inv()) ?: 0
        if (fakeStyle and Typeface.BOLD!=0){
            paint.isFakeBoldText=true
        }

        if (fakeStyle and Typeface.ITALIC!=0){
            paint.textSkewX=-0.25f
        }

        paint.typeface=tf
    }
}