package bmicalculator.bmi.calculator.weightlosstracker.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class CustomXAxisRenderer(val tabPosition:Int,val startDate:String,val context: Context,viewPortHandler: ViewPortHandler?,xAxis: XAxis,transformer: Transformer?) :
    XAxisRenderer(viewPortHandler,xAxis,transformer) {

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val dateTime=LocalDateTime.ofInstant(
            Instant.ofEpochMilli(startDate.toLong()), ZoneId.systemDefault())
        //根据第一个数据获取到起始年月日
        val startYear=dateTime.year
        val startMonth=dateTime.monthValue
        val startDay=dateTime.dayOfMonth
        //获取x轴的值
        val xValue=formattedLabel?.replace(",","")?.toDouble()?.toInt()

        if (xValue==0){
            super.drawLabel(c, "", x, y, anchor, angleDegrees)
        }
        //起始日期的date对象
        val date=LocalDate.of(startYear,startMonth,startDay)


        val newDate=if (tabPosition==0) {
            date.plusDays((xValue!! - 1).toLong())
        }
        else if (tabPosition==1){
            date.plusDays(((xValue!! - 1) * 7).toLong())
        }else{
            date.plusMonths((xValue!!-1).toLong())
        }

        val currentYear=LocalDate.now().year
        val mXaisLabelPaint= Paint()
        val typeFace=ResourcesCompat.getFont(context
            ,R.font.montserrat_extrabold
        )
        val typeFace2=ResourcesCompat.getFont(context,R.font.montserrat_regular)
        mXaisLabelPaint.color= Color.WHITE
        mXaisLabelPaint.textSize=Utils.sp2px(context,12f).toFloat()

        if (tabPosition==0){
            if (newDate.dayOfMonth==1){
                mXaisLabelPaint.typeface=typeFace2
                if (newDate.year==currentYear){
                    val month=Utils.numToMonth(newDate.monthValue)
                    c?.drawText(month,x-Utils.dip2px(context,13f),y-Utils.dip2px(context,168f).toFloat(),mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
                }else{
                    val month="${newDate.year} ${Utils.numToMonth(newDate.monthValue)}"
                    c?.drawText(month,x-Utils.dip2px(context,26.5f),y-Utils.dip2px(context,168f),mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
                }
            }else if (newDate?.dayOfMonth!! <10){
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3.5f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
            }else{
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,6.7f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
            }
        }else if (tabPosition==1){
            if (Utils.isInFirstWeekOfMonth(newDate)){
                mXaisLabelPaint.typeface=typeFace2
                if (newDate.year==currentYear){
                    val month=Utils.numToMonth(newDate.monthValue)
                    c?.drawText(month,x-Utils.dip2px(context,13f),y-Utils.dip2px(context,168f),mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
                }else{
                    val month="${newDate.year} ${Utils.numToMonth(newDate.monthValue)}"
                    c?.drawText(month,x-Utils.dip2px(context,26.5f),y-Utils.dip2px(context,168f),mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
                }
            }else if (newDate?.dayOfMonth!! <10){
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,3.5f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
            }else{
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-Utils.dip2px(context,6.7f),y+Utils.dip2px(context,10f),mXaisLabelPaint)
            }
        }else {
            mXaisLabelPaint.typeface = typeFace2
            if (newDate.monthValue == 1) {
                val year = newDate.year
                c?.drawText(year.toString(), x - Utils.dip2px(context,13f), y - Utils.dip2px(context,168f), mXaisLabelPaint)
                mXaisLabelPaint.typeface = typeFace
                c?.drawText(newDate.monthValue.toString(), x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f), mXaisLabelPaint)
            } else {
                mXaisLabelPaint.typeface = typeFace
                c?.drawText(newDate.monthValue.toString(), x-Utils.dip2px(context,3f),y+Utils.dip2px(context,10f), mXaisLabelPaint)
            }
        }
    }
}