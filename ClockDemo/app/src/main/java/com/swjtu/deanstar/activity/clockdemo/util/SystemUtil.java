package com.swjtu.deanstar.activity.clockdemo.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;

/**
 * Created by yhp5210 on 2016/12/21.
 */

public class SystemUtil {

    public static Point getScreenSize(Activity activity){

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return new Point(width,height);
    }
}
