package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentTimePickerBinding
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import bmicalculator.bmi.calculator.weightlosstracker.util.Utils
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import java.time.LocalDateTime

class TimePickerFragment : DialogFragment() {

    private lateinit var binding: FragmentTimePickerBinding

    private lateinit var mode: CalculatorViewModel

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val phases = mutableListOf(
            getString(R.string.morning),
            getString(R.string.afternoon), getString(R.string.evening), getString(R.string.night)
        )
        val current = LocalDateTime.now()
        binding = FragmentTimePickerBinding.inflate(layoutInflater, container, false)
        binding.wheelPickerTimePhase.data = phases
        binding.wheelPickerTimePhase.setDefaultValue(hourToPhase(current))
        binding.wheelPickerTimePhase.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
        mode = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]
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

        binding.wheelPickerTimePhase.setOnWheelChangedListener(object : OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {

            }

        })

        binding.btnCancelPhase.setOnClickListener {
            onDestroyView()
        }

        binding.btnDonePhase.setOnClickListener {
            mode.setPhase(
                Utils.phaseToNum(
                    requireContext(), binding.wheelPickerTimePhase.getCurrentItem()
                )
            )
            onDestroyView()
        }
    }


    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        val displayMetrics = resources.displayMetrics
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = (displayMetrics.heightPixels * 0.5).toInt()
        params?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun hourToPhase(current: LocalDateTime): String {

        val phase: String = if ((current.hour >= 23) || (current.hour < 8)) {
            getString(R.string.night)
        } else if (current.hour in 8..13) {
            getString(R.string.morning)
        } else if (current.hour in 14..18) {
            getString(R.string.afternoon)
        } else {
            getString(R.string.evening)
        }

        return phase
    }
}