package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

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
import android.widget.AutoCompleteTextView
import android.widget.EditText
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
import bmicalculator.bmi.calculator.weightlosstracker.ui.adapter.AgeSelectorAdapter
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.CalculatorResultFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.DatePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.SettingFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child.TimePickerFragment
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CenterItemUtils
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.LanguageHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.text.NumberFormat
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorBinding.inflate(layoutInflater, container, false)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)
        //设置toolbar的显示
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarCal)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //标题用户点击
        binding.calUser.setOnClickListener {
            val dialog = SettingFragment()
            dialog.show(childFragmentManager, "SettingFragment")
        }

        val formatEnglish = NumberFormat.getNumberInstance(Locale.ENGLISH)
        val ln = (activity as AppCompatActivity).getSharedPreferences("settings", 0).getString(
            "language", "en"
        )
        val formatEurpo = NumberFormat.getNumberInstance(Locale.forLanguageTag(ln!!))

        val df = DecimalFormat("#.00")
        //用于身高保留一位小数
        val tf = DecimalFormat("#.0")

        val ff = DecimalFormat("#")
        //判断是否需要修改数字样式
        val isEu = LanguageHelper.euList.contains(ln)

        //初始化
        if (isEu) {
            binding.htInputCm.setText(tf.format(formatEurpo.format(170.0).toDouble()))
            binding.wtInput.setText(df.format(formatEurpo.format(140.00).toDouble()))
        } else {
            binding.wtInput.setText("140.00")
            binding.htInputCm.setText("170.0")
        }
        binding.htInputFtin1.text = Editable.Factory.getInstance().newEditable("5" + "'")
        binding.htInputFtin2.text = Editable.Factory.getInstance().newEditable("7" + "''")

        //当输入完edittext内容点击done对edittext内容进行修改
        binding.wtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!binding.wtInput.text.isNullOrEmpty()) {
                    var t = binding.wtInput.text.toString()

                    if (isEu) {
                        t = formatEnglish.format(formatEurpo.parse(t))
                    }

                    if (binding.wtTab.getTabAt(0)?.isSelected == true) {
                        if (t.toDouble() < 2) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                if (isEu) {
                                    df.format(formatEurpo.format(2))
                                } else {
                                    df.format(2)
                                }
                            )
                        } else if (t.toDouble() > 551) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                if (isEu) {
                                    df.format(formatEurpo.format(551))
                                } else {
                                    df.format(551)
                                }

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
                                if (isEu) {
                                    df.format(formatEurpo.format(1))
                                } else {
                                    df.format(1)
                                }
                            )
                        } else if (t.toDouble() > 250) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.wtInput.setText(
                                if (isEu) {
                                    df.format(formatEurpo.format(250))
                                } else {
                                    df.format(250)
                                }
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
                            if (isEu) {
                                df.format(formatEurpo.format(140))

                            } else {
                                df.format(140)
                            }
                        )
                    } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {
                        binding.wtInput.setText(
                            if (isEu) {
                                df.format(formatEurpo.format(65))
                            } else {
                                df.format(65)
                            }
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
                    binding.wtInput.setText(
                        if (isEu) {
                            df.format(
                                formatEurpo.format(viewModel.wt_lb.value).replace(",", ".")
                                    .toDouble()
                            )
                        } else {
                            df.format(viewModel.wt_lb.value)
                        }
                    )
                } else {
                    binding.wtInput.setText(

                        if (isEu) {
                            df.format(
                                formatEurpo.format(viewModel.wt_kg.value).replace(",", ".")
                                    .toDouble()
                            )
                        } else {
                            df.format(viewModel.wt_kg.value)
                        }
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val p0: Editable? = binding.wtInput.text
                val num = binding.wtInput.text.toString()
                var str=""
                if (!num.isEmpty()) {
                    str = formatEnglish.format(formatEurpo.parse(num))
                }
                if (isEu&&!str.isEmpty()) {
                    str = df.format(str.toDouble()).replace(",", ".")
                }
                Log.d(TAG, "isEu:${isEu}")
                Log.d(TAG, "267str:${str}")
                if (tab?.position == 0) {
                    if (str.isEmpty()||str.toDouble() < 2) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setwtlb(
                            df.format(2).replace(",",".").toDouble()
                        )
                    } else {
                        viewModel.setwtlb(
                            if (isEu) {
                                df.format(str.toDouble()).replace(",", ".").toDouble()
                            } else {
                                df.format(str.toDouble()).toDouble()
                            }
                        )

                    }
                    if (firstConvert == false) {
                        if (df.format(viewModel.wt_lb.value!! * Mult_1).replace(",", ".")
                                .toDouble() < 1
                        ) {
                            viewModel.setwtkg(
                                if (isEu) {
                                    //formatEnglish.format(df.format(1).toDouble()).toDouble()
                                    df.format(1).replace(",",".").toDouble()
                                } else {
                                    df.format(1).toDouble()
                                }
                            )
                        } else {
                            viewModel.setwtkg(
                                if (isEu) {
                                    //nf_en.format(df.format(viewModel.wt_lb.value!! * Mult_1).toDouble()).toDouble()
                                    df.format(viewModel.wt_lb.value!! * Mult_1).replace(",", ".")
                                        .toDouble()
                                } else {
                                    df.format(viewModel.wt_lb.value!! * Mult_1).toDouble()
                                }
                            )
                        }
                    }
                    firstConvert = false
                } else {
                    if (str.isEmpty()||str.toDouble() < 1) {
                        Toast.makeText(
                            requireContext(),
                            "Please input a valid weight to calculate your BMI accurately",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.setwtkg(
                            if (isEu) {
                                // nf_en.format(df.format(1).toDouble()).toDouble()
                                df.format(1).replace(",",".").toDouble()
                            } else {
                                df.format(1).toDouble()
                            }
                        )
                    } else {
                        viewModel.setwtkg(
                            if (isEu) {
                                df.format(str.toDouble()).replace(",", ".").toDouble()
                            } else {
                                df.format(str.toDouble()).toDouble()
                            }
                        )
                    }
                    viewModel.setwtlb(
                        if (isEu) {
                            df.format(viewModel.wt_kg.value!! / Mult_1).replace(",", ".").toDouble()
                        } else {
                            df.format(viewModel.wt_kg.value!! / Mult_1).toDouble()
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
                if (str.contains(".") && isEu) {
                    isChanged = true
                    binding.wtInput.setText(str.replace(".", ","))
                } else if (str.contains(",") && !isEu) {
                    isChanged = true
                    binding.wtInput.setText(str.replace(",", "."))
                }

                if (str.length == 1 && (str == "." || str == ",")) {
                    isChanged = true
                    binding.wtInput.setText(
                        df.format(1)
                    )
                }

                if (isEu && !str.isEmpty()) {
                    str = str.replace(",", ".")
                } else if (!isEu && !str.isEmpty()) {
                    str = str.replace(",", ".")
                }

                Log.d(TAG, "first str:${str}")

                if (!str.isEmpty()) {
                    if (str.contains(".") && str[str.length - 1] != '.') {
                        //规定小数位数
                        var decimalPart = ""
                        val dotIndex = str.indexOf('.')
                        val lastDotIndex = str.lastIndexOf('.')
                        if (dotIndex != lastDotIndex) {
                            str = str.removeRange(lastDotIndex, lastDotIndex + 1)
                        }

                        decimalPart = str.substring(str.indexOf(".") + 1)
                        Log.d(TAG, "decimalForamat:${decimalPart}")
                        val decimalnumber = decimalPart.length
                        if (decimalnumber > 2) {
                            Toast.makeText(
                                requireContext(),
                                "Please input a valid weight to calculate your BMI accurately",
                                Toast.LENGTH_SHORT
                            ).show()
                            isChanged = true
                            val subStr = str.substring(0, str.indexOf(".") + 3)
                            Log.d(TAG, "subStr:${subStr}")
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
                                    if (isEu) {
                                        df.format(551)
                                    } else {
                                        df.format(551)
                                    }
                                )
                            } else if (str.substring(0, str.indexOf(".")).length > 3) {
                                isChanged
                                binding.wtInput.setText(
                                    if (isEu) {
                                        df.format(
                                            str.substring(str.indexOf(".") - 3).toDouble()
                                        )
                                    } else {
                                        df.format(str.substring(str.indexOf(".") - 3).toDouble())
                                    }
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

                                    if (isEu) {
                                        df.format(250)
                                    } else {
                                        df.format(250)
                                    }
                                )
                            } else if (str.substring(0, str.indexOf(".")).length > 3) {
                                isChanged
                                binding.wtInput.setText(

                                    if (isEu) {
                                        df.format(str.substring(str.indexOf(".") - 3).toDouble())
                                    } else {
                                        df.format(str.substring(str.indexOf(".") - 3).toDouble())
                                    }
                                )
                            }
                        }
                    } else if (!str.contains(".")) {
                        if (binding.wtTab.getTabAt(0)?.isSelected == true) {

                            if (str.length > 3) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()

                                str = str.substring(str.length - 3)
                                Log.d(TAG, "wwwwww:str:${str}")
                                isChanged = true
                                binding.wtInput.setText(

                                    if (isEu) {
                                        df.format(str.toInt())
                                    } else {
                                        df.format(str.toInt())
                                    }

                                )
                            }

                            if (str.toDouble() > 551) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(

                                    if (isEu) {
                                        df.format(551)
                                    } else {
                                        df.format(551)
                                    }
                                )
                            }
                        } else if (binding.wtTab.getTabAt(1)?.isSelected == true) {

                            if (str.length > 3) {
                                str = str.substring(str.length - 3)
                                isChanged = true
                                binding.wtInput.setText(
                                    df.format(str.toInt())
                                )
                            }
                            if (str.toDouble() > 250) {
                                Toast.makeText(
                                    requireContext(),
                                    "Please input a valid weight to calculate your BMI accurately",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isChanged = true
                                binding.wtInput.setText(

                                    if (isEu) {
                                        df.format(250)
                                    } else {
                                        df.format(250)
                                    }
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
        var htfirstConvert = true
        binding.htTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("SetTextI18n")
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
                    binding.htInputCm.setText(
                        if (isEu) {
                            tf.format(viewModel.ht_cm.value?.toDouble())
                        } else {
                            viewModel.ht_cm.value.toString()
                        }
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    val strft = binding.htInputFtin1.text.toString()
                    val strin = binding.htInputFtin2.text.toString()
                    if (!strft.isEmpty()){
                        if (strft.contains("'")) {
                            viewModel.sethtft(
                                strft.dropLast(1).toInt()
                            )
                        } else {
                            viewModel.sethtft(
                                strft.toInt()
                            )
                        }
                    }else{
                        viewModel.sethtft(1)
                    }
                    if (!strin.isEmpty()){
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
                    }else{
                        viewModel.sethtin(0)
                    }


                    if (htfirstConvert) {
                        viewModel.sethtcm(
                            if (isEu) {
                                tf.format(170).replace(",", ".").toDouble()
                            } else {
                                tf.format(170).toDouble()
                            }
                        )
                        htfirstConvert = false
                    } else {
                        viewModel.sethtcm(

                            if (isEu) {
                                tf.format(
                                    viewModel.ht_in.value!! * Mult_htin +
                                            viewModel.ht_ft.value!! * Mult_htft
                                ).replace(",", ".").toDouble()
                            } else {
                                tf.format(
                                    viewModel.ht_in.value!! * Mult_htin +
                                            viewModel.ht_ft.value!! * Mult_htft
                                ).toDouble()
                            }


                        )
                    }

                } else {
                    val htcm=binding.htInputCm.text.toString()
                    if (!htcm.isEmpty()){
                        viewModel.sethtcm(
                            if (isEu) {
                                binding.htInputCm.text.toString().replace(",", ".").toDouble()
                            } else {
                                binding.htInputCm.text.toString().toDouble()
                            }
                        )
                    }else{
                        if (isEu){
                            viewModel.sethtcm(
                                tf.format(1).replace(",",".").toDouble()
                            )
                        }else{
                            viewModel.sethtcm(
                                tf.format(1).toDouble()
                            )
                        }

                    }


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
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        } else if (str.toInt() > 8) {
                            ischanged = true
                            binding.htInputFtin1.setText(
                                ff.format(8)
                            )
                            binding.htInputFtin1.postDelayed({
                                binding.htInputFtin1.error = null
                            }, 3000)
                        }
                    }
                }

                ischanged = false
            }
        })

        binding.htInputFtin1.setOnFocusChangeListener { view, hasFocus ->
            if (!binding.htInputFtin1.text.isEmpty()){
                if (!hasFocus) {
                    if (!binding.htInputFtin1.text.toString().contains("'")) {
                        binding.htInputFtin1.setText(
                            binding.htInputFtin1.text.toString() + "'"
                        )
                    }
                } else {
                    binding.htInputFtin1.setText(binding.htInputFtin1.text.toString().dropLast(1))
                }
            }else{
                if (!hasFocus){
                    binding.htInputFtin1.setText("1'")
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

                if (!str.isEmpty() && !str.contains(".")) {

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

        binding.htInputFtin2.setOnFocusChangeListener { view, hasFocus ->
            if (!binding.htInputFtin2.text.isEmpty()){
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
            }else{
                if (!hasFocus){
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

                if (isEu && !str.isEmpty()) {
                    str = str.replace(",", ".")
                } else if (!isEu && !str.isEmpty()) {
                    str = str.replace(",", ".")
                }

                if (!str.isEmpty()) {
                    if (str.contains(".") && str[str.length - 1] != '.') {
                        //规定小数位数
                        var decimalCount = ""
                        val dotIndex = str.indexOf('.')
                        val lastDotIndex = str.lastIndexOf('.')
                        if (dotIndex != lastDotIndex) {
                            str = str.removeRange(lastDotIndex, lastDotIndex + 1)
                        }
                        decimalCount = str.substring(str.indexOf(".") + 1)
                        if (decimalCount.length > 1) {
                            isChanged = true
                            binding.htInputCm.setText(
                                if (isEu) {
                                    formatEurpo.format(str.substring(0, str.indexOf(".") + 2).toDouble())
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
                    } else if (!str.contains(".") && !str.isEmpty()) {
                        if (str.length > 3) {
                            isChanged = true
                            binding.htInputCm.setText(
                                if (isEu) {
                                    tf.format(str.toInt())
                                } else {
                                    tf.format(str.toInt())
                                }
                                // tf.format((str).toDouble())
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
//                else {
//                    isChanged = true
//                    binding.htInputCm.setText(
//                        tf.format(170)
//                    )
//                }
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
            getString(R.string.night)
        } else if (current.hour >= 8 && current.hour < 14) {
            getString(R.string.morning)
        } else if (current.hour >= 14 && current.hour < 19) {
            getString(R.string.afternoon)
        } else {
            getString(R.string.evening)
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
                binding.timeInputPhase.setText(
                    Utils.numToPhase(
                        requireContext(),
                        viewModel.selectedPhase.value!!
                    )
                )
            }
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

                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmival = viewModel.wt_lb.value!! / Math.pow(
                        ((viewModel.ht_ft.value!! * 12 +
                                viewModel.ht_in.value!!)).toDouble(), 2.0
                    ) * 703
                    viewModel.setBmival(tf.format(bmival.toFloat()).replace(",", ".").toFloat())
                    viewModel.wttype = "lb"
                    viewModel.httype = "ftin"
                }

                if (binding.wtTab.getTabAt(0)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmival = viewModel.wt_lb.value!! * 0.453 / Math.pow(
                        viewModel.ht_cm.value!! * 0.01,
                        2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).replace(",", ".").toFloat())
                    viewModel.wttype = "lb"
                    viewModel.httype = "cm"
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(0)!!.isSelected
                ) {
                    val bmival = viewModel.wt_kg.value!! / Math.pow(
                        viewModel.ht_ft.value!! * 0.3048 + viewModel.ht_in.value!! * 0.0254, 2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).replace(",", ".").toFloat())
                    viewModel.wttype = "kg"
                    viewModel.httype = "ftin"
                }

                if (binding.wtTab.getTabAt(1)!!.isSelected &&
                    binding.htTab.getTabAt(1)!!.isSelected
                ) {
                    val bmival = viewModel.wt_kg.value!! / Math.pow(
                        viewModel.ht_cm.value!! * 0.01, 2.0
                    )
                    viewModel.setBmival(tf.format(bmival.toFloat()).replace(",", ".").toFloat())
                    viewModel.wttype = "kg"
                    viewModel.httype = "cm"
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

                if (isAdded) {
                    val childViewHeight = Utils.dip2px(requireContext(), CHILDVIEWSIZE.toFloat())
                    //获取中心元素到两侧的元素个数
                    Log.d(TAG, "rvWidth:${binding.ageRecyclerView.width}")
                    childViewHalfCount = (binding.ageRecyclerView.width / childViewHeight) / 2 + 1
                    Log.d(TAG, "$childViewHeight + $childViewHalfCount ")
                    initData()
                    findView()
                }


            }
        })
        //滑动之后100ms后移动到中心位置
        binding.ageRecyclerView.postDelayed({
            binding.ageRecyclerView.scrollToPosition(childViewHalfCount + 25)
            //scrollToCenter(28)
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
        viewModel.setAge(pos - 1)
        Log.d(TAG, "当前选中:${ageList.get(pos)}")
    }


}

