package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.util.Event
import kotlinx.coroutines.launch

class CalculatorViewModel(private val repository: Repository, private val state:SavedStateHandle) : ViewModel() {

    companion object{
        const val KEY_NAME="viewModel"
        const val NAV_ID="navId"
    }

    fun saveData(bmiInfo: BmiInfo){
        state[KEY_NAME]=bmiInfo
    }

    fun getData(): BmiInfo? {
        return state[KEY_NAME]
    }

    fun saveNavId(id:Int){
        state[NAV_ID]=id
    }

    fun getNavId():Int?{
        return state[NAV_ID]
    }

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage


    var localeLanguage:String?=null

    //记录用户的cal选择
    var wtType = "error"
    var htType = "error"

    //记录BMI类型
    var bmiType = "null"

    //记录bmi所属类型
    val bmiNewRecord=MutableLiveData<BmiInfo>()

    //记录BMI
    var bmiVal = MutableLiveData(0f)

    fun setBmiVal(data: Float) {
        bmiVal.value = data
    }

    //共享日期
    var selectedDate = MutableLiveData<String>()

    fun setDate(data: String) {
        selectedDate.value = data
    }

    //共享时段
    var selectedPhase = MutableLiveData<Int>()

    fun setPhase(data: Int) {
        selectedPhase.value = data
    }

    //性别
    var selectedGender = MutableLiveData<Char>()

    fun setGender(data: Char) {
        selectedGender.value = data
    }

    //年龄
    val selectedAge = MutableLiveData(25)

    fun setAge(data: Int) {
        selectedAge.value = data
    }

    //体重lb
    private val _wtLb: MutableLiveData<Double> = MutableLiveData(140.00)

    val wtLb: LiveData<Double> get() = _wtLb

    fun setWtLb(value: Double) {
        _wtLb.value = value
    }

    //体重kg
    private val _wtKg: MutableLiveData<Double> = MutableLiveData(65.00)

    val wtKg: LiveData<Double> get() = _wtKg

    fun setWtKg(value: Double) {
        _wtKg.value = value
    }

    //身高fin
    private val _htFt: MutableLiveData<Int> = MutableLiveData(5)
    val htFt: LiveData<Int> get() = _htFt
    fun setHtFt(value: Int) {
        _htFt.value = value
    }

    private val _htIn: MutableLiveData<Int> = MutableLiveData(7)
    val htIn: LiveData<Int> get() = _htIn
    fun setHtIn(value: Int) {
        _htIn.value = value
    }

    //身高ht_cm
    private val _htCm: MutableLiveData<Double> = MutableLiveData(170.0)
    val htCm: LiveData<Double> get() = _htCm
    fun setHtCm(value: Double) {
        _htCm.value = value
    }

    fun insertInfo(bmiInfo: BmiInfo) = viewModelScope.launch {
        val newRowId = repository.insertBmiInfo(bmiInfo)
        if (newRowId > -1) {
            statusMessage.value = Event("bmiInfo Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occur")
        }
    }

    var infoCount = MutableLiveData(-1)

    var allInfo: MutableLiveData<List<BmiInfo>> =
        repository.getAllInfos().asLiveData() as MutableLiveData<List<BmiInfo>>

    fun deleteBmiInfo(bmiInfo: BmiInfo) = viewModelScope.launch {
        val id = repository.deleteBmiInfo(bmiInfo)
        if (id > 0) {
            statusMessage.value = Event("Delete Successfully")
        } else {
            statusMessage.value = Event("Occur Error")
        }
    }

}



