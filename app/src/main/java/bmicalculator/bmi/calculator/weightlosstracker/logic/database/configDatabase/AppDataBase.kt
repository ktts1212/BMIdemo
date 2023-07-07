package bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.dao.BmiInfoDao
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo

@Database(version = 1, entities = [BmiInfo::class])
abstract class AppDataBase : RoomDatabase() {

    abstract fun bmiInfoDao(): BmiInfoDao

    companion object {
        private var instance: AppDataBase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDataBase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java, "app_database"
            ).build().apply {
                instance = this
            }
        }
    }
}