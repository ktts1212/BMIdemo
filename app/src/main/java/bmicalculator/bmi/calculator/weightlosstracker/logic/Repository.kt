package bmicalculator.bmi.calculator.weightlosstracker.logic

import android.content.Context
import androidx.lifecycle.LiveData
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.initialize.MainApplicaiton
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.dao.BmiInfoDao
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Year


class Repository(private val bmiInfoDao: BmiInfoDao) {


    fun selectByYear(year: Int):Flow<List<BmiInfo>>{
        return bmiInfoDao.queryByYear(year)
    }

    fun getAllInfos(): Flow<List<BmiInfo>> {
        return bmiInfoDao.queryAllInfo()
    }

    suspend fun insertBmiInfo(bmiInfo: BmiInfo): Long {
        return bmiInfoDao.insertBmiInfo(bmiInfo)
    }

    suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Int {
        return bmiInfoDao.deleteBmiInfo(bmiInfo)
    }
}