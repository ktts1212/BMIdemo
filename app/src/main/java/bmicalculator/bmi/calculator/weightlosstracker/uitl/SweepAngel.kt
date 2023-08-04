package bmicalculator.bmi.calculator.weightlosstracker.uitl

object SweepAngel {

    fun sweepAngle(num: Float): Float {
        val bmival = if (num > 40.3) 40.3f else if (num < 15.6) 15.6f else num
        return DcFormat.tf.format((bmival - 15.6) / 24.8 * 180 + 90)
            .replace(",", ".").toFloat()
    }

    fun childSweepAngle(num: Float): Float {
        val maxb = ChildBmiDialData.cScaleList[4].toFloat()
        val minb = ChildBmiDialData.cScaleList[0].toFloat()
        val bmival = if (num > maxb) maxb else if (num < minb) minb else num
        return DcFormat.tf.format((bmival - minb) / (maxb - minb) * 180 + 90)
            .replace(",", ".").toFloat()
    }
}