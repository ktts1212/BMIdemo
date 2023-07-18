package bmicalculator.bmi.calculator.weightlosstracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
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

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val fragments = lazy {
//        listOf(
//            CalculatorFragment(),
//            BmiFragment(),
//            StatisticFragment()
//        )
//    }

//    private val calculatorFragment by lazy {
//        CalculatorFragment()
//    }
//
//    private val bmiFragment by lazy {
//        BmiFragment()
//    }
//
//    private val statisticFragment by lazy {
//        StatisticFragment()
//    }

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.toolbar)

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

        viewModel.getAllInfo()

        Log.d(TAG, "num:${viewModel.infoCount.value}")
        //判断底部导航栏是否显示

        viewModel.infoCount.observe(this) {
            Log.d(TAG, it.toString())
            if (it != -1) {
                binding.bottomNavigationView.post {
                    val height = binding.bottomNavigationView.height
                    val params =
                        binding.fragmentContainer.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = height
                    binding.fragmentContainer.layoutParams = params
                }
                //binding.fragmentContainer.visibility = View.VISIBLE
                binding.bottomNavigationView.visibility = View.VISIBLE
                Log.d(TAG, "执行到了这里>>>>")
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
        //val transaction = fragmentManager.beginTransaction()
        var mCurrentFragment:Fragment = CalculatorFragment()
        val transcation=fragmentManager.beginTransaction()
        transcation.add(R.id.fragment_container,mCurrentFragment,"calculator")
        transcation.show(mCurrentFragment).commit()
//        transaction.add(calculatorFragment,"Calculator")
//        transaction.add(bmiFragment,"Bmi")
//        transaction.add(statisticFragment,"Statistic")
        //transaction.replace(R.id.fragment_container, calculatorFragment)
//        transaction.show(calculatorFragment)
//        transaction.commit()


        binding.bottomNavigationView.setOnItemSelectedListener {

            val transcation = fragmentManager.beginTransaction()
            transcation.hide(mCurrentFragment)

            when (it.itemId) {
                R.id.menu_calculator ->{
                    val calculatorFragment = fragmentManager.findFragmentByTag("calculator")
                    mCurrentFragment=if (calculatorFragment==null){
                        CalculatorFragment().also {
                            transcation.add(R.id.fragment_container,it,"calculator")
                        }
                    }else{
                        calculatorFragment
                    }
                }


                R.id.menu_bmi -> {
                    val bmiFragment = fragmentManager.findFragmentByTag("bmi")
                    mCurrentFragment=if (bmiFragment==null){
                        BmiFragment().also {
                            transcation.add(R.id.fragment_container,it,"bmi")
                        }
                    }else{
                        bmiFragment
                    }
                }
                R.id.menu_statistics->{
                    val statisticFragment = fragmentManager.findFragmentByTag("statistic")
                    mCurrentFragment=if (statisticFragment==null){
                        StatisticFragment().also {
                            transcation.add(R.id.fragment_container,it,"statistic")
                        }
                    }else{
                        statisticFragment
                    }
                }
            }
            transcation.show(mCurrentFragment).commit()
            true
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.user -> Toast.makeText(this, "you click me", Toast.LENGTH_SHORT).show()
//            else -> return false
//        }
//        return true
//    }


}