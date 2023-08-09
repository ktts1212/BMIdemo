package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView2(context: Context, layoutResource:Int):MarkerView(context,layoutResource) {
    private val tvContent:TextView=findViewById(R.id.tvContent)
    private val tvImage:ImageView=findViewById(R.id.small_circle)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text="${e?.y} kg"
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-width/2).toFloat(),-height.toFloat()+Utils.dip2px(context,4f))
    }
}