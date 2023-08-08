package bmicalculator.bmi.calculator.weightlosstracker.uitl

import android.content.Context
import android.util.Log
import bmicalculator.bmi.calculator.weightlosstracker.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.math.pow

object Utils {


    private var Min=18.5

    private var Max=24.9

    fun initData(age:Int){
        if (age<=20){

            Min=ChildBmiDialData.cScaleList[1].toDouble()

            Max=ChildBmiDialData.cScaleList[2].toDouble()-0.1

        }else{
            Min=18.5

            Max=24.9
        }
    }

    //根据手机分辨率从dp单位转换为px（像素）
    fun dip2px(context: Context,deValue:Float): Int {
        val scale=context.resources.displayMetrics.density
        //四舍五入取整
        return (deValue*scale+0.5f).toInt()
    }
    //根据手机分辨率从px单位转换成dp
    fun px2dip(context: Context,pxValue:Float):Int{
        val scale=context.resources.displayMetrics.density
        return (pxValue/scale+0.5f).toInt()
    }

    fun sp2px(context: Context,pxValue: Float):Int{
        val scale=context.resources.displayMetrics.scaledDensity
        return (pxValue*scale+0.5f).toInt()
    }

    fun minWtftintokg(f:Int,i:Int): Double {
        return Min* (f * 0.3048 + i * 0.0254).pow(2.0)
    }

    fun maxWtftintokg(f:Int,i:Int): Double {
       return Max* (f * 0.3048 + i * 0.0254).pow(2.0)
    }

    fun minWtftintolb(f:Int,i:Int): Double {
        return Min* (12.0 * f + i).pow(2.0) /703
    }

    fun maxWtftintolb(f:Int,i:Int): Double {
       return Max* (12.0 * f + i).pow(2.0) /703
    }

    fun minCmtokg(t:Double):Double{
        return Min* (t * 0.01).pow(2.0)
    }

    fun maxCmtokg(t:Double):Double{
        return Max* (t * 0.01).pow(2.0)
    }

    fun minCmtolb(t:Double):Double{
        return Min* (t * 0.01).pow(2.0) /0.453
    }

    fun maxCmtolb(t:Double):Double{
        return Max* (t * 0.01).pow(2.0) /0.453
    }

    fun isInCurrentMonth(year:Int,month:Int):Boolean{
        val currentCalendar=Calendar.getInstance()
        return currentCalendar.get(Calendar.YEAR)==year&&
                currentCalendar.get(Calendar.MONTH)+1==month
    }

    fun monthToNumber(month: String): Int {
        val monthnum = when (month) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> -1
        }
        return monthnum
    }

    fun getDayOfYear(dayOfMonth:Int,month:Int,year:Int):Int{
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR,year)
        calendar.set(Calendar.MONTH,month-1)
        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

//    fun getDayOfMonth(dayOFYear:Int,year: Int): TimeBMi {
//        val calendar=Calendar.getInstance()
//        calendar.set(Calendar.YEAR,year)
//        calendar.set(Calendar.DAY_OF_YEAR,dayOFYear)
//        val dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH)
//        val month=calendar.get(Calendar.MONTH)+1
//        return TimeBMi(dayOfMonth,month)
//    }

    fun dayToWeek(dayOfYear:Int):Int{
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR,dayOfYear)
        Log.d("waaa","第${dayOfYear}天 is 第 ${calendar.get(Calendar.WEEK_OF_YEAR)}周")
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun dayToWeekNew(dayOfYear:Int):Int{
        val calendar=Calendar.getInstance()
        calendar.minimalDaysInFirstWeek=4
        calendar.set(Calendar.DAY_OF_YEAR,dayOfYear)
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

//    fun dayToWeekTest(dayOfYear:Int):Int{
//        val currentYear=LocalDate.now().year
//        val dMonth= getDayOfMonth(dayOfYear)
//        val date=LocalDate.of(currentYear,dMonth.month,dMonth.day)
//        val weekFields=WeekFields.of(Locale.getDefault())
//        val weekNumber=date.get(weekFields.weekOfWeekBasedYear())
//        return weekNumber
//    }
    fun dayToMonth(dayOfYear: Int): Int {
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR,LocalDate.now().year)
        calendar.set(Calendar.DAY_OF_YEAR,dayOfYear)
        return calendar.get(Calendar.MONTH)+1
    }

    fun determineDayOfWeekAndWeekOfMonth(date: LocalDate):Boolean{
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR,date.year)
        calendar.set(Calendar.MONTH,date.monthValue)
        calendar.set(Calendar.DAY_OF_MONTH,date.dayOfMonth)
        val dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)
        val weekOfMonth=calendar.get(Calendar.WEEK_OF_MONTH)
        val dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH)

        if (dayOfWeek==Calendar.MONDAY&&weekOfMonth==1&&dayOfMonth<=7){
            return true
        }else{
            return false
        }
    }

    fun isInFirstWeekOfMonth(date:LocalDate):Boolean{
        return date.dayOfMonth<=7
    }

    fun getMondayOnWeek(timeStamp:Long):Long{
        val cal=Calendar.getInstance()
        cal.time= Date(timeStamp)
        cal.firstDayOfWeek=Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
        return cal.time.time
    }

    fun getFirstDayOnMonth(timeStamp: Long):Long{
        val cal=Calendar.getInstance()
        cal.time=Date(timeStamp)
        cal.set(Calendar.DAY_OF_MONTH,1)
        return cal.time.time
    }

    fun monthsBewteen(timeStamp1: Long,timeStamp2: Long):Long{
        val instant1= Instant.ofEpochSecond(timeStamp1/1000)
        val instant2=Instant.ofEpochSecond(timeStamp2/1000)

        val date1=LocalDateTime.ofInstant(instant1, ZoneId.systemDefault()).toLocalDate()
        val date2=LocalDateTime.ofInstant(instant2, ZoneId.systemDefault()).toLocalDate()
        return Math.abs(Period.between(date1,date2).toTotalMonths())
    }

    fun phaseToNum(context: Context,phase:String):Int{
        when(phase){
            context.resources.getString(R.string.morning)->{
                return 1
            }
            context.resources.getString(R.string.afternoon)->{
                return 2
            }
            context.resources.getString(R.string.evening)->{
                return 3
            }
            context.resources.getString(R.string.night)->{
                return 4
            }
            else->{
                throw Exception("phaseToNum occur false")
            }
        }
    }

    fun numToPhase(context: Context,num:Int):String{
        when(num){
            1-> {
                return context.resources.getString(R.string.morning)
            }
            2-> {
                return context.resources.getString(R.string.afternoon)
            }
            3-> {
                return context.resources.getString(R.string.evening)
            }
            4-> {
                return context.resources.getString(R.string.night)
            }
            else->{
                throw Exception("numToPhase occur error")
            }
        }
    }

    fun numToMonth(num: Int): String {
        val monthnum = when (num) {
             1-> "Jan"
             2-> "Feb"
             3-> "Mar"
             4-> "Apr"
             5-> "May"
             6-> "June"
             7-> "July"
             8-> "Aug"
             9-> "Sep"
             10-> "Oct"
             11-> "Nov"
             12-> "Dec"
            else -> "null"
        }
        return monthnum
    }
}