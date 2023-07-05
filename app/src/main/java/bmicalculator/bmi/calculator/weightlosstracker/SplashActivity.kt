package bmicalculator.bmi.calculator.weightlosstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import bmicalculator.bmi.calculator.weightlosstracker.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}