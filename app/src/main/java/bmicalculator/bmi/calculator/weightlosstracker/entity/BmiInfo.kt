package bmicalculator.bmi.calculator.weightlosstracker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class BmiInfo(
    var wt_lb: Double, var wt_kg: Double, var ht_ft: Double, var ht_in: Double,
    var date: Long, var age: Int, var gender: Char
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}