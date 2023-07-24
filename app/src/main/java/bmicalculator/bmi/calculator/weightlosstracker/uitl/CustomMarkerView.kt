package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.widget.TextView
import bmicalculator.bmi.calculator.weightlosstracker.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import org.w3c.dom.Text

class CustomMarkerView(context: Context, layoutResource:Int):MarkerView(context,layoutResource) {
    private val tvContent:TextView=findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text="${e?.y}"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-width/2).toFloat(),-height.toFloat())
    }
}