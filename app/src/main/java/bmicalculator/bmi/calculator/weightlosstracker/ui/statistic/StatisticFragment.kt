package bmicalculator.bmi.calculator.weightlosstracker.ui.statistic

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentStatisticBinding
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

private const val TAG="StatisticFragment"
class StatisticFragment : Fragment() {

    private lateinit var binding: FragmentStatisticBinding

    private val bmiList = ArrayList<Entry>()

    private lateinit var ld: Legend

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.staTabHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        for (i in 1..12) {
            bmiList.add(Entry(i.toFloat(), Random.nextInt(1,i+1) * 10f))
        }



        val lineDataSet = LineDataSet(bmiList, "")
        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
            val fillGradient=GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#E0FFFFFF"),Color.parseColor("#40FFFFFF"))
            )
            lineDataSet.fillDrawable=fillGradient
        }

        val mv=CustomMarkerView(requireContext(),R.layout.mark_view_layout)
        binding.staLinechart1.marker=mv

        lineDataSet.apply {
            color=Color.WHITE
            mode=LineDataSet.Mode.CUBIC_BEZIER
            setCircleColor(Color.WHITE)
            circleRadius=4f
            //setDrawValues(false)
            valueFormatter=object :ValueFormatter(){
                override fun getPointLabel(entry: Entry?): String {
                    //每次获取某个x轴坐标点的对应的所有y轴值
//                    val entries=binding.staLinechart1.data.getDataSetByIndex(0)
//                        .getEntriesForXValue(entry!!.x)

                    val lastIndex=binding.staLinechart1.data.getDataSetByIndex(0)
                        .entryCount

                    Log.d(TAG,lastIndex.toString())
                    if (lastIndex==entry!!.x.toInt()){
                        return entry!!.y.toString()
                    }else{
                        return ""
                    }
                }
            }

            valueTextColor=Color.WHITE
            valueTextSize=14f
            valueTypeface=ResourcesCompat.getFont(requireContext(),R.font.montserrat_extrabold)
            //取消高亮点线绘制
            isHighlightEnabled=true
            setDrawFilled(true)
            fillAlpha=230
        }

        val lineData = LineData(lineDataSet)
        binding.staLinechart1.apply {
            setTouchEnabled(true)
            setDrawGridBackground(false)
            isAutoScaleMinMaxEnabled = true
            isDragXEnabled = true
            setDrawBorders(false)
            setScaleEnabled(false)
            data = lineData
            description.isEnabled = false

        }

        val description = binding.staLinechart1.description
        description.apply {
            description.text = "this is a desp"
            textSize = 20f
            textColor = Color.WHITE
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            textAlign = Paint.Align.RIGHT
        }

        binding.staLinechart1.setVisibleXRangeMaximum(7f)
        val xAxis = binding.staLinechart1.xAxis
        xAxis.apply {
            isEnabled = true
            gridColor = Color.parseColor("#EEEEEE")
            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
            //是否绘制x轴
            setDrawAxisLine(false)
            //设置内容显示个数
            setLabelCount(7,false)
            textColor=Color.WHITE
            textSize=12f
            typeface=ResourcesCompat.getFont(requireContext(),R.font.montserrat_extrabold)
            //setCenterAxisLabels(true)
            setDrawLimitLinesBehindData(false)
            position=XAxis.XAxisPosition.BOTTOM
            valueFormatter=object:ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            spaceMax=0.5f
        }

        binding.staLinechart1.axisRight.apply {
            isEnabled=false
        }

        binding.staLinechart1.axisLeft.apply {
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawZeroLine(false)
            textColor= Color.WHITE
            textSize=12f
            typeface=ResourcesCompat.getFont(requireContext(),R.font.montserrat_extrabold)
            setLabelCount(6,true)
            xOffset=15f
            axisMinimum=0f
            minWidth=38f
            maxWidth=38f
        }

        binding.staLinechart1.invalidate()
    }

}