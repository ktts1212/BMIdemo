package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentSettingBinding
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.destroyImmersionBar
import com.gyf.immersionbar.ktx.immersionBar

class SettingFragment :DialogFragment() {

    private lateinit var binding:FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val window = dialog?.window
//        if (window != null) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = Color.WHITE
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            (activity as AppCompatActivity).window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }
        binding= FragmentSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        binding.settingBack.setOnClickListener {
            onDestroyView()
        }

        binding.settingLanguageCard.setOnClickListener {
            val dialog=LanguageSelectFragment()
            dialog.show(childFragmentManager,"LanguageSelectFragment")
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        val displayMetrics = resources.displayMetrics
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.gravity = Gravity.BOTTOM
        params?.dimAmount = 0.0f
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        immersionBar {
            statusBarColor(R.color.bg1)
            statusBarDarkFont(true)
            titleBar(view)
        }
    }

    override fun onStop() {
        super.onStop()
    }

}