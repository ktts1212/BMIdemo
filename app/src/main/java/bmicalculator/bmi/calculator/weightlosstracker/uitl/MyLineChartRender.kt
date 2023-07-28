package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.graphics.Canvas
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
        val label=formatLabel(formattedLabel.toString())
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)

    }

    fun formatLabel(originalLabel:String):String{
        if (originalLabel=="1"){
            return "Jan"
        }else{
            return ""
        }
    }
}