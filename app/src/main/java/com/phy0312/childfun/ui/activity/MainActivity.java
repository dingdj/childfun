package com.phy0312.childfun.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mapapi.BMapManager;
import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.R;
import com.phy0312.childfun.Utils;
import com.phy0312.childfun.model.PlaceItem;
import com.phy0312.childfun.net.JsonCookieSupportRequest;
import com.phy0312.childfun.net.RequestResponseDataParseUtil;
import com.phy0312.childfun.net.URLManager;
import com.phy0312.childfun.ui.adapter.FeedAdapter;
import com.phy0312.childfun.widget.PullToRefreshLayout;
import com.phy0312.childfun.widget.smoothprogressbar.SmoothProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by ddj on 15-3-21.
 */
public class MainActivity extends BaseActivity implements PullToRefreshLayout.PullRefreshListener,
        ChildFunApplication.LocationChange{

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    ImageButton btnCreate;
    @InjectView(R.id.ptrRefresh)
    PullToRefreshLayout ptrRefresh;
    @InjectView(R.id.ptr_progress_up)
    SmoothProgressBar ptr_progress_up;
    @InjectView(R.id.ptr_progress_down)
    SmoothProgressBar ptr_progress_down;

    private boolean pendingIntroAnimation;

    private FeedAdapter feedAdapter;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChildFunApplication app = (ChildFunApplication) this.getApplication();
        if (app.mBMapMan == null) {
            app.mBMapMan = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapMan.init(ChildFunApplication.BAIDU_MAP_KEY, null);
        }
        ChildFunApplication.appContext.registerLocationChanger(this);

        setContentView(R.layout.activity_main);
        setupFeed();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }

        ptrRefresh.setRecyclerView(rvFeed);
        ptrRefresh.setUpProgressBar(ptr_progress_up);
        ptrRefresh.setBottomProgressBar(ptr_progress_down);
        ptrRefresh.setOnPullRefreshListener(this);
        handler = new Handler();
        startLoad(false, false, false);
    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this);
        rvFeed.setAdapter(feedAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        btnCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        btnCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        //feedAdapter.updateItems(true);
    }

    @Override
    public void onRefreshingUp() {
        startLoad(true, false, false);
    }

    @Override
    public void onRefreshingBottom() {
        startLoad(false, true, false);
    }

    protected void startLoad(final boolean isUp, final boolean isBottom, final boolean append) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PageIndex", 1);
            jsonObject.put("PageSize", 10);
            jsonObject.put("Type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonCookieSupportRequest request = new JsonCookieSupportRequest(Request.Method.GET, URLManager.FEED_LIST, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        new RequestResponseDataParseUtil.ResponseParse() {
                            @Override
                            public void parseResponseDataSection(JSONObject dataJsonObject) {//登录成功处理
                                try {
                                    JSONArray jsonArray = dataJsonObject.getJSONArray("list");
                                    final List<PlaceItem> list = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        PlaceItem placeItem = new PlaceItem();
                                        list.add(placeItem);
                                        placeItem.id = jsonObject.optString("ID");
                                        placeItem.name = jsonObject.optString("Name");
                                        placeItem.iconUrl = jsonObject.optString("IconUrl");
                                        placeItem.latitude = jsonObject.optDouble("Yaxis");
                                        placeItem.longitude = jsonObject.optDouble("Xaxis");
                                        placeItem.desc = jsonObject.optString("Desc");
                                    }
                                    //更新数据
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (append && feedAdapter.getList() != null) {
                                                feedAdapter.getList().addAll(list);
                                            }else{
                                                feedAdapter.getList().clear();
                                                feedAdapter.getList().addAll(list);
                                            }
                                            feedAdapter.updateItems(false);

                                            if (isUp) {
                                                ptrRefresh.setRefreshUpEnd();
                                            }

                                            if (isBottom) {
                                                ptrRefresh.setRefreshingBottomEnd();
                                            }
                                        }
                                    });


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.onResponse(jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        ChildFunApplication.appContext.getRequestQueue().add(request);
    }

    @Override
    public void locationChange() {

    }
}
