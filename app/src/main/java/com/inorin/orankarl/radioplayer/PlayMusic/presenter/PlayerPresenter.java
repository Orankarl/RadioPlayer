package com.inorin.orankarl.radioplayer.PlayMusic.presenter;

import android.content.Context;
import android.util.AndroidException;

import com.inorin.orankarl.radioplayer.BasePresenter;
import com.inorin.orankarl.radioplayer.PlayMusic.model.IPlayerData;
import com.inorin.orankarl.radioplayer.PlayMusic.model.OnLrcLoadListener;
import com.inorin.orankarl.radioplayer.PlayMusic.model.PlayerData;
import com.inorin.orankarl.radioplayer.PlayMusic.view.IPlayerView;

import android.os.Handler;
import java.util.logging.LogRecord;

public class PlayerPresenter implements BasePresenter {
    private IPlayerData playerData;
    private IPlayerView playerView;
    private Handler handler = new Handler();

    public PlayerPresenter(IPlayerView playerView) {
        this.playerView = playerView;
        playerData = new PlayerData();
    }
    @Override
    public void start() {
        playerView.updatePlayerControl();
    }

    public void loadLrc(int id, Context context) {
        playerData.loadLrc(id, new OnLrcLoadListener() {
            @Override
            public void onLoadSuccess(final String lrcStr) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        playerView.initLrcView(lrcStr);
                    }
                });
            }

            @Override
            public void onLoadFailed(String msg) {

            }
        }, context);
    }
}
