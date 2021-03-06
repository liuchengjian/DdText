package com.lchj.ddtexts.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * 设备相关的工具类
 * Created by liuguangli on 17/3/13.
 */

public class DevUtil {
    /**
     * 获取 UID
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String UUID(Context context) {
        TelephonyManager tm = (TelephonyManager)context
                        .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = "";
        try {
            tm.getDeviceId();
        } catch (Exception e) {
            Log.d("UUID", e.getMessage());
        }
        return deviceId + System.currentTimeMillis();
    }
    public static void closeInputMethod(Activity context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
