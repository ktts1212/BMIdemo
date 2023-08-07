package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.DMonth
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

        val startYear=dateTime.year
        val startMonth=dateTime.monthValue
        val startDay=dateTime.dayOfMonth

        val xValue=formattedLabel?.toInt()
        val date=LocalDate.of(startYear,startMonth,startDay)
        val newDate=date.plusDays((xValue!!-1).toLong())



        val currentYear=LocalDate.now().year
        val mXaisLabelPaint= Paint()
        val typeFace=ResourcesCompat.getFont(context
            ,R.font.montserrat_extrabold
        )
        val typeFace2=ResourcesCompat.getFont(context,R.font.montserrat_regular)
        mXaisLabelPaint.color= Color.WHITE
        mXaisLabelPaint.textSize=35f

        if (tabPosition==0){
            if (newDate.dayOfMonth==1){
                mXaisLabelPaint.typeface=typeFace2
                if (newDate.year==currentYear){
                    val month=Utils.numToMonth(newDate.monthValue)
                    c?.drawText(month,x-40f,y-460f,mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-10f,y+30f,mXaisLabelPaint)
                }else{
                    val month="${newDate.year} ${Utils.numToMonth(newDate.monthValue)}"
                    c?.drawText(month,x-80f,y-460f,mXaisLabelPaint)
                    mXaisLabelPaint.typeface=typeFace
                    c?.drawText(newDate.dayOfMonth.toString(),x-10f,y+30f,mXaisLabelPaint)
                }
            }else if (newDate?.dayOfMonth!! <10){
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-11f,y+30f,mXaisLabelPaint)
            }else{
                mXaisLabelPaint.typeface=typeFace
                c?.drawText(newDate.dayOfMonth.toString(),x-20f,y+30f,mXaisLabelPaint)
            }
        }else if (tabPosition==1){

        }

    }



}