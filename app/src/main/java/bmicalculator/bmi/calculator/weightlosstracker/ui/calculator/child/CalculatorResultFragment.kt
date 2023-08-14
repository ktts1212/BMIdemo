package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentCalculatorResultBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.AdInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.ui.statistic.StatisticFragment
import bmicalculator.bmi.calculator.weightlosstracker.util.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.util.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ktx.immersionBar
import java.time.LocalTime
import kotlin.random.Random

class CalculatorResultFragment : DialogFragment() {

    private lateinit var binding: FragmentCalculatorResultBinding

    private lateinit var viewModel: CalculatorViewModel

    private var hasData:Boolean=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculatorResultBinding.inflate(layoutInflater, container, false)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository, requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        hasData =
            (activity as AppCompatActivity).getSharedPreferences("hasData", Context.MODE_PRIVATE)
                .getBoolean("hasData", false)
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


        binding.bmiTip.text = String.format(getString(R.string.bmi_tip_description), "...")


        viewModel.bmiVal.observe(requireActivity()) {
            binding.bmiNum.text =
                DcFormat.tf?.format(viewModel.bmiVal.value)
        }

        binding.bmiArrow.pivotX = Utils.dip2px(
            requireContext(),
            15f
        ).toFloat()
        binding.bmiArrow.pivotY = Utils.dip2px(
            requireContext(),
            15f
        ).toFloat()

        binding.calDiscard.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.tiktok_delete_confirm))
                setMessage(getString(R.string.delete_history_record_content))
                setCancelable(false)
                setPositiveButton(getString(R.string.delete)) { _, _ ->
                    onDestroyView()
                }

                setNegativeButton(getString(R.string.action_cancel)) { _, _ ->

                }
                show()
            }
        }

        viewModel.allInfo.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) {
                binding.bmiTypeTip.visibility = View.VISIBLE
                binding.bmiCalType.visibility = View.GONE
                binding.bmiAdLayout.visibility = View.VISIBLE
            } else {
                binding.bmiTypeTip.visibility = View.GONE
                binding.bmiCalType.visibility = View.VISIBLE
                binding.bmiAdLayout.visibility = View.GONE
            }
        }

        binding.bmiCalTypeCard.setOnClickListener {
            if (hasData) {
                val dialog = BmiCalTypeTableFragment()
                dialog.show(childFragmentManager, "TypeTable")
            }
        }

        binding.bmiArrow.alpha = 0.75f

        //根据bmiVal判断
        val bVal = viewModel.bmiVal.value


        //设置textDisplay
        val sGender = if (viewModel.selectedGender.value!! == '0') "Male"
        else "Female"
        val selectType = viewModel.wtType + viewModel.htType

        ChildBmiDialData.setData(
            viewModel.selectedAge.value!!,
            viewModel.selectedGender.value!!
        )

        Utils.initData(viewModel.selectedAge.value!!)

        val genderString = if (sGender == "Male") getString(R.string.male)
        else getString(R.string.female)
        val minWt: Double
        val maxWt: Double
        val bmiCalInfo = when (selectType) {
            "lbftin" -> {
                minWt = Utils.minWtftintolb(viewModel.htFt.value!!, viewModel.htIn.value!!)
                maxWt = Utils.maxWtftintolb(viewModel.htFt.value!!, viewModel.htIn.value!!)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf?.format(viewModel.wtLb.value)} lb",
                    "${DcFormat.ff?.format(viewModel.htFt.value)} ft " +
                            "${DcFormat.ff?.format(viewModel.htIn.value)} in",
                    genderString, "${viewModel.selectedAge.value}"
                )
            }

            "lbcm" -> {
                minWt = Utils.minCmtolb(viewModel.htCm.value!!)
                maxWt = Utils.maxCmtolb(viewModel.htCm.value!!)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf?.format(viewModel.wtLb.value)} lb",
                    "${DcFormat.tf?.format(viewModel.htCm.value)} cm",
                    genderString, "${viewModel.selectedAge.value}"
                )
            }

            "kgftin" -> {
                minWt = Utils.minWtftintokg(viewModel.htFt.value!!, viewModel.htIn.value!!)
                maxWt = Utils.maxWtftintokg(viewModel.htFt.value!!, viewModel.htIn.value!!)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf?.format(viewModel.wtKg.value!!)} kg",
                    "${DcFormat.ff?.format(viewModel.htFt.value)} ft " +
                            "${DcFormat.ff?.format(viewModel.htIn.value)} in",
                    genderString, "${viewModel.selectedAge.value}"
                )
            }

            else -> {
                minWt = Utils.minCmtokg(viewModel.htCm.value!!)
                maxWt = Utils.maxCmtokg(viewModel.htCm.value!!)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf?.format(viewModel.wtKg.value)} kg",
                    "${DcFormat.tf?.format(viewModel.htCm.value)} cm",
                    genderString, "${viewModel.selectedAge.value}"
                )
            }
        }

        binding.bmiCalInfo.text = bmiCalInfo

        val wtRange =
            "${DcFormat.tf?.format(minWt)} ${viewModel.wtType} - " +
                    "${DcFormat.tf?.format(maxWt)} ${viewModel.wtType}"

        val wtChaZhi = if (viewModel.selectedAge.value!! > 20) {
            if (viewModel.wtType == "kg") {
                if (bVal!! < 18.5) {
                    minWt - viewModel.wtKg.value!!
                } else {
                    viewModel.wtKg.value!! - maxWt
                }
            } else {
                if (bVal!! < 18.5) {
                    minWt - viewModel.wtLb.value!!
                } else {
                    viewModel.wtLb.value!! - maxWt
                }
            }
        } else {

            if (viewModel.wtType == "kg") {
                if (bVal!! < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minWt - viewModel.wtKg.value!!
                } else {
                    viewModel.wtKg.value!! - maxWt
                }
            } else {
                if (bVal!! < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minWt - viewModel.wtLb.value!!
                } else {
                    viewModel.wtLb.value!! - maxWt
                }
            }
        }

        if (viewModel.localeLanguage !in DcFormat.enList){
            binding.newac.bmiVerysevereText2.text=
                binding.newac.bmiVerysevereText2.text.toString().replace(".",",")
            binding.newac.bmiSevereText2.text=
                binding.newac.bmiSevereText2.text.toString().replace(".",",")
            binding.newac.bmiUweightText2.text=
                binding.newac.bmiUweightText2.text.toString().replace(".",",")
            binding.newac.bmiNormalText2.text=
                binding.newac.bmiNormalText2.text.toString().replace(".",",")
            binding.newac.bmiOverweightText2.text=
                binding.newac.bmiOverweightText2.text.toString().replace(".",",")
            binding.newac.bmiOb1Text2.text=
                binding.newac.bmiOb1Text2.text.toString().replace(".",",")
            binding.newac.bmiOb2Text2.text=
                binding.newac.bmiOb2Text2.text.toString().replace(".",",")
            binding.newac.bmiOb3Text2.text=
                binding.newac.bmiOb3Text2.text.toString().replace(".",",")
        }

        if (viewModel.selectedAge.value!! > 20) {
            binding.bmiChildDial.visibility = View.GONE
            binding.bmiDial.visibility = View.VISIBLE
            binding.bmiArrow.rotation = sweepAngle(viewModel.bmiVal.value!!.toFloat())

            if (bVal < 16) {
                viewModel.bmiType = "vsuw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vsuw
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_very_severely_underweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.vsuw
                        )
                    )
                )
                binding.newac.bmiVerysevereLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vsuw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiVerysevereImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiVerysevereText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiVerysevereText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                val spannableString=SpannableStringBuilder("  "+
                        getString(R.string.bmi_range_very_severely_underweight_description)
                )
                val drawable=ContextCompat.getDrawable(requireContext(),R.drawable.adult_suggest1)
                drawable?.setBounds(0,0,Utils.dip2px(requireContext(),16f),Utils.dip2px(
                    requireContext(),16f
                ))
                val imageSpan=ImageSpan(drawable!!,DynamicDrawableSpan.ALIGN_BOTTOM)
                spannableString.setSpan(imageSpan,0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                binding.bmiCalSuggest.text =
                    TextUtils.concat(
                        spannableString,"/n/n",String.format(
                            getString(R.string.bmi_result_suggest_start),
                            if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                            else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                        )
                    )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal >= 16 && bVal < 17) {
                viewModel.bmiType = "suw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.suw
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_severely_underweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.suw
                        )
                    )
                )
                binding.newac.bmiSevereLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.suw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiSevereImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiSevereText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiSevereText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                val spannableString=SpannableStringBuilder("  "+
                        getString(R.string.bmi_range_severely_underweight_description)
                )
                val drawable=ContextCompat.getDrawable(requireContext(),R.drawable.adult_suggest2)
                drawable?.setBounds(0,0,Utils.dip2px(requireContext(),16f),Utils.dip2px(
                    requireContext(),16f
                ))
                val imageSpan=ImageSpan(drawable!!,DynamicDrawableSpan.ALIGN_BOTTOM)
                spannableString.setSpan(imageSpan,0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                binding.bmiCalSuggest.text =
                    TextUtils.concat(
                        spannableString,"/n/n",String.format(
                            getString(R.string.bmi_result_suggest_start),
                            if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                            else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                        )
                    )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal >= 17 && bVal < 18.5) {
                viewModel.bmiType = "uw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_underweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.uw
                        )
                    )
                )
                binding.newac.bmiUweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiUweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiUweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiUweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }

                val spannableString=SpannableStringBuilder("  "+
                    getString(R.string.bmi_range_underweight_adult_description)
                )
                val drawable=ContextCompat.getDrawable(requireContext(),R.drawable.adult_suggest3)
                drawable?.setBounds(0,0,Utils.dip2px(requireContext(),16f),Utils.dip2px(
                    requireContext(),16f
                ))
                val imageSpan=ImageSpan(drawable!!,DynamicDrawableSpan.ALIGN_BOTTOM)
                spannableString.setSpan(imageSpan,0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                binding.bmiCalSuggest.text =
                    TextUtils.concat(
                        spannableString,"/n/n",String.format(
                            getString(R.string.bmi_result_suggest_start),
                            if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                            else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                        )
                    )

                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal >= 18.5 && bVal < 25) {
                viewModel.bmiType = "nm"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.normal_leg)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.normal
                        )
                    )
                )
                binding.newac.bmiNormalLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiNormalImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiNormalText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiNormalText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.setText(R.string.bmi_range_normal_adult_description)
            } else if (bVal >= 25 && bVal < 30) {
                viewModel.bmiType = "ow"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_overweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.ow
                        )
                    )
                )
                binding.newac.bmiOverweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOverweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOverweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOverweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_overweight_adult_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal >= 30 && bVal < 35) {
                viewModel.bmiType = "oc1"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_range_obese_class1)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc1
                        )
                    )
                )
                binding.newac.bmiOb1Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOb1Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOb1Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOb1Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_obeseClassI_adult_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal >= 35 && bVal < 40) {
                viewModel.bmiType = "oc2"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc2
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_range_obese_class2)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc2
                        )
                    )
                )
                binding.newac.bmiOb2Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc2
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOb2Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOb2Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOb2Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_obeseClassII_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else {
                viewModel.bmiType = "oc3"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc3
                    )
                )
                binding.bmiCalTypeText.text = getString(R.string.bmi_range_obese_class3)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc3
                        )
                    )
                )
                binding.newac.bmiOb3Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc3
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOb3Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOb3Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOb3Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_obeseClassIII_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            }
        } else {
            binding.bmiDial.visibility = View.GONE
            binding.bmiChildDial.visibility = View.VISIBLE
            binding.bmiChildDial.getData(ChildBmiDialData.cScaleList, ChildBmiDialData.scaleRange)
            binding.bmiArrow.rotation = childSweepAngle(viewModel.bmiVal.value!!.toFloat())

            binding.newac.bmiVerysevere.visibility = View.GONE
            binding.newac.bmiSevere.visibility = View.GONE
            binding.newac.bmiOb2.visibility = View.GONE
            binding.newac.bmiOb3.visibility = View.GONE
            val list = ChildBmiDialData.cScaleList
            binding.newac.bmiUweightText2.text = "${list[0]} - ${list[1]}"
            binding.newac.bmiNormalText2.text = "${list[1]} - ${list[2]}"
            binding.newac.bmiOverweightText2.text = "${list[2]} - ${list[3]}"
            binding.newac.bmiOb1Text2.text = "${list[3]} - ${list[4]}"

            if (bVal < ChildBmiDialData.cScaleList[1].toFloat()) {
                viewModel.bmiType = "uw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                binding.bmiCalTypeText.setText(R.string.bmi_underweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.uw
                        )
                    )
                )
                binding.newac.bmiUweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiUweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiUweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiUweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_underweight_child_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else if (bVal < ChildBmiDialData.cScaleList[2].toFloat() &&
                bVal >= ChildBmiDialData.cScaleList[1].toFloat()
            ) {
                viewModel.bmiType = "nm"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                binding.bmiCalTypeText.setText(R.string.normal_leg)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.normal
                        )
                    )
                )
                binding.newac.bmiNormalLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiNormalImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiNormalText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiNormalText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text = getString(R.string.bmi_range_normal_child_description)

            } else if (bVal < ChildBmiDialData.cScaleList[3].toFloat() &&
                bVal >= ChildBmiDialData.cScaleList[2].toFloat()
            ) {
                viewModel.bmiType = "ow"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                binding.bmiCalTypeText.setText(R.string.bmi_overweight)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.ow
                        )
                    )
                )
                binding.newac.bmiOverweightLayout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOverweightImage, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOverweightText1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOverweightText2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_overweight_child_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            } else {

                viewModel.bmiType = "oc1"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                binding.bmiCalTypeText.setText(R.string.bmi_range_obese_class1)
                ViewCompat.setBackgroundTintList(
                    binding.bmiTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc1
                        )
                    )
                )
                binding.newac.bmiOb1Layout.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                ViewCompat.setBackgroundTintList(
                    binding.newac.bmiOb1Image, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                )
                binding.newac.bmiOb1Text1.apply {
                    setTextColor(Color.WHITE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        typeface = Typeface.create(
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                            800,
                            false
                        )
                    }
                }
                binding.newac.bmiOb1Text2.apply {
                    setTextColor(Color.WHITE)
                    typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                }
                binding.bmiCalSuggest.text =
                    getString(R.string.bmi_range_obeseClassI_child_description) + "\n\n" +
                            String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                binding.wtRange.text = wtRange
                binding.wtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${viewModel.wtType})"
            }
        }

        adShow(bVal)

        binding.bmiAd1.setOnClickListener {
            try {
                val playStoreUri = Uri.parse(
                    "market://details?id=${binding.bmiAd1Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                startActivity(intent)
            } catch (e: Exception) {
                val playStoreWebUri = Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd1Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.bmiAd2.setOnClickListener {
            try {
                val playStoreUri = Uri.parse(
                    "market://details?id=${binding.bmiAd2Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                startActivity(intent)
            } catch (e: Exception) {
                val playStoreWebUri = Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd2Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.bmiAd3.setOnClickListener {
            try {
                val playStoreUri = Uri.parse(
                    "market://details?id=${binding.bmiAd3Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
                startActivity(intent)
            } catch (e: Exception) {
                val playStoreWebUri = Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd3Tv2.text}"
                )
                val intent = Intent(Intent.ACTION_VIEW, playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.saveBtn.setOnClickListener {
            val now = LocalTime.now()
            val secondsPastMidnight = now.toSecondOfDay()
            val bmiInfo = BmiInfo(
                viewModel.wtLb.value!!.toDouble(),
                viewModel.wtKg.value!!.toDouble(),
                viewModel.htFt.value!!.toInt(), viewModel.htIn.value!!.toInt(),
                viewModel.htCm.value!!.toDouble(),
                viewModel.selectedDate.value, viewModel.selectedPhase.value!!,
                viewModel.selectedAge.value!!.toInt(),
                viewModel.selectedGender.value!!.toChar(),
                viewModel.bmiVal.value!!,
                secondsPastMidnight,
                viewModel.selectedDate.value!!.substring(
                    viewModel.selectedDate.value!!.length - 4
                ).toInt(),
                viewModel.bmiType,
                viewModel.wtType + viewModel.htType,
                System.currentTimeMillis()
            )
            viewModel.insertInfo(bmiInfo)
            viewModel.bmiNewRecord.value = bmiInfo
            val navView =
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId = R.id.menu_statistics
            val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
            val transition = fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container, StatisticFragment())
            transition.commit()
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
        (activity as AppCompatActivity).supportActionBar?.hide()
        immersionBar {
            transparentStatusBar()
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun sweepAngle(num: Float): Float {
        val bmiVal = if (num > 40.3) 40.3f else if (num < 15.6) 15.6f else num
        return DcFormat.tf!!.format((bmiVal - 15.6) / 24.8 * 180 + 90)
            .replace(",", ".").toFloat()
    }

    private fun childSweepAngle(num: Float): Float {
        val maxB = ChildBmiDialData.cScaleList[4].toFloat()
        val minB = ChildBmiDialData.cScaleList[0].toFloat()
        val bmiVal = if (num > maxB) maxB else if (num < minB) minB else num
        return DcFormat.tf!!.format((bmiVal - minB) / (maxB - minB) * 180 + 90).replace(",", ".")
            .toFloat()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun adShow(bmiVal: Float) {
        if ((viewModel.selectedAge.value!! > 20 && bmiVal < 18.5) ||
            (viewModel.selectedAge.value!! <= 20 && bmiVal < ChildBmiDialData.cScaleList[1].toDouble())
        ) {
//            binding.bmiAd1Tv2.visibility = View.GONE
//            binding.bmiAd2Tv2.visibility = View.GONE
//            binding.bmiAd3Tv2.visibility = View.GONE
            val num1 = Random.nextInt(6, 9)
            var adInfo = chooseAd(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd1Iv1)
            binding.bmiAd1Tv2.text = getString(adInfo.appLink)
            binding.bmiAd1Tv1.text = getString(adInfo.appName)
            binding.bmiAd1Tv3.text = getString(adInfo.appScore)

            var num2: Int
            do {
                num2 = Random.nextInt(6, 9)
            } while (num2 == num1)
            adInfo = chooseAd(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd2Iv1)
            binding.bmiAd2Tv1.text = getString(adInfo.appName)
            binding.bmiAd2Tv3.text = getString(adInfo.appScore)
            binding.bmiAd2Tv2.text = getString(adInfo.appLink)

            val numList = listOf(5, 9, 10)
            val randomIndex = Random.nextInt(numList.size)
            val num3 = numList[randomIndex]
            adInfo = chooseAd(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd3Iv1)
            binding.bmiAd3Tv1.text = getString(adInfo.appName)
            binding.bmiAd3Tv3.text = getString(adInfo.appScore)
            binding.bmiAd3Tv2.text = getString(adInfo.appLink)
        } else {
            val numbers = if (viewModel.selectedGender.value!! == '0')
                mutableListOf(2, 3, 6, 7, 8) else mutableListOf(1, 3, 6, 7, 8)
            var randomIndex = Random.nextInt(numbers.size)
            val num1 = numbers[randomIndex]
            var adInfo = chooseAd(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd1Iv1)

            binding.bmiAd1Tv1.text = getString(adInfo.appName)
            binding.bmiAd1Tv2.text = getString(adInfo.appLink)
            binding.bmiAd1Tv3.text = getString(adInfo.appScore)
            numbers.removeAt(randomIndex)

            randomIndex = Random.nextInt(numbers.size)
            val num2 = numbers[randomIndex]
            adInfo = chooseAd(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd2Iv1)
            binding.bmiAd2Tv1.text = getString(adInfo.appName)
            binding.bmiAd2Tv2.text = getString(adInfo.appLink)
            binding.bmiAd2Tv3.text = getString(adInfo.appScore)

            numbers.clear()
            numbers.addAll(listOf(4, 5, 9, 10))
            randomIndex = Random.nextInt(numbers.size)
            val num3 = numbers[randomIndex]
            adInfo = chooseAd(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd3Iv1)
            binding.bmiAd3Tv1.text = getString(adInfo.appName)
            binding.bmiAd3Tv3.text = getString(adInfo.appScore)
            binding.bmiAd3Tv2.text = getString(adInfo.appLink)
        }
    }

    private fun chooseAd(num: Int): AdInfo {
        when (num) {
            1 -> {
                return AdInfo(
                    R.drawable.ad1,
                    R.string.ad1,
                    "",
                    R.string.ad1_url,
                    R.string.ad1_score
                )
            }

            2 -> {
                return AdInfo(
                    R.drawable.ad2,
                    R.string.ad2,
                    "",
                    R.string.ad2_url,
                    R.string.ad2_score
                )
            }

            3 -> {
                return AdInfo(
                    R.drawable.ad3,
                    R.string.ad3,
                    "",
                    R.string.ad3_url,
                    R.string.ad3_score
                )
            }

            4 -> {
                return AdInfo(
                    R.drawable.ad4,
                    R.string.ad4,
                    "",
                    R.string.ad4_url,
                    R.string.ad4_score
                )
            }

            5 -> {
                return AdInfo(
                    R.drawable.ad5,
                    R.string.ad5,
                    "",
                    R.string.ad5_url,
                    R.string.ad5_score
                )
            }

            6 -> {
                return AdInfo(
                    R.drawable.ad6,
                    R.string.ad6,
                    "",
                    R.string.ad6_url,
                    R.string.ad6_score
                )
            }

            7 -> {
                return AdInfo(
                    R.drawable.ad7,
                    R.string.ad7,
                    "",
                    R.string.ad7_url,
                    R.string.ad7_score
                )
            }

            8 -> {
                return AdInfo(
                    R.drawable.ad8,
                    R.string.ad8,
                    "",
                    R.string.ad8_url,
                    R.string.ad8_score
                )
            }

            9 -> {
                return AdInfo(
                    R.drawable.ad9,
                    R.string.ad9,
                    "",
                    R.string.ad9_url,
                    R.string.ad9_score
                )
            }

            else -> {
                return AdInfo(
                    R.drawable.ad10,
                    R.string.ad10,
                    "",
                    R.string.ad10_url,
                    R.string.ad10_score
                )
            }
        }
    }
}