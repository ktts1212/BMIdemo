package bmicalculator.bmi.calculator.weightlosstracker.logic.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiInfoDao {

    @Insert
    suspend fun insertBmiInfo(bmiInfo: BmiInfo): Long

    @Query("select * from BmiInfo")
    suspend fun queryAllInfo(): List<BmiInfo>

    @Delete
    suspend fun deleteBmiInfo(bmiInfo: BmiInfo):Int

//    @Query("delete from BmiInfo")
//    suspend fun deleteALlInfo():Int


}