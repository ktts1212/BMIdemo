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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.RoundedCorner
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
import bmicalculator.bmi.calculator.weightlosstracker.MainActivity
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentCalculatorResultBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.AdInfo
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.ui.statistic.StatisticFragment
import bmicalculator.bmi.calculator.weightlosstracker.uitl.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.UserStatus
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Arrays
import kotlin.math.max
import kotlin.random.Random

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

        if (UserStatus.ishasRecord) {
            binding.bmiTypeTip.visibility = View.VISIBLE
            binding.bmiCalType.visibility = View.GONE
            binding.bmiAdLayout.visibility = View.VISIBLE

        } else {
            binding.bmiTypeTip.visibility = View.GONE
            binding.bmiCalType.visibility = View.VISIBLE
            binding.bmiAdLayout.visibility = View.GONE
        }




        binding.bmiCalTypeCard.setOnClickListener {
            if (UserStatus.ishasRecord) {
                val dialog = BmiCalTypeTableFragment()
                dialog.show(childFragmentManager, "TypeTable")
            }
        }

        binding.bmiArrow.alpha = 0.75f

        //根据bmival判断
        val bval = viewModel.bmival.value


        //设置textdisplay
        val sgender = if (viewModel.selectedGender.value!!.equals('0')) "Male"
        else "Female"
        val selectType = viewModel.wttype + viewModel.httype

        ChildBmiDialData.setData(
            viewModel.selectedAge.value!!,
            viewModel.selectedGender.value!!
        )

        Utils.initData(viewModel.selectedAge.value!!)

        var minwt: Double = 0.0
        var maxwt: Double = 0.0
        val bmicalinfo = if (selectType == "lbftin") {
            minwt = Utils.minWtftintolb(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            maxwt = Utils.maxWtftintolb(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            "${viewModel.wt_lb.value} lb | ${viewModel.ht_ft.value} ft ${viewModel.ht_in.value} " +
                    "in | ${sgender} | ${viewModel.selectedAge.value} years old"
        } else if (selectType == "lbcm") {
            minwt = Utils.minCmtolb(viewModel.ht_cm.value!!)
            maxwt = Utils.maxCmtolb(viewModel.ht_cm.value!!)
            "${viewModel.wt_lb.value} lb | ${viewModel.ht_cm.value} cm | ${sgender} | ${
                viewModel.selectedAge.value
            } years old"
        } else if (selectType == "kgftin") {
            minwt = Utils.minWtftintokg(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            maxwt = Utils.maxWtftintokg(viewModel.ht_ft.value!!, viewModel.ht_in.value!!)
            "${viewModel.wt_kg.value} kg | ${viewModel.ht_ft.value} ft ${viewModel.ht_in.value} " +
                    "in | ${sgender} | ${viewModel.selectedAge.value} years old"
        } else {
            minwt = Utils.minCmtokg(viewModel.ht_cm.value!!)
            maxwt = Utils.maxCmtokg(viewModel.ht_cm.value!!)
            "${viewModel.wt_kg.value} kg | ${viewModel.ht_cm.value} cm | ${sgender} | ${
                viewModel.selectedAge.value
            } years old"
        }

        binding.bmiCalInfo.setText(bmicalinfo)

        var wtrange =
            "${DcFormat.tf.format(minwt)} ${viewModel.wttype} - ${DcFormat.tf.format(maxwt)} ${viewModel.wttype}"

        var wtchazhi = if (viewModel.selectedAge.value!! > 20) {
            if (viewModel.wttype == "kg") {
                if (bval!! < 18.5) {
                    minwt - viewModel.wt_kg.value!!
                } else {
                    viewModel.wt_kg.value!! - maxwt
                }
            } else {
                if (bval!! < 18.5) {
                    minwt - viewModel.wt_lb.value!!
                } else {
                    viewModel.wt_lb.value!! - maxwt
                }
            }
        } else {

            if (viewModel.wttype == "kg") {
                if (bval!! < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minwt - viewModel.wt_kg.value!!
                } else {
                    viewModel.wt_kg.value!! - maxwt
                }
            } else {
                if (bval!! < ChildBmiDialData.cScaleList[0].toFloat()) {
                    minwt - viewModel.wt_lb.value!!
                } else {
                    viewModel.wt_lb.value!! - maxwt
                }
            }
        }


        if (viewModel.selectedAge.value!! > 20) {
            binding.bmiChildDial.visibility = View.GONE
            binding.bmiDial.visibility = View.VISIBLE
            binding.bmiArrow.rotation = sweepAngle(viewModel.bmival.value!!.toFloat())

            if (bval < 16) {
                viewModel.bmitype = "vsuw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.vsuw
                    )
                )
                binding.bmiCalTypeText.setText(R.string.vsuw)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_vsuw) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval >= 16 && bval < 17) {
                viewModel.bmitype = "suw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.suw
                    )
                )
                binding.bmiCalTypeText.setText(R.string.suw)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_suw) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval >= 17 && bval < 18.5) {
                viewModel.bmitype = "uw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                binding.bmiCalTypeText.setText(R.string.uw)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_uw) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval >= 18.5 && bval < 25) {
                viewModel.bmitype = "nm"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                binding.bmiCalTypeText.setText(R.string.nm)
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
                binding.bmiCalSuggest.setText(R.string.adult_nm_enc)
            } else if (bval >= 25 && bval < 30) {
                viewModel.bmitype = "ow"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                binding.bmiCalTypeText.setText(R.string.ow)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_ow) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval >= 30 && bval < 35) {
                viewModel.bmitype = "oc1"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                binding.bmiCalTypeText.setText(R.string.oc1)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_ob1) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval >= 35 && bval < 40) {
                viewModel.bmitype = "oc2"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc2
                    )
                )
                binding.bmiCalTypeText.setText(R.string.oc2)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_ob2) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else {
                viewModel.bmitype = "oc3"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc3
                    )
                )
                binding.bmiCalTypeText.setText(R.string.oc3)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.adult_ob3) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            }
        } else {
            binding.bmiDial.visibility = View.GONE
            binding.bmiChildDial.visibility = View.VISIBLE
            binding.bmiChildDial.getData(ChildBmiDialData.cScaleList, ChildBmiDialData.scaleRange)
            binding.bmiArrow.rotation = childSweepAngle(viewModel.bmival.value!!.toFloat())

            binding.newac.bmiVerysevere.visibility = View.GONE
            binding.newac.bmiSevere.visibility = View.GONE
            binding.newac.bmiOb2.visibility = View.GONE
            binding.newac.bmiOb3.visibility = View.GONE
            val list = ChildBmiDialData.cScaleList
            binding.newac.bmiUweightText2.setText("${list[0]} - ${list[1]}")
            binding.newac.bmiNormalText2.setText("${list[1]} - ${list[2]}")
            binding.newac.bmiOverweightText2.setText("${list[2]} - ${list[3]}")
            binding.newac.bmiOb1Text2.setText("${list[3]} - ${list[4]}")

            if (bval < ChildBmiDialData.cScaleList[1].toFloat()) {
                viewModel.bmitype = "uw"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.uw
                    )
                )
                binding.bmiCalTypeText.setText(R.string.uw)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.child_uw) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(+${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else if (bval < ChildBmiDialData.cScaleList[2].toFloat() &&
                bval >= ChildBmiDialData.cScaleList[1].toFloat()
            ) {
                viewModel.bmitype = "nm"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.normal
                    )
                )
                binding.bmiCalTypeText.setText(R.string.nm)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.child_nm)
                )

            } else if (bval < ChildBmiDialData.cScaleList[3].toFloat() &&
                bval >= ChildBmiDialData.cScaleList[2].toFloat()
            ) {
                viewModel.bmitype = "ow"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.ow
                    )
                )
                binding.bmiCalTypeText.setText(R.string.ow)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.child_ow) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            } else {

                viewModel.bmitype = "oc1"
                binding.bmiCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.oc1
                    )
                )
                binding.bmiCalTypeText.setText(R.string.uw)
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
                binding.bmiCalSuggest.setText(
                    getString(R.string.child_ob1) + "\n\n" +
                            getString(R.string.suggestion) + "(${
                        if (viewModel.httype == "cm") viewModel.ht_cm.value.toString() + "cm"
                        else viewModel.ht_ft.value.toString() + "ft ${viewModel.ht_in.value}in"
                    }):" + "\n"
                )
                binding.wtRange.setText(wtrange)
                binding.wtChazhi.setText("(-${DcFormat.tf.format(wtchazhi)} ${viewModel.wttype})")
            }
        }

        adshow(bval)

        binding.bmiAd1.setOnClickListener {
            try {
                val playStoreUri= Uri.parse(
                    "market://details?id=${binding.bmiAd1Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreUri)
                startActivity(intent)
            }catch ( e:Exception){
                val playStoreWebUri=Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd1Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.bmiAd2.setOnClickListener {
            try {
                val playStoreUri= Uri.parse(
                    "market://details?id=${binding.bmiAd2Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreUri)
                startActivity(intent)
            }catch ( e:Exception){
                val playStoreWebUri=Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd2Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.bmiAd3.setOnClickListener {
            try {
                val playStoreUri= Uri.parse(
                    "market://details?id=${binding.bmiAd3Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreUri)
                startActivity(intent)
            }catch ( e:Exception){
                val playStoreWebUri=Uri.parse(
                    "https://play.google.com/store/apps/details?id=${binding.bmiAd3Tv2.text}")
                val intent=Intent(Intent.ACTION_VIEW,playStoreWebUri)
                startActivity(intent)
            }
        }

        binding.saveBtn.setOnClickListener {
                val bmiInfo = BmiInfo(
                    viewModel.wt_lb.value!!.toDouble(),
                    viewModel.wt_kg.value!!.toDouble(),
                    viewModel.ht_ft.value!!.toInt(), viewModel.ht_in.value!!.toInt(),
                    viewModel.ht_cm.value!!.toDouble(),
                    viewModel.selectedDate.value, viewModel.selectedPhase.value,
                    viewModel.selectedAge.value!!.toInt(),
                    viewModel.selectedGender.value!!.toChar()
                )
             viewModel.insertInfo(bmiInfo)
            val editor=(activity as AppCompatActivity).getSharedPreferences(
                "data",Context.MODE_PRIVATE).edit()
            editor.putBoolean("hasdata",true)
            editor.apply()
            val navView= (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId=R.id.menu_statistics
            val fragmentManager=(activity as AppCompatActivity).supportFragmentManager
            val transition=fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container,StatisticFragment())
            transition.commit()
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

    fun childSweepAngle(num: Float): Float {
        val maxb = ChildBmiDialData.cScaleList[4].toFloat()
        val minb = ChildBmiDialData.cScaleList[0].toFloat()
        val bmival = if (num > maxb) maxb else if (num < minb) minb else num
        return DcFormat.tf.format((bmival - minb) / (maxb - minb) * 180 + 90).toFloat()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun adshow(bmival: Float) {
        if ((viewModel.selectedAge.value!! > 20 && bmival < 18.5)||
            (viewModel.selectedAge.value!! <= 20 && bmival < ChildBmiDialData.cScaleList[1].toDouble()) ) {
//            binding.bmiAd1Tv2.visibility = View.GONE
//            binding.bmiAd2Tv2.visibility = View.GONE
//            binding.bmiAd3Tv2.visibility = View.GONE
            val num1 = Random.nextInt(6, 9)
            var adInfo=choosead(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd1Iv1)
            binding.bmiAd1Tv2.setText(getString(adInfo.applink))
            binding.bmiAd1Tv1.setText(getString(adInfo.appName))
            binding.bmiAd1Tv3.setText(getString(adInfo.appScore))

            var num2:Int
            do{
                num2= Random.nextInt(6,9)
            }while (num2==num1)
            adInfo=choosead(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd2Iv1)
            binding.bmiAd2Tv1.setText(getString(adInfo.appName))
            binding.bmiAd2Tv3.setText(getString(adInfo.appScore))
            binding.bmiAd2Tv2.setText(getString(adInfo.applink))

            val numList= listOf(5,9,10)
            val randomIndex= Random.nextInt(numList.size)
            val num3=numList[randomIndex]
            adInfo=choosead(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd3Iv1)
            binding.bmiAd3Tv1.setText(getString(adInfo.appName))
            binding.bmiAd3Tv3.setText(getString(adInfo.appScore))
            binding.bmiAd3Tv2.setText(getString(adInfo.applink))
        }else{
//            binding.bmiAd1Tv2.visibility = View.VISIBLE
//            binding.bmiAd2Tv2.visibility = View.VISIBLE
//            binding.bmiAd3Tv2.visibility = View.VISIBLE
//            binding.bmiAd1Tv2.visibility = View.GONE
//            binding.bmiAd2Tv2.visibility = View.GONE
//            binding.bmiAd3Tv2.visibility = View.GONE
            val nums=if (viewModel.selectedGender.value!!.equals('0'))
                mutableListOf(2,3,6,7,8) else mutableListOf(1,3,6,7,8)
            var randomIndex= Random.nextInt(nums.size)
            val num1=nums[randomIndex]
            var adInfo=choosead(num1)

            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd1Iv1)

            binding.bmiAd1Tv1.setText(getString(adInfo.appName))
            binding.bmiAd1Tv2.setText(getString(adInfo.applink))
            binding.bmiAd1Tv3.setText(getString(adInfo.appScore))
            nums.removeAt(randomIndex)

            randomIndex=Random.nextInt(nums.size)
            val num2=nums[randomIndex]
            adInfo=choosead(num2)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd2Iv1)
            binding.bmiAd2Tv1.setText(getString(adInfo.appName))
            binding.bmiAd2Tv2.setText(getString(adInfo.applink))
            binding.bmiAd2Tv3.setText(getString(adInfo.appScore))

            nums.clear()
            nums.addAll(Arrays.asList(4,5,9,10))
            randomIndex=Random.nextInt(nums.size)
            val num3=nums[randomIndex]
            adInfo=choosead(num3)
            Glide.with(this)
                .load(adInfo.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .into(binding.bmiAd3Iv1)
            binding.bmiAd3Tv1.setText(getString(adInfo.appName))
            binding.bmiAd3Tv3.setText(getString(adInfo.appScore))
            binding.bmiAd3Tv2.setText(getString(adInfo.applink))
        }
    }

    fun choosead(num: Int): AdInfo {
        when (num) {
            1 -> {
                return AdInfo(R.drawable.ad1, R.string.ad1, "", R.string.ad1_url,R.string.ad1_score)
            }

            2 -> {
                return AdInfo(R.drawable.ad2, R.string.ad2, "", R.string.ad2_url ,R.string.ad2_score)
            }

            3 -> {
                return AdInfo(R.drawable.ad3, R.string.ad3, "", R.string.ad3_url, R.string.ad3_score)
            }

            4 -> {
                return AdInfo(R.drawable.ad4, R.string.ad4, "", R.string.ad4_url, R.string.ad4_score)
            }

            5 -> {
                return AdInfo(R.drawable.ad5, R.string.ad5, "", R.string.ad5_url, R.string.ad5_score)
            }

            6 -> {
                return AdInfo(R.drawable.ad6, R.string.ad6, "", R.string.ad6_url, R.string.ad6_score)
            }

            7 -> {
                return AdInfo(R.drawable.ad7, R.string.ad7, "", R.string.ad7_url, R.string.ad7_score)
            }

            8 -> {
                return AdInfo(R.drawable.ad8, R.string.ad8, "", R.string.ad8_url, R.string.ad8_score)
            }

            9 -> {
                return AdInfo(R.drawable.ad9, R.string.ad9, "", R.string.ad9_url, R.string.ad9_score)
            }

            else -> {
                return AdInfo(R.drawable.ad10, R.string.ad10, "", R.string.ad10_url, R.string.ad10_score)
            }
        }
    }
}