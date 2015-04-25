package com.phy0312.childfun.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * description: <br/>
 * author: dingdj<br/>
 * date: 2014/11/25<br/>
 */
public class AndroidUtil {

    public static final String TAG = AndroidUtil.class.getSimpleName();

    public static final String MAILTO_EMAIL = "mailto:softtestddj@163.com";

    public static final String DEFAULT_NUM = "0312";

    private static float currentDensity = 0;

    /**
     * 安全启动一个Activity
     *
     * @param ctx
     * @param intent
     */
    public static void startActivity(Context ctx, Intent intent) {
        if (intent == null) return;
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取versionName
     *
     * @param context
     * @param packageName
     * @return String
     */
    public static String getVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            PackageInfo packageinfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionName = packageinfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return versionName;
    }


    /**
     * 获取手机信息
     *
     * @param ctx
     * @return 手机信息
     */
    public static String getPhoneInfo(Context ctx) {
        String result = "";
        try {
            result = "Phone=" + Build.MODEL + "\n";
            result += "CPU=" + getCPUABI() + "\n";
            result += "Resolution=" + getScreenResolution(ctx) + "\n";
            result += "FirmwareVersion=" + getFirmWareVersion() + "\n";
        } catch (Exception e) {
            return "";
        }

        return result;
    }


    /**
     * 获取CPU_ABI
     *
     * @return String
     */
    public static String getCPUABI() {
        String abi = Build.CPU_ABI;
        abi = (abi == null || abi.trim().length() == 0) ? "" : abi;
        // 检视是否有第二类型，1.6没有这个字段
        try {
            String cpuAbi2 = Build.class.getField("CPU_ABI2").get(null).toString();
            cpuAbi2 = (cpuAbi2 == null || cpuAbi2.trim().length() == 0) ? null : cpuAbi2;
            if (cpuAbi2 != null) {
                abi = abi + "," + cpuAbi2;
            }
        } catch (Exception e) {
        }
        return abi;
    }

    /**
     * 返回屏幕分辨率,字符串型。如 320x480
     *
     * @param ctx
     * @return String
     */
    public static String getScreenResolution(Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        String resolution = width + "x" + height;
        return resolution;
    }


    public static String getFirmWareVersion() {
        final String version_3 = "1.5";
        final String version_4 = "1.6";
        final String version_5 = "2.0";
        final String version_6 = "2.0.1";
        final String version_7 = "2.1";
        final String version_8 = "2.2";
        final String version_9 = "2.3";
        final String version_10 = "2.3.3";
        final String version_11 = "3.0";
        final String version_12 = "3.1";
        final String version_13 = "3.2";
        final String version_14 = "4.0";
        final String version_15 = "4.0.3";
        final String version_16 = "4.1.1";
        final String version_17 = "4.2";
        final String version_18 = "4.3";
        final String version_19 = "4.4";
        String versionName = "";
        try {
            // android.os.Build.VERSION.SDK_INT Since: API Level 4
            // int version = android.os.Build.VERSION.SDK_INT;
            int version = Integer.parseInt(Build.VERSION.SDK);
            switch (version) {
                case 3:
                    versionName = version_3;
                    break;
                case 4:
                    versionName = version_4;
                    break;
                case 5:
                    versionName = version_5;
                    break;
                case 6:
                    versionName = version_6;
                    break;
                case 7:
                    versionName = version_7;
                    break;
                case 8:
                    versionName = version_8;
                    break;
                case 9:
                    versionName = version_9;
                    break;
                case 10:
                    ;
                    versionName = version_10;
                    break;
                case 11:
                    versionName = version_11;
                    break;
                case 12:
                    versionName = version_12;
                    break;
                case 13:
                    versionName = version_13;
                    break;
                case 14:
                    versionName = version_14;
                    break;
                case 15:
                    versionName = version_15;
                    break;
                case 16:
                    versionName = version_16;
                    break;
                case 17:
                    versionName = version_17;
                    break;
                case 18:
                    versionName = version_18;
                    break;
                case 19:
                    versionName = version_19;
                    break;
                default:
                    versionName = version_7;
            }
        } catch (Exception e) {
            versionName = version_7;
        }
        return versionName;
    }


    /**
     * 取得IMEI号
     *
     * @param ctx
     * @return
     */
    public static String getIMEI(Context ctx) {
        if (ctx == null)
            return DEFAULT_NUM;

        String imei = DEFAULT_NUM;
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            if (imei == null || "".equals(imei))
                return DEFAULT_NUM;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imei;
    }

    public static String getIMSI(Context ctx) {
        if (ctx == null)
            return DEFAULT_NUM;

        String imsi = DEFAULT_NUM;
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (imsi == null || "".equals(imsi))
                return DEFAULT_NUM;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imsi;
    }


    /**
     * 获取App安装包信息
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if(info == null) info = new PackageInfo();
        return info;
    }


    /**
     * @param context context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context);
        if(info != null) {
            return info.versionCode;
        }
        return 1000;
    }


    /**
     * 网络是否可用
     * @param context
     * @return boolean
     */
    public synchronized static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (null != connectivityManager) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * wifi是否启动
     * @param ctx
     * @return boolean
     */
    public static boolean isWifiEnable(Context ctx) {
        if(ctx == null){
            return false;
        }
        Object obj = ctx.getSystemService(Context.WIFI_SERVICE);
        if (obj == null)
            return false;

        WifiManager wifiManager = (WifiManager) obj;
        return wifiManager.isWifiEnabled();
    }


    /**
     * dp转px
     * @param context
     * @param dipValue
     * @return int
     */
    public static int dip2px(Context context, float dipValue) {
        if (currentDensity > 0)
            return (int) (dipValue * currentDensity + 0.5f);

        currentDensity = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * currentDensity + 0.5f);
    }

    /**
     * 获取屏幕宽高
     * @return int[]
     */
    public static int[] getScreenWH(Context ctx) {
        int[] screenWH = { 480, 800 };
        try{
            if(ctx == null){
                Log.e("ScreenUtil.getScreenWH", "ApplicationContext is null!");
                return screenWH;
            }

            final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            final Display display = windowManager.getDefaultDisplay();
            boolean isPortrait = display.getWidth() < display.getHeight();
            final int width = isPortrait ? display.getWidth() : display.getHeight();
            final int height = isPortrait ? display.getHeight() : display.getWidth();
            screenWH[0] = width;
            screenWH[1] = height;
        }catch(Exception e){
            e.printStackTrace();
        }

        return screenWH;
    }
}
