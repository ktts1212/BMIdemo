package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.Exception

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

        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as AppCompatActivity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)
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
            if (it == null || it.size == 0) {

                if (isAdded) {
                    val editor = (activity as AppCompatActivity).getSharedPreferences(
                        "data", Context.MODE_PRIVATE
                    ).edit()
                    editor.putBoolean("hasdata", false)
                    editor.apply()

                    val navView =
                        (activity as AppCompatActivity).findViewById<BottomNavigationView>(
                            R.id.bottom_navigation_view
                        )
                    navView.selectedItemId = R.id.menu_calculator
                    val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
                    val transition = fragmentManager.beginTransaction()
                    transition.replace(R.id.fragment_container, CalculatorFragment())
                    transition.commit()
                    viewModel.UserStatus.value = false
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
        val displayMetrics = resources.displayMetrics
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.BOTTOM
        params?.dimAmount = 0.0f
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun orderList(list: List<BmiInfo>): List<BmiInfo> {

        Log.d(TAG, "ino list:${list.size}")
        val ls = mutableListOf<History>()
        for (i in 0 until list.size) {
            ls.add(History(list[i], orderTime(list[i])))
        }
        ls.sortByDescending { it.datetimestamp }
        for (i in 0 until ls.size) {
            Log.d(TAG, "ls :${ls[i].datetimestamp}")
            Log.d(TAG, "ls :${ls[i].bmiInfo}")
        }

        var x = 0
        var y = 1
        val orderedList = mutableListOf<BmiInfo>()
        val l = mutableListOf<History>()
        while (y < ls.size) {
            l.clear()
            var xadd = false
            while ((y < ls.size) && (ls[x].datetimestamp == ls[y].datetimestamp)) {
                if (xadd == false) {
                    l.add(ls[x])
                    xadd = true
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

    fun orderTime(bmiInfo: BmiInfo): Long {

        val l1 = bmiInfo.date!!.split(" ")
        //获取当前的月份和天数
        val day = l1[1].split(",")[0]
        val month = Utils.monthToNumber(l1[0])
        val dayOfYear = Utils.getDayOfYear(day.toInt(), month)
        return dayOfYear * 10 + bmiInfo.year * 1000 + bmiInfo.phase.toLong()
    }

    fun phaseToNumber(phase: String): Int {
        Log.d(TAG,"phase:${viewModel.selectedPhase.value}")
        Log.d(TAG,"morn:${getString(R.string.morning)}")
        when (phase) {
            getString(R.string.morning) -> return 1
            getString(R.string.afternoon) -> return 2
            getString(R.string.evening) -> return 3
            getString(R.string.night) -> return 4
            else -> throw Exception("phase Error")
        }
    }

}