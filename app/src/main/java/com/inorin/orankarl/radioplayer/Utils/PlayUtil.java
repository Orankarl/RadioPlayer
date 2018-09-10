package com.inorin.orankarl.radioplayer.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Environment;

import com.inorin.orankarl.radioplayer.Bean.MusicBean;
import com.inorin.orankarl.radioplayer.R;
import com.inorin.orankarl.radioplayer.Service.MusicService;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;

public class PlayUtil {
    public static MediaPlayer player = null;
    public final static int PLAY = 0, PAUSE = 1, STOP = 2, LOCALMUSICBEAN = 3, PLAYUTILMUSICBEAN = 4;
    public final static String STOP_SERVICE_ACTION = "stop_service_action", UPDATE_BOTTOM_MUSIC_MSG_ACTION = "update_bottom_music_msg_action";
    public static int CURRENT_STATE = 2;//当前状态
    public static MusicBean currentMusic;

    public static void play(Context context, String musicPath) {
        if (player == null) {
            init(context);
        }
        player.reset();
        try {
//            if (!SDCardUtil.isLocal(currentMusic.getSongid() + "")) {
//                player.setDataSource(musicPath);
//            } else {
//                player.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyMusic" + File.separator + currentMusic.getSongid() + ".mp3");
//            }
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor = context.getAssets().openFd("MMF1.mp3");
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            CURRENT_STATE  = PAUSE;
        } else if (player != null && !player.isPlaying()) {
            player.start();
            CURRENT_STATE = PLAY;
        }
    }

    public static void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            CURRENT_STATE = STOP;
        }
    }

    private static void init(final Context context) {
        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                CURRENT_STATE = PLAY;
                context.sendBroadcast(new Intent(UPDATE_BOTTOM_MUSIC_MSG_ACTION));
            }
        });
    }

    public static void startService(Context context, MusicBean musicBean, int type) {
        currentMusic = musicBean;
        Intent intent = new Intent(context, MusicService.class);
        intent.putExtra("type", type);
//        intent.putExtra("musicPath", musicBean.getDownUrl());
//        intent.putExtra("musicName", musicBean.getSongName());
        intent.putExtra("musicPath", "");
        intent.putExtra("musicName", "夢のつぼみ");
        context.startService(intent);
    }
}
