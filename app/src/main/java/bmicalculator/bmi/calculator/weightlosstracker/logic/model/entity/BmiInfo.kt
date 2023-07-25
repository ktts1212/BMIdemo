package bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
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
    var bmi:Float=0f
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}