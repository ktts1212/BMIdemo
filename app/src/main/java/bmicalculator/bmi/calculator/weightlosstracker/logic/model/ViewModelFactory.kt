package bmicalculator.bmi.calculator.weightlosstracker.logic.model

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel

class ViewModelFactory(
    private val repository: Repository, owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key:String,modelClass: Class<T>,handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            return CalculatorViewModel(repository, handle) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}

//ViewModelProvider.Factory