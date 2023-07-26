package bmicalculator.bmi.calculator.weightlosstracker.ui.statistic

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentStatisticBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.DWeek
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import java.lang.Exception
import java.time.LocalDate

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
        binding.staLinechart1.clear()
        if (!bmiList.isEmpty()){
            bmiList.clear()
        }

        bmiList.add(Entry(1f,0f))

        val currentDate=LocalDate.now()

        Log.d(TAG,bmiList.toString())


        viewModel.listByYear.observe(requireActivity()) { info ->

            info?.let {
                if (it.isNullOrEmpty()) {
                    throw Exception("There is no data")
                } else {
                    viewModel.infoCount.postValue(it.size)

                    Log.d(TAG,"viewModel.listByYear.value.size.toString()")

                    //Log.d(TAG,"list:${viewModel.listByYear.value}")
                    list=orderList(viewModel.listByYear.value!!)

                    Log.d(TAG,"list:${list}")

                    bmiList.clear()

                    bmiList.add(Entry(1f,0f))

                    for (i in 0..list!!.size - 1) {
                        val l1 = list!![i].date!!.split(" ")
                        //获取当前的月份和天数
                        val day = l1[1].split(",")[0]
                        val month = Utils.monthToNumber(l1[0])

                        val dayOfYear=Utils.getDayOfYear(day.toInt(),month)

                        bmiList.add(Entry(dayOfYear.toFloat(),list!![i].bmi))
                    }

                    bmiList.sortBy { it.x }
//                    binding.staLinechart1.notifyDataSetChanged()
//                    binding.staLinechart1.invalidate()
                    Log.d(TAG,"bmiList:${bmiList}")

                    Log.d(TAG, bmiList.toString())

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
                                val lastIndex = binding.staLinechart1.data.getDataSetByIndex(0)
                                    .entryCount

                                if (lastIndex == entry!!.x.toInt()) {
                                    return ""
                                } else {
                                    return ""
                                }
                            }
                        }

                        //数据值设置
                        valueTextColor = Color.WHITE
                        valueTextSize = 14f

                        //检查fragment是否关联到activity
                        if (isAdded()) {
                            valueTypeface = ResourcesCompat.getFont(
                                requireContext(),
                                R.font.montserrat_extrabold
                            )
                        }

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
//                        binding.staLinechart1.rendererXAxis=MyLineChartXRender(binding.staLinechart1.viewPortHandler,
//                            xAxis,binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT))
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
                        if (isAdded()) {
                            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        }
                        //是否绘制x轴
                        setDrawAxisLine(false)
                        //设置内容显示个数
                        setLabelCount(7, false)
                        textColor = Color.WHITE
                        textSize = 12f
                        if (isAdded()) {
                            typeface = ResourcesCompat.getFont(
                                requireContext(),
                                R.font.montserrat_extrabold
                            )
                        }

                        //setCenterAxisLabels(true)
                        setDrawLimitLinesBehindData(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        //yOffset=20f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                if (binding.staTabHeader.getTabAt(0)!!.isSelected){
                                    val dMonth= Utils.getDayOfMonth(value.toInt())

                                    return dMonth.day.toString()
                                }else{
                                    return value.toInt().toString()
                                }

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
                        if (isAdded) {
                            typeface = ResourcesCompat.getFont(
                                requireContext(),
                                R.font.montserrat_extrabold
                            )

                        }
                        setLabelCount(6, true)
                        xOffset = 15f
                        axisMinimum = 0f
                        minWidth = 45f
                        maxWidth = 45f
                        valueFormatter=object :ValueFormatter(){
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                if (value==0f){
                                    return 0f.toString()
                                }else{
                                    return DcFormat.tf.format(value)
                                }
                            }
                        }
                    }

                    binding.staLinechart1.isHighlightPerTapEnabled = true
                    val set = binding.staLinechart1.data.getDataSetByIndex(0)
                    val lastEntryIndex = set.entryCount - 1
                    val lastEntry = set.getEntryForIndex(lastEntryIndex)
                    binding.staLinechart1.highlightValue(lastEntry.x, 0, true)
                    //使用markview显示
                    binding.staLinechart1.data.isHighlightEnabled = true

                    //重绘刷新
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                }
            }
        }
        //viewModel.getAllInfo()


        binding.staTabHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position==1){
                    val list=weekOrderdList(viewModel.listByYear.value!!)
                    bmiList.clear()
                    for (i in 0 until list.size){
                        bmiList.add(Entry(list[i].week.toFloat(),list[i].bmi))
                    }
                    bmiList.sortBy { it.x }
                    Log.d(TAG,"weekList:${bmiList}")
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                }else if (
                    tab!!.position==0
                ){
                    val ls=orderList(viewModel.listByYear.value!!)
                    bmiList.clear()
                    bmiList.add(Entry(1f,0f))
                    for (i in 0 until ls.size){
                        bmiList.add(Entry(getDayOfYearFromDate(list!![i]).toFloat(),list!![i].bmi))
                    }
                    bmiList.sortBy { it.x }
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        //设置气泡
        val mv = CustomMarkerView(requireContext(), R.layout.mark_view_layout)
        binding.staLinechart1.marker = mv

        //重绘刷新
        //binding.staLinechart1.invalidate()
    }

    fun orderList(list:List<BmiInfo>):List<BmiInfo>{
        val ls= mutableListOf<BmiInfo>()
        for (i in 0 until list.size){
            if (ls.filter {
                    it.date == list[i].date
                }.isEmpty()){
                ls.add(list[i])
            }else{
                val index= ls.indexOfFirst { it.date==list[i].date }
                if (ls[index].save_time<list[i].save_time){
                    ls[index]=list[i]
                }
            }
        }
        Log.d(TAG,"ls:${ls}")
        return ls
    }

    fun weekOrderdList(list:List<BmiInfo>):List<DWeek>{
        val ls= mutableListOf<DWeek>()
        for (i in 0 until list.size){
            if (ls.filter {
                    it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i]))
                }.isEmpty()){
                ls.add(DWeek(Utils.dayToWeek(getDayOfYearFromDate(list[i])),list[i].bmi))
            }else{
                val index=ls.indexOfFirst { it.week==Utils.dayToWeek(getDayOfYearFromDate(list[i])) }
                ls[index].bmi=DcFormat.tf.format((ls[index].bmi+list[i].bmi)/2).toFloat()
            }
        }
        return ls
    }

    fun getDayOfYearFromDate(bmiInfo: BmiInfo):Int{
        val l1 = bmiInfo.date!!.split(" ")
        //获取当前的月份和天数
        val day = l1[1].split(",")[0]
        val month = Utils.monthToNumber(l1[0])
        Log.d(TAG,"day is :${Utils.getDayOfYear(day.toInt(),month)}")
        return Utils.getDayOfYear(day.toInt(),month)

    }
}