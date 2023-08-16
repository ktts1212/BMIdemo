package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentRecordBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.AdInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.util.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.util.SweepAngel
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gyf.immersionbar.ktx.immersionBar
import java.lang.RuntimeException
import kotlin.random.Random

private const val TAG = "RecordFragment"

class RecordFragment : DialogFragment() {

    private lateinit var binding: FragmentRecordBinding

    private lateinit var currentBmiInfo: BmiInfo

    private lateinit var viewModel: CalculatorViewModel

    private var mListener: OnDeleteTypeListener? = null

    interface OnDeleteTypeListener {
        fun onDeleteTypeAction(type: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDeleteTypeListener) {
            mListener = context
        } else {
            throw RuntimeException(
                context.toString() + "must implement OnDeleteTypeListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentBmiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("cBI", BmiInfo::class.java)!!
        } else {
            arguments?.getParcelable("cBI")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository, requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        binding = FragmentRecordBinding.inflate(layoutInflater, container, false)
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

        binding.recordBack.setOnClickListener {
            onDestroyView()
        }

        binding.rcTip.text = String.format(getString(R.string.bmi_tip_description), "...")

        binding.rcArrow.alpha = 0.75f
        binding.rcArrow.pivotX = Utils.dip2px(
            requireContext(), 16f
        ).toFloat()

        binding.rcArrow.pivotY = Utils.dip2px(
            requireContext(), 16f
        ).toFloat()

        if (currentBmiInfo.age > 20) {
            binding.rcChildDial.visibility = View.GONE
            binding.rcDial.visibility = View.VISIBLE
            binding.rcArrow.rotation = SweepAngel.sweepAngle(currentBmiInfo.bmi)
            binding.rcNum.text = DcFormat.tf!!.format(currentBmiInfo.bmi)
            getBmiType(currentBmiInfo)
            //getWtHtType(currentBmiInfo)

        } else {
            binding.rcChildDial.visibility = View.VISIBLE
            binding.rcDial.visibility = View.GONE
            ChildBmiDialData.setData(currentBmiInfo.age, currentBmiInfo.gender)
            binding.rcChildDial.getData(ChildBmiDialData.cScaleList, ChildBmiDialData.scaleRange)
            binding.rcArrow.rotation = SweepAngel.childSweepAngle(currentBmiInfo.bmi)
            binding.rcNum.text = DcFormat.tf!!.format(currentBmiInfo.bmi)
            getBmiType(currentBmiInfo)
            //getWtHtType(currentBmiInfo)

        }
        binding.recordTime.text =
            "${currentBmiInfo.date} ${Utils.numToPhase(requireContext(), currentBmiInfo.phase)}"
        adShow(currentBmiInfo.bmi)

        binding.recordDel.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.tiktok_delete_confirm))
                setMessage(getString(R.string.delete_history_record_content))
                setCancelable(false)
                setPositiveButton(getString(R.string.delete)) { _, _ ->
                    viewModel.deleteBmiInfo(currentBmiInfo)
                    currentBmiInfo.bmiType?.let { it1 -> mListener?.onDeleteTypeAction(it1) }
                    onDestroyView()
                }

                setNegativeButton(getString(R.string.cancel)) { _, _ ->

                }
                show()
            }
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
            transparentStatusBar()
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun getBmiType(bmiInfo: BmiInfo) {

        val sGender = if (bmiInfo.gender == '0') getString(R.string.male)
        else getString(R.string.female)

        val minWt: Double
        val maxWt: Double
        val bmiCalInfo = when (bmiInfo.wtHtType) {
            "lbftin" -> {
                minWt = Utils.minWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
                maxWt = Utils.maxWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf!!.format(bmiInfo.wt_lb)} lb",
                    "${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} in", sGender, "${bmiInfo.age}"
                )
            }

            "lbcm" -> {
                minWt = Utils.minCmtolb(bmiInfo.ht_cm)
                maxWt = Utils.maxCmtolb(bmiInfo.ht_cm)

                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf!!.format(bmiInfo.wt_lb)} lb",
                    "${DcFormat.tf!!.format(bmiInfo.ht_cm)} cm", sGender, "${bmiInfo.age}"
                )
            }

            "kgftin" -> {
                minWt = Utils.minWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)
                maxWt = Utils.maxWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)

                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf!!.format(bmiInfo.wt_kg)} kg",
                    "${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} in", sGender, "${bmiInfo.age}"
                )
            }

            else -> {
                minWt = Utils.minCmtokg(bmiInfo.ht_cm)
                maxWt = Utils.maxCmtokg(bmiInfo.ht_cm)
                String.format(
                    getString(R.string.bmi_input_data), "${DcFormat.tf!!.format(bmiInfo.wt_kg)} kg",
                    "${DcFormat.tf!!.format(bmiInfo.ht_cm)} cm", sGender, "${bmiInfo.age}"
                )
            }
        }

        binding.rcCalInfo.text = bmiCalInfo

        val wtType = bmiInfo.wtHtType!!.substring(0, 2)

        val wtRange =
            "${DcFormat.tf?.format(minWt)} $wtType - ${DcFormat.tf?.format(maxWt)} $wtType"

        val wtChaZhi = if (bmiInfo.age > 20) {
            if (wtType == "kg") {
                if (bmiInfo.bmi < 18.5) {
                    minWt - bmiInfo.wt_kg
                } else {
                    bmiInfo.wt_kg - maxWt
                }
            } else {
                if (bmiInfo.bmi < 18.5) {
                    minWt - bmiInfo.wt_lb
                } else {
                    bmiInfo.wt_lb - maxWt
                }
            }
        } else {

            if (wtType == "kg") {
                if (bmiInfo.bmi < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minWt - bmiInfo.wt_kg
                } else {
                    bmiInfo.wt_kg - maxWt
                }
            } else {
                if (bmiInfo.bmi < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minWt - bmiInfo.wt_lb
                } else {
                    bmiInfo.wt_lb - maxWt
                }
            }
        }

        when (bmiInfo.bmiType) {
            "vsuw" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_very_severely_underweight)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.vsuw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.vsuw
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_very_severely_underweight_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest1)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                    binding.rcWtRange.text = wtRange
                    binding.rcWtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
                }

            }

            "suw" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_severely_underweight)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.suw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.suw
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_severely_underweight_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest2)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                    binding.rcWtRange.text = wtRange
                    binding.rcWtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
                }
            }

            "uw" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_underweight)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.uw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.uw
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_underweight_adult_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest3)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                } else {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_underweight_child_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.child_suggest1)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                }
                binding.rcWtRange.text = wtRange
                binding.rcWtChazhi.text = "(+${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
            }

            "nm" -> {
                binding.rcCalTypeText.text = getString(R.string.normal_leg)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.normal)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.normal
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_normal_adult_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest4)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString
                        )
                } else {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_normal_child_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.child_suggest2)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString,
                        )
                }
            }

            "ow" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_overweight)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.ow)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.ow
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_overweight_adult_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest5)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                } else {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_overweight_child_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.child_suggest3)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                }
                binding.rcWtRange.text = wtRange
                binding.rcWtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
            }

            "oc1" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_range_obese_class1)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.oc1)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.oc1
                        )
                    )
                )

                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_obeseClassI_adult_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest6)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                } else {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_obeseClassI_child_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.child_suggest4)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                }
                binding.rcWtRange.text = wtRange
                binding.rcWtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
            }

            "oc2" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_range_obese_class2)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.oc2)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.oc2
                        )
                    )
                )

                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_obeseClassII_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest7)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                }
                binding.rcWtRange.text = wtRange
                binding.rcWtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
            }

            "oc3" -> {
                binding.rcCalTypeText.text = getString(R.string.bmi_range_obese_class3)
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.oc3)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), R.color.oc3
                        )
                    )
                )
                if (bmiInfo.age > 20) {
                    val spannableString = SpannableStringBuilder(
                        "  " +
                                getString(R.string.bmi_range_obeseClassIII_description)
                    )
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adult_suggest8)
                    drawable?.setBounds(
                        0, 0, Utils.dip2px(requireContext(), 16f), Utils.dip2px(
                            requireContext(), 16f
                        )
                    )
                    val imageSpan = ImageSpan(drawable!!, DynamicDrawableSpan.ALIGN_BOTTOM)
                    spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

                    binding.rcCalSuggest.text =
                        TextUtils.concat(
                            spannableString, "\n\n", String.format(
                                getString(R.string.bmi_result_suggest_start),
                                if (viewModel.htType == "cm") viewModel.htCm.value.toString() + "cm"
                                else viewModel.htFt.value.toString() + "ft ${viewModel.htIn.value}in"
                            )
                        )
                }
                binding.rcWtRange.text = wtRange
                binding.rcWtChazhi.text = "(-${DcFormat.tf?.format(wtChaZhi)} ${wtType})"
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun adShow(bmiVal: Float) {
        if ((viewModel.selectedAge.value!! > 20 && bmiVal < 18.5) ||
            (viewModel.selectedAge.value!! <= 20 && bmiVal < ChildBmiDialData.cScaleList[1].toDouble())
        ) {
            val num1 = Random.nextInt(6, 9)
            var adInfo = chooseAd(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd1Iv1)
            binding.rcAd1Tv2.text = getString(adInfo.appLink)
            binding.rcAd1Tv1.text = getString(adInfo.appName)
            binding.rcAd1Tv3.text = getString(adInfo.appScore)

            var num2: Int
            do {
                num2 = Random.nextInt(6, 9)
            } while (num2 == num1)
            adInfo = chooseAd(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd2Iv1)
            binding.rcAd2Tv1.text = getString(adInfo.appName)
            binding.rcAd2Tv3.text = getString(adInfo.appScore)
            binding.rcAd2Tv2.text = getString(adInfo.appLink)

            val numList = listOf(5, 9, 10)
            val randomIndex = Random.nextInt(numList.size)
            val num3 = numList[randomIndex]
            adInfo = chooseAd(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd3Iv1)
            binding.rcAd3Tv1.text = getString(adInfo.appName)
            binding.rcAd3Tv3.text = getString(adInfo.appScore)
            binding.rcAd3Tv2.text = getString(adInfo.appLink)
        } else {
            val numbers = if (currentBmiInfo.age.equals('0'))
                mutableListOf(2, 3, 6, 7, 8) else mutableListOf(1, 3, 6, 7, 8)
            var randomIndex = Random.nextInt(numbers.size)
            val num1 = numbers[randomIndex]
            var adInfo = chooseAd(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd1Iv1)

            binding.rcAd1Tv1.text = getString(adInfo.appName)
            binding.rcAd1Tv2.text = getString(adInfo.appLink)
            binding.rcAd1Tv3.text = getString(adInfo.appScore)
            numbers.removeAt(randomIndex)

            randomIndex = Random.nextInt(numbers.size)
            val num2 = numbers[randomIndex]
            adInfo = chooseAd(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd2Iv1)
            binding.rcAd2Tv1.text = getString(adInfo.appName)
            binding.rcAd2Tv2.text = getString(adInfo.appLink)
            binding.rcAd2Tv3.text = getString(adInfo.appScore)

            numbers.clear()
            numbers.addAll(listOf(4, 5, 9, 10))
            randomIndex = Random.nextInt(numbers.size)
            val num3 = numbers[randomIndex]
            adInfo = chooseAd(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.rcAd3Iv1)
            binding.rcAd3Tv1.text = getString(adInfo.appName)
            binding.rcAd3Tv3.text = getString(adInfo.appScore)
            binding.rcAd3Tv2.text = getString(adInfo.appLink)
        }
    }

    private fun chooseAd(num: Int): AdInfo {
        when (num) {
            1 -> {
                return AdInfo(
                    R.drawable.ad1,
                    R.string.app_recommend_women,
                    "",
                    R.string.ad1_url,
                    R.string.ad1_score
                )
            }

            2 -> {
                return AdInfo(
                    R.drawable.ad2,
                    R.string.app_recommend_men,
                    "",
                    R.string.ad2_url,
                    R.string.ad2_score
                )
            }

            3 -> {
                return AdInfo(
                    R.drawable.ad3,
                    R.string.app_recommend_lose_weight,
                    "",
                    R.string.ad3_url,
                    R.string.ad3_score
                )
            }

            4 -> {
                return AdInfo(
                    R.drawable.ad4,
                    R.string.app_recommend_fasting,
                    "",
                    R.string.ad4_url,
                    R.string.ad4_score
                )
            }

            5 -> {
                return AdInfo(
                    R.drawable.ad5,
                    R.string.app_recommend_walking,
                    "",
                    R.string.ad5_url,
                    R.string.ad5_score
                )
            }

            6 -> {
                return AdInfo(
                    R.drawable.ad6,
                    R.string.app_recommend_home,
                    "",
                    R.string.ad6_url,
                    R.string.ad6_score
                )
            }

            7 -> {
                return AdInfo(
                    R.drawable.ad7,
                    R.string.app_recommend_30_day,
                    "",
                    R.string.ad7_url,
                    R.string.ad7_score
                )
            }

            8 -> {
                return AdInfo(
                    R.drawable.ad8,
                    R.string.app_recommend_six_pack,
                    "",
                    R.string.ad8_url,
                    R.string.ad8_score
                )
            }

            9 -> {
                return AdInfo(
                    R.drawable.ad9,
                    R.string.app_recommend_step_tracker,
                    "",
                    R.string.ad9_url,
                    R.string.ad9_score
                )
            }

            else -> {
                return AdInfo(
                    R.drawable.ad10,
                    R.string.app_recommend_blood_pressure_monitor,
                    "",
                    R.string.ad10_url,
                    R.string.ad10_score
                )
            }
        }
    }

}