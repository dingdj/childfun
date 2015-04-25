package com.phy0312.childfun;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.BMapManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.phy0312.childfun.tools.CrashHandler;
import com.phy0312.childfun.tools.LocationUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;

import timber.log.Timber;

/**
 * Created by ddj on 15-3-21.
 */
public class ChildFunApplication extends Application{

    private static final String TAG = "ChildFunApplication";

    public static final String BAIDU_MAP_KEY = "pkHdxNjR1G3PMy2LY4QQc1yA";

    private RequestQueue requestQueue;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public static ChildFunApplication appContext;
    public double[] curLatLng = new double[] {26.109967, 119.299027}; //当前位置的经纬度信息
    private LocationChange locationChange;
    private boolean isLocation = false;
    public GeofenceClient mGeofenceClient = null;
    public BMapManager mBMapMan = null;
    private Handler handler;
    private boolean isExecute = false;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance(this);
        initImageLoader(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        LocationUtil.setLocationOption(mLocationClient);                   //设置定位参数
        mLocationClient.registerLocationListener(myListener);              //注册监听函数
        mGeofenceClient = new GeofenceClient(getApplicationContext());     //注册地址围栏
        mLocationClient.start();
        handler = new Handler();

        isLocation = true;
        appContext = this;
        Timber.plant(new Timber.DebugTree());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //.writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }


    public RequestQueue getRequestQueue() {
        return requestQueue;
    }


    /**
     * 监听函数，得到当前位置信息
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                Log.e(TAG, "location == null");
                return;
            }
            StringBuilder sb = new StringBuilder(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            Log.e(TAG, sb.toString());
            if (curLatLng == null) {
                curLatLng = new double[2];
            }
            curLatLng[0] = location.getLatitude();
            curLatLng[1] = location.getLongitude();
            notityLocationChange();
            isLocation = false;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        if(mBMapMan!=null){
            mBMapMan.destroy();
            mBMapMan=null;
        }
    }

    public void registerLocationChanger(LocationChange locationChange) {
        this.locationChange = locationChange;
    }

    public void unRegisterLocationChanger() {
        this.locationChange = null;
    }

    public void notityLocationChange() {
        if (this.locationChange != null) {
            locationChange.locationChange();
        }
    }

    public static interface LocationChange {
        public void locationChange();
    }
}
