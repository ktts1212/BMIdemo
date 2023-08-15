package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
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
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.SettingFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.TimePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.util.CenterItemUtils
import bmicalculator.bmi.calculator.weightlosstracker.util.LanguageHelper
import com.google.android.material.tabs.TabLayout
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

private const val TAG = "CalculatorFragment"

private const val Multi_1 = 0.45359237

private const val Multi_haft = 30.48

private const val Multi_tin = 2.54

class CalculatorFragment : Fragment(), LifecycleOwner {

    private val ageList = ArrayList<String?>()

    private val CHILDLESSNESS = 70

    private lateinit var binding: FragmentCalculatorBinding

    private lateinit var viewModel: CalculatorViewModel

    private var centerToLiftDistance: Int = 0

    private var childViewHalfCount: Int = 0

    private lateinit var currentDialogFragmentTag:String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository,requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        //设置toolbar的显示
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarCal)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allInfo.observe(requireActivity()) { info ->
            if (!info.isNullOrEmpty()) {
                if (isAdded) {
                    val constraintSet = ConstraintSet()
                    val constraintLayout = binding.childLayout
                    constraintSet.clone(constraintLayout)
                    constraintSet.connect(
                        R.id.btn_calculate,
                        ConstraintSet.TOP, R.id.gender_card_male, ConstraintSet.BOTTOM,
                        Utils.dip2px(requireContext(), 22.5f)
                    )
                    constraintSet.applyTo(constraintLayout)
                }
            } else {
                if (isAdded) {
                    val constraintSet = ConstraintSet()
                    val constraintLayout = binding.childLayout
                    constraintSet.clone(constraintLayout)
                    constraintSet.connect(
                        R.id.btn_calculate,
                        ConstraintSet.TOP, R.id.gender_card_male, ConstraintSet.BOTTOM,
                        Utils.dip2px(requireContext(), 63.5f)
                    )
                    constraintSet.applyTo(constraintLayout)
                }
            }
        }

        //标题用户点击
        binding.calUser.setOnClickListener {
            val dialog = SettingFragment()
            dialog.show(childFragmentManager, "SettingFragment")
            currentDialogFragmentTag="SettingFragment"
        }

        val formatEnglish = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val ln = (activity as AppCompatActivity).getSharedPreferences("settings", 0).getString(
            "language", "en"
        )
        val formatEuro = NumberFormat.getNumberInstance(Locale.forLanguageTag(ln!!))

        val df = DecimalFormat("#.00")
        //用于身高保留一位小数
        val tf = DecimalFormat("#.0")

        val ff = DecimalFormat("#")
        //判断是否需要修改数字样式
        val isEu = LanguageHelper.euList.contains(ln)

        //初始化
        if (isEu) {
            binding.htInputCm.setText(tf.format(formatEuro.format(170.0).toDouble()))
            binding.wtInput.setText(df.format(formatEuro.format(140.00).toDouble()))
        } else {
            binding.wtInput.setText("140.00")
            binding.htInputCm.setText("170.0")
        }
        binding.htInputFtin1.text = Editable.Factory.getInstance().newEditable("5" + "'")
        binding.htInputFtin2.text = Editable.Factory.getInstance().newEditable("7" + "''")

        //当输入完edittext内容点击done对edittext内容进行修改
        binding.wtInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.wtInput.text.isNullOrEmpty()) {
                    var t = binding.wtInput.text.toString()

                    if (isEu) {
                        t = formatEnglish.format(formatEuro.parse(t))
                    }

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
                //隐藏键盘
                val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        //当输入完edittext内容点击done对edittext内容进行修改
        binding.htInputCm.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.htInputCm.text.isNullOrEmpty()) {
                    var t = binding.htInputCm.text.toString()

                    if (isEu) {
                        t = formatEnglish.format(formatEuro.parse(t))
                    }

                    if (binding.htTab.getTabAt(1)?.isSelected == true) {
                        if (t.toDouble() < 1) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid height to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.htInputCm.setText(
                                df.format(1)
                            )
                        } else if (t.toDouble() > 250) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid height to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.htInputCm.setText(
                                df.format(250)
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid height to calculate your BMI accurately",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.htInputCm.setText(tf.format(170))
                }

                val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        binding.htInputFtin1.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.htInputFtin1.text.isNullOrEmpty()) {
                    var t = binding.htInputFtin1.text.toString()

                    if (isEu) {
                        t = formatEnglish.format(formatEuro.parse(t))
                    }

                    if (binding.htTab.getTabAt(0)?.isSelected == true) {
                        if (t.toDouble() < 1) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid height to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.htInputFtin1.setText(
                                ff.format(1)
                            )
                        } else if (t.toDouble() > 8) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid height to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.htInputFtin1.setText(
                                ff.format(8)
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid height to calculate your BMI accurately",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.htInputFtin1.setText(ff.format(5))
                }

                val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        binding.htInputFtin2.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.htInputFtin2.text.isNullOrEmpty()) {
                    var t = binding.htInputFtin2.text.toString()

                    if (isEu) {
                        t = formatEnglish.format(formatEuro.parse(t))
                    }

                    if (binding.htTab.getTabAt(0)?.isSelected == true) {
                        if (t.toDouble() > 11) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid height to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.htInputFtin2.setText(
                                ff.format(11)
                            )
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid height to calculate your BMI accurately",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.htInputFtin2.setText(ff.format(7))
                }

                val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
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
                    binding.wtInput.setText(
                        if (isEu) {
                            df.format(
                                formatEuro.format(viewModel.wtLb.value).replace(",", ".")
                                    .toDouble()
                            )
                        } else {
                            df.format(viewModel.wtLb.value)
                        }
                    )
                } else {
                    binding.wtInput.setText(

                        if (isEu) {
                            df.format(
                                formatEuro.format(viewModel.wtKg.value).replace(",", ".")
                                    .toDouble()
                            )
                        } else {
                            df.format(viewModel.wtKg.value)
                        }
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val num = binding.wtInput.text.toString()
                var str = ""
                if (num.isNotEmpty()) {
                    str = formatEnglish.format(formatEuro.parse(num))
                }
                if (isEu && str.isNotEmpty()) {
                    str = df.format(str.toDouble()).replace(",", ".")
                }
                if (tab?.position == 0) {
                    if (str.isEmpty() || str.toDouble() < 2) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setWtLb(
                            df.format(2).replace(",", ".").toDouble()
                        )
                    } else {
                        viewModel.setWtLb(
                            if (isEu) {
                                df.format(str.toDouble()).replace(",", ".").toDouble()
                            } else {
                                df.format(str.toDouble()).toDouble()
                            }
                        )

                    }
                    if (!firstConvert) {
                        if (df.format(viewModel.wtLb.value!! * Multi_1).replace(",", ".")
                                .toDouble() < 1
                        ) {
                            viewModel.setWtKg(
                                if (isEu) {
                                    //formatEnglish.format(df.format(1).toDouble()).toDouble()
                                    df.format(1).replace(",", ".").toDouble()
                                } else {
                                    df.format(1).toDouble()
                                }
                            )
                        } else {
                            viewModel.setWtKg(
                                if (isEu) {
                                    df.format(viewModel.wtLb.value!! * Multi_1).replace(",", ".")
                                        .toDouble()
                                } else {
                                    df.format(viewModel.wtLb.value!! * Multi_1).toDouble()
                                }
                            )
                        }
                    }
                    firstConvert = false
                } else {
                    if (str.isEmpty() || str.toDouble() < 1) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setWtKg(
                            if (isEu) {
                                df.format(1).replace(",", ".").toDouble()
                            } else {
                                df.format(1).toDouble()
                            }
                        )
                    } else {
                        viewModel.setWtKg(
                            if (isEu) {
                                df.format(str.toDouble()).replace(",", ".").toDouble()
                            } else {
                                df.format(str.toDouble()).toDouble()
                            }
                        )
                    }
                    viewModel.setWtLb(
                        if (isEu) {
                            df.format(viewModel.wtKg.value!! / Multi_1).replace(",", ".")
                                .toDouble()
                        } else {
                            df.format(viewModel.wtKg.value!! / Multi_1).toDouble()
                        }
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
                var str = p0.toString()
                //根据语言判断是否需要进行数字格式转换
                if (str.contains(".") && isEu) {
                    isChanged = true
                    binding.wtInput.setText(str.replace(".", ","))
                } else if (str.contains(",") && !isEu) {
                    isChanged = true
                    binding.wtInput.setText(str.replace(",", "."))
                }
                //如果直接输入一个小数点，默认为最小值
                if (str.length == 1 && (str == "." || str == ",")) {
                    isChanged = true
                    binding.wtInput.setText(
                        df.format(1)
                    )
                }
                //将数字格式全部转换为小数点
                if (isEu && str.isNotEmpty()) {
                    str = str.replace(",", ".")
                } else if (!isEu && str.isNotEmpty()) {
                    str = str.replace(",", ".")
                }

                if (str.isNotEmpty()) {
                    if (str.contains(".") && str[str.length - 1] != '.') {
                        //规定小数位数
                        //如果有两个小数点，就抹去最后一个小数点及其后面的内容
                        val dotIndex = str.indexOf('.')
                        val lastDotIndex = str.lastIndexOf('.')
                        if (dotIndex != lastDotIndex) {
                            str = str.removeRange(lastDotIndex, lastDotIndex + 1)
                        }
                        //小数部分
                        val decimalPart: String = str.substring(str.indexOf(".") + 1)
                        val decimalNumber = decimalPart.length
                        if (decimalNumber > 2) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            isChanged = true
                            val subStr = str.substring(0, str.indexOf(".") + 3)
                            binding.wtInput.setText(
                                if (isEu) {
                                    df.format(subStr.toDouble())
                                } else {
                                    subStr
                                }
                            )
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
                                        df.format(
                                            str.substring(str.indexOf(".") - 3).toDouble()
                                        )
                                )
                            }
                        } else if (binding.wtTab.getTabAt(1)!!.isSelected) {
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
                    } else if (!str.contains(".")) {
                        if (binding.wtTab.getTabAt(0)?.isSelected == true) {

//                            if (str.length > 3) {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Please input a valid weight to calculate your BMI accurately",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//                                str = str.substring(str.length - 3)
//                                isChanged = true
//                                binding.wtInput.setText(
//                                        df.format(str.toInt())
//                                )
//                            }

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

//                            if (str.length > 3) {
//                                str = str.substring(str.length - 3)
//                                isChanged = true
//                                binding.wtInput.setText(
//                                    df.format(str.toInt())
//                                )
//                            }
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
                binding.wtInput.setSelection(binding.wtInput.text.length)
                isChanged = false
            }
        })


        //身高
        var htFirstConvert = true
        binding.htTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.htCardFtin1.visibility = View.VISIBLE
                    binding.htCardFtin2.visibility = View.VISIBLE
                    binding.htCardCm.visibility = View.INVISIBLE
                    binding.htInputFtin1.setText(viewModel.htFt.value.toString() + "'")
                    binding.htInputFtin2.setText(viewModel.htIn.value.toString() + "''")
                } else {
                    binding.htCardFtin1.visibility = View.INVISIBLE
                    binding.htCardFtin2.visibility = View.INVISIBLE
                    binding.htCardCm.visibility = View.VISIBLE
                    binding.htInputCm.setText(
                        if (isEu) {
                            tf.format(viewModel.htCm.value?.toDouble())
                        } else {
                            viewModel.htCm.value.toString()
                        }
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    val strFt = binding.htInputFtin1.text.toString()
                    val strIn = binding.htInputFtin2.text.toString()
                    if (strFt.isNotEmpty()) {
                        if (strFt.contains("'")) {
                            viewModel.setHtFt(
                                strFt.dropLast(1).toInt()
                            )
                        } else {
                            viewModel.setHtFt(
                                strFt.toInt()
                            )
                        }
                    } else {
                        viewModel.setHtFt(1)
                    }
                    if (strIn.isNotEmpty()) {
                        if (strIn.contains("''")) {
                            viewModel.setHtIn(
                                strIn.dropLast(2).toInt()
                            )
                        } else if (strIn.contains("'")) {
                            viewModel.setHtIn(
                                strIn.dropLast(1).toInt()
                            )
                        } else {
                            viewModel.setHtIn(
                                strIn.toInt()
                            )
                        }
                    } else {
                        viewModel.setHtIn(0)
                    }


                    if (htFirstConvert) {
                        viewModel.setHtCm(
                            if (isEu) {
                                tf.format(170).replace(",", ".").toDouble()
                            } else {
                                tf.format(170).toDouble()
                            }
                        )
                        htFirstConvert = false
                    } else {
                        viewModel.setHtCm(

                            if (isEu) {
                                tf.format(
                                    viewModel.htIn.value!! * Multi_tin +
                                            viewModel.htFt.value!! * Multi_haft
                                ).replace(",", ".").toDouble()
                            } else {
                                tf.format(
                                    viewModel.htIn.value!! * Multi_tin +
                                            viewModel.htFt.value!! * Multi_haft
                                ).toDouble()
                            }


                        )
                    }

                } else {
                    val htCm = binding.htInputCm.text.toString()
                    if (htCm.isNotEmpty()) {
                        viewModel.setHtCm(
                            if (isEu) {
                                binding.htInputCm.text.toString().replace(",", ".").toDouble()
                            } else {
                                binding.htInputCm.text.toString().toDouble()
                            }
                        )
                    } else {
                        if (isEu) {
                            viewModel.setHtCm(
                                tf.format(1).replace(",", ".").toDouble()
                            )
                        } else {
                            viewModel.setHtCm(
                                tf.format(1).toDouble()
                            )
                        }

                    }


                    viewModel.setHtFt(
                        ff.format((viewModel.htCm.value!! / Multi_haft).toInt()).toInt()
                    )
                    viewModel.setHtIn(
                        ff.format(
                            (viewModel.htCm.value!! - viewModel.htFt.value!! * Multi_haft)
                                    / Multi_tin
                        ).toInt()
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        //binding.htInputFtIn1文本
        binding.htInputFtin1.addTextChangedListener(object : TextWatcher {
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
                if (str.contains("'")) {
                    if (str.last().toString() != "'") {

                        str = str.substring(0, str.indexOf("'"))
                        isChanged = true
                        binding.htInputFtin1.setText(
                            ff.format(str.toInt()) + "'"
                        )
                    } else {
                        str = str.dropLast(1)
                    }
                }

                if (str.isNotEmpty()) {
                    if (str.contains(".")) {

                        binding.htInputFtin1.postDelayed({
                            binding.htInputFtin1.error = null
                        }, 3000)

                        val intPart = str.substring(0, str.indexOf("."))
                        if (intPart.isEmpty()) {
                            isChanged = true
                            binding.htInputFtin1.setText(
                                ff.format(5)
                            )
                        } else {
                            isChanged = true
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
                            isChanged = true
                            binding.htInputFtin1.setText(
                                ff.format(1)
                            )
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        } else if (str.toInt() > 8) {
                            isChanged = true
                            binding.htInputFtin1.setText(
                                ff.format(8)
                            )
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        }
                    }
                }

                isChanged = false
            }
        })

        binding.htInputFtin1.setOnFocusChangeListener { _, hasFocus ->
            if (binding.htInputFtin1.text.isNotEmpty()) {
                if (!hasFocus) {
                    if (!binding.htInputFtin1.text.toString().contains("'")) {
                        binding.htInputFtin1.setText(
                            binding.htInputFtin1.text.toString() + "'"
                        )
                    }
                } else {
                    binding.htInputFtin1.setText(binding.htInputFtin1.text.toString().dropLast(1))
                }
            } else {
                if (!hasFocus) {
                    binding.htInputFtin1.setText("1'")
                }
            }
        }

        //binding.htInputFtIn2文本
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
                }

                if (str.contains(".")) {
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

                if (str.isNotEmpty() && !str.contains(".")) {

                    if (str.length > 2) {
                        isChanged = true
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

                isChanged = false
            }
        })

        binding.htInputFtin2.setOnFocusChangeListener { _, hasFocus ->
            if (binding.htInputFtin2.text.isNotEmpty()) {
                if (!hasFocus) {
                    if (!binding.htInputFtin2.text.toString().contains("''")) {
                        binding.htInputFtin2.setText(
                            binding.htInputFtin2.text.toString() + "''"
                        )
                    }
                } else {
                    binding.htInputFtin2.setText(
                        binding.htInputFtin2.text.toString().dropLast(2)
                    )
                }
            } else {
                if (!hasFocus) {
                    binding.htInputFtin2.setText("0''")
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
                if (str.contains(".") && isEu) {
                    isChanged = true
                    binding.htInputCm.setText(str.replace(".", ","))
                } else if (str.contains(",") && !isEu) {
                    isChanged = true
                    binding.htInputCm.setText(str.replace(",", "."))
                }

                if (isEu && str.isNotEmpty()) {
                    str = str.replace(",", ".")
                } else if (!isEu && str.isNotEmpty()) {
                    str = str.replace(",", ".")
                }

                if (str.isNotEmpty()) {
                    if (str.contains(".") && str[str.length - 1] != '.') {
                        //规定小数位数
                        val dotIndex = str.indexOf('.')
                        val lastDotIndex = str.lastIndexOf('.')
                        if (dotIndex != lastDotIndex) {
                            str = str.removeRange(lastDotIndex, lastDotIndex + 1)
                        }
                        val decimalCount: String = str.substring(str.indexOf(".") + 1)
                        if (decimalCount.length > 1) {
                            isChanged = true
                            binding.htInputCm.setText(
                                if (isEu) {
                                    formatEuro.format(
                                        str.substring(0, str.indexOf(".") + 2).toDouble()
                                    )
                                } else {
                                    str.substring(0, str.indexOf(".") + 2)
                                }
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
                        }
                    } else if (!str.contains(".") && str.isNotEmpty()) {
                        if (str.length > 3) {
                            isChanged = true
                            binding.htInputCm.setText(
                                    tf.format(str.toInt())
                            )
                        }
                        if (str.toDouble() > 250) {
                            isChanged = true
                            binding.htInputCm.setText(
                                tf.format(250)
                            )
                        }
                    }
                }
                isChanged = false
            }
        })

        //日期
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        var monthName = DateFormatSymbols(Locale.ENGLISH).shortMonths[month]
        monthName = when (monthName) {
            "Jul" -> "July"
            "Jun" -> "June"
            else -> monthName
        }
        binding.timeInputDate.text = "$monthName ${day},${year}"
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
        val phase: String = when (current.hour) {
            23 -> {
                getString(R.string.night)
            }

            in 0..7 -> {
                getString(R.string.night)
            }

            in 8..13 -> {
                getString(R.string.morning)
            }

            in 14..18 -> {
                getString(R.string.afternoon)
            }

            else -> {
                getString(R.string.evening)
            }
        }
        binding.timeInputPhase.text = phase
        viewModel.setPhase(
            Utils.phaseToNum(
                requireContext(),
                binding.timeInputPhase.text.toString()
            )
        )
        //时间
        binding.timeInputPhase.setOnClickListener {
            val dialog = TimePickerFragment()
            dialog.show(childFragmentManager, "TimePicker")
        }
        viewModel.selectedPhase.observe(requireActivity()) {

            if (isAdded) {
                binding.timeInputPhase.text = Utils.numToPhase(
                    requireContext(),
                    viewModel.selectedPhase.value!!
                )
            }
        }
        //年龄
        ageInit()

        binding.genderSelectedMale.isSelected = false
        binding.genderSelectedFemale.isSelected = false
        //性别
        binding.genderSelectedMale.setOnClickListener { vw ->
            if (!vw.isSelected) {
                vw.isSelected = true

                binding.genderSelectedFemale.also { relativeLayout ->
                    relativeLayout.isSelected = false
                    relativeLayout.alpha = 0.5f
                }
                binding.genderSelectedFemalePic.visibility = View.INVISIBLE
                vw.alpha = 1f
                binding.genderSelectedMalePic.visibility = View.VISIBLE
                viewModel.setGender('0')
            }
        }

        binding.genderSelectedFemale.setOnClickListener { vw ->
            if (!vw.isSelected) {
                vw.isSelected = true

                binding.genderSelectedMale.also { relativeLayout ->
                    relativeLayout.isSelected = false
                    relativeLayout.alpha = 0.5f
                }
                binding.genderSelectedMalePic.visibility = View.INVISIBLE
                vw.alpha = 1f
                binding.genderSelectedFemalePic.visibility = View.VISIBLE
                viewModel.setGender('1')
            }
        }

        viewModel.bmiNewRecord.observe(requireActivity()) { newRecord ->
            if (newRecord != null) {
                Log.d(TAG, "newRecord:${newRecord}")
                when (newRecord.wtHtType) {
                    "lbftin" -> {
                        binding.htCardFtin1.visibility = View.VISIBLE
                        binding.htCardFtin2.visibility = View.VISIBLE
                        binding.htCardCm.visibility = View.INVISIBLE
                        binding.wtTab.getTabAt(0)?.select()
                        binding.wtInput.setText(newRecord.wt_lb.toString())
                        binding.htTab.getTabAt(0)?.select()
                        binding.htInputFtin1.setText("${newRecord.ht_ft}'")
                        binding.htInputFtin2.setText("${newRecord.ht_in}''")
                    }
                    "kgftin" -> {
                        binding.htCardFtin1.visibility = View.VISIBLE
                        binding.htCardFtin2.visibility = View.VISIBLE
                        binding.htCardCm.visibility = View.INVISIBLE
                        binding.wtTab.getTabAt(1)?.select()
                        binding.wtInput.setText(newRecord.wt_kg.toString())
                        binding.htTab.getTabAt(0)?.select()
                        binding.htInputFtin1.setText("${newRecord.ht_ft}'")
                        binding.htInputFtin2.setText("${newRecord.ht_in}''")
                    }
                    "lbcm" -> {
                        binding.htCardFtin1.visibility = View.INVISIBLE
                        binding.htCardFtin2.visibility = View.INVISIBLE
                        binding.htCardCm.visibility = View.VISIBLE
                        binding.wtTab.getTabAt(0)?.select()
                        binding.wtInput.setText(newRecord.wt_lb.toString())
                        binding.htTab.getTabAt(1)?.select()
                        binding.htInputCm.setText(newRecord.ht_cm.toString())
                    }
                    else -> {
                        binding.htCardFtin1.visibility = View.INVISIBLE
                        binding.htCardFtin2.visibility = View.INVISIBLE
                        binding.htCardCm.visibility = View.VISIBLE
                        binding.wtTab.getTabAt(1)?.select()
                        binding.wtInput.setText(newRecord.wt_kg.toString())
                        binding.htTab.getTabAt(1)?.select()
                        binding.htInputCm.setText(newRecord.ht_cm.toString())
                    }
                }
                if (isAdded) {
                    binding.timeInputPhase.text = Utils.numToPhase(
                        requireContext(),
                        newRecord.phase
                    )
                    binding.timeInputDate.text = newRecord.date
                    binding.ageRecyclerView.scrollToPosition(childViewHalfCount + newRecord.age - 1)
                    //滑动之后100ms后移动到中心位置
                    binding.ageRecyclerView.postDelayed({
                        if (::adapter.isInitialized) {
                            scrollToCenter(newRecord.age + 1)
                        }
                    }, 100L)
                }
                if (newRecord.gender == '0') {
                    binding.genderSelectedMale.isSelected = true
                    binding.genderSelectedFemale.also { relativeLayout ->
                        relativeLayout.isSelected = false
                        relativeLayout.alpha = 0.5f
                    }
                    binding.genderSelectedFemalePic.visibility = View.INVISIBLE
                    binding.genderSelectedMale.alpha = 1f
                    binding.genderSelectedMalePic.visibility = View.VISIBLE
                    viewModel.selectedGender.value = '0'
                } else {
                    binding.genderSelectedFemale.isSelected = true
                    binding.genderSelectedMale.also { relativeLayout ->
                        relativeLayout.isSelected = false
                        relativeLayout.alpha = 0.5f
                    }
                    binding.genderSelectedMalePic.visibility = View.INVISIBLE
                    binding.genderSelectedFemale.alpha = 1f
                    binding.genderSelectedFemalePic.visibility = View.VISIBLE
                    viewModel.selectedGender.value = '1'
                }

            }
        }

        binding.btnCalculate.setOnClickListener {

            if (binding.wtTab.getTabAt(0)!!.isSelected) {
                val str = binding.wtInput.text.toString()
                if (str.isNotEmpty()) {
                    viewModel.setWtLb(

                        if (isEu) {
                            str.replace(",", ".").toDouble()
                        } else {
                            str.toDouble()
                        }
                    )
                } else {
                    viewModel.setWtLb(
                        140.00
                    )
                }

            } else {
                val str = binding.wtInput.text.toString()
                if (str.isNotEmpty()) {
                    viewModel.setWtKg(

                        if (isEu) {
                            str.replace(",", ".").toDouble()
                        } else {
                            str.toDouble()
                        }
                    )
                } else {
                    viewModel.setWtKg(
                        65.00
                    )
                }
            }

            if (binding.htTab.getTabAt(0)!!.isSelected) {
                val str = binding.htInputFtin1.text.toString()
                val str2 = binding.htInputFtin2.text.toString()
                if (str.isNotEmpty() && str.isNotEmpty()) {
                    viewModel.setHtFt(
                        if (str.contains("'")) str.dropLast(1).toInt()
                        else str.toInt()

                    )

                    viewModel.setHtIn(
                        if (str2.contains("''")) str2.dropLast(2).toInt()
                        else str2.toInt()
                    )
                } else {
                    if (str.isEmpty()) {
                        viewModel.setHtFt(5)
                        viewModel.setHtIn(str2.toInt())
                    }

                    if (str2.isEmpty()) {
                        viewModel.setHtIn(7)
                        viewModel.setHtFt(str.toInt())
                    }
                }

            } else {
                val str = binding.htInputCm.text.toString()
                if (str.isNotEmpty()) {
                    viewModel.setHtCm(

                        if (isEu) {
                            str.replace(",", ".").toDouble()
                        } else {
                            str.toDouble()
                        }
                    )
                } else {
                    viewModel.setHtCm(
                        170.0
                    )
                }
            }

            if (binding.genderSelectedMale.isSelected || binding.genderSelectedFemale.isSelected) {

                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmiVal = viewModel.wtLb.value!! / ((viewModel.htFt.value!! * 12 +
                            viewModel.htIn.value!!)).toDouble().pow(2.0) * 703
                    viewModel.setBmiVal(tf.format(bmiVal.toFloat()).replace(",", ".").toFloat())
                    viewModel.wtType = "lb"
                    viewModel.htType = "ftin"
                    viewModel.setWtKg(
                        df.format(viewModel.wtLb.value!! * Multi_1)
                            .replace(",", ".")
                            .toDouble()
                    )
                    viewModel.setHtCm(
                        tf.format(
                            viewModel.htIn.value!! * Multi_tin +
                                    viewModel.htFt.value!! * Multi_haft
                        ).replace(",", ".").toDouble()
                    )
                }

                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmiVal =
                        viewModel.wtLb.value!! * 0.453 / (viewModel.htCm.value!! * 0.01).pow(
                            2.0
                        )
                    viewModel.setBmiVal(tf.format(bmiVal.toFloat()).replace(",", ".").toFloat())
                    viewModel.wtType = "lb"
                    viewModel.htType = "cm"
                    viewModel.setWtKg(
                        df.format(viewModel.wtLb.value!! * Multi_1)
                            .replace(",", ".")
                            .toDouble()
                    )
                    viewModel.setHtFt(
                        ff.format((viewModel.htCm.value!! / Multi_haft).toInt()).replace(",", ".")
                            .toInt()
                    )
                    viewModel.setHtIn(
                        ff.format((viewModel.htCm.value!! - viewModel.htFt.value!! * Multi_haft) / Multi_tin)
                            .toInt()
                    )
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmiVal =
                        viewModel.wtKg.value!! /
                                (viewModel.htFt.value!! * 0.3048 + viewModel.htIn.value!! * 0.0254).pow(
                                    2.0
                                )
                    viewModel.setBmiVal(tf.format(bmiVal.toFloat()).replace(",", ".").toFloat())
                    viewModel.wtType = "kg"
                    viewModel.htType = "ftin"
                    viewModel.setWtLb(
                        df.format(viewModel.wtKg.value!! / Multi_1)
                            .replace(",", ".")
                            .toDouble()
                    )
                    viewModel.setHtCm(
                        tf.format(
                            viewModel.htIn.value!! * Multi_tin +
                                    viewModel.htFt.value!! * Multi_haft
                        ).replace(",", ".").toDouble()
                    )
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmiVal = viewModel.wtKg.value!! / (viewModel.htCm.value!! * 0.01).pow(2.0)
                    viewModel.setBmiVal(tf.format(bmiVal.toFloat()).replace(",", ".").toFloat())
                    viewModel.wtType = "kg"
                    viewModel.htType = "cm"
                    viewModel.setWtLb(
                        df.format(viewModel.wtKg.value!! / Multi_1)
                            .replace(",", ".")
                            .toDouble()
                    )
                    viewModel.setHtFt(
                        ff.format((viewModel.htCm.value!! / Multi_haft).toInt()).replace(",", ".")
                            .toInt()
                    )
                    viewModel.setHtIn(
                        ff.format((viewModel.htCm.value!! - viewModel.htFt.value!! * Multi_haft) / Multi_tin)
                            .toInt()
                    )
                }
                val bInfo=BmiInfo(
                    viewModel.wtLb.value!!.toDouble(),
                    viewModel.wtKg.value!!.toDouble(),
                    viewModel.htFt.value!!.toInt(), viewModel.htIn.value!!.toInt(),
                    viewModel.htCm.value!!.toDouble(),
                    viewModel.selectedDate.value, viewModel.selectedPhase.value!!,
                    viewModel.selectedAge.value!!.toInt(),
                    viewModel.selectedGender.value!!.toChar(),
                    viewModel.bmiVal.value!!,
                    -1,
                                -1,
                    viewModel.bmiType,
                    viewModel.wtType + viewModel.htType,
                    0
                )
                viewModel.saveData(bInfo)
                val dialog = CalculatorResultFragment()
                dialog.show(childFragmentManager, "CalculatorResult")

            } else {
                Toast.makeText(requireContext(), "please select your gender", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initBar()
    }

    private lateinit var adapter: AgeSelectorAdapter
    private fun ageInit() {
        binding.ageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.ageRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.ageRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //rv中心到两边的距离
                centerToLiftDistance = binding.ageRecyclerView.width / 2

                if (isAdded) {
                    val childViewHeight = Utils.dip2px(requireContext(), CHILDLESSNESS.toFloat())
                    //获取中心元素到两侧的元素个数
                    Log.d(TAG, "rvWidth:${binding.ageRecyclerView.width}")
                    childViewHalfCount = (binding.ageRecyclerView.width / childViewHeight) / 2 + 1
                    Log.d(TAG, "$childViewHeight + $childViewHalfCount ")
                    initData()
                    findView()
                }


            }
        })
        if (isAdded) {
            binding.ageRecyclerView.scrollToPosition(childViewHalfCount + 24)
            //滑动之后100ms后移动到中心位置
            binding.ageRecyclerView.postDelayed({
                if (::adapter.isInitialized) {
                    scrollToCenter(26)
                }
            }, 100L)
        }
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
                        //获取最中间的itemView
                        val centerPositionDiffer = (la - fi) / 2
                        Log.i(TAG, "onScrollStateChanged:  fi: $fi  la:${la}")
                        var centerChildViewPosition = fi + centerPositionDiffer
                        Log.d(TAG, "centerPosition:${centerChildViewPosition}")
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
                                        abs(centerToLiftDistance - viewLeft)
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
        binding.ageRecyclerView.setOnTouchListener { _, _ ->
            isTouch = true
            false
        }
    }

    private val decelerateInterpolator = DecelerateInterpolator()
    fun scrollToCenter(position: Int) {

        val pos = if (position < childViewHalfCount) childViewHalfCount
        else if (position < adapter.itemCount - childViewHalfCount - 1)
            position
        else adapter.itemCount - childViewHalfCount - 1

        Log.d(TAG, "pos: $pos")

        val linearLayoutManager = binding.ageRecyclerView.layoutManager
                as LinearLayoutManager


        val childView = linearLayoutManager.findViewByPosition(pos)
        Log.d(TAG, "childView:${childView}")
        if (childView == null) return
        val childHalf = childView.width / 2
        val childViewLeft = childView.left
        val viewCTop = centerToLiftDistance
        val smoothDistance = childViewLeft - viewCTop + childHalf
        binding.ageRecyclerView.smoothScrollBy(smoothDistance, 0, decelerateInterpolator)
        adapter.setSelectPosition(pos)
        viewModel.setAge(pos - 1)
        Log.d(TAG, "当前选中:${ageList[pos]}")
    }



    private fun initBar() {
        immersionBar {
            statusBarColor(R.color.bg1)
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initBar()
        }
    }
}

