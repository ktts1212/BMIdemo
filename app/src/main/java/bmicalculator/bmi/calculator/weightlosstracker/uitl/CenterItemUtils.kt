package bmicalculator.bmi.calculator.weightlosstracker.uitl

object CenterItemUtils {

    fun getMinDifferItem(itemHeights:List<CenterViewItem>):CenterViewItem{
        var minItem=itemHeights.get(0)
        for (i in itemHeights.indices){
            //遍历获取最小插值
            if (itemHeights.get(i).differ<=minItem.differ){
                minItem=itemHeights.get(i)
            }
        }
        return minItem
    }

    data class CenterViewItem(var position:Int,var differ:Int){
    }
}