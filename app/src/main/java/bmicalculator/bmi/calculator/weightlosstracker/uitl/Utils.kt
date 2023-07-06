package flashlight.flashlightapp.ledlight.torch.uitl

import android.content.Context

object Utils {

    //根据手机分辨率从dp单位转换为px（像素）
    fun dip2px(context: Context,deValue:Float): Int {
        val scale=context.resources.displayMetrics.density
        //四舍五入取整
        return (deValue*scale+0.5f).toInt()
    }
    //根据手机分辨率从px单位转换成dp
    fun px2dip(context: Context,pxValue:Float):Int{
        val scale=context.resources.displayMetrics.density
        return (pxValue/scale+0.5f).toInt()
    }
}