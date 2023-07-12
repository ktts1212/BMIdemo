package bmicalculator.bmi.calculator.weightlosstracker.ui.calculator.child

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Binder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import bmicalculator.bmi.calculator.weightlosstracker.databinding.FragmentDatePickerBinding

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener{

    private lateinit var binding:FragmentDatePickerBinding

    val months= mutableListOf<String>("Jan","Feb","Mar","Apr","May","June"
    ,"July","Aug","Sep","Oct","Nov","Dec")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDatePickerBinding.inflate(layoutInflater,container,false)
        binding.wheelPickerDateYearWheel.setRange(2000,2035,1)
        binding.wheelPickerDateMonthWheel.setData(months)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onResume() {
        super.onResume()
        val params=dialog?.window?.attributes
        val displayMetrics=resources.displayMetrics
        params?.width=WindowManager.LayoutParams.MATCH_PARENT
        params?.height=(displayMetrics.heightPixels*0.5).toInt()
        params?.gravity=Gravity.BOTTOM
        dialog?.window?.attributes=params as WindowManager.LayoutParams
    }
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val c=Calendar.getInstance()
//        val year=c.get(Calendar.YEAR)
//        val month=c.get(Calendar.MONTH)
//        val day=c.get(Calendar.DAY_OF_MONTH)
//        val datePickerDialog=DatePickerDialog(requireActivity(),this,year,month,day)
//        datePickerDialog.datePicker.calendarViewShown=false
//        datePickerDialog.datePicker.spinnersShown=true
//        return datePickerDialog
//    }

}