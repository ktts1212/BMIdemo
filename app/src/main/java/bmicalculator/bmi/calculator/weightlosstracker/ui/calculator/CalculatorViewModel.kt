package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculatorViewModel (application: Application):AndroidViewModel(application) {

     val allBmiInfos:LiveData<List<BmiInfo>>
     val repository: Repository

    init {
        val dao=AppDataBase.getDatabase(application).bmiInfoDao()
        repository=Repository(dao)
        allBmiInfos=repository.allInfos
    }

    //记录查询到的BimInfo，查不到就为null
//    private val _bmiInfos: MutableLiveData<List<BmiInfo>> by lazy {
//        MutableLiveData<List<BmiInfo>>().also {
//            queryAllInfo()
//        }
//    }

    //共享日期
    var selectedDate=MutableLiveData<String>()

    fun setDate(data:String){
        selectedDate.value=data
    }

    fun insertInfo(bmiInfo: BmiInfo)=viewModelScope.launch{
        repository.insertBmiInfo(bmiInfo)
    }

    //共享时段
    var selectedPhase=MutableLiveData<String>()

    fun setPhase(data:String){
        selectedPhase.value=data
    }

    //性别
    var selectedGender=MutableLiveData<Char>()

    fun setGender(data:Char){
        selectedGender.value=data
    }

    //年龄
    val selectedAge=MutableLiveData<Int>(25)

    fun setAge(data:Int){
        selectedAge.value=data
    }

    //体重lb
    private val _wt_lb:MutableLiveData<Double> = MutableLiveData(140.00)

    val wt_lb:LiveData<Double>get() = _wt_lb

    fun setwtlb(value: Double){
        _wt_lb.value=value
    }
    //体重kg
    private val _wt_kg:MutableLiveData<Double> = MutableLiveData(65.00)

    val wt_kg:LiveData<Double>get() = _wt_kg

    fun setwtkg(value:Double){
        _wt_kg.value=value
    }
    //身高fin
    private val _ht_ft:MutableLiveData<Int> = MutableLiveData(5)
    val ht_ft:LiveData<Int>get() = _ht_ft
    fun sethtft(value: Int){
        _ht_ft.value=value
    }

    private val _ht_in:MutableLiveData<Int> = MutableLiveData(7)
    val ht_in:LiveData<Int>get() = _ht_in
    fun sethtin(value: Int){
        _ht_in.value=value
    }

    //身高ht_cm
    val _ht_cm:MutableLiveData<Double> = MutableLiveData(170.0)
    val ht_cm:LiveData<Double>get()=_ht_cm
    fun sethtcm(value: Double){
        _ht_cm.value=value
    }

    //计算时获取到bmiInfo，
    val bmiInfo=BmiInfo()

    //判断查询到的bimInfo数量
    val infoCount: Int?
        get() = allBmiInfos.value?.size ?:0

//    fun queryAllInfo() = viewModelScope.launch {
//        Repository.queryAllInfo()
//    }
//
//    //该方法返回的是插入数据的ID值
//    fun insertBmiInfo(bmiInfo: BmiInfo)=viewModelScope.launch {
//        Repository.insertBmiInfo(bmiInfo)
//    }


}
