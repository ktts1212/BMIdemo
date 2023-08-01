package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentTimePickerBinding
import bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.CalculatorViewModel
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import java.time.LocalDateTime

private const val TAG = "TimePickerFragment"

class TimePickerFragment : DialogFragment() {

    private lateinit var binding: FragmentTimePickerBinding

    val phases = mutableListOf<String>("Morning", "Afternoon", "Evening", "Night")

//    private var isValid=false
//
//    private var toastCount=0

    private lateinit var mode: CalculatorViewModel

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val current = LocalDateTime.now()
        binding = FragmentTimePickerBinding.inflate(layoutInflater, container, false)
        Log.d(TAG, hourtophase(current))
        binding.wheelPickerTimePhase.setData(phases)
        binding.wheelPickerTimePhase.setDefaultValue(hourtophase(current))
        binding.wheelPickerTimePhase.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.montserrat_extrabold)
        mode = ViewModelProvider(requireActivity()).get(CalculatorViewModel::class.java)
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
                //               val current=LocalDateTime.now()
//                if (phases.indexOf(view?.getCurrentItem())>phases.indexOf(hourtophase(current))){
//                    binding.btnDonePhase.setBackgroundResource(R.drawable.shape_btn_done_invalid)
//                    binding.btnDonePhase.setTextColor(Color.parseColor("#000000"))
//                    isValid = false
//                    if (!isValid && toastCount == 0) {
//                        Toast.makeText(
//                            requireContext(), "Please select a date that is earlier than today",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        toastCount = 1
//                    }
//                }else{
//                    binding.btnDonePhase.setBackgroundResource(R.drawable.shape_btn_done)
//                    binding.btnDonePhase.setTextColor(Color.parseColor("#FFFFFF"))
//                    isValid=true
//                    toastCount=0
//                }
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
            mode.setPhase(binding.wheelPickerTimePhase.getCurrentItem())
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

    fun hourtophase(current: LocalDateTime): String {

        val phase: String = if (current.hour >= 23 && current.hour < 8) {
            "Night"
        } else if (current.hour >= 8 && current.hour < 14) {
            "Morning"
        } else if (current.hour >= 14 && current.hour < 19) {
            "Afternoon"
        } else {
            "Evening"
        }

        return phase
    }
}