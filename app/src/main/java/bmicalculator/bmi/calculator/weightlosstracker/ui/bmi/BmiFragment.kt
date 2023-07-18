package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi

import android.annotation.SuppressLint
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentBmiBinding


class BmiFragment : Fragment() {

    private lateinit var binding:FragmentBmiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBmiBinding.inflate(layoutInflater,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarBmi)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        return binding.root
    }


}