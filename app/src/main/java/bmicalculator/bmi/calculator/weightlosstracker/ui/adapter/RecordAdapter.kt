package bmicalculator.bmi.calculator.weightlosstracker.ui.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import bmicalculator.bmi.calculator.weightlosstracker.R
import bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity.BmiInfo
import bmicalculator.bmi.calculator.weightlosstracker.ui.bmi.child.RecordFragment

class RecordAdapter(private val context: Context,val bmiInfoList:ArrayList<BmiInfo>,
private val childFragmentManager: FragmentManager):RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recordBmi: TextView = view.findViewById(R.id.record_bmi)
        val recordDot: ImageView = view.findViewById(R.id.record_dot)
        val recordType: TextView = view.findViewById(R.id.record_type)
        val recordDate: TextView = view.findViewById(R.id.record_date)
        val recordPhase: TextView = view.findViewById(R.id.record_phase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val dialog=RecordFragment()
            val bundle=Bundle()
            val position=viewHolder.bindingAdapterPosition
            bundle.putParcelable("cBI",bmiInfoList[position])
            dialog.arguments=bundle
            dialog.show(childFragmentManager,"record")
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return bmiInfoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recordBmi.setText(bmiInfoList[position].bmi.toString())
        holder.recordDate.setText(bmiInfoList[position].date)
        holder.recordPhase.setText(bmiInfoList[position].phase)
        holder.recordType.setText(getBmiType(context, bmiInfoList[position].bmiType!!,holder))
    }

    fun getBmiType(context: Context, bmiType: String,holder:ViewHolder): String {
        when (bmiType) {
            "vsuw" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.vsuw
                        )
                    )
                )
                return context.getString(R.string.vsuw)
            }

            "suw" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.suw
                        )
                    )
                )
                return  context.getString(R.string.suw)
            }

            "uw" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.uw
                        )
                    )
                )
                return  context.getString(R.string.uw)
            }

            "nm" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.normal
                        )
                    )
                )
                return context.getString(R.string.nm)
            }

            "ow" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.ow
                        )
                    )
                )
                return  context.getString(R.string.ow)
            }

            "oc1" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.oc1
                        )
                    )
                )
                return  context.getString(R.string.oc1)
            }

            "oc2" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.oc2
                        )
                    )
                )
                return  context.getString(R.string.oc2)
            }

            "oc3" -> {
                ViewCompat.setBackgroundTintList(
                    holder.recordDot, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,R.color.oc3
                        )
                    )
                )
                return  context.getString(R.string.oc3)
            }

            else ->{
                return "error"
            }
        }
    }

}

