package bmicalculator.bmi.calculator.weightlosstracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: CalculatorViewModel

    private var firstStart=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragments=supportFragmentManager.fragments
        val ft=supportFragmentManager.beginTransaction()
        for(fragment in fragments){
            if (fragment!=null){
                ft.remove(fragment)
            }
        }
        ft.commit()


        if (supportFragmentManager.findFragmentByTag("statistic")!=null){
            Log.d(TAG,"存在")
            val fT=supportFragmentManager.beginTransaction()
            fT.remove(supportFragmentManager.findFragmentByTag("statistic")!!)
            fT.commit()
        }

        val dao = AppDataBase.getDatabase(application).bmiInfoDao()
        val factory = ViewModelFactory(Repository(dao))
        //获取viewModel实例
        viewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]

        viewModel.message.observe(this) {event->
            event.getContentIfNotHandled()?.let {msg->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }


        //根据查询到的信息来判断是否是新老用户
        viewModel.allInfo.observe(this) {
            if (!it.isNullOrEmpty()) {
                binding.bottomNavigationView.visibility = View.VISIBLE
                if (firstStart==0){
                    binding.bottomNavigationView.selectedItemId=R.id.menu_bmi
                    val fragmentManager=supportFragmentManager
                    val transition=fragmentManager.beginTransaction()
                    transition.replace(R.id.fragment_container, BmiFragment())
                    transition.commit()
                    firstStart=1
                }
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        //更改状态栏字体颜色

        WindowCompat.setDecorFitsSystemWindows(window,true)

        //fragment切换
        val fragmentManager = supportFragmentManager
        var mCurrentFragment: Fragment = CalculatorFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, mCurrentFragment, "calculator")
        transaction.show(mCurrentFragment).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {

            val transaction = fragmentManager.beginTransaction()
            transaction.hide(mCurrentFragment)
            when (it.itemId) {
                R.id.menu_calculator -> {
                    mCurrentFragment = fragmentManager.findFragmentByTag("calculator")
                        ?: CalculatorFragment().also {fg->
                            transaction.add(R.id.fragment_container, fg, "calculator")
                        }
                }


                R.id.menu_bmi -> {
                    mCurrentFragment = fragmentManager.findFragmentByTag("bmi")
                        ?: BmiFragment().also { fg ->
                            transaction.add(R.id.fragment_container, fg, "bmi")
                        }
                }

                R.id.menu_statistics -> {
                    mCurrentFragment = fragmentManager.findFragmentByTag("statistic")
                        ?: StatisticFragment().also {fg->
                            transaction.add(R.id.fragment_container, fg, "statistic")
                        }
                }
            }
            transaction.show(mCurrentFragment).commit()
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