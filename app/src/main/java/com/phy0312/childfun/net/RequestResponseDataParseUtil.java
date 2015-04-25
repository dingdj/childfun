package com.phy0312.childfun.net;


import com.phy0312.childfun.tools.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 请求体和返回体解析帮助类
 * Created by dingdj on 2014/7/20.
 */
public class RequestResponseDataParseUtil {

    public static final int ACTION_UNIMPLEMENT= -1;
    public static final int RESPONSE_OK = 0;
    public static final int UNLOGIN = 1;
    public static final int ACTION_UNSUPPORT = 2;
    public static final int PARAM_ERROR = 3;
    public static final int PARAM_UNLEGAL = 4;
    public static final int OTHER_FAULT = 60000000;


    /**
     * 处理服务端返回数据通用类
     */
    public static abstract class ResponseParse {

        public void onResponse(JSONObject response) {
            try {
                int code = response.optInt(ResponseUtil.RESULT_CODE);
                JSONObject dataJsonObject = response.getJSONObject(ResponseUtil.RESULT_BODY);
                switch (code) {
                    case RequestResponseDataParseUtil.RESPONSE_OK: //请求成功
                        parseResponseDataSection(dataJsonObject);
                        break;
                    case RequestResponseDataParseUtil.UNLOGIN: //用户未登录
                        actionUserUnLogin(dataJsonObject);
                        break;

                    default: //500错误
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //未知异常
            }
        }

        /**
         * 处理服务端返回数据data部分
         * @param dataJsonObject
         */
        public abstract void parseResponseDataSection(JSONObject dataJsonObject);


        /**
         * 用户未登录
         * @param headObject
         */
        public void actionUserUnLogin(JSONObject headObject) {
        }

    }


}

