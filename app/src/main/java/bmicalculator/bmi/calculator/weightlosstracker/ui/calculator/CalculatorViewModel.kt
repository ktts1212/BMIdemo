package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import android.util.Log
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


    val LocaleLanguage=null

    //记录用户的cal选择
    var wttype = "error"
    var httype = "error"

    //记录BMI类型
    var bmitype = "null"

    //记录bmi所属类型
    val bmiNewRecord=MutableLiveData<BmiInfo>()

    //记录BMI
    var bmival = MutableLiveData<Float>(0f)

    fun setBmival(data: Float) {
        bmival.value = data
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
    val selectedAge = MutableLiveData<Int>(25)

    fun setAge(data: Int) {
        selectedAge.value = data
    }

    //体重lb
    private val _wt_lb: MutableLiveData<Double> = MutableLiveData(140.00)

    val wt_lb: LiveData<Double> get() = _wt_lb

    fun setwtlb(value: Double) {
        _wt_lb.value = value
    }

    //体重kg
    private val _wt_kg: MutableLiveData<Double> = MutableLiveData(65.00)

    val wt_kg: LiveData<Double> get() = _wt_kg

    fun setwtkg(value: Double) {
        _wt_kg.value = value
    }

    //身高fin
    private val _ht_ft: MutableLiveData<Int> = MutableLiveData(5)
    val ht_ft: LiveData<Int> get() = _ht_ft
    fun sethtft(value: Int) {
        _ht_ft.value = value
    }

    private val _ht_in: MutableLiveData<Int> = MutableLiveData(7)
    val ht_in: LiveData<Int> get() = _ht_in
    fun sethtin(value: Int) {
        _ht_in.value = value
    }

    //身高ht_cm
    val _ht_cm: MutableLiveData<Double> = MutableLiveData(170.0)
    val ht_cm: LiveData<Double> get() = _ht_cm
    fun sethtcm(value: Double) {
        _ht_cm.value = value
    }

    fun insertInfo(bmiInfo: BmiInfo) = viewModelScope.launch {
        val newRowId = repository.insertBmiInfo(bmiInfo)
        if (newRowId > -1) {
            statusMessage.value = Event("BminInfo Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occured")
        }
    }

    var infoCount = MutableLiveData<Int>(-1)

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



