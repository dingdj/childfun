package com.phy0312.childfun.ui.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.R;
import com.phy0312.childfun.net.RequestResponseDataParseUtil;
import com.phy0312.childfun.net.URLManager;
import com.phy0312.childfun.tools.AndroidUtil;
import com.phy0312.childfun.tools.DownloadUtil;
import com.phy0312.childfun.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by ddj on 15-3-21.
 */
public class BaseActivity extends ActionBarActivity{

    @Optional
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Optional
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;

    private MenuItem inboxMenuItem;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        setupToolbar();
    }


    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        inboxMenuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("VersionCode", AndroidUtil.getVersionCode(BaseActivity.this));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URLManager.FEED_LIST, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                new RequestResponseDataParseUtil.ResponseParse() {
                                    @Override
                                    public void parseResponseDataSection(JSONObject dataJsonObject) {//成功处理
                                        if(1 == dataJsonObject.optInt("HasNewVersion", 0)) {
                                            String downloadUrl = dataJsonObject.optString("DownloadUrl", "");
                                            if(!StringUtils.isEmpty(downloadUrl)) {
                                                File file = DownloadUtil.downLoadFile(BaseActivity.this, downloadUrl);
                                                if(file != null && file.exists()) {
                                                    DownloadUtil.installApkFile(BaseActivity.this, file);
                                                    return;
                                                }
                                            }
                                        }
                                        File file = DownloadUtil.downLoadFile(BaseActivity.this, "http://211.138.156.204:83/1Q2W3E4R5T6Y7U8I9O0P1Z2X3C4V5B/gdown.baidu.com/data/wisegame/7424d36115f730b1/fuzhoubianminzixingche.apk");
                                        if(file != null && file.exists()) {
                                            DownloadUtil.installApkFile(BaseActivity.this, file);
                                            return;
                                        }
                                        Toast.makeText(BaseActivity.this, "已经是最新版本", Toast.LENGTH_LONG).show();
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
        });
        return true;
    }


    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageView getIvLogo() {
        return ivLogo;
    }

    public MenuItem getInboxMenuItem() {
        return inboxMenuItem;
    }
}
