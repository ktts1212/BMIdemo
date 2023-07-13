package bmicalculator.bmi.calculator.weightlosstracker.logic.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo

@Dao
interface BmiInfoDao {

    @Insert
    suspend fun insertBmiInfo(bmiInfo: BmiInfo): Long

    @Query("select * from BmiInfo")
    fun queryAllInfo(): LiveData<List<BmiInfo>>


}