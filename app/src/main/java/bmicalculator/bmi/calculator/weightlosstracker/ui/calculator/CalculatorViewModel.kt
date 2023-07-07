package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {
    //记录查询到的BimInfo，查不到就为null
    private val _bmiInfos: MutableLiveData<List<BmiInfo>> by lazy {
        MutableLiveData<List<BmiInfo>>().also {
            queryAllInfo()
        }
    }
    //计算时获取到bmiInfo，
    val bmiInfo=MutableLiveData<BmiInfo>()
    //判断查询到的bimInfo数量
    val infoCount: Int?
        get() = _bmiInfos.value?.size ?:0

    fun queryAllInfo() = viewModelScope.launch {
        Repository.queryAllInfo()
    }

    //改方法返回的是插入数据的ID值
    fun insertBmiInfo(bmiInfo: BmiInfo)=viewModelScope.launch {
        Repository.insertBmiInfo(bmiInfo)
    }


}
