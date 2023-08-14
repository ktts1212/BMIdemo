package bmicalculator.bmi.calculator.weightlosstracker

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import bmicalculator.bmi.calculator.weightlosstracker.databinding.ActivitySplashBinding
import bmicalculator.bmi.calculator.weightlosstracker.util.interpolator.BezierInterpolator
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import java.util.Timer
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity(), Animator.AnimatorListener {

    private lateinit var binding: ActivitySplashBinding

    //前一秒指针转动
    private lateinit var anim1: ObjectAnimator

    //前一秒变盘位移
    private lateinit var anim2: ObjectAnimator

    //前一秒指针位移
    private lateinit var anim3: ObjectAnimator

    //前一秒标题位移
    private lateinit var anim4: ObjectAnimator

    //第二秒指针转动
    private lateinit var anim5: ObjectAnimator

    //属性集合
    private val animSet = AnimatorSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initobjAnimator()
    }

    fun initobjAnimator() {
        //前一秒指针转动
        binding.logoArrow.pivotX = Utils.dip2px(this, 4f).toFloat()
        binding.logoArrow.pivotY = Utils.dip2px(this, 4f).toFloat()
        Log.d("SplashAc", (binding.logoArrow.width.toFloat()).toString())
        anim1 = ObjectAnimator.ofFloat(
            binding.logoArrow, "rotation",
            0f, 230f
        )
        anim1.setDuration(1000)
        anim1.setInterpolator(BezierInterpolator(0.25f, 0f, 0.1f, 0.1f))
        //前一秒表盘轻微位移
        anim2 = ObjectAnimator.ofFloat(binding.logoDial, "translationY", 0f, -50f)
        anim2.setDuration(1000)
        //前一秒指针位移
        anim3 = ObjectAnimator.ofFloat(binding.logoArrow, "translationY", 0f, -50f)
        anim3.setDuration(1000)
        //前一秒标题位移
        anim4 = ObjectAnimator.ofFloat(binding.logoText, "translationY", 0f, -50f)
        anim4.setDuration(1000)
        //第二秒指针转动
        anim5 = ObjectAnimator.ofFloat(
            binding.logoArrow, "rotation",
            230f, 130f
        )
        anim5.setDuration(1000)
        anim5.setInterpolator(BezierInterpolator(0.25f, 0f, 0.1f, 0.1f))
        val builder: AnimatorSet.Builder = animSet.play(anim1)
        builder.with(anim2).with(anim3).with(anim4).before(anim5)
        animSet.duration = 1000
        animSet.start()
        animSet.addListener(this)
    }

    fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    override fun onAnimationStart(p0: Animator) {
    }

    override fun onAnimationEnd(p0: Animator) {
        Timer().schedule(1000) {
            toMainActivity()
        }

    }

    override fun onAnimationCancel(p0: Animator) {
    }

    override fun onAnimationRepeat(p0: Animator) {
    }

}