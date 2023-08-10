package bmicalculator.bmi.calculator.weightlosstracker

import android.content.Context
import android.os.Bundle
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

private const val TAG = "MainActivity"

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: CalculatorViewModel

    private var firstStart=0

    private lateinit var currentFragmentTag:String

    private lateinit var mCurrentFragment:Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        //删除fragment堆栈中的所有fragment
//        val fragments=supportFragmentManager.fragments
//        val ft=supportFragmentManager.beginTransaction()
//        for(fragment in fragments){
//            if (fragment!=null){
//                ft.remove(fragment)
//            }
//        }
//        ft.commit()

        if (savedInstanceState!=null){
            currentFragmentTag= savedInstanceState.getString("currentFragmentTag").toString()
        }else{
            currentFragmentTag="calculator"
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
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

//        if (firstStart==0){
//            binding.bottomNavigationView.selectedItemId=R.id.menu_bmi
//            val fragmentManager=supportFragmentManager
//            val transition=fragmentManager.beginTransaction()
//            transition.replace(R.id.fragment_container, BmiFragment())
//            transition.commit()
//            currentFragmentTag="statistic"
//            firstStart=1
//        }



        getFgByTag(currentFragmentTag)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, mCurrentFragment, currentFragmentTag)
        transaction.show(mCurrentFragment).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.hide(mCurrentFragment)
            when (it.itemId) {
                R.id.menu_calculator -> {
                    mCurrentFragment = supportFragmentManager.findFragmentByTag("calculator")
                        ?: CalculatorFragment().also {fg->
                            transaction.add(R.id.fragment_container, fg, "calculator")
                        }
                    currentFragmentTag="calculator"
                    transaction.show(mCurrentFragment).commit()
                }


                R.id.menu_bmi -> {

                    mCurrentFragment = supportFragmentManager.findFragmentByTag("bmi")
                        ?: BmiFragment().also { fg ->
                            transaction.add(R.id.fragment_container, fg, "bmi")
                        }
                    currentFragmentTag="bmi"
                    transaction.show(mCurrentFragment).commit()
                }

                R.id.menu_statistics -> {

                    mCurrentFragment = supportFragmentManager.findFragmentByTag("statistic")
                        ?: StatisticFragment().also {fg->
                            transaction.add(R.id.fragment_container, fg, "statistic")
                        }
                    currentFragmentTag="statistic"
                    transaction.show(mCurrentFragment).commit()
                }
            }

            true
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val context=newBase?.let {
            ContextWrapper.wrap(newBase,LanguageHelper.getLocale(newBase))
        }
        super.attachBaseContext(newBase)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentFragmentTag",currentFragmentTag)
    }

    fun reGetFragment(tag:String){
        val transaction=supportFragmentManager.beginTransaction()
        when(tag){
            "statistic"-> {
                mCurrentFragment=StatisticFragment()
            }
            "bmi"-> {
                mCurrentFragment=BmiFragment()

            }
            "calculator"-> {
                mCurrentFragment=CalculatorFragment()
            }
        }
        transaction.add(R.id.fragment_container,mCurrentFragment,tag)
        transaction.show(mCurrentFragment).commit()
    }

    fun getFgByTag(tag:String){
        when(tag){
            "statistic"-> {
                mCurrentFragment=StatisticFragment()
            }
            "bmi"-> {
                mCurrentFragment=BmiFragment()

            }
            "calculator"-> {
                mCurrentFragment=CalculatorFragment()
            }
        }
    }
}