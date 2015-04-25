package com.phy0312.childfun.tools;

import android.util.Log;

import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.config.MainSp;
import com.phy0312.childfun.net.RequestResponseDataParseUtil;

import java.net.URLDecoder;
import java.util.Map;

/**
 * description: <br/>
 * author: dingdj<br/>
 * data: 2014/12/1<br/>
 */
public class ResponseUtil {
    public static final String TAG = ResponseUtil.class.getSimpleName();

    private static final String SET_COOKIE_KEY = "Set-Cookie";

    private static final String COOKIE_KEY = "Cookie";

    public static final String BODY_ENCRYPT_TYPE = "BodyEncryptType";

    /**
     * 0:普通
     */
    public static final int ENCRYPT_NORMAL = 0;

    /**
     * 1:gzip
     */
    public static final int ENCRYPT_GZIP = 1;

    public static final String RESULT_CODE = "ResultCode";

    public static final String RESULT_MESSAGE = "ResultMessage";

    public static final String RESULT_BODY = "ResultBody";
    /**
     * 保存cookie
     * @param headers
     */
    public static void saveSessionCookie(Map<String, String> headers){
        if (headers.containsKey(SET_COOKIE_KEY)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            if (cookie.length() > 0) {
                MainSp.getInstance(ChildFunApplication.appContext).setCookie(cookie);
            }
        }
    }

    /**
     * 如果存在cookie設置
     * @param headers
     */
    public static void addCookieIfHave(Map<String, String> headers){
        String cookie = MainSp.getInstance(ChildFunApplication.appContext).getCookie();
        if(!StringUtils.isEmpty(cookie)) {
            headers.put(COOKIE_KEY, cookie);
        }
    }

    /**
     * 获取响应加密方式
     * @param headers
     */
    public static int getBodyEncryptType(Map<String, String> headers){
        try {
            int type =  Integer.parseInt(headers.get(BODY_ENCRYPT_TYPE));
            return type;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param headers
     */
    public static int getResultCode(Map<String, String> headers) {
        try {
            int resultCode =  Integer.parseInt(headers.get(RESULT_CODE));
            Log.e(TAG, resultCode+"");
            return resultCode;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return RequestResponseDataParseUtil.OTHER_FAULT;
    }

    /**
     * @param headers
     */
    public static String getResultMessage(Map<String, String> headers) {
        try {
            String resultMessage = URLDecoder.decode(headers.get(RESULT_MESSAGE), "utf-8");
            Log.e(TAG, resultMessage);
            return resultMessage;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "other fault";
    }


}
