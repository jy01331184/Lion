package com.tech.android.treeview.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

/**
 * Created by tianyang on 16/12/29.
 */
public class SpanUtil {
    public static void append(String str, SpannableStringBuilder builder,Object what){
        int len = builder.length();
        builder.append(str);
        builder.setSpan(what,len,builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }
}
