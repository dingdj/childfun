package com.phy0312.childfun.net;


import com.phy0312.childfun.ChildFunApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * description: <br/>
 * author: dingdj<br/>
 * date: 2014/12/1<br/>
 */
public class URLManager {

    public static String DOMAIN = "http://42.121.28.162:8099";

    //服务端接口地址

    public static String FEED_LIST = DOMAIN+"/Admin/PhoneService/qinziyou/1";

    public static String FEED_DETAIL_LIST = DOMAIN+"/Admin/PhoneService/qinziyou/2";

    public static String VERSION_UPDATE = DOMAIN+"/Admin/PhoneService/qinziyou/3";




    public static String utf8Encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDealUrl() {
        if(ChildFunApplication.appContext.curLatLng == null) {
            return FEED_LIST;
        }else{
            double latitude = ChildFunApplication.appContext.curLatLng[0];
            double longtitude = ChildFunApplication.appContext.curLatLng[1];
            return "http://lite.m.dianping.com/Fy6ffEz88j?uid=com_phy0312_shopassistant&longitude="+longtitude+"&latitude="+latitude+"&" +
                    "hasheader=1&category="+ utf8Encode("美食");
        }
    }
}
