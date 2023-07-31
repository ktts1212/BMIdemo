package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import bmicalculator.bmi.calculator.weightlosstracker.uitl.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.uitl.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.uitl.SweepAngel
import bmicalculator.bmi.calculator.weightlosstracker.uitl.Utils

class RecordFragment : DialogFragment() {

    private lateinit var binding: FragmentRecordBinding

    private lateinit var currentBmiInfo:BmiInfo

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            currentBmiInfo= arguments?.getParcelable("cBI",BmiInfo::class.java)!!
        }else{
            currentBmiInfo= arguments?.getParcelable<BmiInfo>("cBI")!!
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as AppCompatActivity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository)
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

        binding.rcArrow.alpha = 0.75f
        binding.rcArrow.pivotX = Utils.dip2px(
            requireContext(), 18f
        ).toFloat()

        binding.rcArrow.pivotY = Utils.dip2px(
            requireContext(), 18f
        ).toFloat()

        if (currentBmiInfo.age>20){
            binding.rcChildDial.visibility=View.GONE
            binding.rcDial.visibility=View.VISIBLE
            binding.rcArrow.rotation= SweepAngel.sweepAngle(currentBmiInfo.bmi)
            binding.rcNum.setText(currentBmiInfo.bmi.toString())
            getBmiType(currentBmiInfo.bmiType!!,currentBmiInfo)
            getWtHtType(currentBmiInfo)

        }else{
            binding.rcChildDial.visibility=View.VISIBLE
            binding.rcDial.visibility=View.GONE
            ChildBmiDialData.setData(currentBmiInfo.age,currentBmiInfo.gender)
            binding.rcChildDial.getData(ChildBmiDialData.cScaleList,ChildBmiDialData.scaleRange)
            binding.rcArrow.rotation= SweepAngel.childSweepAngle(currentBmiInfo.bmi)
            binding.rcNum.setText(currentBmiInfo.bmi.toString())
            getBmiType(currentBmiInfo.bmiType!!,currentBmiInfo)
            getWtHtType(currentBmiInfo)

        }
        binding.recordTime.setText("${currentBmiInfo.date} ${currentBmiInfo.phase}")


        binding.recordDel.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Delete confirm")
                setMessage("Are you sure you want to delete this record?")
                setCancelable(false)
                setPositiveButton("Delete") { dialog, which ->
                    viewModel.deleteBmiInfo(currentBmiInfo)
                    onDestroyView()
                }

                setNegativeButton("Cancel") { dialog, which ->

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
    }

    @SuppressLint("SetTextI18n")
    fun getBmiType(bmiType:String, bmiInfo: BmiInfo){

        val sgender = if (bmiInfo.gender=='0') "Male"
        else "Female"
        var minwt: Double = 0.0
        var maxwt: Double = 0.0
        val bmicalinfo = if (bmiInfo.wtHtType == "lbftin") {
            minwt = Utils.minWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
            maxwt = Utils.maxWtftintolb(bmiInfo.ht_ft, bmiInfo.ht_in)
            "${bmiInfo.wt_lb} lb | ${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} " +
                    "in | ${sgender} | ${bmiInfo.age} years old"
        } else if (bmiInfo.wtHtType == "lbcm") {
            minwt = Utils.minCmtolb(bmiInfo.ht_cm)
            maxwt = Utils.maxCmtolb(bmiInfo.ht_cm)
            "${bmiInfo.wt_lb} lb | ${bmiInfo.ht_cm} cm | ${sgender} | ${
                bmiInfo.age
            } years old"
        } else if (bmiInfo.wtHtType == "kgftin") {
            minwt = Utils.minWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)
            maxwt = Utils.maxWtftintokg(bmiInfo.ht_ft, bmiInfo.ht_in)
            "${bmiInfo.wt_kg} kg | ${bmiInfo.ht_ft} ft ${bmiInfo.ht_in} " +
                    "in | ${sgender} | ${bmiInfo.age} years old"
        } else {
            minwt = Utils.minCmtokg(bmiInfo.ht_cm)
            maxwt = Utils.maxCmtokg(bmiInfo.ht_cm)
            "${bmiInfo.wt_kg} kg | ${bmiInfo.ht_cm} cm | ${sgender} | ${
                bmiInfo.age
            } years old"
        }

        binding.rcCalInfo.setText(bmicalinfo)
        val wtType=bmiInfo.wtHtType!!.substring(0,2)
        val htType=bmiInfo.wtHtType!!.substring(2)

        //Log.d("cccc",wtType)
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

        when (bmiType) {
            "vsuw" -> {
                binding.rcCalTypeText.setText(getString(R.string.vsuw))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.vsuw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.vsuw
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_vsuw) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                    binding.rcWtRange.setText(
                        wtrange
                    )
                }

            }

            "suw" -> {
                binding.rcCalTypeText.setText(getString(R.string.suw))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.suw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.suw
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_suw) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                    binding.rcWtRange.setText(wtrange)
                }
            }

            "uw" -> {
                binding.rcCalTypeText.setText(getString(R.string.uw))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.uw)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.uw
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_uw) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }else{
                    binding.rcCalSuggest.setText(
                        getString(R.string.child_uw) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }
                binding.rcWtRange.setText(wtrange)
            }

            "nm" -> {
                binding.rcCalTypeText.setText(getString(R.string.nm))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.normal)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.normal
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(getString(R.string.adult_nm_enc))
                }else{
                    binding.rcCalSuggest.setText(getString(R.string.child_nm))
                }
            }

            "ow" -> {
                binding.rcCalTypeText.setText(getString(R.string.ow))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.ow)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.ow
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_ow) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }else{
                    binding.rcCalSuggest.setText(
                        getString(R.string.child_ow) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }
                binding.rcWtRange.setText(wtrange)
            }

            "oc1" -> {
                binding.rcCalTypeText.setText(getString(R.string.oc1))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.oc1)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.oc1
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_ob1) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }else{
                    binding.rcCalSuggest.setText(
                        getString(R.string.child_ob1) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }
                binding.rcWtRange.setText(wtrange)
            }

            "oc2" -> {
                binding.rcCalTypeText.setText(getString(R.string.oc2))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.oc2)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.oc2
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_ob2) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }
                binding.rcWtRange.setText(wtrange)
            }

            "oc3" -> {
                binding.rcCalTypeText.setText(getString(R.string.oc3))
                binding.rcCalTypeDisplay.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),R.color.oc3)
                )
                ViewCompat.setBackgroundTintList(
                    binding.rcTypeTip, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),R.color.oc3
                        )
                    )
                )
                if (bmiInfo.age>20){
                    binding.rcCalSuggest.setText(
                        getString(R.string.adult_ob3) + "\n\n" +
                                getString(R.string.suggestion) + "(${
                            if (htType == "cm") bmiInfo.ht_cm.toString() + "cm"
                            else bmiInfo.ht_ft.toString() + "ft ${bmiInfo.ht_in}in"
                        }):" + "\n"
                    )
                }
                binding.rcWtRange.setText(wtrange)
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun getWtHtType(newRecord: BmiInfo) {

        val gender = if (newRecord.gender == '0') "Male" else "Female"

        when (newRecord.wtHtType) {
            "lbftin" -> {
                binding.rcCalInfo.setText(
                    "${newRecord.wt_lb} lb | ${newRecord.ht_ft} ft " +
                            "${newRecord.ht_in} in | ${gender} | ${newRecord.age} years old"
                )
            }

            "lbcm" -> {
                binding.rcCalInfo.setText(
                    "${newRecord.wt_lb} lb | " +
                            " ${newRecord.ht_cm} cm | ${gender} | ${newRecord.age} years old"
                )
            }

            "kgftin" -> {
                binding.rcCalInfo.setText(
                    "${newRecord.wt_kg} kg | ${newRecord.ht_ft} ft " +
                            "${newRecord.ht_in} in | ${gender} | ${newRecord.age} years old"
                )
            }

            "kgcm" -> {
                binding.rcCalInfo.setText(
                    "${newRecord.wt_kg} kg | ${newRecord.ht_cm} cm" +
                            " | ${gender} | ${newRecord.age} years old"
                )
            }
        }
    }

}