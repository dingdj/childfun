package com.phy0312.childfun.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.R;
import com.phy0312.childfun.model.PlaceItem;
import com.phy0312.childfun.net.JsonCookieSupportRequest;
import com.phy0312.childfun.net.RequestResponseDataParseUtil;
import com.phy0312.childfun.net.URLManager;
import com.phy0312.childfun.tools.AndroidUtil;
import com.phy0312.childfun.tools.ImageLoaderUtil;
import com.phy0312.childfun.tools.ShareUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ddj on 15-3-29.
 */
public class DetailActivity extends Activity {

    private String id;

    @InjectView(R.id.ivFinish)
    ImageView ivFinish;

    @InjectView(R.id.tvTitle)
    TextView tvTitle;

    @InjectView(R.id.ivShare)
    ImageView ivShare;

    @InjectView(R.id.tvAddress)
    TextView tvAddress;

    @InjectView(R.id.tvPhone)
    TextView tvPhone;

    @InjectView(R.id.tvTime)
    TextView tvTime;

    @InjectView(R.id.wvDesc)
    WebView wvDesc;

    @InjectView(R.id.flImage)
    ImageView flImage;

    PlaceItem placeItem;

    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        placeItem = getIntent().getParcelableExtra("PlaceItem");
        tvTitle.setText(placeItem.name);

        ivShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //todo
                ShareUtil.share(DetailActivity.this, placeItem.name+"大家快来一起玩吧");
            }
        });

        tvAddress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvAddress.getPaint().setAntiAlias(true);
        tvAddress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, LocationOverlayActivity.class);
                intent.putExtra("targetLocation", new double[]{placeItem.latitude, placeItem.longitude});
                AndroidUtil.startActivity(DetailActivity.this, intent);
            }
        });

        tvPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvPhone.getPaint().setAntiAlias(true);

        options = ImageLoaderUtil.newDisplayImageOptionsInstance();
        ImageLoader.getInstance().displayImage(placeItem.iconUrl, flImage, options);
        loadData();
    }

    private void loadData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", placeItem.id);
            Log.e("dddd", placeItem.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonCookieSupportRequest request = new JsonCookieSupportRequest(Request.Method.GET, URLManager.FEED_DETAIL_LIST+"?ID="+placeItem.id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        new RequestResponseDataParseUtil.ResponseParse() {
                            @Override
                            public void parseResponseDataSection(JSONObject dataJsonObject) {
                                try {
                                    //wvDesc.getSettings().setLoadWithOverviewMode(true);
                                    //wvDesc.setInitialScale(39);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        wvDesc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                                    } else {
                                        wvDesc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                                    }
                                    String data = dataJsonObject.optString("Detail");
                                    //wvDesc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                                    wvDesc.loadData(getHtmlData(data), "text/html; charset=UTF-8", null);
                                    tvAddress.setText(dataJsonObject.optString("Address"));
                                    final String phone = dataJsonObject.optString("Phone");
                                    tvPhone.setText(phone);
                                    tvPhone.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            new MaterialDialog.Builder(DetailActivity.this)
                                                    .title("拨打电话")
                                                    .content("确定拨打:"+phone + "?")
                                                    .positiveText("拨打")
                                                    .negativeText("取消")
                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                        @Override
                                                        public void onPositive(MaterialDialog dialog) {
                                                            super.onPositive(dialog);
                                                            Intent intent = new Intent(
                                                                    Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                                                            try {
                                                                startActivity(intent);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onNegative(MaterialDialog dialog) {
                                                            super.onNegative(dialog);
                                                        }
                                                    })
                                                    .show();

                                        }
                                    });
                                } catch (Exception e) {
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

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }



}
