package com.inorin.orankarl.radioplayer.Utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSink;
import okio.GzipSource;
import okio.InflaterSource;
import okio.Okio;
import okio.Sink;

public class BilibiliUtil {

    final static int CID_FINISH = 0, DANMAKU_FINISH = 1;
    public String html, cid;
    public String danmaku;
    boolean waitForCID = true;
    public int avNumber;
    Context context;

    public void init(int avNumber, Context context) {
        this.avNumber = avNumber;
        this.context = context;
        getCID(this.avNumber);
    }

    public void getDanmaku() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
//                .addHeader("Accept-Encoding", "gzip")
                .url("https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid)
                .build();
        final String string = request.header("Accept-Encoding");
        Log.d("header", " " + string);
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                    Log.d("path", mSDCardPath);
                    File dest = new File(mSDCardPath, "123.xml");
                    if (!dest.exists()) dest.createNewFile();
                    sink = Okio.sink(dest);

                    byte[] decompressBytes = decompress(response.body().bytes());
                    Log.d("length", String.valueOf(decompressBytes.length));

                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.write(decompressBytes);

                    bufferedSink.close();
                    Log.i("DOWNLOAD","download success");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });

    }

    public static byte[] decompress(byte[] data) {
        byte[] output;

        Inflater decompresser = new Inflater(true);
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    public void getCID(int avNumber) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url("https://www.bilibili.com/video/av" + String.valueOf(avNumber))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                html = response.body().string();
                Pattern pattern = Pattern.compile("cid=(.*?)&aid=");
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    cid = matcher.group(1);
                } else {
                    cid =  " ";
                }
                Log.d("cid", cid);
                handler.sendEmptyMessage(CID_FINISH);
            }
        });
    }

    static class BilibiliHandler extends Handler {

        WeakReference<BilibiliUtil> utilWeakReference;

        BilibiliHandler(BilibiliUtil util) {
            utilWeakReference = new WeakReference<>(util);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BilibiliUtil.CID_FINISH) {
                utilWeakReference.get().getDanmaku();
            }
        }
    }

    private BilibiliHandler handler = new BilibiliHandler(this);
    
}
