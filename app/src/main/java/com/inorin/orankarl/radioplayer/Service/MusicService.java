package com.inorin.orankarl.radioplayer.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.inorin.orankarl.radioplayer.R;
import com.inorin.orankarl.radioplayer.Utils.PlayUtil;

import java.io.File;

public class MusicService extends Service {

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private RemoteViews remoteViews;
    private static final int STOP_SERVICE = 3;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new Notification.Builder(this);
        remoteViews = new RemoteViews(getPackageName(), R.layout.nf_layout);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("type", STOP_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContent(remoteViews).setSmallIcon(R.drawable.ic_music_default_24dp);
        startForeground(1, builder.build());
        notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra("type", PlayUtil.STOP);
        String musicName = intent.getStringExtra("musicName");
        remoteViews.setTextViewText(R.id.music_name, musicName);
//        String albumpic_small = PlayUtil.currentMusic.getAlbumPicSmall();
//        if (albumpic_small != null && !"".equals(albumpic_small)) {
//            String filename = albumpic_small.substring(albumpic_small.lastIndexOf("/") + 1);
//            Bitmap bitmap = BitmapFactory.decodeFile(new File(this.getExternalCacheDir(), filename).getAbsolutePath());
//            if (bitmap != null) {
//                remoteViews.setImageViewBitmap(R.id.music_thumbnail, bitmap);
//            }
//        }
        notificationManager.notify(1, builder.build());
        switch (type) {
            case PlayUtil.PLAY:
                PlayUtil.play(this, intent.getStringExtra("musicPath"));
                break;
            case PlayUtil.PAUSE:
                PlayUtil.pause();
                break;
            case PlayUtil.STOP:
                PlayUtil.stop();
                break;
            case STOP_SERVICE:
                stopSelf();
                sendBroadcast(new Intent(PlayUtil.STOP_SERVICE_ACTION));
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlayUtil.stop();
    }
}
