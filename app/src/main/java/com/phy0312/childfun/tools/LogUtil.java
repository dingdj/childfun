package com.phy0312.childfun.tools;

import android.util.Log;

/**
 * description: <br/>
 * author: dingdj<br/>
 * date: 2015/1/5<br/>
 */
public class LogUtil {
    public static final boolean DEBUG = true;
    public static final String TAG = LogUtil.class.getSimpleName()+"-------------------------";

    public static void e(String msg) {
        if(DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
