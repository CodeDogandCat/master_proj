package cn.edu.hfut.lilei.shareboard.utils;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

public class ScreenUtil {
    /**
     * pixel转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static float convertPixelsToDp(Context context, float px) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    /**
     * dp转pixel
     *
     * @param context
     * @param dp
     * @return
     */
    public static float convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = Resources.getSystem()
                .getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static float pxFromDp(Context context, float dp) {
        return dp * context.getResources()
                .getDisplayMetrics().density;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    /**
     * px转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static int convertPxToDp(Context context, int px) {
        return Math.round(px / (Resources.getSystem()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * px转dp
     *
     * @param context
     * @param px
     * @return
     */
    public static float dpFromPx(Context context, float px) {
        return px / context.getResources()
                .getDisplayMetrics().density;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels - getStatusBarHeight(context);
    }

    /**
     * 获取屏幕的高度（像素）
     *
     * @param context
     * @return the pixels of the screen height
     */
    public static int getHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj)
                    .toString());
            statusBarHeight = context.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取工具栏高度
     *
     * @param context
     * @return
     */
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme()
                .obtainStyledAttributes(
                        new int[]{android.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }


}
