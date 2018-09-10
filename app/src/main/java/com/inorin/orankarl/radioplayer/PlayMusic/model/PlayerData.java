package com.inorin.orankarl.radioplayer.PlayMusic.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class PlayerData implements IPlayerData {
    @Override
    public void loadLrc(int id, OnLrcLoadListener listener, Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("MMF1.lrc");
            int count = 0;
            while(count == 0) {
                count = inputStream.available();
            }
            byte[] bytes = new byte[count];
            inputStream.read(bytes);
            inputStream.close();
            String lrcStr = new String(bytes);
            listener.onLoadSuccess(lrcStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
