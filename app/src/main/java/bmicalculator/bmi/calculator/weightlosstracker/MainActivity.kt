package bmicalculator.bmi.calculator.weightlosstracker

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.databinding.ActivityMainBinding
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorFragment
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val calculatorFragment by lazy {
        CalculatorFragment()
    }

    private lateinit var viewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //获取viewModel实例
        viewModel = ViewModelProvider(this).get(
            CalculatorViewModel::class.java
        )

        //判断底部导航栏是否显示
        if (viewModel.infoCount == 0) {
            binding.bottomNavigationView.visibility = View.GONE
        } else {
            binding.bottomNavigationView.post{
                val height=binding.bottomNavigationView.height
                val params =
                    binding.fragmentContainer.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin=height
                binding.fragmentContainer.layoutParams=params
            }
            binding.fragmentContainer.visibility = View.VISIBLE
        }

        //更改状态栏字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        //fragment切换
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, calculatorFragment)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.user -> Toast.makeText(this, "you click me", Toast.LENGTH_SHORT).show()
            else -> return false
        }
        return true
    }
}