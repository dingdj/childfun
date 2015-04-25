package com.phy0312.childfun.tools;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ddj on 15-4-5.
 */
public class ShareUtil {

    /**
     * 分享
     * @param context
     * @param text
     */
    public static void share(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        try {
            context.startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
