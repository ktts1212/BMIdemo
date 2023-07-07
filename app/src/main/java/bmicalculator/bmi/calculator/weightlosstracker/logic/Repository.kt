package bmicalculator.bmi.calculator.weightlosstracker.logic

import bmicalculator.bmi.calculator.weightlosstracker.logic.database.initialize.MainApplicaiton
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object Repository {
    val bmiInfoDao=MainApplicaiton.instance?.database?.bmiInfoDao()

    suspend fun queryAllInfo(): List<BmiInfo>? {
        return withContext(Dispatchers.IO){
            bmiInfoDao?.queryAllInfo()
        }
    }

    suspend fun insertBmiInfo(bmiInfo: BmiInfo):Long{
        return withContext(Dispatchers.IO){
            bmiInfoDao?.insertBmiInfo(bmiInfo)!!
        }
    }
}