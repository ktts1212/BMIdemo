package bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity
@Parcelize
data class BmiInfo(
    var wt_lb: Double = 140.00,
    var wt_kg: Double = 65.00,
    var ht_ft: Int = 5,
    var ht_in: Int = 7,
    var ht_cm: Double = 170.0,
    var date: String? = null,
    var phase: String? = null,
    var age: Int = 25,
    var gender: Char = '0',
    var bmi:Float=0f,
    var save_time:Int=-1,
    var year:Int=-1,
    var bmiType:String?=null,
    var wtHtType:String?=null,
    var timestape:Long=0
) :Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}