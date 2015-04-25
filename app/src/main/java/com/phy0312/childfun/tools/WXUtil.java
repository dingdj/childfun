package com.phy0312.childfun.tools;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * description: <br/>
 * author: dingdongjin_91<br/>
 * date: 2014/12/17<br/>
 */
public class WXUtil {

    public static final boolean isSupportWX(Context context) {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false);
        return wxapi.isWXAppInstalled() && wxapi.isWXAppSupportAPI();
    }

    public static final boolean isWXInstalled(Context context) {
        return WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false).isWXAppInstalled();
    }

    public static final boolean isSupportTimeLine(Context context) {
        int wxSdkVersion = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, false).getWXAppSupportAPI();
        return (wxSdkVersion >= Constants.TIMELINE_SUPPORTED_VERSION);
    }
}
