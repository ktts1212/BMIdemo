package bmicalculator.bmi.calculator.weightlosstracker.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object DcFormat {

    val nf=NumberFormat.getInstance(Locale.ENGLISH)

    val tf = DecimalFormat("#.0")

    val df = DecimalFormat("#.00")

    val ff = DecimalFormat("#")
}