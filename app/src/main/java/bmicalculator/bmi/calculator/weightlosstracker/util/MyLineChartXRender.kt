package bmicalculator.bmi.calculator.weightlosstracker.util

import android.graphics.Canvas
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class MyLineChartXRender(
    viewPointHandler: ViewPortHandler,
    xAxis: XAxis,
    transformer: Transformer
):XAxisRenderer(viewPointHandler,xAxis,transformer){

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {


        if (formattedLabel=="31"){
            val labelX=x
            val labelY=y-600f
            val textStyle=mAxisLabelPaint
            textStyle.color=Color.WHITE
            c!!.drawText("Jan",labelX,labelY,textStyle)
        }else{
            super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
        }
    }

}