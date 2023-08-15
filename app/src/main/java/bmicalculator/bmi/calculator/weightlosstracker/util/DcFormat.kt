package bmicalculator.bmi.calculator.weightlosstracker.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

object DcFormat {

    var tf: DecimalFormat? =null

    var df:DecimalFormat?=null

    var ff:DecimalFormat?=null

    val enList= listOf("en","zh-rCN","zh-rTW","ko")


    fun setData(language:String){
        if (language in enList){
            tf = DecimalFormat("#.0", DecimalFormatSymbols(Locale.ENGLISH))

            df = DecimalFormat("#.00",DecimalFormatSymbols(Locale.ENGLISH))

            ff = DecimalFormat("#",DecimalFormatSymbols(Locale.ENGLISH))
        }else{
            tf = DecimalFormat("#.0", DecimalFormatSymbols(Locale.GERMAN))

            df = DecimalFormat("#.00",DecimalFormatSymbols(Locale.GERMAN))

            ff = DecimalFormat("#",DecimalFormatSymbols(Locale.GERMAN))
        }
    }

}