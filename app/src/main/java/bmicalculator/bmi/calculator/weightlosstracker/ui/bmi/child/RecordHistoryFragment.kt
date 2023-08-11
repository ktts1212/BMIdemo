package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentRecordHistoryBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.History
import bmicalculator.bmi.calculator.weightlosstracker.ui.adapter.RecordAdapter
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ktx.immersionBar

private const val TAG = "RecordHistory"

class RecordHistoryFragment : DialogFragment() {

    private lateinit var binding: FragmentRecordHistoryBinding

    private var bmiInfoList = ArrayList<BmiInfo>()

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordHistoryBinding.inflate(layoutInflater, container, false)

        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository,requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        bmiInfoList.clear()

        viewModel.allInfo.observe(requireActivity()) {
            val list = viewModel.allInfo.value
            if (it == null || it.isEmpty()) {

                if (isAdded) {
                    val navView =
                        (activity as AppCompatActivity).findViewById<BottomNavigationView>(
                            R.id.bottom_navigation_view
                        )
                    navView.selectedItemId = R.id.menu_calculator
                    val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    val transition = fragmentManager.beginTransaction()
                    transition.replace(R.id.fragment_container, CalculatorFragment())
                    transition.commit()
                }

            }
            bmiInfoList = orderList(list!!) as ArrayList<BmiInfo>

            if (isAdded) {
                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.recordRecyclerview.layoutManager = layoutManager
                val adapter = RecordAdapter(requireContext(), bmiInfoList, childFragmentManager)
                binding.recordRecyclerview.adapter = adapter
            }

        }

        binding.recordListBack.setOnClickListener {
            onDestroyView()
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.BOTTOM
        params?.dimAmount = 0.0f
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        immersionBar {
            statusBarColor(R.color.bg1)
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    private fun orderList(list: List<BmiInfo>): List<BmiInfo> {

        val ls = mutableListOf<History>()
        for (i in list.indices) {
            ls.add(History(list[i], orderTime(list[i])))
        }
        ls.sortByDescending { it.datetimestamp }

        var x = 0
        var y = 1
        val orderedList = mutableListOf<BmiInfo>()
        val l = mutableListOf<History>()
        while (y < ls.size) {
            l.clear()
            var xAdd = false
            while ((y < ls.size) && (ls[x].datetimestamp == ls[y].datetimestamp)) {
                if (!xAdd) {
                    l.add(ls[x])
                    xAdd = true
                }
                l.add(ls[y])
                y++
            }

            if (y - x == 1) {
                orderedList.add(ls[x].bmiInfo)
                x = y
                y++
            } else {
                l.sortByDescending { it.bmiInfo.save_time }
                Log.d(TAG, "l:  l.toList().toString()")
                l.forEach { orderedList.add(it.bmiInfo) }
                x = y
                y++
            }
            l.clear()
        }
        for (i in x until ls.size) {
            l.add(ls[i])
        }
        l.sortByDescending { it.bmiInfo.timestape }
        l.forEach { orderedList.add(it.bmiInfo) }
        return orderedList
    }

    private fun orderTime(bmiInfo: BmiInfo): Long{

        val l1 = bmiInfo.date!!.split(" ")
        //获取当前的月份和天数
        val day = l1[1].split(",")[0]
        val month = Utils.monthToNumber(l1[0])
        val dayOfYear = Utils.getDayOfYear(day.toInt(), month,bmiInfo.year)
        return dayOfYear * 10 + bmiInfo.year * 10000 + bmiInfo.phase.toLong()
    }
}