package bmicalculator.bmi.calculator.weightlosstracker

import android.app.Application
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.DatePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.TimePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.statistic.StatisticFragment
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.external.ExternalAdaptInfo

class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        //多线程适配
        AutoSize.initCompatMultiProcess(this)
        AutoSizeConfig.getInstance().setCustomFragment(true)

        customAdapterForExternal()
    }

    fun customAdapterForExternal(){
        AutoSizeConfig.getInstance().externalAdaptManager
            .addExternalAdaptInfoOfActivity(DatePickerFragment::class.java,
                ExternalAdaptInfo(true,500f))
            .addExternalAdaptInfoOfActivity(TimePickerFragment::class.java,
            ExternalAdaptInfo(true,500f))
    }
}