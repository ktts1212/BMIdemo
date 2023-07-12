package bmicalculator.bmi.calculator.weightlosstracker.logic.model.entity

data class DateEntity(var year:Int,var month:Int,var day:Int) {
    companion object{
        fun target(year: Int,month: Int,dayOfMonth:Int):DateEntity{
            return DateEntity(year,month,dayOfMonth)
        }
    }
}