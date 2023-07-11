package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentCalculatorBinding
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.DatePickerFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "CalculatorFragment"

private const val Mult_1 = 0.45359237

private const val Mult_htft = 30.48

private const val Mult_htin = 2.54

class CalculatorFragment : Fragment(), LifecycleOwner {

    private lateinit var binding: FragmentCalculatorBinding

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.root.setOnTouchListener { view, motionEvent ->
//            if (view is TextInputLayout&&motionEvent.action==MotionEvent.ACTION_DOWN){
//                val rect= Rect()
//                view.getGlobalVisibleRect(rect)
//                if (!rect.contains(motionEvent.rawX.toInt(),motionEvent.rawY.toInt())){
//                    view.clearFocus()
//                    val inputMethodManager=
//                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
//                                InputMethodManager
//                    inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
//                }
//            }
//            false
//        }

        val df = DecimalFormat("#.00")
        //用于身高保留一位小数
        var tf = DecimalFormat("#.0")

        var ff = DecimalFormat("#")
//        df.minimumFractionDigits = 2
//        df.maximumFractionDigits = 6
        binding.htInputFtin1.text = Editable.Factory.getInstance().newEditable("5" + "'")
        binding.htInputFtin2.text = Editable.Factory.getInstance().newEditable("7" + "''")
        binding.htInputCm.text = Editable.Factory.getInstance().newEditable("170.0")
        Log.d(TAG, "${viewModel.bmiInfo}")
        //判断体重计量标准

        //当输入完edittext内容点击done对edittext内容进行修改
        binding.wtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.wtInput.text.isNullOrEmpty()) {
                    var t = binding.wtInput.text.toString()
                    if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                        if (t.toDouble() < 2) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                df.format(2)
                            )
                        } else if (t.toDouble() > 551) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                df.format(551)
                            )
                        }
                    } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {
                        if (t.toDouble() < 1) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                df.format(1)
                            )
                        } else if (t.toDouble() > 250) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                df.format(250)
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid weight to calculate your BMI accurately",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                        binding.wtInput.setText(
                            df.format(140)
                        )
                    } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {
                        binding.wtInput.setText(
                            df.format(65)
                        )
                    }
                }
                true
            } else {
                false
            }
        }
        //点击单位转换按钮
        var firstConvert = true
        binding.wtTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    if (viewModel.wt_lb.value!! < 2) {
                        binding.wtInput.setText(df.format(1))
                    } else if (viewModel.wt_lb.value!! > 551) {
                        binding.wtInput.setText(df.format(551))
                    } else {
                        binding.wtInput.setText(df.format(viewModel.wt_lb.value))
                    }
                } else {
                    if (viewModel.wt_kg.value!! < 1) {
                        binding.wtInput.setText(df.format(1))
                    } else if (viewModel.wt_kg.value!! > 250) {
                        binding.wtInput.setText(df.format(250))
                    } else {
                        binding.wtInput.setText(df.format(viewModel.wt_kg.value))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var p0: Editable? = binding.wtInput.text
                if (tab?.position == 0) {
                    if (!p0.isNullOrEmpty()) {
                        if (p0.toString().toDouble() >= 2 && p0.toString().toDouble() <= 551) {
                            viewModel.setwtlb(
                                df.format(binding.wtInput.text.toString().toDouble()).toDouble()
                            )
                            if (firstConvert == false) {
                                viewModel.setwtkg(
                                    df.format(viewModel.wt_lb.value!! * Mult_1).toDouble()
                                )
                            }
                            firstConvert = false
                        } else if (p0.toString().toDouble() > 551) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setwtlb(
                                df.format(551.00).toDouble()
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setwtlb(
                                df.format(2).toDouble()
                            )
                        }
                    } else if (p0.isNullOrEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.wtInput.setText(df.format(140))
                        viewModel.setwtlb(
                            df.format(140.00).toDouble()
                        )
                        viewModel.setwtkg(
                            df.format(65).toDouble()
                        )
                    }
                }

                if (tab?.position == 1) {
                    if (!p0.isNullOrEmpty()) {
                        if (p0.toString().toDouble() >= 1 && p0.toString().toDouble() <= 250) {
                            viewModel.setwtkg(
                                df.format(binding.wtInput.text.toString().toDouble()).toDouble()
                            )
                            viewModel.setwtlb(
                                df.format(viewModel.wt_kg.value!! / Mult_1).toDouble()
                            )
                        } else if (p0.toString().toDouble() > 250) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()

                            viewModel.setwtkg(
                                df.format(250).toDouble()
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.setwtkg(
                                df.format(1).toDouble()
                            )
                        }
                    } else if (p0.isNullOrEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setwtkg(
                            df.format(65).toDouble()
                        )
                        viewModel.setwtlb(
                            df.format(140).toDouble()
                        )
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        //EditText监听事件，限制输入时小数点位数最多为6位
        binding.wtInput.addTextChangedListener(object : TextWatcher {
            private var isChanged = false
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                if (isChanged) {
                    return
                }
                val str = p0.toString()
                if (str.contains(".")) {
                    val decimalPart = str.substring(str.indexOf(".") + 1)
                    val decimalnumber = decimalPart.length
                    if (decimalnumber > 2) {
                        isChanged = true
                        val subStr = str.substring(0, str.indexOf(".") + 3)
                        binding.wtInput.setText(subStr)
                        binding.wtInput.setError("小数位数不能超过2位")
                    }

                    if (str.substring(0,str.indexOf(".")).length>3){
                        binding.wtInput.setText(
                            str.substring(1)
                        )
                    }
                }
                if (!str.isEmpty()) {
                    if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                        if (str.toDouble() < 2) {
                            isChanged = true
                            binding.wtInput.setText(
                                df.format(2)
                            )
                            binding.wtInput.setError("最小值为2")
                        } else if (str.toDouble() > 551) {
                            isChanged = true
                            binding.wtInput.setText(
                                df.format(551)
                            )
                            binding.wtInput.setError("最大值为551")
                        }
                    } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {
                        if (str.toDouble() < 1) {
                            isChanged = true
                            binding.wtInput.setError("最小值为1")
                            binding.wtInput.setText(
                                df.format(1)
                            )
                        } else if (str.toDouble() > 250) {
                            isChanged = true
                            binding.wtInput.setError("最大值为250")
                            binding.wtInput.setText(
                                df.format(250)
                            )
                        }
                    }
                } else {
                    if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                        isChanged = true
                        binding.wtInput.setText(
                            df.format(140)
                        )
                    } else {
                        isChanged = true
                        binding.wtInput.setText(
                            df.format(65)
                        )
                    }
                }
                isChanged = false
            }
        })

        //身高
        var htfirstConvert = true
        binding.htTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.htCardFtin1.visibility = View.VISIBLE
                    binding.htCardFtin2.visibility = View.VISIBLE
                    binding.htCardCm.visibility = View.INVISIBLE
                    binding.htInputFtin1.setText(viewModel.ht_ft.value.toString() + "'")
                    binding.htInputFtin2.setText(viewModel.ht_in.value.toString() + "''")
                } else {
                    binding.htCardFtin1.visibility = View.INVISIBLE
                    binding.htCardFtin2.visibility = View.INVISIBLE
                    binding.htCardCm.visibility = View.VISIBLE
                    binding.htInputCm.setText(viewModel.ht_cm.value.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    val strft = binding.htInputFtin1.text.toString()
                    val strin = binding.htInputFtin2.text.toString()
                    if (strft.contains("'")) {
                        viewModel.sethtft(
                            strft.dropLast(1).toInt()
                        )
                    } else {
                        viewModel.sethtft(
                            strft.toInt()
                        )
                    }
                    if (strin.contains("''")) {
                        viewModel.sethtin(
                            strin.dropLast(2).toInt()
                        )
                    } else if (strin.contains("'")) {
                        viewModel.sethtin(
                            strin.dropLast(1).toInt()
                        )
                    } else {
                        viewModel.sethtin(
                            strin.toInt()
                        )
                    }
                    if (htfirstConvert) {
                        viewModel.sethtcm(
                            tf.format(170).toDouble()
                        )
                        htfirstConvert = false
                    } else {
                        viewModel.sethtcm(
                            tf.format(
                                viewModel.ht_in.value!! * Mult_htin +
                                        viewModel.ht_ft.value!! * Mult_htft
                            ).toDouble()
                        )
                    }

                } else {
                    viewModel.sethtcm(
                        binding.htInputCm.text.toString().toDouble()
                    )

                    viewModel.sethtft(
                        ff.format((viewModel.ht_cm.value!! / Mult_htft).toInt()).toInt()
                    )
                    viewModel.sethtin(
                        ff.format((viewModel.ht_cm.value!! - viewModel.ht_ft.value!! * Mult_htft)
                                / Mult_htin).toInt()
                    )
                }
            }


            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        //binding.htInputFtin1文本
        binding.htInputFtin1.addTextChangedListener(object : TextWatcher {
            private var ischanged = false
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {

                if (ischanged) {
                    return
                }
                var str = p0.toString()
                if (str.contains("'")) {
                    str = str.dropLast(1)
                }

                if (str.contains(".")) {
                    Log.d(TAG, str)
                    binding.htInputFtin1.setError("只能输入整数")
                    binding.htInputFtin1.postDelayed({
                        binding.htInputFtin1.error = null
                    }, 3000)

                    val intPart = str.substring(0, str.indexOf("."))

                    if (intPart.isEmpty()) {
                        ischanged = true
                        binding.htInputFtin1.setText(
                            ff.format(5) + "'"
                        )
                    } else {
                        ischanged = true
                        binding.htInputFtin1.setText(
                            intPart + "'"
                        )
                    }
                }

                if (!str.isEmpty() && !str.contains(".")) {
                    if (str.length>1){
                        binding.htInputFtin1.setText(
                            str.substring(1)
                        )
                    }

                    if (str.toInt() < 1) {
                        ischanged = true
                        binding.htInputFtin1.setText(
                            ff.format(1) + "'"
                        )
                        binding.htInputFtin1.setError("最小值为1")
                        binding.htInputFtin1.postDelayed({
                            binding.htInputFtin1.error = null
                        }, 3000)
                    } else if (str.toInt() > 8) {
                        ischanged = true
                        binding.htInputFtin1.setText(
                            ff.format(8) + "'"
                        )
                        binding.htInputFtin1.setError("最大值为8")
                        binding.htInputFtin1.postDelayed({
                            binding.htInputFtin1.error = null
                        }, 3000)
                    }
                }
                if (str.isEmpty()) {
                    ischanged = true
                    binding.htInputFtin1.setText(
                        ff.format(5) + "'"
                    )
                }
                ischanged = false
            }
        })

        binding.htInputFtin1.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (!binding.htInputFtin1.text.toString().contains("'")) {
                    binding.htInputFtin1.setText(
                        binding.htInputFtin1.text.toString() + "'"
                    )
                }
            }
        }

        //binding.htInputFtin2文本
        binding.htInputFtin2.addTextChangedListener(object : TextWatcher {
            private var isChanged = false
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {

                if (isChanged) {
                    return
                }
                var str = p0.toString()
                if (str.contains("''")) {
                    str = str.dropLast(2)
                } else if (str.contains("'")) {
                    str = str.dropLast(1)
                }

                if (str.contains(".")) {
                    binding.htInputFtin2.setError("只能输入整数")
                    binding.htInputFtin2.postDelayed({
                        binding.htInputFtin2.error = null
                    }, 3000)
                    val intPart = str.substring(0, str.indexOf("."))

                    if (intPart.isEmpty()) {
                        isChanged = true
                        binding.htInputFtin2.setText(
                            ff.format(7) + "''"
                        )
                    } else {
                        isChanged = true
                        binding.htInputFtin2.setText(
                            intPart + "''"
                        )
                    }

                }

                if (!str.isEmpty() && !str.contains(".")) {

                    if (str.length > 2) {
                        isChanged = true
                        binding.htInputFtin2.setError("只能输入0-11整数")
                        binding.htInputFtin2.postDelayed({
                            binding.htInputFtin2.error = null
                        }, 3000)
                        binding.htInputFtin2.setText(
                            str.substring(1) + "''"
                        )
                    }

                    if (str.toInt() > 11) {
                        isChanged = true
                        binding.htInputFtin2.setText(
                            ff.format(11) + "''"
                        )
                    }
                }

                if (str.isEmpty()) {
                    isChanged = true
                    binding.htInputFtin2.setText(
                        ff.format(7) + "''"
                    )
                }
                isChanged = false
            }
        })

        binding.htInputFtin2.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (!binding.htInputFtin2.text.toString().contains("''")) {
                    if (!binding.htInputFtin2.text.toString().contains("'")) {
                        binding.htInputFtin2.setText(
                            binding.htInputFtin2.text.toString() + "''"
                        )
                    } else {
                        binding.htInputFtin2.setText(
                            binding.htInputFtin2.text.toString() + "'"
                        )
                    }
                }
            }
        }

        binding.htInputCm.addTextChangedListener(object : TextWatcher {
            private var isChanged = false
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                if (isChanged) {
                    return
                }

                var str = binding.htInputCm.text.toString()
                if (str.contains(".")) {
                    val decimalCount = str.substring(str.indexOf("."))
                    if (decimalCount.length > 1) {
                        isChanged = true
                        binding.htInputCm.setText(
                            str.substring(0, str.indexOf(".") + 2)
                        )
                    }
                    if (str.toDouble() > 250) {
                        isChanged = true
                        binding.htInputCm.setText(
                            tf.format(250)
                        )
                    } else if (str.toDouble() < 1) {
                        isChanged
                        binding.htInputCm.setText(
                            tf.format(1)
                        )
                    }
                }

                if (!str.contains(".")) {
                    if (str.toDouble() > 250) {
                        isChanged = true
                        binding.htInputCm.setText(
                            tf.format(250)
                        )
                    } else if (str.toDouble() < 1) {
                        isChanged = true
                        binding.htInputCm.setText(
                            tf.format(1)
                        )
                    }
                }

                if (str.isEmpty()) {
                    isChanged = true
                    binding.htInputCm.setText(
                        tf.format(170)
                    )
                }


                isChanged = false
            }

        })

        //日期
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val monthName=DateFormatSymbols(Locale.ENGLISH).shortMonths[month]
        binding.timeInputDate.setText("${monthName} ${day},${year}")
        binding.timeInputDate.setOnClickListener {
            val dialog=DatePickerFragment()
            dialog.show(childFragmentManager,"DatePicker")
        }
    }

}

