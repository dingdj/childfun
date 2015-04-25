package com.phy0312.childfun.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * description: 应用主要的SP<br/>
 * author: dingdj<br/>
 * date: 2014/11/24<br/>
 */
public class MainSp {

    private static MainSp instance;


    public static MainSp getInstance(Context context){
        if(instance == null) {
            instance = new MainSp(context);
        }
        return instance;
    }

    private static final String SP_NAME = "main_sp";

    private static final String CONFIG_KEY_FIRST_USE = "config_key_first_use";

    private static final String CONFIG_KEY_COOKIE = "config_key_cookie";
    /**
     * 用户名
     */
    private static final String INFO_KEY_USERNAME = "info_key_username";

    /**
     * 用户手机号码
     */
    private static final String INFO_KEY_PHONE = "info_key_phone";

    /**
     * 商场ID
     */
    private static final String INFO_KEY_PLAZA_ID = "info_key_plaza_id";



    private SharedPreferences sp;

    private boolean isFirstUse;
    private String cookie;
    private String phone;
    private String userName;
    private String plazaId;

    private MainSp(Context mContext) {
        sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        isFirstUse = sp.getBoolean(CONFIG_KEY_FIRST_USE, true);
        cookie = sp.getString(CONFIG_KEY_COOKIE, "");
        phone = sp.getString(INFO_KEY_PHONE, "");
        userName = sp.getString(INFO_KEY_USERNAME, "");
        plazaId = sp.getString(INFO_KEY_PLAZA_ID, "");
    }

    public boolean isFirstUse() {
        return isFirstUse;
    }

    public void setFirstUse(boolean isFirstUse) {
        this.isFirstUse = isFirstUse;
        sp.edit().putBoolean(CONFIG_KEY_FIRST_USE, isFirstUse).commit();
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
        sp.edit().putString(CONFIG_KEY_COOKIE, cookie).commit();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        sp.edit().putString(INFO_KEY_PHONE, phone).commit();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        sp.edit().putString(INFO_KEY_USERNAME, userName).commit();
    }

    public String getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(String plazaId) {
        this.plazaId = plazaId;
        sp.edit().putString(INFO_KEY_PLAZA_ID, plazaId).commit();
    }
}
