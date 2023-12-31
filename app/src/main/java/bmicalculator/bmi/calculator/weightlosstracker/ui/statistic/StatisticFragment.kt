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
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentStatisticBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

private const val TAG = "StatisticFragment"

class StatisticFragment : Fragment() {

    private lateinit var binding: FragmentStatisticBinding

    private val bmiList = ArrayList<Entry>()

    private lateinit var ld: Legend

    private lateinit var viewModel: CalculatorViewModel

    private var list: List<BmiInfo>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val factory = ViewModelFactory(Repository(dao))

        viewModel = ViewModelProvider(requireActivity(), factory).get(
            CalculatorViewModel::class.java
        )
        list = viewModel.allInfo.value
        binding = FragmentStatisticBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allInfo.observe(requireActivity()) { info ->
            info?.let {
                if (it.isEmpty()) {

                } else {
                    viewModel.infoCount.postValue(it.size)
                    list = viewModel.allInfo.value
                    for (i in 0..list!!.size - 1) {
                        val year = list!![i].date!!.substring(list!![i].date!!.length - 4)
                        // val day=list[i].date!!.subSequence(list[i].date!!.length-7,)
                        Log.d(TAG, year)
                        val l1 = list!![i].date!!.split(" ")
                        val day = l1[1].split(",")[0]
                        val month = Utils.monthToNumber(l1[0])
                        if (Utils.isInCurrentMonth(year.toInt(), month)) {
                            var exists=false

                            for (j in 0..bmiList.size-1){
                                if (bmiList[j].x==day.toFloat()){
                                    bmiList[j].y= list!![i].bmi
                                    exists=true
                                }
                            }

                            if (exists==false){
                                bmiList.add(Entry(day.toFloat(),list!![i].bmi))
                            }
                        }
                    }

                    Log.d(TAG,bmiList.toString())
                    val lineDataSet = LineDataSet(bmiList, null)
                    //填充x轴到曲线之间区域
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        val fillGradient = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(Color.parseColor("#E0FFFFFF"), Color.parseColor("#40FFFFFF"))
                        )
                        lineDataSet.fillDrawable = fillGradient
                    }

                    lineDataSet.apply {
                        color = Color.WHITE
                        //曲线
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        setCircleColor(Color.WHITE)
                        circleRadius = 4f
                        //setDrawValues(false)
                        valueFormatter = object : ValueFormatter() {
                            override fun getPointLabel(entry: Entry?): String {
                                //每次获取某个x轴坐标点的对应的所有y轴值
//                    val entries=binding.staLinechart1.data.getDataSetByIndex(0)
//                        .getEntriesForXValue(entry!!.x)

                                val lastIndex = binding.staLinechart1.data.getDataSetByIndex(0)
                                    .entryCount

                                //Log.d(TAG, lastIndex.toString())
                                if (lastIndex == entry!!.x.toInt()) {
                                    return ""
                                    // return entry!!.y.toString()
                                } else {
                                    return ""
                                }
                            }
                        }

                        //数据值设置
                        valueTextColor = Color.WHITE
                        valueTextSize = 14f
                        valueTypeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                        //取消高亮点线绘制
                        //isHighlightEnabled = false
                        setDrawFilled(true)
                        fillAlpha = 230
                        //关闭垂直高亮线
                        setDrawVerticalHighlightIndicator(false)
                        //关闭水平高亮线
                        setDrawHorizontalHighlightIndicator(false)
                    }
                    val lineData = LineData(lineDataSet)

                    binding.staLinechart1.apply {
                        setTouchEnabled(true)
                        setDrawGridBackground(false)
                        legend.isEnabled = false
                        //自适应
                        isAutoScaleMinMaxEnabled = true
                        //x轴可拖拽
                        isDragXEnabled = true
                        setDrawBorders(false)
                        setScaleEnabled(false)
                        data = lineData
                        description.isEnabled = false
                    }

                    val limitLine = LimitLine(5f, "Month")
                    limitLine.apply {
                        lineColor = Color.TRANSPARENT
                        textColor = Color.WHITE
                        textSize = 11f
                        labelPosition = LimitLabelPosition.RIGHT_TOP
                    }
                    binding.staLinechart1.setVisibleXRangeMaximum(7f)
                    val xAxis = binding.staLinechart1.xAxis
                    xAxis.apply {
                        addLimitLine(limitLine)
                        isEnabled = true
                        gridColor = Color.parseColor("#EEEEEE")
                        gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        //是否绘制x轴
                        setDrawAxisLine(false)
                        //设置内容显示个数
                        setLabelCount(7, false)
                        textColor = Color.WHITE
                        textSize = 12f
                        typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                        //setCenterAxisLabels(true)
                        setDrawLimitLinesBehindData(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        //yOffset=20f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()
                            }
                        }
                        spaceMax = 0.5f
                    }

                    binding.staLinechart1.axisRight.apply {
                        isEnabled = false
                        setDrawGridLines(false)
                    }

                    binding.staLinechart1.axisLeft.apply {
                        setDrawAxisLine(false)
                        setDrawGridLines(false)
                        setDrawZeroLine(false)
                        textColor = Color.WHITE
                        textSize = 12f
                        typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                        setLabelCount(6, true)
                        xOffset = 15f
                        axisMinimum = 0f
                        minWidth = 38f
                        maxWidth = 38f
//            axisMaximum=120f
//            axisMinimum=0f
                    }

                    binding.staLinechart1.isHighlightPerTapEnabled = true
                    val set = binding.staLinechart1.data.getDataSetByIndex(0)
                    val lastEntryIndex = set.entryCount - 1
                    val lastEntry = set.getEntryForIndex(lastEntryIndex)
                    binding.staLinechart1.highlightValue(lastEntry.x, 0, true)
                    //使用markview显示
                    binding.staLinechart1.data.isHighlightEnabled = true

                    //重绘刷新
                    binding.staLinechart1.invalidate()
                }
            }
        }
        //viewModel.getAllInfo()


        binding.staTabHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })



        //设置气泡
        val mv = CustomMarkerView(requireContext(), R.layout.mark_view_layout)
        binding.staLinechart1.marker = mv

        //设置linedataset属性





        //默认最后一个被选中

        val description = binding.staLinechart1.description
        description.apply {
            description.text = "this is a desp"
            textSize = 20f
            textColor = Color.WHITE
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            textAlign = Paint.Align.RIGHT
        }





        //重绘刷新
        binding.staLinechart1.invalidate()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ON start")
        Log.d(TAG, list.toString())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ON Resume")
        Log.d(TAG, list.toString())
    }
}