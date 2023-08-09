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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentStatisticBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiDate
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.History
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.KgDate
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomMarkerView2
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomXAxisRenderer
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

private const val TAG = "StatisticFragment"

class StatisticFragment : Fragment() {

    private lateinit var binding: FragmentStatisticBinding

    private val kgDayList = ArrayList<Entry>()

    private val dateList = ArrayList<History>()

    private lateinit var viewModel: CalculatorViewModel

    private var list: List<BmiInfo>? = null

    private var dayList = ArrayList<Entry>()

    private var weekList = ArrayList<Entry>()

    private var kgWeekList=ArrayList<Entry>()

    private var monthList=ArrayList<Entry>()

    private var kgMonthList=ArrayList<Entry>()

    private var maxY = 0f

    private var maxKg=0f

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

        viewModel.allInfo.observe(requireActivity()) { info ->

            info?.let {
                if (it.isNullOrEmpty()) {
                    //throw Exception("There is no data")
                } else {

                    //设置dayBMI
                    viewModel.infoCount.postValue(it.size)
                    //每日数据只有一个且为最新保存的数据
                    list = orderList(viewModel.allInfo.value!!)

                    //dayList记录已天为单位的数据
                    if (!dayList.isEmpty()) {
                        dayList.clear()
                    }
                    //保存kg的日单位数据
                    kgDayList.clear()

                    dateList.clear()
                    //往dateList中添加数据
                    //将所有数据及其根据日期获得的时间戳加入dateList
                    for (i in 0 until list!!.size) {
                        if (list!![i].bmi > maxY) {
                            maxY = list!![i].bmi
                        }
                        if (list!![i].wt_kg>maxKg){
                            maxKg=list!![i].wt_kg.toFloat()
                        }
                        val l1 = list!![i].date!!.split(" ")
                        //获取当前的月份和天数，年
                        val day = l1[1].split(",")[0]
                        val month = Utils.monthToNumber(l1[0])
                        val year = list!![i].year
                        val date = LocalDate.of(year, month, day.toInt())
                        val timeStamp = date.atStartOfDay(
                            ZoneId.systemDefault()
                        ).toInstant().toEpochMilli()
                        dateList.add(History(list!![i], timeStamp))
                    }
                    //对dateList进行排序，获取正确的数据顺序
                    //将第一个数据设置为起始1，
                    dateList.sortBy { it.datetimestamp }
                    //添加BMI的day单位数据
                    dayList.add(Entry(1f, dateList[0].bmiInfo.bmi))
                    //将第一个数据设置为起始1，添加其他数据并计算相差天数
                    for (i in 1 until dateList.size) {
                        val timeStamp = dateList[i].datetimestamp
                        val diffInMill = Math.abs(timeStamp - dateList[0].datetimestamp)
                        //将毫秒转化为天数
                        Log.d(TAG, "diffInMill:${diffInMill}")
                        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMill)
                        Log.d(TAG, "diffInDays:${diffInDays}")
                        dayList.add(Entry((diffInDays + 1).toFloat(), dateList[i].bmiInfo.bmi))
                    }
                    chartStyle(dayList)

                    //往kgDayList中添加数据
                    for (i in 0 until dateList.size){
                        val timeStamp=dateList[i].datetimestamp
                        val differInMill=Math.abs(timeStamp-dateList[0].datetimestamp)
                        val diffInDays=TimeUnit.MILLISECONDS.toDays(differInMill)
                        kgDayList.add(Entry((diffInDays+1).toFloat(),dateList[i].bmiInfo.wt_kg.toFloat()))
                    }
                    chart2Style(kgDayList)

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

                    if (isAdded) {
                        binding.staLinechart1.setXAxisRenderer(
                            CustomXAxisRenderer(
                                0,
                                dateList[0].datetimestamp.toString(), requireContext(),
                                binding.staLinechart1.viewPortHandler, binding.staLinechart1.xAxis,
                                binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT)
                            )
                        )

                        binding.staLinechart2.setXAxisRenderer(
                            CustomXAxisRenderer(
                                0,
                                dateList[0].datetimestamp.toString(), requireContext(),
                                binding.staLinechart2.viewPortHandler, binding.staLinechart2.xAxis,
                                binding.staLinechart2.getTransformer(YAxis.AxisDependency.LEFT)
                            )
                        )
                    }



                    val xAxis = binding.staLinechart1.xAxis

                    xAxis.apply {
                        isEnabled = true
                        gridColor = Color.parseColor("#60EEEEEE")
                        if (isAdded()) {
                            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        }
                        //是否绘制x轴
                        setDrawAxisLine(false)
                        textColor = Color.WHITE
                        setDrawLimitLinesBehindData(false)
                        position = XAxis.XAxisPosition.BOTTOM
                    }

                    val xAxisto = binding.staLinechart2.xAxis

                    xAxisto.apply {
                        isEnabled = true
                        gridColor = Color.parseColor("#60EEEEEE")
                        if (isAdded()) {
                            gridLineWidth = Utils.dip2px(requireContext(), 0.5f).toFloat()
                        }
                        //是否绘制x轴
                        setDrawAxisLine(false)
                        setDrawLimitLinesBehindData(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        }

                    //取消右侧y轴
                    binding.staLinechart1.axisRight.apply {
                        isEnabled = false
                        setDrawGridLines(false)
                    }

                    binding.staLinechart2.axisRight.apply {
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
                        //axisMinimum = 0f
                        axisMaximum = maxY * 4 / 3
                        minWidth = 45f
                        maxWidth = 45f
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                return DcFormat.tf.format(value)
                            }
                        }
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
                        axisMaximum = maxKg * 4 / 3
                        minWidth = 45f
                        maxWidth = 45f
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                    return DcFormat.tf.format(value)
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
                }
            }
        }

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
                //以周为单位
                if (tab!!.position == 1) {
                    //每日数据只有一个且为最新保存的数据
                    val ls = orderList(viewModel.allInfo.value!!)
                    weekList.clear()
                    kgWeekList.clear()

                    //往dateList中添加数据
                    //将所有数据及其根据日期获得的时间戳加入dateList
                    maxY = 0f
                    maxKg=0f
                    for (i in 0 until ls.size) {
                        if (ls[i].bmi > maxY) {
                            maxY = ls[i].bmi
                        }

                        if (ls[i].wt_kg>maxKg){
                            maxKg=ls[i].wt_kg.toFloat()
                        }
                    }
                    //对dateList进行排序，获取正确的数据顺序
                    //将第一个数据设置为起始1，
                    dateList.sortBy { it.datetimestamp }

                    val list = weekOrderdList(dateList)
                    val kgList=wtKgWeekOrderList(dateList)

                    //根据时间戳进行排序
                    list.sortedBy { it.timeStamp }
                    kgList.sortedBy { it.timeStamp }

                    for (i in 0 until list.size) {
                        //weekList不需要计算相差的天数，需要计算相差的周数
                        val timeStamp = list[i].timeStamp
                        val diffInMill = Math.abs(timeStamp - list[0].timeStamp)
                        //将毫秒转化为天数
                        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMill)
                        //天数除以7获取相差的周数
                        val diffInWeeks = (diffInDays + 1) / 7 + 1
                        weekList.add(Entry(diffInWeeks.toFloat(), list[i].bmi))
                        kgWeekList.add(Entry(diffInWeeks.toFloat(), kgList[i].kg))
                    }
                    chartStyle(weekList)
                    chart2Style(kgWeekList)
                    binding.staLinechart1.setXAxisRenderer(
                        CustomXAxisRenderer(
                            1,
                            list[0].timeStamp.toString(), requireContext(),
                            binding.staLinechart1.viewPortHandler, binding.staLinechart1.xAxis,
                            binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )

                    binding.staLinechart2.setXAxisRenderer(
                        CustomXAxisRenderer(
                            1,
                            kgList[0].timeStamp.toString(), requireContext(),
                            binding.staLinechart2.viewPortHandler, binding.staLinechart2.xAxis,
                            binding.staLinechart2.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )

                    //重绘刷新
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                }else if (tab.position==2){
                    //每日数据只有一个且为最新保存的数据
                    val ls = orderList(viewModel.allInfo.value!!)
                    monthList.clear()
                    kgMonthList.clear()
                    //往dateList中添加数据
                    //将所有数据及其根据日期获得的时间戳加入dateList
                    maxY = 0f
                    maxKg=0f
                    for (i in 0 until ls.size) {
                        if (ls[i].bmi > maxY) {
                            maxY = ls[i].bmi
                        }
                        if (ls[i].wt_kg>maxKg){
                            maxKg=ls[i].wt_kg.toFloat()
                        }
                    }

                    val list=monthOrderedList(dateList)
                    val kgList=wtKgMonthOrderList(dateList)
                    //根据时间戳进行排序
                    list.sortedBy { it.timeStamp }
                    kgList.sortedBy { it.timeStamp }

                    for (i in 0 until list.size) {
                        //monthList不需要计算相差的天数，需要计算相差的月数
                        val differMonth=Utils.monthsBewteen(list[0].timeStamp,list[i].timeStamp)
                        monthList.add(Entry(differMonth.toFloat()+1, list[i].bmi))
                        kgMonthList.add(Entry(differMonth.toFloat()+1,kgList[i].kg))
                    }
                    chartStyle(monthList)
                    chart2Style(kgMonthList)
                    binding.staLinechart1.setXAxisRenderer(
                        CustomXAxisRenderer(
                            2,
                            list[0].timeStamp.toString(), requireContext(),
                            binding.staLinechart1.viewPortHandler, binding.staLinechart1.xAxis,
                            binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )
                    binding.staLinechart2.setXAxisRenderer(
                        CustomXAxisRenderer(
                            2,
                            list[0].timeStamp.toString(), requireContext(),
                            binding.staLinechart2.viewPortHandler, binding.staLinechart2.xAxis,
                            binding.staLinechart2.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
                }else{
                    dayList.clear()
                    kgDayList.clear()
                    dayList.add(Entry(1f, dateList[0].bmiInfo.bmi))
                    kgDayList.add(Entry(1f,dateList[0].bmiInfo.wt_kg.toFloat()))
                    //将第一个数据设置为起始1，添加其他数据并计算相差天数
                    for (i in 1 until dateList.size) {
                        val timeStamp = dateList[i].datetimestamp
                        val diffInMill = Math.abs(timeStamp - dateList[0].datetimestamp)
                        //将毫秒转化为天数
                        Log.d(TAG, "diffInMill:${diffInMill}")
                        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMill)
                        Log.d(TAG, "diffInDays:${diffInDays}")
                        dayList.add(Entry((diffInDays + 1).toFloat(), dateList[i].bmiInfo.bmi))
                        kgDayList.add(Entry((diffInDays+1).toFloat(),dateList[i].bmiInfo.wt_kg.toFloat()))
                    }
                    chartStyle(dayList)
                    chart2Style(kgDayList)
                    binding.staLinechart1.setXAxisRenderer(
                        CustomXAxisRenderer(
                            0,
                            dateList[0].datetimestamp.toString(), requireContext(),
                            binding.staLinechart1.viewPortHandler, binding.staLinechart1.xAxis,
                            binding.staLinechart1.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )
                    binding.staLinechart2.setXAxisRenderer(
                        CustomXAxisRenderer(
                            0,
                            dateList[0].datetimestamp.toString(), requireContext(),
                            binding.staLinechart2.viewPortHandler, binding.staLinechart2.xAxis,
                            binding.staLinechart2.getTransformer(YAxis.AxisDependency.LEFT)
                        )
                    )
                    binding.staLinechart1.notifyDataSetChanged()
                    binding.staLinechart1.invalidate()
                    binding.staLinechart2.notifyDataSetChanged()
                    binding.staLinechart2.invalidate()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        //设置气泡
        val mv = CustomMarkerView(requireContext(), R.layout.mark_view_layout)
        val mv2=CustomMarkerView2(requireContext(),R.layout.mark_view_layout2)
        binding.staLinechart1.marker = mv
        binding.staLinechart2.marker = mv2

        binding.staDisp2.setOnClickListener {
            val navView=
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId=R.id.menu_calculator
            val fragmentManager=(activity as AppCompatActivity).supportFragmentManager
            val transition=fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container,CalculatorFragment())
            transition.commit()
        }

        binding.staWtdisp2.setOnClickListener {
            val navView=
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId=R.id.menu_calculator
            val fragmentManager=(activity as AppCompatActivity).supportFragmentManager
            val transition=fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container,CalculatorFragment())
            transition.commit()
        }

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

    fun wtKgWeekOrderList(list: List<History>): List<KgDate> {
        val ls = mutableListOf<KgDate>()
        val lsCount= mutableListOf<Int>()
        for (i in 0 until list.size) {
            val monday=Utils.getMondayOnWeek(list[i].datetimestamp)
            if (ls.filter { it.timeStamp==monday }.isEmpty()){
                ls.add(KgDate(monday,list[i].bmiInfo.wt_kg.toFloat()))
                lsCount.add(1)
            }else{
                val index=
                    ls.indexOfFirst { it.timeStamp==monday }
                lsCount[index]++
                ls[index].kg=
                    DcFormat.tf.format(
                        (ls[index].kg+list[i].bmiInfo.wt_kg)
                    ).replace(",",".").toFloat()
            }
        }
        for (i in 0 until ls.size){
            ls[i].kg=DcFormat.tf.format(ls[i].kg/lsCount[i])
                .replace(",",".").toFloat()
        }
        return ls
    }

    fun weekOrderdList(list: List<History>):List<BmiDate> {
        val ls = mutableListOf<BmiDate>()
        val lsCount= mutableListOf<Int>()
        for (i in 0 until list.size) {
            val monday = Utils.getMondayOnWeek(list[i].datetimestamp)
            if (ls.filter {
                    it.timeStamp == monday
                }.isEmpty()) {
                ls.add(BmiDate(monday, list[i].bmiInfo.bmi))
                lsCount.add(1)
            } else {
                val index =
                    ls.indexOfFirst { it.timeStamp == monday }
                lsCount[index]++
                ls[index].bmi =
                    DcFormat.tf.format(
                        (ls[index].bmi + list[i].bmiInfo.bmi))
                        .replace(",", ".").toFloat()
            }
        }
        for (i in 0 until ls.size){
            ls[i].bmi=DcFormat.tf.format(ls[i].bmi/lsCount[i])
                .replace(",",".").toFloat()
        }
        return ls
    }

    fun wtKgMonthOrderList(list: List<History>): List<KgDate> {
        val ls = mutableListOf<KgDate>()
        val lsCount= mutableListOf<Int>()
        for (i in 0 until list.size) {
            val firstDayOnMonth=Utils.getFirstDayOnMonth(list[i].datetimestamp)
            if (ls.filter {it.timeStamp==firstDayOnMonth  }.isEmpty()){
                ls.add(KgDate(firstDayOnMonth,list[i].bmiInfo.wt_kg.toFloat()))
                lsCount.add(1)
            }else{
                val index=ls.indexOfFirst { it.timeStamp==firstDayOnMonth }
                lsCount[index]++
                ls[index].kg=DcFormat.tf.format((ls[index].kg + list[i].bmiInfo.wt_kg))
                    .replace(",",".").toFloat()
            }
        }
        for (i in 0 until  ls.size){
            ls[i].kg=DcFormat.tf.format((ls[i].kg) / lsCount[i])
                .replace(",",".").toFloat()
        }
        return ls
    }

    fun monthOrderedList(list:List<History>):List<BmiDate>{
        val ls= mutableListOf<BmiDate>()
        //用来记录这个月有多少数据
        val lsCount= mutableListOf<Int>()
        for (i in 0 until list.size){
            val firstDayOnMonth=Utils.getFirstDayOnMonth(list[i].datetimestamp)
            if (ls.filter {it.timeStamp==firstDayOnMonth  }.isEmpty()){
                ls.add(BmiDate(firstDayOnMonth,list[i].bmiInfo.bmi))
                lsCount.add(1)
            }else{
                val index=ls.indexOfFirst { it.timeStamp==firstDayOnMonth }
                lsCount[index]++
                ls[index].bmi=DcFormat.tf.format((ls[index].bmi + list[i].bmiInfo.bmi))
                    .replace(",",".").toFloat()
            }
        }
        for (i in 0 until  ls.size){
            ls[i].bmi=DcFormat.tf.format((ls[i].bmi) / lsCount[i])
                .replace(",",".").toFloat()
        }
        return ls
    }
    fun chartStyle(bmiList: List<Entry>) {

        val lineDataSet = LineDataSet(bmiList, null)
        //填充数据线到x轴之间的区域
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
                    //获取图表一的数据集
                        return ""
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



        if (binding.staTabHeader.getTabAt(0)!!.isSelected==true){
            binding.staLinechart1.xAxis.axisMaximum=bmiList[bmiList.size-1].x+7f
            binding.staLinechart1.xAxis.axisMinimum=bmiList[0].x-7f
            binding.staLinechart1.setVisibleXRange(7f, 7f)
            binding.staLinechart1.xAxis.setLabelCount(7, false)
        }else if (binding.staTabHeader.getTabAt(1)!!.isSelected==true){
            binding.staLinechart1.xAxis.axisMaximum=bmiList[bmiList.size-1].x+7f
            binding.staLinechart1.xAxis.axisMinimum=bmiList[0].x-7f
            binding.staLinechart1.setVisibleXRange(7f, 7f)
            binding.staLinechart1.xAxis.setLabelCount(7, false)
        }else if (binding.staTabHeader.getTabAt(2)!!.isSelected==true){
            binding.staLinechart1.xAxis.axisMaximum=bmiList[bmiList.size-1].x+7f
            binding.staLinechart1.xAxis.axisMinimum=bmiList[0].x-7f
            binding.staLinechart1.setVisibleXRange(7f, 7f)
            binding.staLinechart1.xAxis.setLabelCount(7, false)
        }
        binding.staLinechart1.moveViewToX(bmiList[bmiList.size - 1].x-4f)
        binding.staLinechart1.xAxis.granularity = 1f
        binding.staLinechart1.axisLeft.spaceTop = 25f
        binding.staLinechart1.setExtraOffsets(0f,0f,35f,0f)
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
                        return ""
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
        if (binding.staTabHeader.getTabAt(0)!!.isSelected==true){
            binding.staLinechart2.xAxis.axisMaximum=wtList[wtList.size-1].x+7f
            binding.staLinechart2.xAxis.axisMinimum=wtList[0].x-7f
            binding.staLinechart2.setVisibleXRange(7f, 7f)
            binding.staLinechart2.xAxis.setLabelCount(7, false)
        }else if (binding.staTabHeader.getTabAt(1)!!.isSelected==true){
            binding.staLinechart2.xAxis.axisMaximum=wtList[wtList.size-1].x+7f
            binding.staLinechart2.xAxis.axisMinimum=wtList[0].x-7f
            binding.staLinechart2.setVisibleXRange(7f, 7f)
            binding.staLinechart2.xAxis.setLabelCount(7, false)
        }else if (binding.staTabHeader.getTabAt(2)!!.isSelected==true) {
            binding.staLinechart2.xAxis.axisMaximum = wtList[wtList.size - 1].x + 7f
            binding.staLinechart2.xAxis.axisMinimum=wtList[0].x-7f
            binding.staLinechart2.setVisibleXRange(7f, 7f)
            binding.staLinechart2.xAxis.setLabelCount(7, false)
        }
        binding.staLinechart2.moveViewToX(wtList[wtList.size - 1].x-4f)
        binding.staLinechart2.xAxis.granularity = 1f
        binding.staLinechart2.axisLeft.spaceTop = 25f
        binding.staLinechart2.setExtraOffsets(0f,0f,35f,0f)
    }

}