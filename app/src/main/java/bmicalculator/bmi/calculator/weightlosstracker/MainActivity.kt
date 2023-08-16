package bmicalculator.bmi.calculator.weightlosstracker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.databinding.ActivityMainBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.BmiFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child.RecordFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.ui.statistic.StatisticFragment
import bmicalculator.bmi.calculator.weightlosstracker.util.ContextWrapper
import bmicalculator.bmi.calculator.weightlosstracker.util.DcFormat
import bmicalculator.bmi.calculator.weightlosstracker.util.LanguageHelper

private const val TAG = "MainActivity"



class MainActivity : AppCompatActivity(), RecordFragment.OnDeleteTypeListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: CalculatorViewModel

    private lateinit var currentFragmentTag: String

    private lateinit var mCurrentFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("hasData", Context.MODE_PRIVATE)
        val hasData = prefs.getBoolean("hasData", false)
        val lan = getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString(
                "language", "en"
            )
        lan?.let { DcFormat.setData(it) }

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag").toString()
        }

        val dao = AppDataBase.getDatabase(application).bmiInfoDao()
        val factory = ViewModelFactory(Repository(dao), this)
        //获取viewModel实例
        viewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]
        viewModel.localeLanguage= lan
        val bmiInfo = viewModel.getData()
        if (bmiInfo != null) {
            viewModel.setWtKg(bmiInfo.wt_kg)
            viewModel.setWtLb(bmiInfo.wt_lb)
            viewModel.setHtFt(bmiInfo.ht_ft)
            viewModel.setHtIn(bmiInfo.ht_in)
            viewModel.setHtCm(bmiInfo.ht_cm)
            bmiInfo.date?.let { viewModel.setDate(it) }
            viewModel.setPhase(bmiInfo.phase)
            viewModel.setAge(bmiInfo.age)
            viewModel.setGender(bmiInfo.gender)
            viewModel.setBmiVal(bmiInfo.bmi)
            viewModel.bmiType = bmiInfo.bmiType.toString()
            viewModel.wtType = bmiInfo.wtHtType?.substring(0, 2) ?: "error"
            viewModel.htType = bmiInfo.wtHtType?.substring(2) ?: "error"
        }

        if (viewModel.getNavId()!=null){
            val fragments = supportFragmentManager.fragments
            val ft = supportFragmentManager.beginTransaction()
            for (fragment in fragments) {
                Log.d("alltag","${fragment.tag}")
                if (fragment.tag !=idToTag(viewModel.getNavId()!!) ) {
                    Log.d("fgtag","${fragment.tag}")
                    ft.remove(fragment)
                }
            }
            ft.commit()
        }

        viewModel.message.observe(this) { event ->
            event.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }


        //根据查询到的信息来判断是否是新老用户
        viewModel.allInfo.observe(this) {
            if (!it.isNullOrEmpty()) {
                binding.bottomNavigationView.visibility = View.VISIBLE
                val edit = prefs.edit()
                edit.putBoolean("hasData", true)
                edit.apply()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
                val edit = prefs.edit()
                edit.putBoolean("hasData", false)
                edit.apply()
            }

        }
        if (viewModel.getNavId() != null) {
            currentFragmentTag = idToTag(viewModel.getNavId()!!)
            binding.bottomNavigationView.selectedItemId = viewModel.getNavId()!!
            getFgByTag(currentFragmentTag)
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.add(R.id.fragment_container, mCurrentFragment, currentFragmentTag)
//            transaction.show(mCurrentFragment).commit()
        } else {
            if (!hasData) {
                currentFragmentTag = "calculator"
                binding.bottomNavigationView.selectedItemId = R.id.menu_calculator
                getFgByTag(currentFragmentTag)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_container, mCurrentFragment, currentFragmentTag)
                transaction.show(mCurrentFragment).commit()
            } else {
                currentFragmentTag = "bmi"
                binding.bottomNavigationView.selectedItemId = R.id.menu_bmi
                getFgByTag(currentFragmentTag)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.add(R.id.fragment_container, mCurrentFragment, currentFragmentTag)
                transaction.show(mCurrentFragment).commit()
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            viewModel.saveNavId(it.itemId)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.hide(mCurrentFragment)
            when (it.itemId) {
                R.id.menu_calculator -> {
                    mCurrentFragment = supportFragmentManager.findFragmentByTag("calculator")
                        ?: CalculatorFragment().also { fg ->
                            transaction.add(R.id.fragment_container, fg, "calculator")
                        }
                    currentFragmentTag = "calculator"
                    transaction.show(mCurrentFragment).commit()
                }


                R.id.menu_bmi -> {

                    mCurrentFragment = supportFragmentManager.findFragmentByTag("bmi")
                        ?: BmiFragment().also { fg ->
                            transaction.add(R.id.fragment_container, fg, "bmi")
                        }
                    currentFragmentTag = "bmi"
                    transaction.show(mCurrentFragment).commit()
                }

                R.id.menu_statistics -> {

                    mCurrentFragment = supportFragmentManager.findFragmentByTag("statistic")
                        ?: StatisticFragment().also { fg ->
                            transaction.add(R.id.fragment_container, fg, "statistic")
                        }
                    currentFragmentTag = "statistic"
                    transaction.show(mCurrentFragment).commit()
                }
            }

            true
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase?.let {
            ContextWrapper.wrap(newBase, LanguageHelper.getLocale(newBase))
        }
        super.attachBaseContext(newBase)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag", currentFragmentTag)
    }

    private fun idToTag(id:Int):String{
       return when(id){
            R.id.menu_calculator->{
                "calculator"
            }
            R.id.menu_bmi->{
                "bmi"
            }
            R.id.menu_statistics->{
                "statistic"
            }
            else->{
                "error"
            }
        }
    }

    private fun getFgByTag(tag: String) {
        when (tag) {
            "statistic" -> {
                mCurrentFragment = StatisticFragment()
            }

            "bmi" -> {
                mCurrentFragment = BmiFragment()

            }

            "calculator" -> {
                mCurrentFragment = CalculatorFragment()
            }
        }
    }

    override fun onDeleteTypeAction(type: String) {
        val fragment = supportFragmentManager.findFragmentByTag("bmi") as BmiFragment
        when (type) {
            "vsuw" -> {
                val layout =
                    fragment.view?.findViewById<RelativeLayout>((R.id.bmi_verysevere_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_verysevere_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.vsuw)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_verysevere_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_verysevere_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "suw" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_uweight_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_uweight_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.suw)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_uweight_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_uweight_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "uw" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_uweight_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_uweight_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.uw)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_uweight_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_uweight_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "nm" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_normal_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_normal_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.normal)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_normal_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_normal_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "ow" -> {
                val layout =
                    fragment.view?.findViewById<RelativeLayout>((R.id.bmi_overweight_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_overweight_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.ow)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_overweight_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_overweight_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "oc1" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_ob1_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_ob1_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.oc1)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_ob1_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_ob1_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "oc2" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_ob2_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_ob2_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.oc2)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_ob2_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_ob2_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }

            "oc3" -> {
                val layout = fragment.view?.findViewById<RelativeLayout>((R.id.bmi_ob3_layout))
                layout?.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.white)
                )
                val dotImage = fragment.view?.findViewById<ImageView>(R.id.bmi_ob3_image)
                ViewCompat.setBackgroundTintList(
                    dotImage!!, ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.oc3)
                    )
                )
                val textView1 = fragment.view?.findViewById<TextView>(R.id.bmi_ob3_text1)
                textView1?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
                val textView2 = fragment.view?.findViewById<TextView>(R.id.bmi_ob3_text2)
                textView2?.apply {
                    setTextColor(Color.BLACK)
                    typeface = null
                }
            }
        }
    }
}