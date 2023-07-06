package bmicalculator.bmi.calculator.weightlosstracker.configDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bmicalculator.bmi.calculator.weightlosstracker.entity.BmiInfo

@Database(version = 1, entities = [BmiInfo::class])
abstract class AppDataBase : RoomDatabase() {
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