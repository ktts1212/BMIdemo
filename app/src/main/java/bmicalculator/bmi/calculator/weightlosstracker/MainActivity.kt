package bmicalculator.bmi.calculator.weightlosstracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.databinding.ActivityMainBinding
import bmicalculator.bmi.calculator.weightlosstracker.logic.Repository
import bmicalculator.bmi.calculator.weightlosstracker.logic.database.configDatabase.AppDataBase
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.ViewModelFactory
import bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.BmiFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.ui.statistic.StatisticFragment
import bmicalculator.bmi.calculator.weightlosstracker.util.ContextWrapper
import bmicalculator.bmi.calculator.weightlosstracker.util.LanguageHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.util.concurrent.Flow

private const val TAG = "MainActivity"


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

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

        //删除fragment堆栈中的所有fragment
        val fragments = supportFragmentManager.fragments
        val ft = supportFragmentManager.beginTransaction()
        for (fragment in fragments) {
            if (fragment != null) {
                ft.remove(fragment)
            }
        }
        ft.commit()

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("currentFragmentTag").toString()
        }

        val dao = AppDataBase.getDatabase(application).bmiInfoDao()
        val factory = ViewModelFactory(Repository(dao), this)
        //获取viewModel实例
        viewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]

        val bmiInfo = viewModel.getData()
        if (bmiInfo != null) {
            viewModel.setwtkg(bmiInfo.wt_kg)
            viewModel.setwtlb(bmiInfo.wt_lb)
            viewModel.sethtft(bmiInfo.ht_ft)
            viewModel.sethtin(bmiInfo.ht_in)
            viewModel.sethtcm(bmiInfo.ht_cm)
            bmiInfo.date?.let { viewModel.setDate(it) }
            viewModel.setPhase(bmiInfo.phase)
            viewModel.setAge(bmiInfo.age)
            viewModel.setGender(bmiInfo.gender)
            viewModel.setBmival(bmiInfo.bmi)
            viewModel.bmitype = bmiInfo.bmiType.toString()
            viewModel.wttype = bmiInfo.wtHtType?.substring(0, 2) ?: "error"
            viewModel.httype = bmiInfo.wtHtType?.substring(2) ?: "error"
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


        binding.bottomNavigationView.setOnItemSelectedListener {
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

    fun reGetFragment(tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
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
        transaction.add(R.id.fragment_container, mCurrentFragment, tag)
        transaction.show(mCurrentFragment).commit()
    }

    fun getFgByTag(tag: String) {
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
}