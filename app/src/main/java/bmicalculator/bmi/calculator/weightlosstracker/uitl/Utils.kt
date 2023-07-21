package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context



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
        return Min*Math.pow(f*0.3048+i*0.0254,2.0)
    }

    fun maxWtftintokg(f:Int,i:Int): Double {
       return Max*Math.pow(f*0.3048+i*0.0254,2.0)
    }

    fun minWtftintolb(f:Int,i:Int): Double {
        return Min*Math.pow(12.0*f+i,2.0)/703
    }

    fun maxWtftintolb(f:Int,i:Int): Double {
       return Max*Math.pow(12.0*f+i,2.0)/703
    }

    fun minCmtokg(t:Double):Double{
        return Min*Math.pow(t*0.01,2.0)
    }

    fun maxCmtokg(t:Double):Double{
        return Max*Math.pow(t*0.01,2.0)
    }

    fun minCmtolb(t:Double):Double{
        return Min*Math.pow(t*0.01,2.0)/0.453
    }

    fun maxCmtolb(t:Double):Double{
        return Max*Math.pow(t*0.01,2.0)/0.453
    }
}