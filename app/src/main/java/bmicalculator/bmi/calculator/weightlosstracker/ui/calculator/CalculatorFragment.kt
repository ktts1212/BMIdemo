package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentCalculatorBinding
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat

private const val TAG = "CalculatorFragment"

private const val Mult_1 = 0.45359237

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
        val df = DecimalFormat("#.00")
        //用于身高保留一位小数
        var tf = DecimalFormat("#.0")

        var ff=DecimalFormat("#")
//        df.minimumFractionDigits = 2
//        df.maximumFractionDigits = 6
        binding.htInputFtin1.text = Editable.Factory.getInstance().newEditable("5" + "'")
        binding.htInputFtin2.text = Editable.Factory.getInstance().newEditable("7" + "'")
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
                    if (decimalnumber > 6) {
                        isChanged = true
                        val subStr = str.substring(0, str.indexOf(".") + 7)
                        binding.wtInput.setText(subStr)
                        binding.wtInput.setError("小数位数不能6位")
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

        var htfirstConvert = true
        binding.htTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.htCardFtin1.visibility = View.VISIBLE
                    binding.htCardFtin2.visibility = View.VISIBLE
                    binding.htCardCm.visibility = View.INVISIBLE
                    binding.htInputFtin1.setText(viewModel.ht_ft.value.toString() + "'")
                    binding.htInputFtin2.setText(viewModel.ht_ni.value.toString() + "'")
                } else {
                    binding.htCardFtin1.visibility = View.INVISIBLE
                    binding.htCardFtin2.visibility = View.INVISIBLE
                    binding.htCardCm.visibility = View.VISIBLE
                    binding.htInputCm.setText(viewModel.ht_cm.value.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    if (!binding.htInputFtin1.text.isNullOrEmpty() ||
                        !binding.htInputFtin2.text.isNullOrEmpty()) {
                        viewModel.sethtft(
                            ff.format(binding.htInputFtin1.text).toInt()
                        )
                        viewModel.sethtin(
                            ff.format(binding.htInputFtin2.text).toInt()
                        )
                    }
                    if ()

                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }
}

