package com.phy0312.childfun.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ddj on 15-4-18.
 */
public class DownloadUtil {

    public static File downLoadFile(Context context, String httpUrl) {
        final String fileName = "update.apk";
        File downloadDir = new File(Environment.getExternalStorageDirectory()+"/update");
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        final File file = new File(Environment.getExternalStorageDirectory()+"/update/" + fileName);
        BufferedOutputStream bos = null;
        InputStream is = null;
        try {
            HttpGet httpGet = new HttpGet(httpUrl);
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
            FileOutputStream fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                is = httpResponse.getEntity().getContent();
                byte[] buffer = new byte[8192];
                int count;
                while ((count = is.read(buffer)) != -1){
                    bos.write(buffer, 0, count);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(bos != null) bos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if(is != null) is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return file;
    }

    public static void installApkFile(Context context, File file) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
