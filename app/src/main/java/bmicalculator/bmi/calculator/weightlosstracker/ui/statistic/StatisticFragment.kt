package bmicalculator.bmi.calculator.weightlosstracker.ui.statistic

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.DYear
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.WtWeek
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.WtYear
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import java.time.LocalDateTime

private const val TAG = "StatisticFragment"

class StatisticFragment : Fragment() {

    private lateinit var binding: FragmentStatisticBinding

    private val bmiList = ArrayList<Entry>()

    private val kgList = ArrayList<Entry>()

    private val lbList = ArrayList<Entry>()

    private lateinit var viewModel: CalculatorViewModel

    private var list: List<BmiInfo>? = null

    private var dayList = ArrayList<Entry>()

    private var maxY = 0f

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

        if (viewModel.wttype == "error") {
            viewModel.wttype = "kg"
        }

        viewModel.listByYear.observe(requireActivity()) { info ->

            info?.let {
                if (it.isNullOrEmpty()) {
                    //throw Exception("There is no data")
                } else {

                    //设置dayBMI
                    viewModel.infoCount.postValue(it.size)
                    list = orderList(viewModel.listByYear.value!!)
                    bmiList.clear()
                    val currentTime = LocalDateTime.now()

                    
                    if (dayList.isEmpty()) {
                        dayList.add(Entry(0f, Float.NaN))
                    }
                    for (i in 1..currentTime.dayOfYear) {
                        dayList.add(Entry(i.toFloat(), Float.NaN))
                    }

                    for (i in 0 until list!!.size) {
                        val l1 = list!![i].date!!.split(" ")
                        //获取当前的月份和天数
                        val day = l1[1].split(",")[0]
                        val month = Utils.monthToNumber(l1[0])
                        val dayOfYear = Utils.getDayOfYear(day.toInt(), month)
                        dayList[dayOfYear].y = list!![i].bmi
                    }

                    Log.d(TAG, "dayList:${dayList}")


                    kgList.clear()
                    lbList.clear()

                    for (i in 0..list!!.size - 1) {
                        val l1 = list!![i].date!!.split(" ")
                        //获取当前的月份和天数
                        val day = l1[1].split(",")[0]
                        val month = Utils.monthToNumber(l1[0])

                        val dayOfYear = Utils.getDayOfYear(day.toInt(), month)
                        kgList.add(Entry(dayOfYear.toFloat(), list!![i].wt_kg.toFloat()))
                        lbList.add(Entry(dayOfYear.toFloat(), list!![i].wt_lb.toFloat()))
                        bmiList.add(Entry(dayOfYear.toFloat(), list!![i].bmi))
                    }


                    bmiList.sortBy { it.x }
                    if (bmiList[0].x != 1f) {
                        bmiList.add(0, Entry(1f, 0f))
                    }

                    val currentDate = LocalDateTime.now()
                    while (bmiList[bmiList.size - 1].x < currentDate.dayOfYear) {
                        val day = bmiList[bmiList.size - 1].x + 1
                        bmiList.add(Entry(day, 0f))

                    }
                    chartStyle(dayList)
                    if (viewModel.wttype == "kg") {
                        kgList.sortBy { it.x }
                        if (kgList[0].x != 1f) {
                            kgList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(kgList)
                    } else {
                        lbList.sortBy { it.x }
                        if (lbList[0].x != 1f) {
                            lbList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(lbList)
                    }


                    binding.staLinechart2.apply {
                        setTouchEnabled(true)
                        setDrawGridBackground(false)
                        legend.isEnabled = false
                        //自适应
                        isAutoScaleMinMaxEnabled = true
                        //x轴可拖拽
                        isDragXEnabled = true
                        setDrawBorders(false)
                        setScaleEnabled(false)
                        description.isEnabled = false
                    }

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
                        description.isEnabled = false
                    }

                    val xAxisto = binding.staLinechart2.xAxis

                    xAxisto.apply {
                        isEnabled = true
                        gridColor = Color.parseColor("#EEEEEE")
                        if (isAdded()) {
                            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        }
                        //是否绘制x轴
                        setDrawAxisLine(false)
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
                                if (binding.staTabHeader.getTabAt(0)!!.isSelected) {
                                    val dMonth = Utils.getDayOfMonth(value.toInt())

                                    return dMonth.day.toString()
                                } else {
                                    return value.toInt().toString()
                                }

                            }
                        }
                        spaceMax = 0.5f
                    }

                    val xAxis = binding.staLinechart1.xAxis
//                    val render=MyLineChartXRender(binding.staLinechart1.viewPortHandler,
//                    binding.staLinechart1.xAxis,binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT))
//                    binding.staLinechart1.setXAxisRenderer(render)

                    xAxis.apply {
                        isEnabled = true
                        gridColor = Color.parseColor("#EEEEEE")
                        if (isAdded()) {
                            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        }
                        //是否绘制x轴
                        setDrawAxisLine(false)
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
                                if (binding.staTabHeader.getTabAt(0)!!.isSelected) {
                                    val dMonth = Utils.getDayOfMonth(value.toInt())
                                    return dMonth.day.toString()
                                } else {
                                    return value.toInt().toString()
                                }

                            }
                        }
                        spaceMax = 0.5f
                    }

                    binding.staLinechart2.axisRight.apply {
                        isEnabled = false
                        setDrawGridLines(false)
                    }

                    binding.staLinechart2.axisLeft.apply {
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
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                if (value == 0f) {
                                    return 0f.toString()
                                } else {
                                    return DcFormat.tf.format(value)
                                }
                            }
                        }

                        spaceTop = 25f
                    }

                    binding.staLinechart2.isHighlightPerTapEnabled = true
                    val set2 = binding.staLinechart2.data.getDataSetByIndex(0)
                    val lastEntryIndex2 = set2.entryCount - 1
                    val lastEntry2 = set2.getEntryForIndex(lastEntryIndex2)
                    binding.staLinechart2.highlightValue(lastEntry2.x, 0, true)
                    //使用markview显示
                    binding.staLinechart2.data.isHighlightEnabled = true

                    //重绘刷新
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()

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
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                if (value == 0f) {
                                    return 0f.toString()
                                } else {
                                    return DcFormat.tf.format(value)
                                }
                            }

                            override fun getFormattedValue(value: Float): String {
                                Log.d(TAG, "value:${value}")
                                if (value == 0f) {
                                    return ""
                                } else {
                                    return value.toString()
                                }
                            }
                        }

                        spaceTop = 25f
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

        //根据当前viewmodel中存储的年龄性别来判断bmi
        binding.staLinechart1.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {

            }

            override fun onNothingSelected() {

            }

        })

        binding.staLinechart1.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {

            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {

            }

            override fun onChartLongPressed(me: MotionEvent?) {
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {

                val lowX = binding.staLinechart1.lowestVisibleX.toInt()

                val highX = binding.staLinechart1.highestVisibleX.toInt()

                val dataSet = binding.staLinechart1.data.getDataSetByIndex(0)

                for (i in 0 until dataSet.entryCount) {
                    val entry = dataSet.getEntryForIndex(i)
                    if (entry != null && entry.x.toInt() in lowX..highX && entry.y > maxY) {
                        maxY = entry.y
                    }
                }
                binding.staLinechart1.axisLeft.axisMaximum = maxY * 4 / 3
            }

        }

        binding.staTabHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 1) {
                    val ls = orderList(viewModel.listByYear.value!!)
                    val list = weekOrderdList(ls)
                    val wtList: List<WtWeek>
                    if (viewModel.wttype == "kg") {
                        wtList = wtKgWeekOrderList(ls)
                        kgList.clear()
                        for (i in 0 until wtList.size) {
                            kgList.add(Entry(wtList[i].week.toFloat(), wtList[i].weight))
                        }
                        kgList.sortBy { it.x }
                        if (kgList[0].x != 1f) {
                            kgList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(kgList)
                    } else {
                        wtList = wtLbWeekOrderList(ls)
                        lbList.clear()
                        for (i in 0 until wtList.size) {
                            lbList.add(Entry(wtList[i].week.toFloat(), wtList[i].weight))
                        }
                        lbList.sortBy { it.x }
                        if (lbList[0].x != 1f) {
                            lbList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(lbList)
                    }
                    bmiList.clear()

                    for (i in 0 until list.size) {
                        bmiList.add(Entry(list[i].week.toFloat(), list[i].bmi))
                    }
                    bmiList.sortBy { it.x }
                    if (bmiList[0].x != 1f) {
                        bmiList.add(0, Entry(1f, 0f))
                    }
                    Log.d(TAG, "weekList:${bmiList}")
                    chartStyle(bmiList)
                    //重绘刷新
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                } else if (
                    tab.position == 0
                ) {
                    val ls = orderList(viewModel.listByYear.value!!)
                    bmiList.clear()
                    if (viewModel.wttype == "kg") {
                        kgList.clear()
                        for (i in 0 until ls.size) {
                            kgList.add(
                                Entry(
                                    getDayOfYearFromDate(list!![i]).toFloat(),
                                    list!![i].wt_kg.toFloat()
                                )
                            )
                        }
                        kgList.sortBy { it.x }
                        if (kgList[0].x != 1f) {
                            kgList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(kgList)
                    } else {
                        lbList.clear()
                        for (i in 0 until ls.size) {
                            lbList.add(
                                Entry(
                                    getDayOfYearFromDate(list!![i]).toFloat(),
                                    list!![i].wt_lb.toFloat()
                                )
                            )
                        }
                        lbList.sortBy { it.x }
                        if (lbList[0].x != 1f) {
                            lbList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(lbList)
                    }
                    for (i in 0 until ls.size) {
                        bmiList.add(Entry(getDayOfYearFromDate(list!![i]).toFloat(), list!![i].bmi))
                    }

                    bmiList.sortBy { it.x }
                    if (bmiList[0].x != 1f) {
                        bmiList.add(0, Entry(1f, 0f))
                    }
                    Log.d(TAG, "daybmilist:${bmiList}")
                    chartStyle(bmiList)
                    //重绘刷新
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                } else if (tab.position == 2) {
                    val ls = orderList(viewModel.listByYear.value!!)
                    val list = monthOrderList(ls)
                    bmiList.clear()
                    val wtList: List<WtYear>
                    if (viewModel.wttype == "kg") {
                        kgList.clear()
                        wtList = wtKgMonthOrderList(ls)
                        for (i in 0 until wtList.size) {
                            kgList.add(
                                Entry(
                                    wtList[i].month.toFloat(),
                                    wtList[i].weight
                                )
                            )
                        }
                        kgList.sortBy { it.x }
                        if (kgList[0].x != 1f) {
                            kgList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(kgList)
                    } else {
                        lbList.clear()
                        wtList = wtLbMonthOrderList(ls)
                        for (i in 0 until wtList.size) {
                            lbList.add(
                                Entry(
                                    wtList[i].month.toFloat(),
                                    wtList[i].weight
                                )
                            )
                        }
                        lbList.sortBy { it.x }
                        if (lbList[0].x != 1f) {
                            lbList.add(0, Entry(1f, 0f))
                        }
                        chart2Style(lbList)
                    }

                    for (i in 0 until list.size) {
                        bmiList.add(
                            Entry(
                                list[i].month.toFloat(),
                                list[i].bmi
                            )
                        )
                    }
                    bmiList.sortBy { it.x }
                    if (bmiList[0].x != 1f) {
                        bmiList.add(0, Entry(1f, 0f))
                    }
                    chartStyle(bmiList)
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
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
        binding.staLinechart2.marker = mv

        //重绘刷新
        //binding.staLinechart1.invalidate()
    }

    //根据日期给查询出的list进行排序
    fun orderList(list: List<BmiInfo>): List<BmiInfo> {
        val ls = mutableListOf<BmiInfo>()
        for (i in 0 until list.size) {
            if (ls.filter {
                    it.date == list[i].date
                }.isEmpty()) {
                ls.add(list[i])
            } else {
                val index = ls.indexOfFirst { it.date == list[i].date }
                if (ls[index].save_time < list[i].save_time) {
                    ls[index] = list[i]
                }
            }
        }
        Log.d(TAG, "ls:${ls}")
        return ls
    }

    fun wtKgWeekOrderList(list: List<BmiInfo>): List<WtWeek> {
        val ls = mutableListOf<WtWeek>()
        for (i in 0 until list.size) {
            if (ls.filter {
                    it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i]))
                }.isEmpty()) {
                ls.add(
                    WtWeek(
                        Utils.dayToWeek(getDayOfYearFromDate(list[i])),
                        list[i].wt_kg.toFloat()
                    )
                )
            } else {
                val index =
                    ls.indexOfFirst { it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i])) }
                ls[index].weight =
                    DcFormat.tf.format((ls[index].weight + list[i].wt_kg) / 2).toFloat()
            }
        }
        return ls
    }

    fun wtLbWeekOrderList(list: List<BmiInfo>): List<WtWeek> {
        val ls = mutableListOf<WtWeek>()
        for (i in 0 until list.size) {
            if (ls.filter {
                    it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i]))
                }.isEmpty()) {
                ls.add(
                    WtWeek(
                        Utils.dayToWeek(getDayOfYearFromDate(list[i])),
                        list[i].wt_lb.toFloat()
                    )
                )
            } else {
                val index =
                    ls.indexOfFirst { it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i])) }
                ls[index].weight =
                    DcFormat.tf.format((ls[index].weight + list[i].wt_lb) / 2).toFloat()
            }
        }
        return ls
    }


    fun weekOrderdList(list: List<BmiInfo>): List<DWeek> {

        val ls = mutableListOf<DWeek>()
        for (i in 0 until list.size) {
            if (ls.filter {
                    it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i]))
                }.isEmpty()) {
                ls.add(DWeek(Utils.dayToWeek(getDayOfYearFromDate(list[i])), list[i].bmi))
            } else {
                val index =
                    ls.indexOfFirst { it.week == Utils.dayToWeek(getDayOfYearFromDate(list[i])) }
                ls[index].bmi = DcFormat.tf.format((ls[index].bmi + list[i].bmi) / 2).toFloat()
            }
        }
        return ls
    }

    fun getDayOfYearFromDate(bmiInfo: BmiInfo): Int {
        val l1 = bmiInfo.date!!.split(" ")
        //获取当前的月份和天数
        val day = l1[1].split(",")[0]
        val month = Utils.monthToNumber(l1[0])
        Log.d(TAG, "day is :${Utils.getDayOfYear(day.toInt(), month)}")
        return Utils.getDayOfYear(day.toInt(), month)
    }

    fun monthOrderList(list: List<BmiInfo>): List<DYear> {
        val ls = mutableListOf<DYear>()
        for (i in 0 until list.size) {
            val month = Utils.dayToMonth(getDayOfYearFromDate(list[i]))
            if (ls.filter {
                    it.month == month
                }.isEmpty()) {
                ls.add(DYear(month, list[i].bmi))
            } else {
                val index = ls.indexOfFirst { it.month == month }
                ls[index].bmi = DcFormat.tf.format((ls[index].bmi + list[i].bmi) / 2).toFloat()
            }
        }
        return ls
    }

    fun wtKgMonthOrderList(list: List<BmiInfo>): List<WtYear> {
        val ls = mutableListOf<WtYear>()
        for (i in 0 until list.size) {
            val month = Utils.dayToMonth(getDayOfYearFromDate(list[i]))
            if (ls.filter {
                    it.month == month
                }.isEmpty()) {
                ls.add(WtYear(month, list[i].wt_kg.toFloat()))
            } else {
                val index = ls.indexOfFirst { it.month == month }
                ls[index].weight =
                    DcFormat.tf.format((ls[index].weight + list[i].wt_kg) / 2).toFloat()
            }
        }
        return ls
    }

    fun wtLbMonthOrderList(list: List<BmiInfo>): List<WtYear> {
        val ls = mutableListOf<WtYear>()
        for (i in 0 until list.size) {
            val month = Utils.dayToMonth(getDayOfYearFromDate(list[i]))
            if (ls.filter {
                    it.month == month
                }.isEmpty()) {
                ls.add(WtYear(month, list[i].wt_lb.toFloat()))
            } else {
                val index = ls.indexOfFirst { it.month == month }
                ls[index].weight =
                    DcFormat.tf.format((ls[index].weight + list[i].wt_lb) / 2).toFloat()
            }
        }
        return ls
    }


    fun chartStyle(bmiList: List<Entry>) {

        val lineDataSet = LineDataSet(bmiList, null)

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
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
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

        binding.staLinechart1.data = lineData

        binding.staLinechart1.moveViewToX(bmiList[bmiList.size - 1].x)
        if (bmiList[bmiList.size - 1].x < 7) {

            binding.staLinechart1.xAxis.setLabelCount(bmiList.size, false)
            binding.staLinechart1.setVisibleXRange(bmiList.size.toFloat(), bmiList.size.toFloat())
        } else {
            binding.staLinechart1.setVisibleXRange(7f, 7f)
            binding.staLinechart1.xAxis.setLabelCount(7, false)
        }
        binding.staLinechart1.xAxis.granularity = 1f
        binding.staLinechart1.axisLeft.spaceTop = 25f
    }

    fun chart2Style(wtList: List<Entry>) {

        val lineDataSet = LineDataSet(wtList, null)

        val fillGradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor("#E0FFFFFF"), Color.parseColor("#40FFFFFF"))
        )
        lineDataSet.fillDrawable = fillGradient

        lineDataSet.apply {
            color = Color.WHITE
            //曲线
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            //setDrawValues(false)
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry?): String {
                    val lastIndex = binding.staLinechart2.data.getDataSetByIndex(0)
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

        binding.staLinechart2.data = lineData

        binding.staLinechart2.moveViewToX(wtList[wtList.size - 1].x)
        if (wtList[wtList.size - 1].x < 7) {

            binding.staLinechart2.xAxis.setLabelCount(wtList.size, false)
            binding.staLinechart2.setVisibleXRange(wtList.size.toFloat(), wtList.size.toFloat())
        } else {
            binding.staLinechart2.setVisibleXRange(7f, 7f)
            binding.staLinechart2.xAxis.setLabelCount(7, false)
        }
        binding.staLinechart2.xAxis.granularity = 1f
        binding.staLinechart2.axisLeft.spaceTop = 25f
    }

}