package com.phy0312.childfun.tools;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * Created by dingdj on 2014/12/6.
 */
public class LocationUtil {

    /**
     * 启动定位配置
     * @author dingdj
     * Date:2013-11-8下午3:34:06
     */
    public static void setLocationOption(LocationClient mLocClient){
        if(mLocClient == null) return;
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("gcj02");
        option.setOpenGps(false);
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setScanSpan(1000*60*5); //请求定位时间间隔5分钟
        option.setProdName("com.phy0312.shopassistant");
        mLocClient.setLocOption(option);
    }

    /**
     * 计算两点间距离
     * @param p1
     * @param p2
     * @return
     */
    public static String calcDistance(GeoPoint p1, GeoPoint p2) {
        if(p1 == null || p2 == null) return "";
        return String.format("%.1f", DistanceUtil.getDistance(p1, p2)/1000);
    }
}
