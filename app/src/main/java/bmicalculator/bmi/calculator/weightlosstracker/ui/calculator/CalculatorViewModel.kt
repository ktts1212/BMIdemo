package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class CalculatorViewModel(private val repository: Repository) : ViewModel() {

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage


    //记录用户的cal选择
    var wtval: Float = 0f
    var wttype = "error"
    var htval: Float = 0f
    var httype = "error"

    //记录BMI类型
    var bmitype = "null"

    //记录bmi所属类型

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
    var selectedPhase = MutableLiveData<String>()

    fun setPhase(data: String) {
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

    //计算时获取到bmiInfo，
    val bmiInfo = BmiInfo()


    fun insertInfo(bmiInfo: BmiInfo) = viewModelScope.launch {
        val newRowId = repository.insertBmiInfo(bmiInfo)
        if (newRowId > -1) {
            statusMessage.value = Event("BminInfo Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occured")
        }
    }

    var infoCount = MutableLiveData<Int>(-1)

    var allInfo:MutableLiveData<List<BmiInfo>> = repository.getAllInfos().asLiveData() as MutableLiveData<List<BmiInfo>>



    var listByYear : MutableLiveData<List<BmiInfo>> = repository.selectByYear(LocalDate.now().year).asLiveData() as MutableLiveData<List<BmiInfo>>

}



