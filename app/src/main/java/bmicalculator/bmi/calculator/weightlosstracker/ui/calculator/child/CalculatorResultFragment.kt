package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.CustomTypefaceSpan
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils

private const val TAG = "Calres"

class CalculatorResultFragment : DialogFragment() {

    private lateinit var binding: FragmentCalculatorResultBinding

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalculatorResultBinding.inflate(layoutInflater, container, false)
        //(activity as AppCompatActivity).setSupportActionBar(binding.toolbarCalRes)
        //(activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        //setHasOptionsMenu(true)

        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), factory).get(CalculatorViewModel::class.java)

        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as AppCompatActivity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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

        viewModel.bmival.observe(requireActivity()) {
            binding.bmiNum.text = viewModel.bmival.value.toString()
        }

        binding.bmiArrow.pivotX = Utils.dip2px(
            requireContext(),
            18f
        ).toFloat()
        binding.bmiArrow.pivotY = Utils.dip2px(
            requireContext(),
            18f
        ).toFloat()

        binding.calDiscard.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete confirm")
                setMessage("Are you sure you want to delete this record?")
                setCancelable(false)
                setPositiveButton("Delete") { dialog, which ->
                    onDestroyView()
                }

                setNegativeButton("Cancel") { dialog, which ->

                }
                show()
            }
        }

        Log.d(TAG, Utils.px2dip(requireContext(), 40f).toString())
        binding.bmiArrow.rotation = sweepAngle(viewModel.bmival.value!!.toFloat())
        binding.bmiArrow.alpha = 0.75f

        //设置textdisplay
        val sgender = if (viewModel.selectedGender.value!!.equals('0')) "Male"
        else "Female"
        val selectType = viewModel.wttype + viewModel.httype

        var minwt:Double=0.0
        var maxwt:Double=0.0
        val bmicalinfo= if (selectType == "lbftin") {
            minwt = Utils.minWtftintolb(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            maxwt = Utils.maxWtftintolb(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            "${viewModel.wt_lb.value} lb | ${viewModel.ht_ft.value} ft ${viewModel.ht_in.value} " +
                    "in | ${sgender} | ${viewModel.selectedAge.value} years old"
        }
        else if (selectType == "lbcm") {
            minwt=Utils.minCmtolb(viewModel.ht_cm.value!!)
            maxwt=Utils.maxCmtolb(viewModel.ht_cm.value!!)
            "${viewModel.wt_lb.value} lb | ${viewModel.ht_cm.value} cm | ${sgender} | ${
                viewModel.selectedAge.value
            } years old"
        }
        else if (selectType == "kgftin") {
            minwt = Utils.minWtftintokg(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            maxwt = Utils.maxWtftintokg(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            "${viewModel.wt_kg.value} kg | ${viewModel.ht_ft.value} ft ${viewModel.ht_in.value} " +
                    "in | ${sgender} | ${viewModel.selectedAge.value} years old"
        }
        else {
            minwt=Utils.minCmtokg(viewModel.ht_cm.value!!)
            maxwt=Utils.maxCmtokg(viewModel.ht_cm.value!!)
            "${viewModel.wt_kg.value} kg | ${viewModel.ht_cm.value} cm | ${sgender} | ${
                viewModel.selectedAge.value
            } years old"
        }

        binding.bmiCalInfo.setText(bmicalinfo)

        var wtrange="${DcFormat.tf.format(minwt)} ${viewModel.wttype} - ${DcFormat.tf.format(maxwt)} ${viewModel.wttype}"

//        val spannable=SpannableString(wtrange)
//        spannable.apply {
//            setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(requireContext(),R.font.montserrat_extrabold)),0,wtrange.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
        //根据bmival判断
        val bval = viewModel.bmival.value
        var wtchazhi=if (viewModel.wttype=="kg"){
            if (bval!!<18.5){
                minwt-viewModel.wt_kg.value!!
            }else{
                viewModel.wt_kg.value!!-maxwt
            }
        }else{
            if (bval!!<18.5){
                minwt-viewModel.wt_lb.value!!
            }else{
                viewModel.wt_lb.value!!-maxwt
            }
        }
        if (bval!! < 16) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.vsuw
                )
            )
            binding.bmiCalTypeText.setText(R.string.vsuw)
            binding.bmiVerysevereLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.vsuw
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiVerysevereImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiVerysevereText1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiVerysevereText2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_vsuw) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                            if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                            else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                        }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else if (bval >= 16 && bval < 17) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.suw
                )
            )
            binding.bmiCalTypeText.setText(R.string.suw)
            binding.bmiSevereLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.suw
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiSevereImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiSevereText1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiSevereText2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_suw) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else if (bval >= 17 && bval < 18.5) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.uw
                )
            )
            binding.bmiCalTypeText.setText(R.string.uw)
            binding.bmiUweightLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.uw
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiUweightImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiUweightText1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiUweightText2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_uw) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else if (bval >= 18.5 && bval < 25) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.normal
                )
            )
            binding.bmiCalTypeText.setText(R.string.nm)
            binding.bmiNormalLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.normal
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiNormalImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiNormalText1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiNormalText2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(R.string.adult_nm_enc)
        } else if (bval >= 25 && bval < 30) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ow
                )
            )
            binding.bmiCalTypeText.setText(R.string.ow)
            binding.bmiOverweightLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ow
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiOverweightImage, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiOverweightText1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiOverweightText2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_ow) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else if (bval >= 30 && bval < 35) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc1
                )
            )
            binding.bmiCalTypeText.setText(R.string.oc1)
            binding.bmiOb1Layout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc1
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiOb1Image, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiOb1Text1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiOb1Text2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_ob1) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else if (bval >= 35 && bval < 40) {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc2
                )
            )
            binding.bmiCalTypeText.setText(R.string.oc2)
            binding.bmiOb2Layout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc2
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiOb2Image, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiOb2Text1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiOb2Text2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_ob2) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
        } else {
            binding.bmiCalTypeDisplay.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc3
                )
            )
            binding.bmiCalTypeText.setText(R.string.oc3)
            binding.bmiOb3Layout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.oc3
                )
            )
            ViewCompat.setBackgroundTintList(
                binding.bmiOb3Image, ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            )
            binding.bmiOb3Text1.apply {
                setTextColor(Color.WHITE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    typeface = Typeface.create(
                        ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold),
                        800,
                        false
                    )
                }
            }
            binding.bmiOb3Text2.apply {
                setTextColor(Color.WHITE)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
            }
            binding.bmiCalSuggest.setText(
                getString(R.string.adult_ob3) + "\n\n" +
                        getString(R.string.suggestion) + "(${
                    if (viewModel.httype=="cm") viewModel.ht_cm.value.toString()+"cm"
                    else viewModel.ht_ft.value.toString()+"ft ${viewModel.ht_in.value}in"
                }):"+"\n"
            )
            binding.wtRange.setText(wtrange)
            binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
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
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    fun sweepAngle(num: Float): Float {
        val bmival = if (num > 40.3) 40.3f else if (num < 15.6) 15.6f else num
        return DcFormat.tf.format((bmival - 15.6) / 24.8 * 180 + 90).toFloat()
    }
}