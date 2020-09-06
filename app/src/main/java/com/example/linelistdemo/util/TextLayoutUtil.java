package com.example.linelistdemo.util;

import android.content.Context;
import android.text.TextUtils;

public class TextLayoutUtil {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static String canceltoPoint(String data) {
        if (TextUtils.isEmpty(data)) return "0";
        if ((data).contains(".0")) {
            return (data).replace(".0", "");
        }
        return data;
    }

}
