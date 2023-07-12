package bmicalculator.bmi.calculator.weightlosstracker.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import bmicalculator.bmi.calculator.weightlosstracker.R

class AgeSelectorAdapter(val ageList: ArrayList<String?>):RecyclerView.Adapter<AgeSelectorAdapter.ViewHolder>() {

    private var selectPosition=26

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val ageView:TextView=view.findViewById(R.id.age_select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.item_age_select,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.ageView.setOnClickListener {
            Toast.makeText(parent.context,
                "you clicked me,me value is ${viewHolder.ageView.text}",
                Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return ageList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (selectPosition==position){
            holder.ageView.setTextColor(Color.parseColor("#000000"))
        }else{
            holder.ageView.setTextColor(Color.parseColor("#DCDCDC"))
        }


        holder.ageView.setText(ageList.get(position))

//        if (TextUtils.isEmpty(ageList.get(position))){
//            holder.itemView.visibility=View.INVISIBLE
//        }else{
//            holder.ageView.visibility=View.VISIBLE
//            holder.ageView.setText(ageList.get(position))
//        }

    }

    public fun setSelectPosition(cposition: Int){
        selectPosition=cposition
        notifyDataSetChanged()
    }
}