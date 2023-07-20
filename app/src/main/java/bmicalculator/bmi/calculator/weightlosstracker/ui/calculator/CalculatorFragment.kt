package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import kotlin.math.pow
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentCalculatorBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.adapter.AgeSelectorAdapter
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.CalculatorResultFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.DatePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.TimePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CenterItemUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

private const val TAG = "CalculatorFragment"

private const val Mult_1 = 0.45359237

private const val Mult_htft = 30.48

private const val Mult_htin = 2.54

class CalculatorFragment : Fragment(), LifecycleOwner {

    private val ageList = ArrayList<String?>()

    private val CHILDVIEWSIZE = 70

    private lateinit var binding: FragmentCalculatorBinding

    private lateinit var viewModel: CalculatorViewModel

    private var centerToLiftDistance: Int = 0

    private var childViewHalfCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        //viewModel = ViewModelProvider(requireActivity()).get(CalculatorViewModel::class.java)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarCal)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = CalculatorResultFragment()
        dialog.show(childFragmentManager, "CalculatorResult")
        //当输入完后点击键盘的done
        binding.root.setOnTouchListener { view, motionEvent ->
            if (view is TextInputLayout && motionEvent.action == MotionEvent.ACTION_DOWN) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                if (!rect.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())) {
                    view.clearFocus()
                    val inputMethodManager =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                                InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }

        val df = DecimalFormat("#.00")
        //用于身高保留一位小数
        val tf = DecimalFormat("#.0")

        val ff = DecimalFormat("#")
        //初始化
        binding.htInputFtin1.text = Editable.Factory.getInstance().newEditable("5" + "'")
        binding.htInputFtin2.text = Editable.Factory.getInstance().newEditable("7" + "''")
        binding.htInputCm.text = Editable.Factory.getInstance().newEditable("170.0")
        //Log.d(TAG, "${viewModel.bmiInfo}")
        //判断体重计量标准

        //当输入完edittext内容点击done对edittext内容进行修改
        binding.wtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.wtInput.text.isNullOrEmpty()) {
                    val t = binding.wtInput.text.toString()
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
                    binding.wtInput.setText(df.format(viewModel.wt_lb.value))
                } else {
                    binding.wtInput.setText(df.format(viewModel.wt_kg.value))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val p0: Editable? = binding.wtInput.text
                val str = binding.wtInput.text.toString()
                if (tab?.position == 0) {
                    if (str.toDouble() < 2) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setwtlb(
                            df.format(2).toDouble()
                        )
                    } else {
                        viewModel.setwtlb(
                            df.format(str.toDouble()).toDouble()
                        )
                    }
                    if (firstConvert == false) {
                        if (df.format(viewModel.wt_lb.value!! * Mult_1).toDouble() < 1) {
                            viewModel.setwtkg(
                                df.format(1).toDouble()
                            )
                        } else {
                            viewModel.setwtkg(
                                df.format(viewModel.wt_lb.value!! * Mult_1).toDouble()
                            )
                        }
                    }
                    firstConvert = false
                } else {
                    if (str.toDouble() < 1) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setwtkg(
                            df.format(1).toDouble()
                        )
                    } else {
                        viewModel.setwtkg(
                            df.format(str.toDouble()).toDouble()
                        )
                    }
                    viewModel.setwtlb(
                        df.format(viewModel.wt_kg.value!! / Mult_1).toDouble()
                    )
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

                if (str.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid weight to calculate your BMI accurately",
                        Toast.LENGTH_SHORT
                    ).show()
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
                } else {

                    if (str.contains(".")) {
                        val decimalPart = str.substring(str.indexOf(".") + 1)
                        val decimalnumber = decimalPart.length
                        if (decimalnumber > 2) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            isChanged = true
                            val subStr = str.substring(0, str.indexOf(".") + 3)
                            binding.wtInput.setText(subStr)
                            binding.wtInput.setError("小数位数不能超过2位")
                        } else if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                            if (str.toDouble() > 551) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(
                                    df.format(551)
                                )
                            } else if (str.substring(0, str.indexOf(".")).length > 3) {
                                isChanged
                                binding.wtInput.setText(
                                    df.format(str.substring(str.indexOf(".") - 3).toDouble())
                                )
                            }
                        } else if (binding.wtTab.getTabAt(1)!!.isSelected == true) {
                            if (str.toDouble() > 250) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(
                                    df.format(250)
                                )
                            } else if (str.substring(0, str.indexOf(".")).length > 3) {
                                isChanged
                                binding.wtInput.setText(
                                    df.format(str.substring(str.indexOf(".") - 3).toDouble())
                                )
                            }
                        }
                    } else {
                        if (binding.wtTab.getTabAt(0)?.isSelected == true) {

                            if (str.length > 3) {
                                str.substring(str.length - 4)
                                isChanged = true
                                binding.wtInput.setText(df.format(str.toInt()))
                            }

                            if (str.toDouble() > 551) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(
                                    df.format(551)
                                )
                            }
                        } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {

                            if (str.length > 3) {
                                str.substring(str.length - 4)
                                isChanged = true
                                binding.wtInput.setText(df.format(str.toInt()))
                            }
                            if (str.toDouble() > 250) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(
                                    df.format(250)
                                )
                            }
                        }
                    }
                }

                if (binding.wtTab.getTabAt(0)!!.isSelected){
                    viewModel.setwtlb(df.format(
                        binding.wtInput.text.toString().toDouble()
                    ).toDouble())
                }else{
                    viewModel.setwtkg(df.format(
                        binding.wtInput.text.toString().toDouble()
                    ).toDouble())
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
                        ff.format(
                            (viewModel.ht_cm.value!! - viewModel.ht_ft.value!! * Mult_htft)
                                    / Mult_htin
                        ).toInt()
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
                    if (str.last().toString() != "'") {

                        str = str.substring(0, str.indexOf("'"))
                        ischanged = true
                        binding.htInputFtin1.setText(
                            ff.format(str.toInt()) + "'"
                        )
                    } else {
                        str = str.dropLast(1)
                    }
                }

                if (!str.isEmpty()) {
                    if (str.contains(".")) {
                        binding.htInputFtin1.setError("只能输入整数")
                        binding.htInputFtin1.postDelayed({
                            binding.htInputFtin1.error = null
                        }, 3000)

                        val intPart = str.substring(0, str.indexOf("."))
                        if (intPart.isEmpty()) {
                            ischanged = true
                            binding.htInputFtin1.setText(
                                ff.format(5)
                            )
                        } else {
                            ischanged = true
                            binding.htInputFtin1.setText(
                                intPart
                            )
                        }
                    } else {
                        if (str.length > 1) {
                            binding.htInputFtin1.setText(
                                str.substring(1)
                            )
                        }

                        if (str.toInt() < 1) {
                            ischanged = true
                            binding.htInputFtin1.setText(
                                ff.format(1)
                            )
                            binding.htInputFtin1.setError("最小值为1")
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        } else if (str.toInt() > 8) {
                            ischanged = true
                            binding.htInputFtin1.setText(
                                ff.format(8)
                            )
                            binding.htInputFtin1.setError("最大值为8")
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        }
                    }
                } else {
                    ischanged = true
                    binding.htInputFtin1.setText(
                        ff.format(5)
                    )
                }


                if (binding.htInputFtin1.text.toString().contains("'")){
                    viewModel.sethtft(
                        binding.htInputFtin1.text.toString().dropLast(1).toInt()
                    )
                }else{
                    viewModel.sethtft(
                        binding.htInputFtin1.text.toString().toInt()
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
            }else{
                binding.htInputFtin1.setText(binding.htInputFtin1.text.toString().dropLast(1))
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
//
//                if (str.contains("'")) {
//                    str = str.substring(0, str.indexOf("'"))
//                    isChanged = true
//                    binding.htInputFtin2.setText(
//                        ff.format(str.toInt())
//                    )
//                }

                if (str.contains("''")){
                    str=str.dropLast(2)
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
                            ff.format(7)
                        )
                    } else {
                        isChanged = true
                        binding.htInputFtin2.setText(
                            intPart
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
                            str.substring(1)
                        )
                    }

                    if (str.toInt() > 11) {
                        isChanged = true
                        binding.htInputFtin2.setText(
                            ff.format(11)
                        )
                    }
                }

                if (str.isEmpty()) {
                    isChanged = true
                    binding.htInputFtin2.setText(
                        ff.format(7)
                    )
                }

                if (binding.htInputFtin2.text.toString().contains("''")){
                    viewModel.sethtin(
                        binding.htInputFtin2.text.toString().dropLast(2).toInt()
                    )
                }else{
                    viewModel.sethtin(
                        binding.htInputFtin2.text.toString().toInt()
                    )
                }

                isChanged = false
            }
        })

        binding.htInputFtin2.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (!binding.htInputFtin2.text.toString().contains("''")){
                    binding.htInputFtin2.setText(
                        binding.htInputFtin2.text.toString() + "''"
                    )
                }
            }else{
                binding.htInputFtin2.setText(
                    binding.htInputFtin2.text.toString().dropLast(2)
                )
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

                if (!str.isEmpty()) {
                    if (str.contains(".")) {
                        val decimalCount = str.substring(str.indexOf("."))
                        if (decimalCount.length > 1) {
                            isChanged = true
                            binding.htInputCm.setText(
                                str.substring(0, str.indexOf(".") + 2)
                            )
                        }

                        val intCount = str.substring(0, str.indexOf("."))
                        if (intCount.length > 3) {
                            isChanged = true
                            binding.htInputCm.setText(
                                tf.format(
                                    (
                                            intCount.substring(intCount.length - 4).toDouble()
                                                    + decimalCount.toDouble() / 10)
                                )
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
                    } else {
                        if (str.length > 3) {
                            isChanged = true
                            binding.htInputCm.setText(
                                tf.format((str).toDouble())
                            )
                        }
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
                } else {
                    isChanged = true
                    binding.htInputCm.setText(
                        tf.format(170)
                    )
                }
                viewModel.sethtcm(
                    binding.htInputCm.text.toString().toDouble()
                )
                isChanged = false
            }

        })

        //日期
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = DateFormatSymbols(Locale.ENGLISH).shortMonths[month]
        binding.timeInputDate.setText("${monthName} ${day},${year}")
        viewModel.setDate(binding.timeInputDate.text.toString())
        binding.timeInputDate.setOnClickListener {
            val dialog = DatePickerFragment()
            dialog.show(childFragmentManager, "DatePicker")
        }
        viewModel.selectedDate.observe(requireActivity()) {
            binding.timeInputDate.text = viewModel.selectedDate.value.toString()
        }

        val current = LocalDateTime.now()

        Log.d(TAG, "current: ${current.hour}:${current.minute}:${current.second}")
        val phase: String = if (current.hour >= 23 && current.hour < 8) {
            "Night"
        } else if (current.hour >= 8 && current.hour < 14) {
            "Morning"
        } else if (current.hour >= 14 && current.hour < 19) {
            "Afternoon"
        } else {
            "Evening"
        }
        binding.timeInputPhase.text = phase
        viewModel.setPhase(binding.timeInputPhase.text.toString())
        //时间
        binding.timeInputPhase.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(childFragmentManager, "TimePicker")
        }
        viewModel.selectedPhase.observe(requireActivity()) {
            binding.timeInputPhase.setText(viewModel.selectedPhase.value)
        }
        //年龄
        ageinit()

        binding.genderSelectedMale.isSelected = false
        binding.genderSelectedFemale.isSelected = false
        //性别
        binding.genderSelectedMale.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true

                binding.genderSelectedFemale.also {
                    it.isSelected = false
                    it.alpha = 0.5f
                }
                binding.genderSelectedFemalePic.visibility = View.INVISIBLE
                it.alpha = 1f
                binding.genderSelectedMalePic.visibility = View.VISIBLE
                viewModel.setGender('0')
            }
        }

        binding.genderSelectedFemale.setOnClickListener {
            if (!it.isSelected) {
                it.isSelected = true

                binding.genderSelectedMale.also {
                    it.isSelected = false
                    it.alpha = 0.5f
                }
                binding.genderSelectedMalePic.visibility = View.INVISIBLE
                it.alpha = 1f
                binding.genderSelectedFemalePic.visibility = View.VISIBLE
                viewModel.setGender('1')
            }
        }

        binding.btnCalculate.setOnClickListener {

            if (viewModel.selectedGender.value != null) {

                val bmiInfo = BmiInfo(
                    viewModel.wt_lb.value!!.toDouble(),
                    viewModel.wt_kg.value!!.toDouble(),
                    viewModel.ht_ft.value!!.toInt(), viewModel.ht_in.value!!.toInt(),
                    viewModel.ht_cm.value!!.toDouble(),
                    viewModel.selectedDate.value, viewModel.selectedPhase.value,
                    viewModel.selectedAge.value!!.toInt(),
                    viewModel.selectedGender.value!!.toChar()
                )
                Log.d("CCCCCC", bmiInfo.toString())
                // viewModel.insertInfo(bmiInfo)
                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmival = viewModel.wt_lb.value!! / Math.pow(
                        ((viewModel.ht_ft.value!! * 12 +
                                viewModel.ht_in.value!!)).toDouble(), 2.0
                    ) * 703
                    viewModel.setBmival(tf.format(bmival.toFloat()).toFloat())
                    viewModel.wttype= "lb"
                    viewModel.httype="ftin"
                }

                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmival = viewModel.wt_lb.value!! * 0.453 / Math.pow(
                        viewModel.ht_cm.value!! * 0.01,
                        2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).toFloat())
                    viewModel.wttype= "lb"
                    viewModel.httype="cm"
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmival = viewModel.wt_kg.value!! / Math.pow(
                       viewModel.ht_ft.value!! * 0.3048 + viewModel.ht_in.value!! * 0.0254, 2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).toFloat())
                    viewModel.wttype= "kg"
                    viewModel.httype="ftin"
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmival = viewModel.wt_kg.value!! / Math.pow(
                        viewModel.ht_cm.value!! * 0.01, 2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).toFloat())
                    viewModel.wttype= "kg"
                    viewModel.httype="cm"
                }

                val dialog = CalculatorResultFragment()
                dialog.show(childFragmentManager, "CalculatorResult")

            } else {
                Toast.makeText(requireContext(), "please select your gender", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    fun ageinit() {
        binding.ageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.ageRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.ageRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //rv中心到两边的距离
                centerToLiftDistance = binding.ageRecyclerView.width / 2

                val childViewHeight = Utils.dip2px(requireContext(), CHILDVIEWSIZE.toFloat())
                //获取中心元素到两侧的元素个数
                Log.d(TAG, "rvWidth:${binding.ageRecyclerView.width}")
                childViewHalfCount = (binding.ageRecyclerView.width / childViewHeight) / 2 + 1
                Log.d(TAG, "$childViewHeight + $childViewHalfCount ")
                initData()
                findView()
            }
        })
        //滑动之后100ms后移动到中心位置
        binding.ageRecyclerView.postDelayed({
            binding.ageRecyclerView.scrollToPosition(childViewHalfCount + 25)
            //scrollToCenter(childViewHalfCount+25)
        }, 100L)
    }

    fun initData() {
        if (ageList.isEmpty()) {
            for (i in 2..99) {
                ageList.add(i.toString())
            }

            for (j in 0 until childViewHalfCount) {
                ageList.add(0, null)
            }

            for (k in 0 until childViewHalfCount) {
                ageList.add(null)
            }
        }
    }

    private val centerViewItems = ArrayList<CenterItemUtils.CenterViewItem>()
    private var isTouch = false
    private lateinit var adapter: AgeSelectorAdapter

    @SuppressLint("ClickableViewAccessibility")
    fun findView() {
        Log.d(TAG, ageList.toString())
        adapter = AgeSelectorAdapter(ageList)
        binding.ageRecyclerView.adapter = adapter

        binding.ageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //当滑动滚动状态为空闲时执行
                    val linearLayoutManager =
                        recyclerView.layoutManager as LinearLayoutManager
                    //获取当前可见元素的位置
                    val fi = linearLayoutManager.findFirstVisibleItemPosition()
                    val la = linearLayoutManager.findLastVisibleItemPosition()
                    if (isTouch) {
                        isTouch = false
                        //获取最中间的itemview
                        val centerPositionDiffer = (la - fi) / 2
                        Log.i(TAG, "onScrollStateChanged:  fi: ${fi}  la:${la}")
                        var centerChildViewPosition = fi + centerPositionDiffer
                        Log.d(TAG, "centerposition:${centerChildViewPosition}")
                        centerViewItems.clear()
                        //遍历循环，获取到和中线相差最小的条目索引
                        if (centerChildViewPosition != 0) {
                            for (i in centerChildViewPosition - 1 until centerChildViewPosition + 2) {
                                val cView =
                                    recyclerView.layoutManager!!.findViewByPosition(i)
                                val viewLeft = cView!!.left + (cView.width) / 2
                                centerViewItems.add(
                                    CenterItemUtils.CenterViewItem(
                                        i,
                                        Math.abs(centerToLiftDistance - viewLeft)
                                    )
                                )
                            }
                            val centerViewItem = CenterItemUtils.getMinDifferItem(centerViewItems)
                            centerChildViewPosition = centerViewItem.position
                        }
                        scrollToCenter(centerChildViewPosition)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                for (i in 0 until binding.ageRecyclerView.childCount) {
                    binding.ageRecyclerView.getChildAt(i).invalidate()
                }
            }
        })
        binding.ageRecyclerView.setOnTouchListener { view, motionEvent ->
            isTouch = true
            false
        }
    }

    private val decelerateInterpolator = DecelerateInterpolator()
    fun scrollToCenter(position: Int) {
        //var pos = if (position < childViewHalfCount) childViewHalfCount else position

        Log.d(TAG, "itemCOunt :${adapter.itemCount}")
//        var pos = if (position < adapter.itemCount - childViewHalfCount - 1) position
//        else adapter.itemCount - childViewHalfCount - 1

        var pos = if (position < childViewHalfCount) childViewHalfCount
        else if (position >= childViewHalfCount && position < adapter.itemCount - childViewHalfCount - 1)
            position
        else adapter.itemCount - childViewHalfCount - 1

        Log.d(TAG, "pos: ${pos}")

        val linearLayoutManager = binding.ageRecyclerView.layoutManager
                as LinearLayoutManager


        val childView = linearLayoutManager.findViewByPosition(pos)
        Log.d(TAG, "childView:${childView}")
        if (childView == null) return
        val childVhalf = childView.width / 2
        val childViewLeft = childView.left
        val viewCTop = centerToLiftDistance
        val smoothDistance = childViewLeft - viewCTop + childVhalf
        binding.ageRecyclerView.smoothScrollBy(smoothDistance, 0, decelerateInterpolator)
        adapter.setSelectPosition(pos)
        viewModel.setAge(pos-1)
        Log.d(TAG, "当前选中:${ageList.get(pos)}")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_cal, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.user -> Toast.makeText(requireContext(), "you click me", Toast.LENGTH_SHORT).show()
            else -> return false
        }
        return true
    }


}

