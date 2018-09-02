package com.inorin.orankarl.radioplayer.PlayMusic.model;

public interface OnLrcLoadListener {
    void onLoadSuccess(String lrcStr);
    void onLoadFailed(String msg);
}
