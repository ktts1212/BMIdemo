package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.util.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.util.SweepAngel
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.gyf.immersionbar.ktx.immersionBar

private const val TAG="RecordFragment"
class RecordFragment : DialogFragment() {

    private lateinit var binding: FragmentRecordBinding

    private lateinit var currentBmiInfo: BmiInfo

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentBmiInfo = arguments?.getParcelable("cBI", BmiInfo::class.java)!!
        } else {
            currentBmiInfo = arguments?.getParcelable<BmiInfo>("cBI")!!
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository,requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)
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

        binding.rcTip.setText(String.format(getString(R.string.bmi_tip_description),"..."))

        binding.rcArrow.alpha = 0.75f
        binding.rcArrow.pivotX = Utils.dip2px(
            requireContext(), 18f
        ).toFloat()

        binding.rcArrow.pivotY = Utils.dip2px(
            requireContext(), 18f
        ).toFloat()

        if (currentBmiInfo.age > 20) {
            binding.rcChildDial.visibility = View.GONE
            binding.rcDial.visibility = View.VISIBLE
            binding.rcArrow.rotation = SweepAngel.sweepAngle(currentBmiInfo.bmi)
            binding.rcNum.setText(currentBmiInfo.bmi.toString())
            getBmiType(currentBmiInfo)
            //getWtHtType(currentBmiInfo)

        } else {
            binding.rcChildDial.visibility = View.VISIBLE
            binding.rcDial.visibility = View.GONE
            ChildBmiDialData.setData(currentBmiInfo.age, currentBmiInfo.gender)
            binding.rcChildDial.getData(ChildBmiDialData.cScaleList, ChildBmiDialData.scaleRange)
            binding.rcArrow.rotation = SweepAngel.childSweepAngle(currentBmiInfo.bmi)
            binding.rcNum.setText(currentBmiInfo.bmi.toString())
            getBmiType(currentBmiInfo)
            //getWtHtType(currentBmiInfo)

        }
        binding.recordTime.setText("${currentBmiInfo.date} ${currentBmiInfo.phase}")


        binding.recordDel.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle(getString(R.string.tiktok_delete_confirm))
                setMessage(getString(R.string.delete_history_record_content))
                setCancelable(false)
                setPositiveButton(getString(R.string.delete)) { dialog, which ->
                    viewModel.deleteBmiInfo(currentBmiInfo)
                    onDestroyView()
                }

                setNegativeButton(getString(R.string.cancel)) { dialog, which ->

                }
                show()
            }
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
        immersionBar {
            transparentStatusBar()
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    @SuppressLint("SetTextI18n")
    fun getBmiType( bmiInfo: BmiInfo) {

        val sgender = if (bmiInfo.gender == '0') getString(R.string.male)
        else getString(R.string.female)

        var minwt: Double = 0.0
        var maxwt: Double = 0.0
        val bmiCalInfo = if (bmiInfo.wtHtType == "lbftin") {
            minwt = Utils.minWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
            maxwt = Utils.maxWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
            String.format(getString(R.string.bmi_input_data),"${bmiInfo.wt_lb} lb",
                "${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} in",sgender,"${bmiInfo.age}"
                )
        } else if (bmiInfo.wtHtType == "lbcm") {
            minwt = Utils.minCmtolb(bmiInfo.ht_cm)
            maxwt = Utils.maxCmtolb(bmiInfo.ht_cm)

            String.format(getString(R.string.bmi_input_data),"${bmiInfo.wt_lb} lb",
                "${bmiInfo.ht_cm} cm",sgender,"${bmiInfo.age}"
            )
        } else if (bmiInfo.wtHtType == "kgftin") {
            minwt = Utils.minWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)
            maxwt = Utils.maxWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)

            String.format(getString(R.string.bmi_input_data),"${bmiInfo.wt_kg} kg",
                "${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} in",sgender,"${bmiInfo.age}"
            )
        } else {
            minwt = Utils.minCmtokg(bmiInfo.ht_cm)
            maxwt = Utils.maxCmtokg(bmiInfo.ht_cm)
            String.format(getString(R.string.bmi_input_data),"${bmiInfo.wt_kg} kg",
                "${bmiInfo.ht_cm} cm",sgender,"${bmiInfo.age}"
            )
        }

        binding.rcCalInfo.setText(bmiCalInfo)

        val wtType = bmiInfo.wtHtType!!.substring(0, 2)
        val htType = bmiInfo.wtHtType!!.substring(2)

        var wtrange =
            "${DcFormat.tf.format(minwt)} ${wtType} - ${DcFormat.tf.format(maxwt)} ${wtType}"

        var wtchazhi = if (bmiInfo.age > 20) {
            if (wtType == "kg") {
                if (bmiInfo.bmi < 18.5) {
                    minwt - bmiInfo.wt_kg
                } else {
                    bmiInfo.wt_kg - maxwt
                }
            } else {
                if (bmiInfo.bmi < 18.5) {
                    minwt - bmiInfo.wt_lb
                } else {
                    bmiInfo.wt_lb - maxwt
                }
            }
        } else {

            if (wtType == "kg") {
                if (bmiInfo.bmi < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minwt - bmiInfo.wt_kg
                } else {
                    bmiInfo.wt_kg - maxwt
                }
            } else {
                if (bmiInfo.bmi < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minwt - bmiInfo.wt_lb
                } else {
                    bmiInfo.wt_lb - maxwt
                }
            }
        }

        when (bmiInfo.bmiType) {
            "vsuw" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_very_severely_underweight))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_very_severely_underweight_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                    binding.rcWtRange.setText(
                        wtrange
                    )
                    binding.rcWtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${wtType})")
                }

            }

            "suw" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_severely_underweight))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_severely_underweight_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                    binding.rcWtRange.setText(wtrange)
                    binding.rcWtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${wtType})")
                }
            }

            "uw" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_underweight))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_underweight_adult_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                } else {
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_underweight_child_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                }
                binding.rcWtRange.setText(wtrange)
                binding.rcWtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${wtType})")
            }

            "nm" -> {
                binding.rcCalTypeText.setText(getString(R.string.normal_leg))
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
                    binding.rcCalSuggest.setText(getString(R.string.bmi_range_normal_adult_description))
                } else {
                    binding.rcCalSuggest.setText(getString(R.string.bmi_range_normal_child_description))
                }
            }

            "ow" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_overweight))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_overweight_adult_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                } else {
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_overweight_child_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                }
                binding.rcWtRange.setText(wtrange)
                binding.rcWtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${wtType})")
            }

            "oc1" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_range_obese_class1))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_obeseClassI_adult_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                } else {
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_obeseClassI_child_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                }
                binding.rcWtRange.setText(wtrange)
                binding.rcWtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${wtType})")
            }

            "oc2" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_range_obese_class2))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_obeseClassII_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                }
                binding.rcWtRange.setText(wtrange)
                binding.rcWtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${wtType})")
            }

            "oc3" -> {
                binding.rcCalTypeText.setText(getString(R.string.bmi_range_obese_class3))
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
                    binding.rcCalSuggest.setText(
                        getString(R.string.bmi_range_obeseClassIII_description) + "\n\n" +
                                String.format(
                                    getString(R.string.bmi_result_suggest_start),
                                    if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                                    else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                                )
                    )
                }
                binding.rcWtRange.setText(wtrange)
                binding.rcWtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${wtType})")
            }

        }
    }

}