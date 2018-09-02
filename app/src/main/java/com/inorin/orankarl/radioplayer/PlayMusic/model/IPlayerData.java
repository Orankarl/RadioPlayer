package com.inorin.orankarl.radioplayer.PlayMusic.model;

import android.content.Context;

public interface IPlayerData {
    void loadLrc(int id, OnLrcLoadListener listener, Context context);
}
