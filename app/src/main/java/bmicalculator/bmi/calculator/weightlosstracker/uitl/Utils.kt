package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.util.Log
import java.util.Calendar
import kotlin.math.pow

object Utils {


    private var Min=18.5

    private var Max=24.9

    fun initData(age:Int){
        if (age<=20){

            Min=ChildBmiDialData.cScaleList[1].toDouble()

            Max=ChildBmiDialData.cScaleList[2].toDouble()-0.1

        }else{
            Min=18.5

            Max=24.9
        }
    }

    //根据手机分辨率从dp单位转换为px（像素）
    fun dip2px(context: Context,deValue:Float): Int {
        val scale=context.resources.displayMetrics.density
        //四舍五入取整
        return (deValue*scale+0.5f).toInt()
    }
    //根据手机分辨率从px单位转换成dp
    fun px2dip(context: Context,pxValue:Float):Int{
        val scale=context.resources.displayMetrics.density
        return (pxValue/scale+0.5f).toInt()
    }

    fun minWtftintokg(f:Int,i:Int): Double {
        return Min* (f * 0.3048 + i * 0.0254).pow(2.0)
    }

    fun maxWtftintokg(f:Int,i:Int): Double {
       return Max* (f * 0.3048 + i * 0.0254).pow(2.0)
    }

    fun minWtftintolb(f:Int,i:Int): Double {
        return Min* (12.0 * f + i).pow(2.0) /703
    }

    fun maxWtftintolb(f:Int,i:Int): Double {
       return Max* (12.0 * f + i).pow(2.0) /703
    }

    fun minCmtokg(t:Double):Double{
        return Min* (t * 0.01).pow(2.0)
    }

    fun maxCmtokg(t:Double):Double{
        return Max* (t * 0.01).pow(2.0)
    }

    fun minCmtolb(t:Double):Double{
        return Min* (t * 0.01).pow(2.0) /0.453
    }

    fun maxCmtolb(t:Double):Double{
        return Max* (t * 0.01).pow(2.0) /0.453
    }

    fun isInCurrentMonth(year:Int,month:Int):Boolean{
        val currentCalendar=Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR)==year&&
                currentCalendar.get(Calendar.MONTH)+1==month
    }

    fun monthToNumber(month: String): Int {
        val monthnum = when (month) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> -1
        }
        return monthnum
    }
}