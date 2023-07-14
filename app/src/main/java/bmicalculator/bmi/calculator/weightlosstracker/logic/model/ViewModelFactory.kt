package bmicalculator.bmi.calculator.weightlosstracker.logic.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel

class ViewModelFactory(private val repository:Repository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)){
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }

}