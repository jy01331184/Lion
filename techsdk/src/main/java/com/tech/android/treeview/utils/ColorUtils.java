package com.tech.android.treeview.utils;

import android.graphics.Color;

/**
 * Created by tianyang on 16/12/28.
 */
public class ColorUtils {
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int COLOR1 = Color.parseColor("#9dc794");
    public static final int COLOR2 = Color.parseColor("#2272eb");
    public static final int COLOR3 = Color.parseColor("#6b14ee");
    public static final int COLOR4 = Color.parseColor("#e3ff8e");
    public static final int COLOR5 = Color.parseColor("#9ffffe");
    public static final int COLOR6 = Color.parseColor("#a23c3c");
    public static final int COLOR7 = Color.parseColor("#bfbdbd");
    public static final int COLOR8 = Color.parseColor("#fd77ff");
    public static final int COLOR9 = Color.parseColor("#85ff85");


    public static final int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED,COLOR1,COLOR2,COLOR3,COLOR4,COLOR5,COLOR6,COLOR7,COLOR8,COLOR9};

    private static int COLOR_INDEX = 0;

    public static final int pickColor() {
        return COLORS[(int) Math.round(Math.random() * (COLORS.length - 1))];
    }

    public static final int nextColor() {
        if (COLOR_INDEX >= COLORS.length) {
            COLOR_INDEX = 0;
        }
        return COLORS[COLOR_INDEX++];
    }
}
