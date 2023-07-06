package bmicalculator.bmi.calculator.weightlosstracker.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import bmicalculator.bmi.calculator.weightlosstracker.entity.BmiInfo

@Dao
interface BmiInfoDao {

    @Insert
    fun insertBmiInfo(bmiInfo: BmiInfo): Long

    @Query("select * from BmiInfo")
    fun queryAllInfo(): List<BmiInfo>


}