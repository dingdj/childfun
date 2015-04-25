package com.phy0312.childfun.net;

import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.phy0312.childfun.ChildFunApplication;
import com.phy0312.childfun.tools.AndroidUtil;
import com.phy0312.childfun.tools.Constants;
import com.phy0312.childfun.tools.LogUtil;
import com.phy0312.childfun.tools.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by dingdj on 2014/7/22.
 */
public class JsonCookieSupportRequest extends JsonObjectRequest {

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link org.json.JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonCookieSupportRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @param url
     * @param jsonRequest
     * @param listener
     * @param errorListener
     */
    public JsonCookieSupportRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    //拼接成一个新的JsonObject
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        ResponseUtil.saveSessionCookie(response.headers);
        int resultCode = ResponseUtil.getResultCode(response.headers);
        String resultMessage = ResponseUtil.getResultMessage(response.headers);
        int bodyEncryptType = ResponseUtil.getBodyEncryptType(response.headers);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ResponseUtil.RESULT_CODE, resultCode);
            jsonObject.put(ResponseUtil.RESULT_MESSAGE, resultMessage);

            if (bodyEncryptType == ResponseUtil.ENCRYPT_GZIP) {
                String jsonString = getRealString(response.data, HttpHeaderParser.parseCharset(response.headers));
                LogUtil.e(jsonString);
                jsonObject.put(ResponseUtil.RESULT_BODY, new JSONObject(jsonString));
            } else {
                String jsonString = new String(response.data,  HttpHeaderParser.parseCharset(response.headers));
                LogUtil.e(jsonString);
                jsonObject.put(ResponseUtil.RESULT_BODY, new JSONObject(jsonString));
            }
            return Response.success(jsonObject, parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public Cache.Entry parseCacheHeaders(NetworkResponse response) {
        return HttpHeaderParser.parseCacheHeaders(response, Constants.CACHE_TIME_SECONDS);
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link com.android.volley.AuthFailureError} as authentication may be required to
     * provide these values.
     *
     * @throws com.android.volley.AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        ResponseUtil.addCookieIfHave(headers);
        try {
            headers.put("OsPlatform", "android");
            headers.put("SupPhone", URLEncoder.encode(Build.MODEL, "UTF-8"));
            headers.put("SupFirm", Build.VERSION.RELEASE);
            headers.put("IMEI", AndroidUtil.getIMEI(ChildFunApplication.appContext));
            headers.put("IMSI", AndroidUtil.getIMSI(ChildFunApplication.appContext));
            headers.put("AppVerCode", AndroidUtil.getVersionCode(ChildFunApplication.appContext) + "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return headers;
    }


    private int getShort(byte[] data) {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }

    /**
     * 解压gzip内容
     * @param data
     * @param charsetName
     * @return
     */
    private String getRealString(byte[] data, String charsetName) {
        byte[] h = new byte[2];
        h[0] = (data)[0];
        h[1] = (data)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in;
        StringBuilder sb = new StringBuilder();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            if (t) {
                in = new GZIPInputStream(bis);
            } else {
                in = bis;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in, charsetName), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
