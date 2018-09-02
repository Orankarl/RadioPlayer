package com.inorin.orankarl.radioplayer.Utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class BilibiliUtil {

    final static int CID_FINISH = 0, DANMAKU_FINISH = 1;
    public String html, cid;
    public String danmaku;
    boolean waitForCID = true;
    public int avNumber;

    public void init(int avNumber) {
        this.avNumber = avNumber;
        getCID(this.avNumber);
    }

    public void getDanmaku() {
//        OkHttpClient client = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .get()
//                .url("https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid)
//                .build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
////                danmaku = response.body().string();
////                Log.d("弹幕", danmaku);
//
//                Sink sink = null;
//                BufferedSink bufferedSink = null;
//                try {
//                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath();
//                    Log.d("path", mSDCardPath);
//                    File dest = new File(mSDCardPath, "123.xml");
//                    if (!dest.exists()) dest.createNewFile();
//                    sink = Okio.sink(dest);
//                    bufferedSink = Okio.buffer(sink);
//                    bufferedSink.writeAll(response.body().source());
//
//                    bufferedSink.close();
//                    Log.i("DOWNLOAD","download success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.i("DOWNLOAD","download failed");
//                } finally {
//                    if(bufferedSink != null){
//                        bufferedSink.close();
//                    }
//
//                }
//            }
//        });
        String url = "https://api.bilibili.com/x/v1/dm/list.so?oid=" + cid;
        DownloadUtil.get().download(url, Environment.getExternalStorageDirectory().getAbsolutePath(), "danmaku.xml", new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Log.d("Download Successful", " ");
                //下载完成进行相关逻辑操作

            }

            @Override
            public void onDownloading(int progress) {
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Log.d("Download Failed", " ");
                //下载异常进行相关提示操作
            }
        });
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
