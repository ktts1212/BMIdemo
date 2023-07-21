package bmicalculator.bmi.calculator.weightlosstracker.ui.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentStatisticBinding

class StatisticFragment : Fragment() {

    private lateinit var binding:FragmentStatisticBinding

    private val tabTitles= listOf("Day","Week","Month")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentStatisticBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter=
    }

    inner class viewPaperAdapter:FragmentStateAdapter(this){
        override fun getItemCount(): Int {
            return
        }

        override fun createFragment(position: Int): Fragment {

        }
    }

}