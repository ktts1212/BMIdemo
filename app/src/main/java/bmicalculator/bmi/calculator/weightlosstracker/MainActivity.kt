package bmicalculator.bmi.calculator.weightlosstracker

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import bmicalculator.bmi.calculator.weightlosstracker.uitl.ContextWrapper
import bmicalculator.bmi.calculator.weightlosstracker.uitl.LanguageHelper

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragments=supportFragmentManager.fragments
        if (fragments!=null){
            val ft=supportFragmentManager.beginTransaction()
            for(fragment in fragments){
                if (fragment!=null){
                    ft.remove(fragment)
                }
            }
            ft.commit()
        }


        if (supportFragmentManager.findFragmentByTag("statistic")!=null){
            Log.d(TAG,"存在")
            val ft=supportFragmentManager.beginTransaction()
            ft.remove(supportFragmentManager.findFragmentByTag("statistic")!!)
            ft.commit()
        }

        val dao = AppDataBase.getDatabase(application).bmiInfoDao()
        val factory = ViewModelFactory(Repository(dao))
        //获取viewModel实例
        viewModel = ViewModelProvider(this, factory).get(
            CalculatorViewModel::class.java
        )

        viewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }


        //根据查询到的信息来判断是否是新老用户
        viewModel.allInfo.observe(this) {
            if (!it.isNullOrEmpty()) {
                binding.bottomNavigationView.visibility = View.VISIBLE

            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        //更改状态栏字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        //fragment切换
        val fragmentManager = supportFragmentManager
        var mCurrentFragment: Fragment = CalculatorFragment()
        val transcation = fragmentManager.beginTransaction()
        transcation.add(R.id.fragment_container, mCurrentFragment, "calculator")
        transcation.show(mCurrentFragment).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {

            val transcation = fragmentManager.beginTransaction()
            transcation.hide(mCurrentFragment)
            when (it.itemId) {
                R.id.menu_calculator -> {
                    val calculatorFragment = fragmentManager.findFragmentByTag("calculator")
                    mCurrentFragment = if (calculatorFragment == null) {
                        CalculatorFragment().also {
                            transcation.add(R.id.fragment_container, it, "calculator")
                        }
                    } else {
                        calculatorFragment
                    }
                }


                R.id.menu_bmi -> {
                    val bmiFragment = fragmentManager.findFragmentByTag("bmi")
                    mCurrentFragment = if (bmiFragment == null) {
                        BmiFragment().also {
                            transcation.add(R.id.fragment_container, it, "bmi")
                        }
                    } else {
                        bmiFragment
                    }
                }

                R.id.menu_statistics -> {
                    val statisticFragment = fragmentManager.findFragmentByTag("statistic")
                    mCurrentFragment = if (statisticFragment == null) {
                        StatisticFragment().also {
                            transcation.add(R.id.fragment_container, it, "statistic")
                        }
                    } else {
                        statisticFragment
                    }
                }
            }
            transcation.show(mCurrentFragment).commit()
            true
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val context=newBase?.let {
            ContextWrapper.wrap(newBase,LanguageHelper.getLocale(newBase))
        }
        super.attachBaseContext(newBase)
    }
}