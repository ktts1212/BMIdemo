package bmicalculator.bmi.calculator.weightlosstracker.logic

import android.content.Context
import androidx.lifecycle.LiveData
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.initialize.MainApplicaiton
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.dao.BmiInfoDao
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Repository(private val bmiInfoDao: BmiInfoDao) {


    suspend fun getAllInfos():List<BmiInfo>{
        return bmiInfoDao.queryAllInfo()
    }
//    suspend fun queryAllInfo(): List<BmiInfo>? {
//        return withContext(Dispatchers.IO){
//            bmiInfoDao?.queryAllInfo()
//        }
//    }

//    suspend fun insertBmiInfo(bmiInfo: BmiInfo):Long{
//        return withContext(Dispatchers.IO){
//            bmiInfoDao?.insertBmiInfo(bmiInfo)!!
//        }
//    }

    suspend fun insertBmiInfo(bmiInfo: BmiInfo): Long {
        return bmiInfoDao.insertBmiInfo(bmiInfo)
    }

//    suspend fun deleteAllInfos(): Int {
//        return bmiInfoDao.deleteALlInfo()
//    }

    suspend fun deleteBmiInfo(bmiInfo: BmiInfo): Int {
        return bmiInfoDao.deleteBmiInfo(bmiInfo)
    }
}