package zx.com.genius.cn.myapplication;

import android.content.Context;
import android.graphics.PointF;

/**
 * @author: Genius on 2018/11/6
 * @package: zx.com.genius.cn.myapplication
 * @function:
 */
public class Utils {

    /**
     * 将dp转换为px
     * @param context 上下文环境
     * @param dp dp值
     * @return px值
     */
    public static int dip2px(Context context, int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5);
    }

    /** px转换dip */
    public static int px2dip(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    /** px转换sp */
    public static int px2sp(Context context, int pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    /** sp转换px */
    public static int sp2px(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float evaluateTwoPointDis(PointF startPoint, PointF endPoint){
        return (float) Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
    }
}
