package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentDatePickerBinding
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "DatePickerFragment"

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentDatePickerBinding

    private var isValid = true

    private var toastTimes = 0

    private lateinit var model: CalculatorViewModel

    private val months = mutableListOf(
        "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        model = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]
        binding = FragmentDatePickerBinding.inflate(layoutInflater, container, false)
        binding.wheelPickerDateYearWheel.setRange(2000, 2035, 1)
        binding.wheelPickerDateMonthWheel.data = months
        binding.wheelPickerDateYearWheel.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
        binding.wheelPickerDateMonthWheel.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
        binding.wheelPickerDateDayWheel.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
        val currentDate = LocalDate.now()
        val day = currentDate.dayOfMonth
        val year = currentDate.year
        val month = currentDate.monthValue
        Log.d(TAG, "Current Month:${month}")
        binding.wheelPickerDateDayWheel.setRange(1, YearMonth.of(year, month).lengthOfMonth(), 1)
        binding.wheelPickerDateDayWheel.setDefaultValue(day)
        binding.wheelPickerDateMonthWheel.setDefaultPosition(month - 1)
        binding.wheelPickerDateYearWheel.setDefaultValue(year)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        binding.wheelPickerDateYearWheel.setOnWheelChangedListener(object : OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                val monthSelected: String = binding.wheelPickerDateMonthWheel.getCurrentItem()
                Log.d(TAG, monthSelected)
                Log.d(TAG, "Current Year:${view!!.getCurrentItem<Int>()}")
                val monthNum = monthToNumber(monthSelected)
                val daysInMonth = YearMonth.of(view.getCurrentItem(), monthNum).lengthOfMonth()
                binding.wheelPickerDateDayWheel.setRange(1, daysInMonth, 1)


                val currentDate = LocalDate.now()
                val day = currentDate.dayOfMonth
                val year = currentDate.year
                val month = currentDate.monthValue
                if ((year < binding.wheelPickerDateYearWheel.getCurrentItem<Int>()) ||
                    (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month < monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            ) || (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month == monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            && day < binding.wheelPickerDateDayWheel.getCurrentItem<Int>())
                ) {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done_invalid)
                    binding.btnDone.setTextColor(Color.parseColor("#000000"))
                    isValid = false
                    if (toastTimes == 0) {
                        Toast.makeText(
                            requireContext(), getString(R.string.select_right_date_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        toastTimes = 1
                    }
                } else {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done)
                    binding.btnDone.setTextColor(Color.parseColor("#FFFFFF"))
                    isValid = true
                    toastTimes = 0
                }
            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })

        binding.wheelPickerDateMonthWheel.setOnWheelChangedListener(object :
            OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                val yearSelected: Int = binding.wheelPickerDateYearWheel.getCurrentItem()
                val monthNum = monthToNumber(view!!.getCurrentItem())
                val daysInMonth = YearMonth.of(yearSelected, monthNum).lengthOfMonth()
                binding.wheelPickerDateDayWheel.setRange(1, daysInMonth, 1)
                val currentDate = LocalDate.now()
                val day = currentDate.dayOfMonth
                val year = currentDate.year
                val month = currentDate.monthValue
                if ((year < binding.wheelPickerDateYearWheel.getCurrentItem<Int>()) ||
                    (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month < monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            ) || (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month == monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            && day < binding.wheelPickerDateDayWheel.getCurrentItem<Int>())
                ) {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done_invalid)
                    binding.btnDone.setTextColor(Color.parseColor("#000000"))
                    isValid = false
                    if (toastTimes == 0) {
                        Toast.makeText(
                            requireContext(), getString(R.string.select_right_date_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        toastTimes = 1
                    }
                } else {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done)
                    binding.btnDone.setTextColor(Color.parseColor("#FFFFFF"))
                    isValid = true
                    toastTimes = 0
                }
            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })

        binding.wheelPickerDateDayWheel.setOnWheelChangedListener(object : OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                val currentDate = LocalDate.now()
                val day = currentDate.dayOfMonth
                val year = currentDate.year
                val month = currentDate.monthValue
                if ((year < binding.wheelPickerDateYearWheel.getCurrentItem<Int>()) ||
                    (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month < monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            ) || (year == binding.wheelPickerDateYearWheel.getCurrentItem<Int>() &&
                            month == monthToNumber(binding.wheelPickerDateMonthWheel.getCurrentItem())
                            && day < binding.wheelPickerDateDayWheel.getCurrentItem<Int>())
                ) {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done_invalid)
                    binding.btnDone.setTextColor(Color.parseColor("#000000"))
                    isValid = false
                    if (toastTimes == 0) {
                        Toast.makeText(
                            requireContext(), getString(R.string.select_right_date_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        toastTimes = 1
                    }
                } else {
                    binding.btnDone.setBackgroundResource(R.drawable.shape_btn_done)
                    binding.btnDone.setTextColor(Color.parseColor("#FFFFFF"))
                    isValid = true
                    toastTimes = 0
                }
            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })

        binding.btnCancel.setOnClickListener {
            onDestroyView()
        }

        binding.btnDone.setOnClickListener {


            if (isValid) {
                val dateSelected = binding.wheelPickerDateMonthWheel.getCurrentItem<String>() +
                        " ${binding.wheelPickerDateDayWheel.getCurrentItem<Int>()}" +
                        ",${binding.wheelPickerDateYearWheel.getCurrentItem<Int>()}"
                model.setDate(dateSelected)
                onDestroyView()
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.select_right_date_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        val displayMetrics = resources.displayMetrics
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = (displayMetrics.heightPixels * 0.5).toInt()
        params?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDestroyView()
    }

    fun monthToNumber(month: String): Int {
        val monthNum = when (month) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "June" -> 6
            "July" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> -1
        }
        return monthNum
    }
}

