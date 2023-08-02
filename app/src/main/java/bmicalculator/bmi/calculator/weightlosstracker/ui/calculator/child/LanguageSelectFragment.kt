package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

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
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentLanguageSelectBinding

class LanguageSelectFragment : DialogFragment() {

    private lateinit var binding:FragmentLanguageSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.WHITE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as AppCompatActivity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding= FragmentLanguageSelectBinding.inflate(layoutInflater,container,false)
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
    }
}