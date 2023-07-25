package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.graphics.Canvas

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class XLabelRenderer(viewPortHandler: ViewPortHandler,xAxis: XAxis,trans:Transformer):XAxisRenderer(
    viewPortHandler, xAxis, trans
) {
    private val dateFormat=SimpleDateFormat("MMMM", Locale.getDefault())
    private val calendar=Calendar.getInstance()

    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
//        val date=
      //  calendar.time=date
    }
}