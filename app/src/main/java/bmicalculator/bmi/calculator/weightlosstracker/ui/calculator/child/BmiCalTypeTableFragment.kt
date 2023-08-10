package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentBmiCalTypeTableBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.ChildBmiDialData

class BmiCalTypeTableFragment : DialogFragment() {

    private lateinit var binding: FragmentBmiCalTypeTableBinding

    private lateinit var viewModel: CalculatorViewModel

    private var scSize: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBmiCalTypeTableBinding.inflate(layoutInflater, container, false)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        getBmiType(viewModel.bmitype)
        if (viewModel.selectedAge.value!! > 20) {
            binding.typeTableChildDial.visibility = View.GONE
            binding.typeTableDial.visibility = View.VISIBLE
            binding.typeTableTipText2.visibility = View.INVISIBLE
            scSize = 0.75
        } else {
            val gender = if (viewModel.selectedGender.value!!.equals('0')) "Boy" else "Girl"

            binding.typeTableTipText1.setText("BMI for teenagers")
            binding.typeTableTipText2.setText(
                "${viewModel.selectedAge.value} years old (${gender})"
            )
            ChildBmiDialData.setData(
                viewModel.selectedAge.value!!,
                viewModel.selectedGender.value!!
            )
            binding.typeTableChildDial.getData(
                ChildBmiDialData.cScaleList,
                ChildBmiDialData.scaleRange
            )
            binding.typeTableChildDial.visibility = View.VISIBLE
            binding.typeTableDial.visibility = View.GONE
            binding.typeTable.bmiVerysevere.visibility = View.GONE
            binding.typeTable.bmiSevere.visibility = View.GONE
            binding.typeTable.bmiOb2.visibility = View.GONE
            binding.typeTable.bmiOb3.visibility = View.GONE

            val ls = ChildBmiDialData.cScaleList
            binding.typeTable.bmiUweightText2.setText("${ls[0]} - ${ls[1]}")
            binding.typeTable.bmiNormalText2.setText("${ls[1]} - ${ls[2]}")
            binding.typeTable.bmiOverweightText2.setText("${ls[2]} - ${ls[3]}")
            binding.typeTable.bmiOb1Text2.setText("${ls[3]} - ${ls[4]}")
            scSize = 0.6
        }

        binding.typeTableBtn.setOnClickListener {
            onDestroyView()
        }
    }


    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        val displayMetrics = resources.displayMetrics
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = (displayMetrics.heightPixels * scSize).toInt()
        params?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun getBmiType(bmitype: String) {
        when (bmitype) {
            "vsuw" -> {
                binding.typeTable.bmiVerysevereLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vsuw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiVerysevereImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiVerysevereText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiVerysevereText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "suw" -> {
                binding.typeTable.bmiSevereLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.suw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiSevereImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiSevereText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiSevereText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "uw" -> {
                binding.typeTable.bmiUweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiUweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiUweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiUweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "nm" -> {
                binding.typeTable.bmiNormalLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiNormalImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiNormalText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiNormalText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "ow" -> {
                binding.typeTable.bmiOverweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiOverweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiOverweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiOverweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "oc1" -> {
                binding.typeTable.bmiOb1Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiOb1Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiOb1Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiOb1Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "oc2" -> {
                binding.typeTable.bmiOb2Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc2
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiOb2Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiOb2Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiOb2Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }

            "oc3" -> {
                binding.typeTable.bmiOb3Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc3
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.typeTable.bmiOb3Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.typeTable.bmiOb3Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.typeTable.bmiOb3Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
            }
        }
    }

}