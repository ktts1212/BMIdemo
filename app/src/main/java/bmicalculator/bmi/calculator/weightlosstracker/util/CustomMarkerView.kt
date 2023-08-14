package bmicalculator.bmi.calculator.weightlosstracker.util

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

class CustomMarkerView(context: Context, layoutResource:Int,val language:String):MarkerView(context,layoutResource) {
    private val tvContent:TextView=findViewById(R.id.tvContent)
    private val tvImage:ImageView=findViewById(R.id.small_circle)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text=if (language !in DcFormat.enList) "${e?.y}".replace(".",",")
        else "${e?.y}"
        if (e?.y!! <16){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.vsuw
                    )
                )
            )
        }else if (e.y in 16.0..16.9){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.suw
                    )
                )
            )
        }else if (e.y in 17.0..18.4){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.uw
                    )
                )
            )
        }else if (e.y in 18.5..24.9){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.normal
                    )
                )
            )
        }else if (e.y in 25.0..29.9){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.ow
                    )
                )
            )
        }else if (e.y in 30.0..34.9){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.oc1
                    )
                )
            )
        }else if (e.y in 35.0..39.9){
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.oc2
                    )
                )
            )
        }else{
            ViewCompat.setBackgroundTintList(
                tvImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,R.color.oc3
                    )
                )
            )
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-width/2).toFloat(),-height.toFloat()+Utils.dip2px(context,4f))
    }
}