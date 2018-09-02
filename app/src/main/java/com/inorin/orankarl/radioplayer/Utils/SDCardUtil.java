package com.inorin.orankarl.radioplayer.Utils;

import android.os.Environment;

import java.io.File;

public class SDCardUtil {
    public static boolean isLocal(String songid) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Radio" + File.separator + songid + ".mp3";
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }
}
