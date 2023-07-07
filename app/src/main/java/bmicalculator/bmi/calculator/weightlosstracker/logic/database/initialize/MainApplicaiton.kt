package bmicalculator.bmi.calculator.weightlosstracker.logic.database.initialize

import android.app.Application
import androidx.room.Room
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase

class MainApplicaiton:Application() {
    var database: AppDataBase?=null
        private set
    companion object{
        var instance:MainApplicaiton?=null
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance=this
        database= Room.databaseBuilder(this, AppDataBase::class.java,"app_database").build()
    }
}