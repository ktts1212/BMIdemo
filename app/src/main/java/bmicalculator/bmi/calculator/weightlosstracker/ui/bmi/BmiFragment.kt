package bmicalculator.bmi.calculator.weightlosstracker.ui.bmi

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentBmiBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child.RecordHistoryFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.ChildBmiDialData
import bmicalculator.bmi.calculator.weightlosstracker.util.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.util.SweepAngel
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ktx.immersionBar

private const val TAG = "BmiFragment"

class BmiFragment : Fragment() {

    private lateinit var binding: FragmentBmiBinding

    private lateinit var viewModel: CalculatorViewModel

    private val allInfoList = ArrayList<BmiInfo>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBmiBinding.inflate(layoutInflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarBmi)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        val dao = AppDataBase.getDatabase(requireContext().applicationContext).bmiInfoDao()
        val repository = Repository(dao)
        val factory = ViewModelFactory(repository, requireActivity())
        viewModel =
            ViewModelProvider(requireActivity(), factory)[CalculatorViewModel::class.java]
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bmiLo.setOnClickListener {
            val navView =
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId = R.id.menu_calculator
            val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
            val transition = fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container, CalculatorFragment(),"calculator")
            transition.commit()
        }

        binding.toolbarBmi.setOnClickListener {
            val navView =
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId = R.id.menu_calculator
            val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
            val transition = fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container, CalculatorFragment(),"calculator")
            transition.commit()
        }

        binding.bmiSvChild.setOnClickListener {
            val navView =
                (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.selectedItemId = R.id.menu_calculator
            val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
            val transition = fragmentManager.beginTransaction()
            transition.replace(R.id.fragment_container, CalculatorFragment(),"calculator")
            transition.commit()
        }

        binding.bmiArrow.alpha = 0.75f
        binding.bmiArrow.pivotX = Utils.dip2px(
            requireContext(), 16f
        ).toFloat()

        binding.bmiArrow.pivotY = Utils.dip2px(
            requireContext(), 16f
        ).toFloat()

        if (viewModel.localeLanguage !in DcFormat.enList){
            binding.newRecord.bmiVerysevereText2.text=
                binding.newRecord.bmiVerysevereText2.text.toString().replace(".",",")
            binding.newRecord.bmiSevereText2.text=
                binding.newRecord.bmiSevereText2.text.toString().replace(".",",")
            binding.newRecord.bmiUweightText2.text=
                binding.newRecord.bmiUweightText2.text.toString().replace(".",",")
            binding.newRecord.bmiNormalText2.text=
                binding.newRecord.bmiNormalText2.text.toString().replace(".",",")
            binding.newRecord.bmiOverweightText2.text=
                binding.newRecord.bmiOverweightText2.text.toString().replace(".",",")
            binding.newRecord.bmiOb1Text2.text=
                binding.newRecord.bmiOb1Text2.text.toString().replace(".",",")
            binding.newRecord.bmiOb2Text2.text=
                binding.newRecord.bmiOb2Text2.text.toString().replace(".",",")
            binding.newRecord.bmiOb3Text2.text=
                binding.newRecord.bmiOb3Text2.text.toString().replace(".",",")
        }

        viewModel.allInfo.observe(requireActivity()) { bmiInfo ->

            if (bmiInfo.isNotEmpty()){
                allInfoList.clear()

                for (element in bmiInfo) {
                    allInfoList.add(element)
                }

                if (isAdded) {
                    binding.bmiLabel.text =
                        String.format(getString(R.string.bmi_tip_description), "...")
                }


                allInfoList.sortByDescending { it.timestape }
                val newRecord = allInfoList.maxBy { it.timestape }
                viewModel.bmiNewRecord.value = newRecord
                Log.d(TAG, newRecord.toString())
                if (newRecord.age > 20) {
                    binding.typeTableChildDial.visibility = View.GONE
                    binding.typeTableDial.visibility = View.VISIBLE
                    binding.bmiArrow.rotation = SweepAngel.sweepAngle(newRecord.bmi)
                    binding.bmiDate.text = newRecord.date
                    binding.bmiValue.text = DcFormat.tf!!.format(newRecord.bmi)
                    if (isAdded) {
                        getBmiType(newRecord.bmiType!!)
                        getWtHtType(newRecord)
                    }

                } else {
                    binding.typeTableChildDial.visibility = View.VISIBLE
                    binding.typeTableDial.visibility = View.GONE
                    binding.bmiValue.text = DcFormat.tf!!.format(newRecord.bmi)
                    binding.bmiDate.text = newRecord.date
                    ChildBmiDialData.setData(newRecord.age, newRecord.gender)
                    binding.typeTableChildDial.getData(
                        ChildBmiDialData.cScaleList,
                        ChildBmiDialData.scaleRange
                    )
                    binding.newRecord.bmiVerysevere.visibility = View.GONE
                    binding.newRecord.bmiSevere.visibility = View.GONE
                    binding.newRecord.bmiOb2.visibility = View.GONE
                    binding.newRecord.bmiOb3.visibility = View.GONE
                    val ls = ChildBmiDialData.cScaleList
                    binding.newRecord.bmiUweightText2.setText("${ls[0]} - ${ls[1]}")
                    binding.newRecord.bmiNormalText2.setText("${ls[1]} - ${ls[2]}")
                    binding.newRecord.bmiOverweightText2.setText("${ls[2]} - ${ls[3]}")
                    binding.newRecord.bmiOb1Text2.setText("${ls[3]} - ${ls[4]}")
                    getBmiType(newRecord.bmiType.toString())
                    binding.bmiArrow.rotation = SweepAngel.childSweepAngle(newRecord.bmi)
                    getWtHtType(newRecord)
                }
            }else{
                onDestroyView()
            }

        }

        binding.menuRecent.setOnClickListener {
            val dialog = RecordHistoryFragment()
            dialog.show(childFragmentManager, "RecordHistory")
        }

    }

    override fun onResume() {
        super.onResume()
        initBar()
    }

    private fun initBar() {
        immersionBar {
            titleBar(view)
            statusBarColor(R.color.white)
            transparentStatusBar()
            statusBarDarkFont(true)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initBar()
        }
    }

    private fun getBmiType(bmiType: String) {
        when (bmiType) {
            "vsuw" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text =
                        getString(R.string.bmi_very_severely_underweight)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.vsuw
                        )
                    )
                    binding.newRecord.bmiVerysevereLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.vsuw
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiVerysevereImage, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiVerysevereText1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiVerysevereText2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }
            }

            "suw" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_severely_underweight)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.suw
                        )
                    )
                    binding.newRecord.bmiSevereLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.suw
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiSevereImage, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiSevereText1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiSevereText2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }

            }

            "uw" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_underweight)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.uw
                        )
                    )
                    binding.newRecord.bmiUweightLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.uw
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiUweightImage, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiUweightText1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiUweightText2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }

            }

            "nm" -> {

                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.normal_leg)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.normal
                        )
                    )
                    binding.newRecord.bmiNormalLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.normal
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiNormalImage, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiNormalText1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiNormalText2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }

            }

            "ow" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_overweight)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.ow
                        )
                    )
                    binding.newRecord.bmiOverweightLayout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.ow
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiOverweightImage, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiOverweightText1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiOverweightText2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }

            }

            "oc1" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_range_obese_class1)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc1
                        )
                    )
                    binding.newRecord.bmiOb1Layout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc1
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiOb1Image, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiOb1Text1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiOb1Text2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }
            }

            "oc2" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_range_obese_class2)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc2
                        )
                    )
                    binding.newRecord.bmiOb2Layout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc2
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiOb2Image, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiOb2Text1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiOb2Text2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }
            }

            "oc3" -> {
                if (isAdded) {
                    binding.bmiCalTypeTextNew.text = getString(R.string.bmi_range_obese_class3)
                    binding.bmiCalTypeDisplayNew.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc3
                        )
                    )
                    binding.newRecord.bmiOb3Layout.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.oc3
                        )
                    )
                    ViewCompat.setBackgroundTintList(
                        binding.newRecord.bmiOb3Image, ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    )
                    binding.newRecord.bmiOb3Text1.apply {
                        setTextColor(Color.WHITE)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            typeface = Typeface.create(
                                ResourcesCompat.getFont(
                                    requireContext(),
                                    R.font.montserrat_extrabold
                                ),
                                800,
                                false
                            )
                        }
                    }
                    binding.newRecord.bmiOb3Text2.apply {
                        setTextColor(Color.WHITE)
                        typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun getWtHtType(newRecord: BmiInfo) {
        if (isAdded){
            val gender = if (newRecord.gender == '0') getString(R.string.male) else getString(
                R.string.female
            )

            when (newRecord.wtHtType) {
                "lbftin" -> {
                    binding.bmiCalInfoNew.text = String.format(
                        getString(R.string.bmi_input_data),
                        "${DcFormat.tf!!.format(newRecord.wt_lb)} lb",
                        "${newRecord.ht_ft} ft ${newRecord.ht_in} in",
                        gender,
                        "${newRecord.age}"
                    )
                }

                "lbcm" -> {
                    binding.bmiCalInfoNew.text = String.format(
                        getString(R.string.bmi_input_data),
                        "${DcFormat.tf!!.format(newRecord.wt_lb)} lb",
                        "${DcFormat.tf!!.format(newRecord.ht_cm)} cm",
                        gender,
                        "${newRecord.age}"
                    )
                }

                "kgftin" -> {
                    binding.bmiCalInfoNew.text = String.format(
                        getString(R.string.bmi_input_data),
                        "${DcFormat.tf!!.format(newRecord.wt_kg)} kg",
                        "${newRecord.ht_ft} ft ${newRecord.ht_in} in",
                        gender,
                        "${newRecord.age}"
                    )
                }

                "kgcm" -> {
                    binding.bmiCalInfoNew.text = String.format(
                        getString(R.string.bmi_input_data),
                        "${DcFormat.tf!!.format(newRecord.wt_kg)} kg",
                        "${DcFormat.tf!!.format(newRecord.ht_cm)} cm",
                        gender,
                        "${newRecord.age}"
                    )
                }
            }
        }

    }
}